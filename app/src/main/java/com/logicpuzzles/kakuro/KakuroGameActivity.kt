package com.logicpuzzles.kakuro

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.logicpuzzles.MainActivity
import com.cyberhub.logicgames.R
import com.logicpuzzles.utils.applySystemBarInsets
import com.logicpuzzles.utils.CompletionDialogs
import com.logicpuzzles.utils.gameInstructionRow
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.numberText
import com.logicpuzzles.utils.puzzleHeader
import com.logicpuzzles.utils.resetSymbolButton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class KakuroGameActivity : AppCompatActivity() {

    private var difficulty = 0
    private var puzzleIndex = 0
    private lateinit var puzzle: KakuroPuzzle
    private lateinit var values: Array<IntArray> // 0 = empty, 1-9 = filled
    private lateinit var fixed: Array<BooleanArray>
    private lateinit var cellViews: Array<Array<View?>>
    private var selectedRow = -1
    private var selectedCol = -1
    private var solved = false
    private var themeSignature = 0
    private lateinit var boardContainer: FrameLayout
    private lateinit var zoomPercentText: TextView
    private var zoomLevel = 1f
    private var minimumZoom = MIN_ZOOM
    private var isLoading = false
    private var loadFailed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        findViewById<View>(R.id.game_root).applySystemBarInsets()

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)

        val catalogIndex = PrefsManager(this).getCatalogIndex(MainActivity.TYPE_KAKURO, difficulty, puzzleIndex)
        loadPuzzle(catalogIndex)
    }

    override fun onResume() {
        super.onResume()
        if (themeSignature != 0 && ThemeManager.paletteSignature(this) != themeSignature) {
            when {
                ::puzzle.isInitialized -> buildUi()
                isLoading -> showLoadingUi()
                loadFailed -> showLoadErrorUi()
            }
        }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun loadPuzzle(catalogIndex: Int) {
        if (isLoading) return
        isLoading = true
        loadFailed = false
        showLoadingUi()

        lifecycleScope.launch {
            try {
                val loadedPuzzle = withContext(Dispatchers.Default) {
                    KakuroPuzzles.get(difficulty, catalogIndex)
                }
                initializePuzzle(loadedPuzzle)
                buildUi()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                Log.e(TAG, "Unable to prepare Kakuro d=$difficulty i=$puzzleIndex", error)
                loadFailed = true
                showLoadErrorUi()
            } finally {
                isLoading = false
            }
        }
    }

    private fun initializePuzzle(loadedPuzzle: KakuroPuzzle) {
        puzzle = loadedPuzzle
        values = Array(puzzle.rows) { r -> IntArray(puzzle.cols) { c -> puzzle.initialAt(r, c) } }
        fixed = Array(puzzle.rows) { r -> BooleanArray(puzzle.cols) { c -> puzzle.initialAt(r, c) > 0 } }
        cellViews = Array(puzzle.rows) { arrayOfNulls<View>(puzzle.cols) }
        minimumZoom = overviewZoom()
        zoomLevel = minimumZoom
    }

    private fun showLoadingUi() {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_KAKURO)
        themeSignature = ThemeManager.paletteSignature(this)
        val root = findViewById<FrameLayout>(R.id.game_root)
        root.removeAllViews()
        root.setBackgroundColor(palette.background)
        root.addView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(ProgressBar(this@KakuroGameActivity).apply {
                isIndeterminate = true
                indeterminateTintList = ColorStateList.valueOf(accent)
            })
            addView(TextView(this@KakuroGameActivity).apply {
                text = getString(R.string.kakuro_loading)
                setTextColor(palette.textSecondary)
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(dp(16), dp(16), dp(16), 0)
            })
        }, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))
    }

    private fun showLoadErrorUi() {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_KAKURO)
        themeSignature = ThemeManager.paletteSignature(this)
        val root = findViewById<FrameLayout>(R.id.game_root)
        root.removeAllViews()
        root.setBackgroundColor(palette.background)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(24), dp(24), dp(24))
            addView(TextView(this@KakuroGameActivity).apply {
                text = getString(R.string.kakuro_load_failed)
                setTextColor(palette.textPrimary)
                textSize = 16f
                gravity = Gravity.CENTER
            })
            addView(LinearLayout(this@KakuroGameActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                setPadding(0, dp(16), 0, 0)
                addView(Button(this@KakuroGameActivity).apply {
                    text = getString(R.string.back)
                    setTextColor(palette.buttonText)
                    setBackgroundColor(palette.button)
                    setOnClickListener { finish() }
                })
                addView(Button(this@KakuroGameActivity).apply {
                    text = getString(R.string.retry)
                    setTextColor(palette.accentText)
                    setBackgroundColor(accent)
                    setOnClickListener {
                        val retryCatalogIndex = PrefsManager(this@KakuroGameActivity)
                            .getCatalogIndex(MainActivity.TYPE_KAKURO, difficulty, puzzleIndex)
                        loadPuzzle(retryCatalogIndex)
                    }
                }, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginStart = dp(12) })
            })
        }
        root.addView(content, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))
    }

    private fun buildUi() {
        val palette = ThemeManager.currentPalette(this)
        themeSignature = ThemeManager.paletteSignature(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_KAKURO)
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
            text = puzzleHeader(R.string.puzzle_kakuro, difficulty, puzzleIndex)
            setTextColor(palette.textPrimary)
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(Button(this).apply {
            text = getString(R.string.action_check)
            textSize = 12f
            setBackgroundColor(accent)
            setTextColor(palette.accentText)
            setOnClickListener { checkSolution() }
        })
        main.addView(header)

        main.addView(gameInstructionRow(
            MainActivity.TYPE_KAKURO,
            getString(R.string.instruction_kakuro)
        ))

        main.addView(buildZoomControls())

        val boardWrap = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        boardWrap.addView(buildBoardScroller())
        main.addView(boardWrap)

        // Numpad
        val numpad = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(palette.surface)
            setPadding(dp(4), dp(8), dp(4), dp(8))
        }
        for (n in 1..9) {
            numpad.addView(makeNumpadBtn(n.toString()) {
                if (selectedRow >= 0 && selectedCol >= 0 && !fixed[selectedRow][selectedCol]) {
                    values[selectedRow][selectedCol] = n
                    paintCell(selectedRow, selectedCol)
                }
            })
        }
        numpad.addView(makeNumpadBtn("✕") {
            if (selectedRow >= 0 && selectedCol >= 0 && !fixed[selectedRow][selectedCol]) {
                values[selectedRow][selectedCol] = 0
                paintCell(selectedRow, selectedCol)
            }
        })
        main.addView(numpad)

        root.addView(main)
    }

    private fun makeNumpadBtn(label: String, onClick: () -> Unit): View {
        val palette = ThemeManager.currentPalette(this)
        return Button(this).apply {
            text = label
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(palette.buttonText)
            setBackgroundColor(palette.button)
            layoutParams = LinearLayout.LayoutParams(dp(32), dp(40)).apply {
                setMargins(dp(2), 0, dp(2), 0)
            }
            setOnClickListener { onClick() }
        }
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
            contentDescription = "Reset zoom to overview"
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setTextColor(palette.textSecondary)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dp(58), dp(32)).apply {
                marginStart = dp(2)
                marginEnd = dp(2)
            }
            setOnClickListener { setZoom(minimumZoom) }
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
        zoomLevel = value.coerceIn(minimumZoom, MAX_ZOOM)
        if (::zoomPercentText.isInitialized) zoomPercentText.text = zoomText()
        if (::boardContainer.isInitialized) refreshBoard()
    }

    private fun zoomText(): String = "${(zoomLevel * 100).roundToInt()}%"

    private fun refreshBoard() {
        boardContainer.removeAllViews()
        cellViews = Array(puzzle.rows) { arrayOfNulls<View>(puzzle.cols) }
        boardContainer.addView(buildBoard())
    }

    private fun overviewZoom(): Float {
        val display = resources.displayMetrics
        val availableWidth = (display.widthPixels - dp(32)).coerceAtLeast(dp(160))
        val availableHeight = (display.heightPixels - dp(230)).coerceAtLeast(dp(180))
        val baseCellSize = dp(BASE_CELL_DP)
        val fitWidth = availableWidth.toFloat() / (puzzle.cols * baseCellSize)
        val fitHeight = availableHeight.toFloat() / (puzzle.rows * baseCellSize)
        return minOf(1f, fitWidth, fitHeight).coerceIn(MIN_ZOOM, 1f)
    }

    private fun buildBoardScroller(): View {
        val vScroll = ScrollView(this).apply {
            isFillViewport = true
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        val hScroll = HorizontalScrollView(this).apply {
            isFillViewport = true
            overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
        }
        val boardFrame = FrameLayout(this).apply {
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }
        boardContainer = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            addView(buildBoard())
        }
        boardFrame.addView(boardContainer)
        hScroll.addView(boardFrame)
        vScroll.addView(hScroll)
        return vScroll
    }

    private fun buildBoard(): View {
        val cellSize = (dp(BASE_CELL_DP) * zoomLevel).roundToInt().coerceIn(dp(12), dp(96))

        val gridLayout = GridLayout(this).apply {
            rowCount = puzzle.rows
            columnCount = puzzle.cols
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER }
            layoutParams = lp
        }

        for (r in 0 until puzzle.rows) {
            for (c in 0 until puzzle.cols) {
                val cellView = makeCell(puzzle.grid[r][c], r, c)
                val gp = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(r)
                    columnSpec = GridLayout.spec(c)
                    width = cellSize
                    height = cellSize
                }
                cellView.layoutParams = gp
                gridLayout.addView(cellView)
                cellViews[r][c] = cellView
                if (puzzle.grid[r][c] is KCell.White) {
                    paintCell(r, c)
                }
            }
        }
        return gridLayout
    }

    private fun makeCell(cell: KCell, r: Int, c: Int): View {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_KAKURO)
        return when (cell) {
            KCell.Void -> View(this).apply {
                setBackgroundColor(Color.TRANSPARENT)
                isEnabled = false
            }
            KCell.Black -> View(this).apply {
                background = cellDrawable(palette.locked)
            }
            is KCell.Clue -> {
                ClueCellView(
                    context = this,
                    downSum = cell.downSum,
                    rightSum = cell.rightSum,
                    fillColor = palette.surfaceStrong,
                    lineColor = palette.gridLine,
                    downTextColor = palette.warning,
                    rightTextColor = accent
                )
            }
            is KCell.White -> {
                val tv = TextView(this).apply {
                    gravity = Gravity.CENTER
                    textSize = (18f * zoomLevel).coerceIn(10f, 30f)
                    setTypeface(null, Typeface.BOLD)
                    includeFontPadding = false
                    background = cellDrawable(palette.cellEmpty)
                    setOnClickListener { selectCell(r, c) }
                }
                tv
            }
        }
    }

    private fun selectCell(r: Int, c: Int) {
        if (solved || fixed[r][c]) return
        selectedRow = r
        selectedCol = c
        for (rr in 0 until puzzle.rows) for (cc in 0 until puzzle.cols) {
            if (puzzle.grid[rr][cc] is KCell.White) paintCell(rr, cc)
        }
    }

    private fun paintCell(r: Int, c: Int) {
        val palette = ThemeManager.currentPalette(this)
        val view = cellViews[r][c] as? TextView ?: return
        if (puzzle.grid[r][c] !is KCell.White) return
        val isSelected = (r == selectedRow && c == selectedCol)
        val isFixed = fixed[r][c]
        val v = values[r][c]
        view.text = if (v == 0) "" else numberText(v)
        view.background = cellDrawable(when {
            isSelected -> palette.cellSelected
            isFixed -> palette.cellFixed
            else -> palette.cellEmpty
        })
        view.setTextColor(when {
            isSelected -> palette.cellSelectedText
            isFixed -> palette.cellFixedText
            else -> palette.cellText
        })
    }

    private fun resetPuzzle() {
        solved = false
        selectedRow = -1
        selectedCol = -1
        values = Array(puzzle.rows) { r -> IntArray(puzzle.cols) { c -> puzzle.initialAt(r, c) } }
        for (r in 0 until puzzle.rows) {
            for (c in 0 until puzzle.cols) {
                if (puzzle.grid[r][c] is KCell.White) paintCell(r, c)
            }
        }
    }

    private fun checkSolution() {
        // Check every white cell is filled
        for (r in 0 until puzzle.rows) for (c in 0 until puzzle.cols) {
            if (puzzle.grid[r][c] is KCell.White && values[r][c] == 0) {
                Toast.makeText(this, "Fill every cell first.", Toast.LENGTH_SHORT).show()
                return
            }
            if (puzzle.grid[r][c] is KCell.White && values[r][c] !in 1..9) {
                Toast.makeText(this, "Use digits 1-9 in white cells.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        // Verify every horizontal and vertical run
        for (r in 0 until puzzle.rows) {
            for (c in 0 until puzzle.cols) {
                val cell = puzzle.grid[r][c]
                if (cell is KCell.Clue) {
                    if (cell.rightSum > 0) {
                        val cells = mutableListOf<Int>()
                        var cc = c + 1
                        while (cc < puzzle.cols && puzzle.grid[r][cc] is KCell.White) {
                            cells.add(values[r][cc]); cc++
                        }
                        if (cells.sum() != cell.rightSum || cells.toSet().size != cells.size) {
                            Toast.makeText(this, "Some runs are wrong.", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                    if (cell.downSum > 0) {
                        val cells = mutableListOf<Int>()
                        var rr = r + 1
                        while (rr < puzzle.rows && puzzle.grid[rr][c] is KCell.White) {
                            cells.add(values[rr][c]); rr++
                        }
                        if (cells.sum() != cell.downSum || cells.toSet().size != cells.size) {
                            Toast.makeText(this, "Some runs are wrong.", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                }
            }
        }
        solved = true
        PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_KAKURO, difficulty, puzzleIndex)
        CompletionDialogs.showSolved(
            this,
            "Solved!",
            "All runs sum correctly.",
            MainActivity.TYPE_KAKURO,
            difficulty,
            puzzleIndex,
            KakuroGameActivity::class.java
        )
    }

    private fun cellDrawable(fill: Int): GradientDrawable {
        val palette = ThemeManager.currentPalette(this)
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(fill)
            setStroke(dp(1), palette.gridLine)
        }
    }

    private companion object {
        const val TAG = "KakuroGameActivity"
        const val BASE_CELL_DP = 42
        const val MIN_ZOOM = 0.3f
        const val MAX_ZOOM = 2.25f
        const val ZOOM_FACTOR = 1.25f
    }

    private class ClueCellView(
        context: android.content.Context,
        private val downSum: Int,
        private val rightSum: Int,
        private val fillColor: Int,
        private val lineColor: Int,
        private val downTextColor: Int,
        private val rightTextColor: Int
    ) : View(context) {
        private val boundsRect = Rect()
        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
        private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            boundsRect.set(0, 0, width, height)
            val cellSize = minOf(width, height).toFloat()
            fillPaint.color = fillColor
            linePaint.color = lineColor
            linePaint.strokeWidth = maxOf(1f, cellSize * 0.045f)
            canvas.drawRect(boundsRect, fillPaint)
            canvas.drawLine(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                linePaint
            )
            canvas.drawRect(boundsRect, linePaint)

            textPaint.textSize = (cellSize * 0.27f).coerceAtLeast(8f)
            if (rightSum > 0) {
                textPaint.color = rightTextColor
                drawCenteredText(canvas, context.numberText(rightSum), width * 0.68f, height * 0.29f)
            }
            if (downSum > 0) {
                textPaint.color = downTextColor
                drawCenteredText(canvas, context.numberText(downSum), width * 0.32f, height * 0.74f)
            }
        }

        private fun drawCenteredText(canvas: Canvas, text: String, centerX: Float, centerY: Float) {
            val baseline = centerY - (textPaint.descent() + textPaint.ascent()) / 2f
            canvas.drawText(text, centerX, baseline, textPaint)
        }
    }
}
