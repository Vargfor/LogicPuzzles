package com.logicpuzzles.slitherlink

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewConfiguration
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.logicpuzzles.MainActivity
import com.cyberhub.logicgames.R
import com.logicpuzzles.utils.applySystemBarInsets
import com.logicpuzzles.utils.CompletionDialogs
import com.logicpuzzles.utils.gameInstructionRow
import com.logicpuzzles.utils.DragPaintSession
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.SlitherlinkEdgeHitTester
import com.logicpuzzles.utils.SlitherlinkEdgeTarget
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.loadGamePuzzle
import com.logicpuzzles.utils.numberText
import com.logicpuzzles.utils.puzzleHeader
import com.logicpuzzles.utils.resetSymbolButton
import java.util.ArrayDeque
import kotlin.math.ceil
import kotlin.math.hypot
import kotlin.math.roundToInt

class SlitherlinkGameActivity : AppCompatActivity() {

    companion object {
        private const val MIN_ZOOM = 0.3f
        private const val MAX_ZOOM = 3.0f
        private const val ZOOM_FACTOR = 1.2f
    }

    private var difficulty = 0
    private var puzzleIndex = 0
    private lateinit var puzzle: SlitherlinkPuzzle
    private lateinit var hEdges: Array<BooleanArray>  // (rows+1) x cols
    private lateinit var vEdges: Array<BooleanArray>  // rows x (cols+1)
    private lateinit var hEdgeViews: Array<Array<View?>>
    private lateinit var vEdgeViews: Array<Array<View?>>
    private var solved = false
    private var zoomLevel = 1.0f
    private lateinit var boardContainer: FrameLayout
    private lateinit var boardGrid: GridLayout
    private lateinit var zoomPercentText: TextView
    private var themeSignature = 0
    private var boardCellSize = 0
    private var boardEdgeSize = 0
    private var boardPadding = 0
    private val dragSession = DragPaintSession<SlitherlinkEdgeTarget>()
    private var dragStarted = false
    private var suppressNextClick = false
    private var downRawX = 0f
    private var downRawY = 0f
    private var lastRawX = 0f
    private var lastRawY = 0f
    private val touchSlop by lazy { ViewConfiguration.get(this).scaledTouchSlop.toFloat() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        findViewById<View>(R.id.game_root).applySystemBarInsets()

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)
        val catalogIndex = PrefsManager(this).getCatalogIndex(MainActivity.TYPE_SLITHERLINK, difficulty, puzzleIndex)
        loadGamePuzzle(MainActivity.TYPE_SLITHERLINK, "Slitherlink d=$difficulty i=$puzzleIndex", {
            SlitherlinkPuzzles.get(difficulty, catalogIndex)
        }) { loaded ->
            puzzle = loaded
            hEdges = Array(puzzle.rows + 1) { BooleanArray(puzzle.cols) }
            vEdges = Array(puzzle.rows) { BooleanArray(puzzle.cols + 1) }
            hEdgeViews = Array(puzzle.rows + 1) { arrayOfNulls<View>(puzzle.cols) }
            vEdgeViews = Array(puzzle.rows) { arrayOfNulls<View>(puzzle.cols + 1) }
            buildUi()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::puzzle.isInitialized && themeSignature != 0 && ThemeManager.paletteSignature(this) != themeSignature) {
            buildUi()
        }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun dp(v: Float) = (v * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val palette = ThemeManager.currentPalette(this)
        themeSignature = ThemeManager.paletteSignature(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_SLITHERLINK)
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
        header.addView(resetSymbolButton { resetPuzzle() })
        header.addView(TextView(this).apply {
            text = puzzleHeader(R.string.puzzle_slitherlink, difficulty, puzzleIndex)
            setTextColor(palette.textPrimary); textSize = 16f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(Button(this).apply {
            text = getString(R.string.action_check); textSize = 11f
            setBackgroundColor(accent)
            setTextColor(palette.accentText)
            setOnClickListener { checkSolution() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { marginStart = dp(4) }
        })
        main.addView(header)

        main.addView(gameInstructionRow(
            MainActivity.TYPE_SLITHERLINK,
            getString(R.string.instruction_slitherlink)
        ))

        main.addView(buildZoomControls())

        // 2D scrollable board container
        val verticalScroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        val horizontalScroll = HorizontalScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        boardContainer = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        boardContainer.addView(buildBoard())
        horizontalScroll.addView(boardContainer)
        verticalScroll.addView(horizontalScroll)
        main.addView(verticalScroll)

        root.addView(main)
    }

    private fun buildZoomControls(): View {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(12), 0, dp(12), dp(8))

            addView(zoomButton("-", "Zoom out") { setZoom(zoomLevel / ZOOM_FACTOR) })
            zoomPercentText = zoomPercentLabel()
            addView(zoomPercentText)
            addView(zoomButton("+", "Zoom in") { setZoom(zoomLevel * ZOOM_FACTOR) })
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
            setTypeface(null, Typeface.BOLD)
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

    private fun setZoom(value: Float) {
        zoomLevel = value.coerceIn(MIN_ZOOM, MAX_ZOOM)
        if (::zoomPercentText.isInitialized) zoomPercentText.text = zoomText()
        if (::boardContainer.isInitialized) refreshBoard()
    }

    private fun zoomText(): String = "${(zoomLevel * 100).roundToInt()}%"

    private fun refreshBoard() {
        boardContainer.removeAllViews()
        // Reset view caches before rebuild
        hEdgeViews = Array(puzzle.rows + 1) { arrayOfNulls<View>(puzzle.cols) }
        vEdgeViews = Array(puzzle.rows) { arrayOfNulls<View>(puzzle.cols + 1) }
        boardContainer.addView(buildBoard())
    }

    private fun resetPuzzle() {
        solved = false
        hEdges = Array(puzzle.rows + 1) { BooleanArray(puzzle.cols) }
        vEdges = Array(puzzle.rows) { BooleanArray(puzzle.cols + 1) }
        for (r in 0..puzzle.rows) for (c in 0 until puzzle.cols) paintHEdge(r, c)
        for (r in 0 until puzzle.rows) for (c in 0..puzzle.cols) paintVEdge(r, c)
    }

    private fun buildBoard(): View {
        val palette = ThemeManager.currentPalette(this)
        val rows = puzzle.rows; val cols = puzzle.cols
        val displayW = resources.displayMetrics.widthPixels
        val pad = dp(16)
        val maxBoardW = displayW - 2 * pad
        // edgeSize = cellSize / 3 (thicker than before)
        // total width = cols*cellSize + (cols+1)*cellSize/3 = cellSize * (3*cols + cols + 1) / 3
        // = cellSize * (4*cols + 1) / 3
        // cellSize = maxBoardW * 3 / (4*cols + 1)
        val baseCellSize = (maxBoardW * 3 / (4 * cols + 1)).coerceAtLeast(dp(28))
        val cellSize = (baseCellSize * zoomLevel).toInt().coerceAtLeast(dp(12))
        val edgeSize = (cellSize / 3).coerceAtLeast(dp(4))

        val gl = GridLayout(this).apply {
            rowCount = 2 * rows + 1
            columnCount = 2 * cols + 1
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = lp
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setBackgroundColor(palette.cellEmpty)
        }
        boardGrid = gl
        boardCellSize = cellSize
        boardEdgeSize = edgeSize
        boardPadding = dp(8)

        for (gr in 0 until 2 * rows + 1) {
            for (gc in 0 until 2 * cols + 1) {
                val w = if (gc % 2 == 0) edgeSize else cellSize
                val h = if (gr % 2 == 0) edgeSize else cellSize
                val view: View = when {
                    gr % 2 == 0 && gc % 2 == 0 -> {
                        View(this).apply { setBackgroundColor(palette.cellFilled) }
                    }
                    gr % 2 == 0 && gc % 2 == 1 -> {
                        val r = gr / 2; val c = gc / 2
                        val v = View(this).apply {
                            val target = SlitherlinkEdgeTarget(horizontal = true, row = r, col = c)
                            setOnTouchListener { touched, event -> handleEdgeTouch(touched, event, target) }
                            setOnClickListener {
                                if (suppressNextClick) {
                                    suppressNextClick = false
                                    return@setOnClickListener
                                }
                                hEdges[r][c] = !hEdges[r][c]
                                paintHEdge(r, c)
                            }
                        }
                        hEdgeViews[r][c] = v
                        v
                    }
                    gr % 2 == 1 && gc % 2 == 0 -> {
                        val r = gr / 2; val c = gc / 2
                        val v = View(this).apply {
                            val target = SlitherlinkEdgeTarget(horizontal = false, row = r, col = c)
                            setOnTouchListener { touched, event -> handleEdgeTouch(touched, event, target) }
                            setOnClickListener {
                                if (suppressNextClick) {
                                    suppressNextClick = false
                                    return@setOnClickListener
                                }
                                vEdges[r][c] = !vEdges[r][c]
                                paintVEdge(r, c)
                            }
                        }
                        vEdgeViews[r][c] = v
                        v
                    }
                    else -> {
                        val r = gr / 2; val c = gc / 2
                        val clue = puzzle.clues[r][c]
                        TextView(this).apply {
                            text = if (clue >= 0) numberText(clue) else ""
                            setTextColor(palette.cellText)
                            textSize = (cellSize / resources.displayMetrics.density / 3.5f)
                                .coerceAtLeast(8f)
                                .coerceAtMost(22f)
                            setTypeface(null, Typeface.BOLD)
                            gravity = Gravity.CENTER
                            setBackgroundColor(palette.cellEmpty)
                        }
                    }
                }
                view.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(gr)
                    columnSpec = GridLayout.spec(gc)
                    width = w
                    height = h
                }
                gl.addView(view)
            }
        }

        for (r in 0..rows) for (c in 0 until cols) paintHEdge(r, c)
        for (r in 0 until rows) for (c in 0..cols) paintVEdge(r, c)

        return gl
    }

    private fun handleEdgeTouch(view: View, event: MotionEvent, start: SlitherlinkEdgeTarget): Boolean {
        if (solved) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                view.parent?.requestDisallowInterceptTouchEvent(true)
                dragStarted = false
                suppressNextClick = false
                downRawX = event.rawX
                downRawY = event.rawY
                lastRawX = event.rawX
                lastRawY = event.rawY
                dragSession.begin(targetState = !isEdgeSet(start))
            }

            MotionEvent.ACTION_MOVE -> {
                if (!dragStarted && hypot(event.rawX - downRawX, event.rawY - downRawY) >= touchSlop) {
                    dragStarted = true
                    applyDragEdge(start)
                }
                if (dragStarted) {
                    visitEdgeSegment(lastRawX, lastRawY, event.rawX, event.rawY)
                    lastRawX = event.rawX
                    lastRawY = event.rawY
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                view.parent?.requestDisallowInterceptTouchEvent(false)
                if (dragStarted) {
                    suppressNextClick = true
                    view.post { suppressNextClick = false }
                }
                dragSession.end()
                dragStarted = false
            }
        }
        return false
    }

    private fun visitEdgeSegment(fromX: Float, fromY: Float, toX: Float, toY: Float) {
        val distance = hypot(toX - fromX, toY - fromY)
        val sampleSize = (minOf(boardCellSize, boardEdgeSize) / 2f).coerceAtLeast(2f)
        val steps = ceil(distance / sampleSize).toInt().coerceAtLeast(1)
        for (step in 0..steps) {
            val progress = step / steps.toFloat()
            val target = edgeAt(
                fromX + (toX - fromX) * progress,
                fromY + (toY - fromY) * progress
            ) ?: continue
            applyDragEdge(target)
        }
    }

    private fun edgeAt(rawX: Float, rawY: Float): SlitherlinkEdgeTarget? {
        if (!::boardGrid.isInitialized) return null
        val location = IntArray(2)
        boardGrid.getLocationOnScreen(location)
        return SlitherlinkEdgeHitTester.hit(
            x = rawX - location[0],
            y = rawY - location[1],
            rows = puzzle.rows,
            cols = puzzle.cols,
            cellSize = boardCellSize,
            edgeSize = boardEdgeSize,
            padding = boardPadding
        )
    }

    private fun isEdgeSet(target: SlitherlinkEdgeTarget): Boolean = if (target.horizontal) {
        hEdges[target.row][target.col]
    } else {
        vEdges[target.row][target.col]
    }

    private fun applyDragEdge(target: SlitherlinkEdgeTarget) {
        val targetState = dragSession.visit(target) ?: return
        if (target.horizontal) {
            hEdges[target.row][target.col] = targetState
            paintHEdge(target.row, target.col)
        } else {
            vEdges[target.row][target.col] = targetState
            paintVEdge(target.row, target.col)
        }
    }

    private fun paintHEdge(r: Int, c: Int) {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_SLITHERLINK)
        hEdgeViews[r][c]?.setBackgroundColor(
            if (hEdges[r][c]) accent else palette.gridLine
        )
    }

