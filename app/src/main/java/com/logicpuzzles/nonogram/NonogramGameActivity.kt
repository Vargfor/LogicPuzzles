package com.logicpuzzles.nonogram

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.logicpuzzles.MainActivity
import com.cyberhub.logicgames.R
import com.logicpuzzles.utils.applySystemBarInsets
import com.logicpuzzles.utils.CompletionDialogs
import com.logicpuzzles.utils.gameInstructionRow
import com.logicpuzzles.utils.DragPaintSession
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.loadGamePuzzle
import com.logicpuzzles.utils.numberText
import com.logicpuzzles.utils.puzzleHeader
import com.logicpuzzles.utils.resetSymbolButton
import com.logicpuzzles.utils.interpolatedGridCells
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.roundToInt

class NonogramGameActivity : AppCompatActivity() {

    companion object {
        private const val MIN_ZOOM = 0.75f
        private const val MAX_ZOOM = 2.5f
        private const val ZOOM_STEP = 0.25f
    }

    private var difficulty = 0
    private var puzzleIndex = 0
    private lateinit var solution: Array<IntArray>
    private lateinit var grid: Array<IntArray>  // 0=empty, 1=filled, 2=marked
    private lateinit var cellViews: Array<Array<TextView>>
    private lateinit var boardContainer: FrameLayout
    private lateinit var boardGrid: GridLayout
    private lateinit var zoomPercentText: TextView
    private var rows = 5
    private var cols = 5
    private var solved = false
    private var zoomLevel = 1f
    private var themeSignature = 0
    private var boardCellSize = 0
    private var boardGridPadding = 0
    private var boardRowClueColumns = 0
    private var boardColumnClueRows = 0
    private val dragSession = DragPaintSession<Pair<Int, Int>>()
    private var dragStarted = false
    private var suppressNextClick = false
    private var downRawX = 0f
    private var downRawY = 0f
    private var lastDragCell: Pair<Int, Int>? = null
    private val touchSlop by lazy { ViewConfiguration.get(this).scaledTouchSlop.toFloat() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        findViewById<View>(R.id.game_root).applySystemBarInsets()

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)

        val catalogIndex = PrefsManager(this).getCatalogIndex(MainActivity.TYPE_NONOGRAM, difficulty, puzzleIndex)
        loadGamePuzzle(MainActivity.TYPE_NONOGRAM, "Nonogram d=$difficulty i=$puzzleIndex", {
            NonogramPuzzles.get(difficulty, catalogIndex)
        }) { loaded ->
            solution = loaded
            rows = solution.size
            cols = solution[0].size
            grid = Array(rows) { IntArray(cols) }
            buildUi()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::solution.isInitialized && themeSignature != 0 && ThemeManager.paletteSignature(this) != themeSignature) {
            buildUi()
        }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val palette = ThemeManager.currentPalette(this)
        themeSignature = ThemeManager.paletteSignature(this)
        val root = findViewById<FrameLayout>(R.id.game_root)
        root.removeAllViews()
        root.setBackgroundColor(palette.background)

