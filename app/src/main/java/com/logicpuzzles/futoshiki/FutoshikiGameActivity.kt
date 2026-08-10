package com.logicpuzzles.futoshiki

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.logicpuzzles.MainActivity
import com.cyberhub.logicgames.R
import com.logicpuzzles.utils.applySystemBarInsets
import com.logicpuzzles.utils.CompletionDialogs
import com.logicpuzzles.utils.gameInstructionRow
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.ZoomableBoardHost
import com.logicpuzzles.utils.loadGamePuzzle
import com.logicpuzzles.utils.numberText
import com.logicpuzzles.utils.puzzleHeader
import com.logicpuzzles.utils.resetSymbolButton

class FutoshikiGameActivity : AppCompatActivity() {

    private var difficulty = 0
    private var puzzleIndex = 0
    private lateinit var puzzle: FutoshikiPuzzle
    private lateinit var values: Array<IntArray>
    private lateinit var fixed: Array<BooleanArray>
    private lateinit var cellViews: Array<Array<TextView>>
    private var selectedRow = -1
    private var selectedCol = -1
    private var solved = false
    private var themeSignature = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        findViewById<View>(R.id.game_root).applySystemBarInsets()

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)
        val catalogIndex = PrefsManager(this).getCatalogIndex(MainActivity.TYPE_FUTOSHIKI, difficulty, puzzleIndex)
        loadGamePuzzle(MainActivity.TYPE_FUTOSHIKI, "Futoshiki d=$difficulty i=$puzzleIndex", {
            FutoshikiPuzzles.get(difficulty, catalogIndex)
        }) { loaded ->
            puzzle = loaded
            val n = puzzle.size
            values = Array(n) { puzzle.initial[it].copyOf() }
            fixed = Array(n) { r -> BooleanArray(n) { c -> puzzle.initial[r][c] != 0 } }
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

    private fun buildUi() {
        val palette = ThemeManager.currentPalette(this)
        themeSignature = ThemeManager.paletteSignature(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_FUTOSHIKI)
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
            text = puzzleHeader(R.string.puzzle_futoshiki, difficulty, puzzleIndex)
            setTextColor(palette.textPrimary); textSize = 18f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(Button(this).apply {
            text = getString(R.string.action_check); textSize = 12f
            setBackgroundColor(accent)
            setTextColor(palette.accentText)
            setOnClickListener { checkSolution() }
        })
        main.addView(header)

        main.addView(gameInstructionRow(
            MainActivity.TYPE_FUTOSHIKI,
            if (puzzle.size > 9) {
                getString(R.string.instruction_futoshiki_symbols)
            } else {
                getString(R.string.instruction_futoshiki, puzzle.size)
            }
        ))

        main.addView(
            ZoomableBoardHost(this, MainActivity.TYPE_FUTOSHIKI) { zoom -> buildBoard(zoom) },
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        )

        main.addView(buildNumpad(palette.surface))

        root.addView(main)
    }

    private fun numBtn(label: String, onClick: () -> Unit): View {
        val palette = ThemeManager.currentPalette(this)
        return Button(this).apply {
            text = label; textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(palette.buttonText)
            setBackgroundColor(palette.button)
            minWidth = 0
            minimumWidth = 0
            layoutParams = LinearLayout.LayoutParams(0, dp(48), 1f).apply {
                setMargins(dp(2), 0, dp(2), 0)
            }
            setOnClickListener { onClick() }
        }
    }

    private fun buildNumpad(backgroundColor: Int): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(backgroundColor)
        setPadding(dp(8), dp(8), dp(8), dp(8))

        addView(LinearLayout(this@FutoshikiGameActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            for (value in 1..minOf(9, puzzle.size)) {
                addView(numBtn(valueText(value)) { setValue(value) })
            }
            if (puzzle.size <= 9) addView(numBtn("X") { setValue(0) })
        })

        if (puzzle.size > 9) {
            addView(LinearLayout(this@FutoshikiGameActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(6) }
                for (value in 10..puzzle.size) {
                    addView(numBtn(valueText(value)) { setValue(value) })
                }
                addView(numBtn("X") { setValue(0) })
            })
        }
    }

    private fun buildBoard(zoom: Float): View {
        val n = puzzle.size
        val displayW = resources.displayMetrics.widthPixels
        val displayH = resources.displayMetrics.heightPixels
        val pad = dp(16)
        val available = minOf(displayW - 2 * pad, (displayH * 0.56f).toInt())
        val geometry = FutoshikiBoardGeometry.calculate(
            availablePx = available,
            size = n,
            zoom = zoom,
            density = resources.displayMetrics.density
        )
        val cellSize = geometry.cellSizePx
        val ineqSize = geometry.inequalitySizePx

        val board = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER }
            layoutParams = lp
        }
        cellViews = Array(n) { Array(n) { TextView(this) } }

        for (r in 0 until n) {
            // Cell row
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            for (c in 0 until n) {
                val tv = FutoshikiCellView(
                    context = this,
                    cellRow = r,
                    cellCol = c,
                    gridStrokeWidth = geometry.gridStrokePx,
                    regionStrokeWidth = geometry.regionStrokePx
                ).apply {
                    gravity = Gravity.CENTER
                    textSize = geometry.fontSizeSp
                    setTypeface(null, Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(cellSize, cellSize)
                    setOnClickListener { selectCell(r, c) }
                }
                cellViews[r][c] = tv
                row.addView(tv)
                paintCell(r, c)

                if (c < n - 1) {
                    row.addView(InequalityView(this, puzzle.hConstraints[r][c], horizontal = true).apply {
                        layoutParams = LinearLayout.LayoutParams(ineqSize, cellSize)
                    })
                }
            }
            board.addView(row)

            if (r < n - 1) {
                val ineqRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                for (c in 0 until n) {
                    ineqRow.addView(InequalityView(this, puzzle.vConstraints[r][c], horizontal = false).apply {
                        layoutParams = LinearLayout.LayoutParams(cellSize, ineqSize)
                    })
                    if (c < n - 1) {
                        ineqRow.addView(View(this).apply {
                            layoutParams = LinearLayout.LayoutParams(ineqSize, ineqSize)
                        })
                    }
                }
                board.addView(ineqRow)
            }
        }

        return board
    }

    private fun selectCell(r: Int, c: Int) {
        if (solved) return
        if (fixed[r][c]) return
        selectedRow = r
        selectedCol = c
        for (rr in 0 until puzzle.size) for (cc in 0 until puzzle.size) paintCell(rr, cc)
    }

    private fun setValue(v: Int) {
        if (solved) return
        if (selectedRow < 0 || selectedCol < 0) return
        if (fixed[selectedRow][selectedCol]) return
        values[selectedRow][selectedCol] = v
        paintCell(selectedRow, selectedCol)
    }

    private fun paintCell(r: Int, c: Int) {
        val palette = ThemeManager.currentPalette(this)
        val tv = cellViews[r][c]
        val v = values[r][c]
        val isSelected = (r == selectedRow && c == selectedCol)
        val isFixed = fixed[r][c]

        tv.text = if (v == 0) "" else valueText(v)
        tv.setBackgroundColor(when {
            isSelected -> palette.cellSelected
            isFixed -> palette.cellFixed
            else -> palette.cellEmpty
        })
        tv.setTextColor(when {
            isSelected -> palette.cellSelectedText
            isFixed -> palette.cellFixedText
            else -> palette.cellText
        })
    }

    private fun resetPuzzle() {
        solved = false
        selectedRow = -1
        selectedCol = -1
        values = Array(puzzle.size) { puzzle.initial[it].copyOf() }
        for (r in 0 until puzzle.size) for (c in 0 until puzzle.size) paintCell(r, c)
    }

    private fun checkSolution() {
        val n = puzzle.size
        // All cells filled with 1..n
        for (r in 0 until n) for (c in 0 until n) {
            if (values[r][c] !in 1..n) {
                Toast.makeText(this, "Fill every cell first.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        // Rows unique
        for (r in 0 until n) {
            val seen = HashSet<Int>()
            for (c in 0 until n) {
                if (!seen.add(values[r][c])) {
                    Toast.makeText(this, "Duplicate in row ${r + 1}.", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
        // Cols unique
        for (c in 0 until n) {
            val seen = HashSet<Int>()
            for (r in 0 until n) {
                if (!seen.add(values[r][c])) {
                    Toast.makeText(this, "Duplicate in column ${c + 1}.", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
        // Sudoku regions unique
        for (boxR in 0 until n / puzzle.boxRows) {
            for (boxC in 0 until n / puzzle.boxCols) {
                val seen = HashSet<Int>()
                for (r in boxR * puzzle.boxRows until (boxR + 1) * puzzle.boxRows) {
                    for (c in boxC * puzzle.boxCols until (boxC + 1) * puzzle.boxCols) {
                        if (!seen.add(values[r][c])) {
                            Toast.makeText(this, "Duplicate in Sudoku region.", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                }
            }
        }
        // Horizontal inequalities
        for (r in 0 until n) for (c in 0 until n - 1) {
            val a = values[r][c]; val b = values[r][c + 1]
            when (puzzle.hConstraints[r][c]) {
                1 -> if (a >= b) { Toast.makeText(this, "Inequality violated.", Toast.LENGTH_SHORT).show(); return }
                2 -> if (a <= b) { Toast.makeText(this, "Inequality violated.", Toast.LENGTH_SHORT).show(); return }
            }
        }
        // Vertical inequalities
        for (r in 0 until n - 1) for (c in 0 until n) {
            val a = values[r][c]; val b = values[r + 1][c]
            when (puzzle.vConstraints[r][c]) {
                1 -> if (a >= b) { Toast.makeText(this, "Inequality violated.", Toast.LENGTH_SHORT).show(); return }
                2 -> if (a <= b) { Toast.makeText(this, "Inequality violated.", Toast.LENGTH_SHORT).show(); return }
            }
        }
        solved = true
        PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_FUTOSHIKI, difficulty, puzzleIndex)
        CompletionDialogs.showSolved(
            this,
            "Solved!",
            "Futoshiki complete.",
            MainActivity.TYPE_FUTOSHIKI,
            difficulty,
            puzzleIndex,
            FutoshikiGameActivity::class.java
        )
    }

    private fun valueText(value: Int): String = when {
        value <= 0 -> ""
        value <= 9 -> numberText(value)
        else -> ('A'.code + value - 10).toChar().toString()
    }

    private inner class FutoshikiCellView(
        context: Context,
        private val cellRow: Int,
        private val cellCol: Int,
        gridStrokeWidth: Float,
        regionStrokeWidth: Float
    ) : TextView(context) {
        private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = gridStrokeWidth
        }
        private val regionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = regionStrokeWidth
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val outlineColor = ThemeManager.currentPalette(this@FutoshikiGameActivity).gridLine
            gridPaint.color = outlineColor
            gridPaint.alpha = 128
            regionPaint.color = outlineColor
            regionPaint.alpha = 128
            val gridInset = gridPaint.strokeWidth / 2f
            canvas.drawRect(
                gridInset,
                gridInset,
                width.toFloat() - gridInset,
                height.toFloat() - gridInset,
                gridPaint
            )

            val regionInset = regionPaint.strokeWidth / 2f
            val right = width.toFloat() - regionInset
            val bottom = height.toFloat() - regionInset
            if (cellRow % puzzle.boxRows == 0) canvas.drawLine(0f, regionInset, width.toFloat(), regionInset, regionPaint)
            if ((cellRow + 1) % puzzle.boxRows == 0) canvas.drawLine(0f, bottom, width.toFloat(), bottom, regionPaint)
            if (cellCol % puzzle.boxCols == 0) canvas.drawLine(regionInset, 0f, regionInset, height.toFloat(), regionPaint)
            if ((cellCol + 1) % puzzle.boxCols == 0) canvas.drawLine(right, 0f, right, height.toFloat(), regionPaint)
        }
    }

    private inner class InequalityView(
        context: Context,
        private val relation: Int,
        private val horizontal: Boolean
    ) : View(context) {
        private val markerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        private val markerPath = Path()

        init {
            contentDescription = when {
                relation == 0 -> null
                horizontal && relation == 1 -> "Left is less than right"
                horizontal -> "Left is greater than right"
                relation == 1 -> "Top is less than bottom"
                else -> "Top is greater than bottom"
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            if (relation == 0) return

            markerPaint.color = ThemeManager.puzzleAccent(
                this@FutoshikiGameActivity,
                MainActivity.TYPE_FUTOSHIKI
            )
            markerPaint.strokeWidth = maxOf(1f, minOf(width, height) * 0.12f)
            markerPath.reset()

            if (horizontal) {
                val innerX = width * 0.28f
                val outerX = width * 0.72f
                val topY = height * 0.34f
                val middleY = height * 0.5f
                val bottomY = height * 0.66f
                if (relation == 1) {
                    markerPath.moveTo(outerX, topY)
                    markerPath.lineTo(innerX, middleY)
                    markerPath.lineTo(outerX, bottomY)
                } else {
                    markerPath.moveTo(innerX, topY)
                    markerPath.lineTo(outerX, middleY)
                    markerPath.lineTo(innerX, bottomY)
                }
            } else {
                val leftX = width * 0.34f
                val middleX = width * 0.5f
                val rightX = width * 0.66f
                val innerY = height * 0.28f
                val outerY = height * 0.72f
                if (relation == 1) {
                    markerPath.moveTo(leftX, outerY)
                    markerPath.lineTo(middleX, innerY)
                    markerPath.lineTo(rightX, outerY)
                } else {
                    markerPath.moveTo(leftX, innerY)
                    markerPath.lineTo(middleX, outerY)
                    markerPath.lineTo(rightX, innerY)
                }
            }
            canvas.drawPath(markerPath, markerPaint)
        }
    }
}