    private fun paintVEdge(r: Int, c: Int) {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_SLITHERLINK)
        vEdgeViews[r][c]?.setBackgroundColor(
            if (vEdges[r][c]) accent else palette.gridLine
        )
    }

    private fun checkSolution() {
        val rows = puzzle.rows; val cols = puzzle.cols

        for (r in 0 until rows) for (c in 0 until cols) {
            val clue = puzzle.clues[r][c]
            if (clue < 0) continue
            var count = 0
            if (hEdges[r][c]) count++
            if (hEdges[r + 1][c]) count++
            if (vEdges[r][c]) count++
            if (vEdges[r][c + 1]) count++
            if (count != clue) {
                Toast.makeText(this, "Clue mismatch at row ${r + 1}, col ${c + 1}.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val degree = Array(rows + 1) { IntArray(cols + 1) }
        for (r in 0..rows) for (c in 0 until cols) {
            if (hEdges[r][c]) {
                degree[r][c]++
                degree[r][c + 1]++
            }
        }
        for (r in 0 until rows) for (c in 0..cols) {
            if (vEdges[r][c]) {
                degree[r][c]++
                degree[r + 1][c]++
            }
        }
        for (r in 0..rows) for (c in 0..cols) {
            if (degree[r][c] != 0 && degree[r][c] != 2) {
                Toast.makeText(this, "Loop must not branch.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        var sr = -1; var sc = -1
        outer@ for (r in 0..rows) for (c in 0..cols) {
            if (degree[r][c] > 0) { sr = r; sc = c; break@outer }
        }
        if (sr == -1) {
            Toast.makeText(this, "No loop drawn.", Toast.LENGTH_SHORT).show()
            return
        }

        var totalUsed = 0
        for (r in 0..rows) for (c in 0..cols) if (degree[r][c] > 0) totalUsed++

        val visited = Array(rows + 1) { BooleanArray(cols + 1) }
        val q = ArrayDeque<Pair<Int, Int>>()
        q.add(sr to sc); visited[sr][sc] = true
        var visitedCount = 1
        while (q.isNotEmpty()) {
            val (r, c) = q.removeFirst()
            if (r > 0 && vEdges[r - 1][c] && !visited[r - 1][c]) {
                visited[r - 1][c] = true; q.add(r - 1 to c); visitedCount++
            }
            if (r < rows && vEdges[r][c] && !visited[r + 1][c]) {
                visited[r + 1][c] = true; q.add(r + 1 to c); visitedCount++
            }
            if (c > 0 && hEdges[r][c - 1] && !visited[r][c - 1]) {
                visited[r][c - 1] = true; q.add(r to c - 1); visitedCount++
            }
            if (c < cols && hEdges[r][c] && !visited[r][c + 1]) {
                visited[r][c + 1] = true; q.add(r to c + 1); visitedCount++
            }
        }

        if (visitedCount != totalUsed) {
            Toast.makeText(this, "Edges form multiple loops.", Toast.LENGTH_SHORT).show()
            return
        }

        solved = true
        PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_SLITHERLINK, difficulty, puzzleIndex)
        CompletionDialogs.showSolved(
            this,
            "Solved!",
            "Slitherlink complete.",
            MainActivity.TYPE_SLITHERLINK,
            difficulty,
            puzzleIndex,
            SlitherlinkGameActivity::class.java
        )
    }
}
