package com.logicpuzzles.lightsout

import android.graphics.Color
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.logicpuzzles.MainActivity
import com.logicpuzzles.R
import com.logicpuzzles.utils.PrefsManager

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

        val puzzle = LightsOutPuzzles.get(difficulty, puzzleIndex)
        size = puzzle.size
        initial = Array(size) { puzzle.initial[it].copyOf() }
        grid = Array(size) { initial[it].copyOf() }

        buildUi()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val root = findViewById<FrameLayout>(R.id.game_root)
        root.removeAllViews()

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
            text = "Lights Out • ${diffName(difficulty)} #${puzzleIndex + 1}"
            setTextColor(Color.WHITE)
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        movesText = TextView(this).apply {
            text = "Moves: 0"
            setTextColor(Color.parseColor("#FFD93D"))
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
        }
        header.addView(movesText)
        main.addView(header)

        main.addView(TextView(this).apply {
            text = "Tap a light to toggle it and its neighbors. Turn all OFF!"
            setTextColor(Color.parseColor("#A0A0C0"))
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
            text = "Reset Puzzle"
            setBackgroundColor(Color.parseColor("#0F3460"))
            setTextColor(Color.WHITE)
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
        val on = grid[r][c]
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(8).toFloat()
            setColor(if (on) Color.parseColor("#FFD93D") else Color.parseColor("#1A2040"))
            setStroke(2, if (on) Color.parseColor("#FFE066") else Color.parseColor("#2A3560"))
        }
        cellViews[r][c].background = drawable
    }

    private fun onCellClick(r: Int, c: Int) {
        if (solved) return
        toggle(r, c)
        moves++
        movesText.text = "Moves: $moves"
        for (rr in 0 until size) for (cc in 0 until size) paintCell(rr, cc)
        if (isSolved()) {
            solved = true
            PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_LIGHTS_OUT, difficulty, puzzleIndex)
            AlertDialog.Builder(this)
                .setTitle("All Lights Out!")
                .setMessage("Cleared in $moves moves.")
                .setPositiveButton("Back to Menu") { _, _ -> finish() }
                .setCancelable(false)
                .show()
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
        movesText.text = "Moves: 0"
        for (r in 0 until size) for (c in 0 until size) paintCell(r, c)
    }

    private fun diffName(d: Int) = when (d) {
        0 -> "Easy"; 1 -> "Medium"; 2 -> "Hard"; else -> "Expert"
    }
}
