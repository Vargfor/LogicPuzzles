package com.logicpuzzles.nurikabe

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.logicpuzzles.MainActivity
import com.logicpuzzles.R
import com.logicpuzzles.utils.PrefsManager
import java.util.ArrayDeque

class NurikabeGameActivity : AppCompatActivity() {

    private var difficulty = 0
    private var puzzleIndex = 0
    private lateinit var puzzle: NurikabePuzzle
    private lateinit var shaded: Array<BooleanArray>  // true = shaded (river)
    private lateinit var cellViews: Array<Array<TextView>>
    private var solved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)
        puzzle = NurikabePuzzles.get(difficulty, puzzleIndex)
        shaded = Array(puzzle.rows) { BooleanArray(puzzle.cols) }

        buildUi()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

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
            text = "Nurikabe • ${diffName(difficulty)} #${puzzleIndex + 1}"
            setTextColor(Color.WHITE); textSize = 18f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(Button(this).apply {
            text = "Check"; textSize = 12f
            setBackgroundColor(Color.parseColor("#06B6D4"))
            setTextColor(Color.WHITE)
            setOnClickListener { checkSolution() }
        })
        main.addView(header)

        main.addView(TextView(this).apply {
            text = "Tap to shade. Each number forms a white island of exactly that size. All shaded cells must connect. No 2×2 shaded."
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

        root.addView(main)
    }

    private fun buildBoard(): View {
        val displayW = resources.displayMetrics.widthPixels
        val pad = dp(16)
        val cellSize = ((displayW - 2 * pad) / puzzle.cols).coerceAtMost(dp(56)).coerceAtLeast(dp(28))

        val gl = GridLayout(this).apply {
            rowCount = puzzle.rows
            columnCount = puzzle.cols
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER }
            layoutParams = lp
        }
        cellViews = Array(puzzle.rows) { Array(puzzle.cols) { TextView(this) } }

        for (r in 0 until puzzle.rows) {
            for (c in 0 until puzzle.cols) {
                val tv = TextView(this).apply {
                    gravity = Gravity.CENTER
                    textSize = 16f
                    setTypeface(null, Typeface.BOLD)
                    setOnClickListener { onCellClick(r, c) }
                }
                tv.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(r)
                    columnSpec = GridLayout.spec(c)
                    width = cellSize
                    height = cellSize
                    setMargins(1, 1, 1, 1)
                }
                cellViews[r][c] = tv
                gl.addView(tv)
                paintCell(r, c)
            }
        }
        return gl
    }

    private fun onCellClick(r: Int, c: Int) {
        if (solved) return
        if (puzzle.numbers[r][c] > 0) return  // numbered cells stay white
        shaded[r][c] = !shaded[r][c]
        paintCell(r, c)
    }

    private fun paintCell(r: Int, c: Int) {
        val tv = cellViews[r][c]
        val n = puzzle.numbers[r][c]
        if (n > 0) {
            tv.text = n.toString()
            tv.setBackgroundColor(Color.WHITE)
            tv.setTextColor(Color.parseColor("#0E7490"))
        } else if (shaded[r][c]) {
            tv.text = ""
            tv.setBackgroundColor(Color.parseColor("#1A1A2E"))
        } else {
            tv.text = ""
            tv.setBackgroundColor(Color.WHITE)
        }
    }

    private fun checkSolution() {
        val rows = puzzle.rows; val cols = puzzle.cols
        val white = Array(rows) { r -> BooleanArray(cols) { c -> !shaded[r][c] } }
        // No 2x2 of shaded
        for (r in 0 until rows - 1) for (c in 0 until cols - 1) {
            if (shaded[r][c] && shaded[r][c + 1] && shaded[r + 1][c] && shaded[r + 1][c + 1]) {
                Toast.makeText(this, "No 2×2 of shaded cells allowed.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        // Each numbered cell's white island must have the correct size and contain no other number.
        // Unnumbered white regions are allowed.
        val visited = Array(rows) { BooleanArray(cols) }
        for (r in 0 until rows) for (c in 0 until cols) {
            if (!white[r][c] || visited[r][c] || puzzle.numbers[r][c] == 0) continue
            val number = puzzle.numbers[r][c]
            val region = mutableListOf<Pair<Int, Int>>()
            val q = ArrayDeque<Pair<Int, Int>>()
            q.add(r to c); visited[r][c] = true
            while (q.isNotEmpty()) {
                val (cr, cc) = q.removeFirst()
                region.add(cr to cc)
                for ((dr, dc) in listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) {
                    val nr = cr + dr; val nc = cc + dc
                    if (nr in 0 until rows && nc in 0 until cols && white[nr][nc] && !visited[nr][nc]) {
                        visited[nr][nc] = true
                        q.add(nr to nc)
                    }
                }
            }
            if (region.count { puzzle.numbers[it.first][it.second] > 0 } > 1) {
                Toast.makeText(this, "Two island numbers cannot share a white region.", Toast.LENGTH_SHORT).show()
                return
            }
            if (region.size != number) {
                Toast.makeText(this, "Island $number has wrong size (got ${region.size}, need $number).", Toast.LENGTH_SHORT).show()
                return
            }
        }
        // All shaded cells must form one connected group
        val totalShaded = shaded.sumOf { row -> row.count { it } }
        if (totalShaded > 0) {
            var sr = -1; var sc = -1
            outer@ for (r in 0 until rows) for (c in 0 until cols) {
                if (shaded[r][c]) { sr = r; sc = c; break@outer }
            }
            val seen = Array(rows) { BooleanArray(cols) }
            val q = ArrayDeque<Pair<Int, Int>>()
            q.add(sr to sc); seen[sr][sc] = true
            var count = 0
            while (q.isNotEmpty()) {
                val (cr, cc) = q.removeFirst(); count++
                for ((dr, dc) in listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) {
                    val nr = cr + dr; val nc = cc + dc
                    if (nr in 0 until rows && nc in 0 until cols && shaded[nr][nc] && !seen[nr][nc]) {
                        seen[nr][nc] = true
                        q.add(nr to nc)
                    }
                }
            }
            if (count != totalShaded) {
                Toast.makeText(this, "All shaded cells must form one connected river.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        solved = true
        PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_NURIKABE, difficulty, puzzleIndex)
        AlertDialog.Builder(this)
            .setTitle("Solved!")
            .setMessage("Nurikabe complete.")
            .setPositiveButton("Back to Menu") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun diffName(d: Int) = when (d) {
        0 -> "Easy"; 1 -> "Medium"; 2 -> "Hard"; else -> "Expert"
    }
}