        val main = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(8))
        }
        header.addView(resetSymbolButton { resetGrid() })
        header.addView(TextView(this).apply {
            text = puzzleHeader(R.string.puzzle_nonogram, difficulty, puzzleIndex)
            setTextColor(palette.textPrimary)
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        main.addView(header)

        main.addView(gameInstructionRow(
            MainActivity.TYPE_NONOGRAM,
            getString(R.string.instruction_nonogram)
        ))

        main.addView(buildZoomControls())

        val scroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
            isFillViewport = true
        }
        boardContainer = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            addView(buildBoard())
        }
        val horizontalScroll = HorizontalScrollView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            isFillViewport = true
            addView(boardContainer)
        }
        scroll.addView(horizontalScroll)
        main.addView(scroll)

        root.addView(main)
    }

    private fun buildZoomControls(): View {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(12), 0, dp(12), dp(8))

            addView(zoomButton("-", "Zoom out") { changeZoom(-ZOOM_STEP) })
            zoomPercentText = zoomPercentLabel()
            addView(zoomPercentText)
            addView(zoomButton("+", "Zoom in") { changeZoom(ZOOM_STEP) })
        }
    }

    private fun zoomPercentLabel(): TextView {
        val palette = ThemeManager.currentPalette(this)
        return TextView(this).apply {
            text = zoomText()
            contentDescription = "Reset zoom"
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setTextColor(palette.textSecondary)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dp(58), dp(32)).apply {
                marginStart = dp(2)
                marginEnd = dp(2)
            }
            setOnClickListener { setZoom(1f) }
        }
    }

    private fun zoomButton(label: String, description: String, onClick: () -> Unit): Button {
        val palette = ThemeManager.currentPalette(this)
        return Button(this).apply {
            text = label
            contentDescription = description
            textSize = 12f
            setBackgroundColor(palette.button)
            setTextColor(palette.buttonText)
            minWidth = 0
            minimumWidth = 0
            minHeight = 0
            minimumHeight = 0
            setPadding(0, 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(dp(32), dp(32)).apply {
                marginStart = dp(2)
                marginEnd = dp(2)
            }
            setOnClickListener { onClick() }
        }
    }

    private fun changeZoom(delta: Float) {
        setZoom(zoomLevel + delta)
    }

    private fun setZoom(value: Float) {
        zoomLevel = value.coerceIn(MIN_ZOOM, MAX_ZOOM)
        if (::zoomPercentText.isInitialized) zoomPercentText.text = zoomText()
        if (::boardContainer.isInitialized) {
            boardContainer.removeAllViews()
            boardContainer.addView(buildBoard())
        }
    }

    private fun zoomText(): String = "${(zoomLevel * 100).roundToInt()}%"

    private fun buildBoard(): View {
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_NONOGRAM)
        val rowClues = Array(rows) { r ->
            val clues = mutableListOf<Int>()
            var run = 0
            for (c in 0 until cols) {
                if (solution[r][c] == 1) run++
                else if (run > 0) { clues.add(run); run = 0 }
            }
            if (run > 0) clues.add(run)
            if (clues.isEmpty()) clues.add(0)
            clues
        }
        val colClues = Array(cols) { c ->
            val clues = mutableListOf<Int>()
            var run = 0
            for (r in 0 until rows) {
                if (solution[r][c] == 1) run++
                else if (run > 0) { clues.add(run); run = 0 }
            }
            if (run > 0) clues.add(run)
            if (clues.isEmpty()) clues.add(0)
            clues
        }

        val maxRowClues = rowClues.maxOf { it.size }
        val maxColClues = colClues.maxOf { it.size }

        val displayW = resources.displayMetrics.widthPixels
        val pad = dp(8)
        val totalCols = maxRowClues + cols
        val fittedCellSize = ((displayW - pad * 2) / totalCols).coerceAtMost(dp(48)).coerceAtLeast(dp(18))
        val cellSize = (fittedCellSize * zoomLevel).roundToInt().coerceIn(dp(12), dp(120))
        val clueTextSize = scaledTextSize(11f)
        val markTextSize = scaledTextSize(14f)

        val gridLayout = GridLayout(this).apply {
            rowCount = maxColClues + rows
            columnCount = maxRowClues + cols
            setPadding(pad, pad, pad, pad)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }
        boardGrid = gridLayout
        boardCellSize = cellSize
        boardGridPadding = pad
        boardRowClueColumns = maxRowClues
        boardColumnClueRows = maxColClues

        cellViews = Array(rows) { Array(cols) { TextView(this) } }

        // Top-left corner (empty)
        for (r in 0 until maxColClues) {
            for (c in 0 until maxRowClues) {
                gridLayout.addView(blankCell(r, c, cellSize))
            }
        }

        // Column clues
        for (c in 0 until cols) {
            val clues = colClues[c]
            val padding = maxColClues - clues.size
            for (i in 0 until maxColClues) {
                val tv = TextView(this).apply {
                    if (i >= padding) text = numberText(clues[i - padding])
                    setTextColor(accent)
                    textSize = clueTextSize
                    gravity = Gravity.CENTER
                    setTypeface(null, Typeface.BOLD)
                }
                tv.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(i)
                    columnSpec = GridLayout.spec(maxRowClues + c)
                    width = cellSize
                    height = cellSize
                }
                gridLayout.addView(tv)
            }
        }

        // Row clues
        for (r in 0 until rows) {
            val clues = rowClues[r]
            val padding = maxRowClues - clues.size
            for (i in 0 until maxRowClues) {
                val tv = TextView(this).apply {
                    if (i >= padding) text = numberText(clues[i - padding])
                    setTextColor(accent)
                    textSize = clueTextSize
                    gravity = Gravity.CENTER
                    setTypeface(null, Typeface.BOLD)
                }
                tv.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(maxColClues + r)
                    columnSpec = GridLayout.spec(i)
                    width = cellSize
                    height = cellSize
                }
                gridLayout.addView(tv)
            }
        }

        // Cells
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cell = TextView(this).apply {
                    gravity = Gravity.CENTER
                    textSize = markTextSize
                    setTypeface(null, Typeface.BOLD)
                }
                cell.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(maxColClues + r)
                    columnSpec = GridLayout.spec(maxRowClues + c)
                    width = cellSize
                    height = cellSize
                    setMargins(1, 1, 1, 1)
                }
                attachCellHandlers(cell, r, c)
                cellViews[r][c] = cell
                gridLayout.addView(cell)
                paintCell(r, c)
            }
        }

        return gridLayout
    }

    private fun scaledTextSize(baseSp: Float): Float {
        return (baseSp * zoomLevel).coerceIn(9f, 28f)
    }

    private fun blankCell(row: Int, col: Int, size: Int): View {
        val tv = TextView(this)
        tv.layoutParams = GridLayout.LayoutParams().apply {
            rowSpec = GridLayout.spec(row)
            columnSpec = GridLayout.spec(col)
            width = size
            height = size
        }
        return tv
    }

    private fun attachCellHandlers(cell: TextView, r: Int, c: Int) {
        cell.setOnTouchListener { view, event -> handleCellTouch(view, event, r, c) }
        cell.setOnClickListener {
            if (suppressNextClick) {
                suppressNextClick = false
                return@setOnClickListener
            }
            if (!solved) {
                grid[r][c] = if (grid[r][c] == 1) 0 else 1
                paintCell(r, c)
                checkWin()
            }
        }
        cell.setOnLongClickListener {
            if (!solved) {
                grid[r][c] = if (grid[r][c] == 2) 0 else 2
                paintCell(r, c)
            }
            true
        }
    }

    private fun handleCellTouch(view: View, event: MotionEvent, startRow: Int, startCol: Int): Boolean {
        if (solved) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                view.parent?.requestDisallowInterceptTouchEvent(true)
                dragStarted = false
                suppressNextClick = false
                downRawX = event.rawX
                downRawY = event.rawY
                lastDragCell = startRow to startCol
                dragSession.begin(targetState = grid[startRow][startCol] != 1)
            }

            MotionEvent.ACTION_MOVE -> {
                if (!dragStarted && hypot(event.rawX - downRawX, event.rawY - downRawY) >= touchSlop) {
                    dragStarted = true
                    applyDragCell(startRow to startCol)
                }
                if (dragStarted) {
                    val current = cellAt(event.rawX, event.rawY)
                    val previous = lastDragCell
                    if (current != null && previous != null) {
                        for (target in interpolatedGridCells(previous.first, previous.second, current.first, current.second)) {
                            applyDragCell(target)
                        }
                        lastDragCell = current
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                view.parent?.requestDisallowInterceptTouchEvent(false)
                if (dragStarted) {
                    suppressNextClick = true
                    view.post { suppressNextClick = false }
                    checkWin()
                }
                dragSession.end()
                lastDragCell = null
                dragStarted = false
            }
        }
        return false
    }

    private fun cellAt(rawX: Float, rawY: Float): Pair<Int, Int>? {
        if (!::boardGrid.isInitialized || boardCellSize <= 0) return null
        val location = IntArray(2)
        boardGrid.getLocationOnScreen(location)
        val gridCol = floor((rawX - location[0] - boardGridPadding) / boardCellSize).toInt()
        val gridRow = floor((rawY - location[1] - boardGridPadding) / boardCellSize).toInt()
        val row = gridRow - boardColumnClueRows
        val col = gridCol - boardRowClueColumns
        return if (row in 0 until rows && col in 0 until cols) row to col else null
    }

    private fun applyDragCell(cell: Pair<Int, Int>) {
        val targetState = dragSession.visit(cell) ?: return
        val (row, col) = cell
        grid[row][col] = if (targetState) 1 else 0
        paintCell(row, col)
    }

    private fun paintCell(r: Int, c: Int) {
        val palette = ThemeManager.currentPalette(this)
        val cell = cellViews[r][c]
        when (grid[r][c]) {
            0 -> {
                cell.setBackgroundColor(palette.cellEmpty)
                cell.setTextColor(palette.cellText)
                cell.text = ""
            }
            1 -> {
                cell.setBackgroundColor(palette.cellFilled)
                cell.setTextColor(palette.cellFilledText)
                cell.text = ""
            }
            2 -> {
                cell.setBackgroundColor(palette.cellEmpty)
                cell.text = "X"
                cell.setTextColor(palette.danger)
            }
        }
    }

    private fun resetGrid() {
        solved = false
        for (r in 0 until rows) for (c in 0 until cols) {
            grid[r][c] = 0
            paintCell(r, c)
        }
    }

    private fun checkWin() {
        for (r in 0 until rows) {
            if (lineClues(solution[r]) != lineClues(IntArray(cols) { c -> if (grid[r][c] == 1) 1 else 0 })) return
        }
        for (c in 0 until cols) {
            val expected = IntArray(rows) { r -> solution[r][c] }
            val actual = IntArray(rows) { r -> if (grid[r][c] == 1) 1 else 0 }
            if (lineClues(expected) != lineClues(actual)) return
        }
        solved = true
        PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_NONOGRAM, difficulty, puzzleIndex)
        CompletionDialogs.showSolved(
            this,
            "Solved!",
            "Nonogram complete.",
            MainActivity.TYPE_NONOGRAM,
            difficulty,
            puzzleIndex,
            NonogramGameActivity::class.java
        )
    }

    private fun lineClues(line: IntArray): List<Int> {
        val clues = mutableListOf<Int>()
        var run = 0
        for (cell in line) {
            if (cell == 1) {
                run++
            } else if (run > 0) {
                clues.add(run)
                run = 0
            }
        }
        if (run > 0) clues.add(run)
        return clues
    }
}
