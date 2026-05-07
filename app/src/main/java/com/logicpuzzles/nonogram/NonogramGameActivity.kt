package com.logicpuzzles.nonogram

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.logicpuzzles.MainActivity
import com.logicpuzzles.R
import com.logicpuzzles.utils.CompletionDialogs
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.numberText
import com.logicpuzzles.utils.puzzleHeader

class NonogramGameActivity : AppCompatActivity() {

    private var difficulty = 0
    private var puzzleIndex = 0
    private lateinit var solution: Array<IntArray>
    private lateinit var grid: Array<IntArray>  // 0=empty, 1=filled, 2=marked
    private lateinit var cellViews: Array<Array<TextView>>
    private var rows = 5
    private var cols = 5
    private var solved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)

        val catalogIndex = PrefsManager(this).getCatalogIndex(MainActivity.TYPE_NONOGRAM, difficulty, puzzleIndex)
        solution = NonogramPuzzles.get(difficulty, catalogIndex)
        rows = solution.size
        cols = solution[0].size
        grid = Array(rows) { IntArray(cols) }

        buildUi()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val palette = ThemeManager.currentPalette(this)
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
        header.addView(TextView(this).apply {
            text = puzzleHeader(R.string.puzzle_nonogram, difficulty, puzzleIndex)
            setTextColor(palette.textPrimary)
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(Button(this).apply {
            text = getString(R.string.reset)
            textSize = 12f
            setBackgroundColor(palette.button)
            setTextColor(palette.buttonText)
            setOnClickListener { resetGrid() }
        })
        main.addView(header)

        main.addView(TextView(this).apply {
            text = getString(R.string.instruction_nonogram)
            setTextColor(palette.textSecondary)
            textSize = 12f
            setPadding(dp(12), 0, dp(12), dp(8))
        })

        val scroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        scroll.addView(buildBoard())
        main.addView(scroll)

        root.addView(main)
    }

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
        val cellSize = ((displayW - pad * 2) / totalCols).coerceAtMost(dp(48)).coerceAtLeast(dp(18))

        val gridLayout = GridLayout(this).apply {
            rowCount = maxColClues + rows
            columnCount = maxRowClues + cols
            setPadding(pad, pad, pad, pad)
        }

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
                    textSize = 11f
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
                    textSize = 11f
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
                    textSize = 14f
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
        cell.setOnClickListener {
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
                cell.text = "✕"
                cell.setTextColor(palette.danger)
            }
        }
    }

    private fun resetGrid() {
        if (solved) return
        for (r in 0 until rows) for (c in 0 until cols) {
            grid[r][c] = 0
            paintCell(r, c)
        }
    }

    private fun checkWin() {
        for (r in 0 until rows) for (c in 0 until cols) {
            val expected = solution[r][c]
            val actual = if (grid[r][c] == 1) 1 else 0
            if (expected != actual) return
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
}
