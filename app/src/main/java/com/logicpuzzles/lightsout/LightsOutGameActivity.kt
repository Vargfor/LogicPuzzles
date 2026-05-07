package com.logicpuzzles.lightsout

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.logicpuzzles.MainActivity
import com.logicpuzzles.R
import com.logicpuzzles.utils.CompletionDialogs
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.puzzleHeader

class LightsOutGameActivity : AppCompatActivity() {

    private var difficulty = 0
    private var puzzleIndex = 0
    private var size = 5
    private lateinit var initial: Array<BooleanArray>
    private lateinit var grid: Array<BooleanArray>
    private lateinit var cellViews: Array<Array<View>>
    private var moves = 0
    private var solved = false
    private lateinit var movesText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)

        val catalogIndex = PrefsManager(this).getCatalogIndex(MainActivity.TYPE_LIGHTS_OUT, difficulty, puzzleIndex)
        val puzzle = LightsOutPuzzles.get(difficulty, catalogIndex)
        size = puzzle.size
        initial = Array(size) { puzzle.initial[it].copyOf() }
        grid = Array(size) { initial[it].copyOf() }

        buildUi()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_LIGHTS_OUT)
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
            text = puzzleHeader(R.string.puzzle_lights_out, difficulty, puzzleIndex)
            setTextColor(palette.textPrimary)
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        movesText = TextView(this).apply {
            text = getString(R.string.moves_count, 0)
            setTextColor(accent)
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
        }
        header.addView(movesText)
        main.addView(header)

        main.addView(TextView(this).apply {
            text = getString(R.string.instruction_lights_out)
            setTextColor(palette.textSecondary)
            textSize = 12f
            setPadding(dp(12), 0, dp(12), dp(8))
        })

        val boardWrap = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        boardWrap.addView(buildBoard())
        main.addView(boardWrap)

        main.addView(Button(this).apply {
            text = getString(R.string.action_reset_puzzle)
            setBackgroundColor(palette.button)
            setTextColor(palette.buttonText)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(dp(8), dp(4), dp(8), dp(8)) }
            layoutParams = lp
            setOnClickListener { reset() }
        })

        root.addView(main)
    }

    private fun buildBoard(): View {
        val displayW = resources.displayMetrics.widthPixels
        val pad = dp(16)
        val cellSize = ((displayW - pad * 2) / size).coerceAtMost(dp(72))

        val gridLayout = GridLayout(this).apply {
            rowCount = size
            columnCount = size
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER }
            layoutParams = lp
        }

        cellViews = Array(size) { Array<View>(size) { View(this) } }
        for (r in 0 until size) {
            for (c in 0 until size) {
                val cell = View(this)
                val gp = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(r)
                    columnSpec = GridLayout.spec(c)
                    width = cellSize
                    height = cellSize
                    setMargins(dp(2), dp(2), dp(2), dp(2))
                }
                cell.layoutParams = gp
                cell.setOnClickListener { onCellClick(r, c) }
                cellViews[r][c] = cell
                gridLayout.addView(cell)
                paintCell(r, c)
            }
        }
        return gridLayout
    }

    private fun paintCell(r: Int, c: Int) {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_LIGHTS_OUT)
        val on = grid[r][c]
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(8).toFloat()
            setColor(if (on) accent else palette.shadedCell)
            setStroke(2, if (on) palette.warning else palette.gridLine)
        }
        cellViews[r][c].background = drawable
    }

    private fun onCellClick(r: Int, c: Int) {
        if (solved) return
        toggle(r, c)
        moves++
        movesText.text = getString(R.string.moves_count, moves)
        for (rr in 0 until size) for (cc in 0 until size) paintCell(rr, cc)
        if (isSolved()) {
            solved = true
            PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_LIGHTS_OUT, difficulty, puzzleIndex)
            CompletionDialogs.showSolved(
                this,
                "All Lights Out!",
                "Cleared in $moves moves.",
                MainActivity.TYPE_LIGHTS_OUT,
                difficulty,
                puzzleIndex,
                LightsOutGameActivity::class.java
            )
        }
    }

    private fun toggle(r: Int, c: Int) {
        grid[r][c] = !grid[r][c]
        if (r > 0) grid[r - 1][c] = !grid[r - 1][c]
        if (r < size - 1) grid[r + 1][c] = !grid[r + 1][c]
        if (c > 0) grid[r][c - 1] = !grid[r][c - 1]
        if (c < size - 1) grid[r][c + 1] = !grid[r][c + 1]
    }

    private fun isSolved(): Boolean = grid.all { row -> row.none { it } }

    private fun reset() {
        if (solved) return
        grid = Array(size) { initial[it].copyOf() }
        moves = 0
        movesText.text = getString(R.string.moves_count, 0)
        for (r in 0 until size) for (c in 0 until size) paintCell(r, c)
    }
}
