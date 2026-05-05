package com.logicpuzzles.skyscraper

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

class SkyscraperGameActivity : AppCompatActivity() {

    private var difficulty = 0
    private var puzzleIndex = 0
    private lateinit var puzzle: SkyscraperPuzzle
    private lateinit var values: Array<IntArray>
    private lateinit var fixed: Array<BooleanArray>
    private lateinit var cellViews: Array<Array<TextView>>
    private var selectedRow = -1
    private var selectedCol = -1
    private var solved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)
        puzzle = SkyscraperPuzzles.get(difficulty, puzzleIndex)

        val n = puzzle.size
        values = Array(n) { puzzle.initial[it].copyOf() }
        fixed = Array(n) { r -> BooleanArray(n) { c -> puzzle.initial[r][c] != 0 } }

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
            text = "Skyscraper • ${diffName(difficulty)} #${puzzleIndex + 1}"
            setTextColor(Color.WHITE); textSize = 18f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(Button(this).apply {
            text = "Check"; textSize = 12f
            setBackgroundColor(Color.parseColor("#3B82F6"))
            setTextColor(Color.WHITE)
            setOnClickListener { checkSolution() }
        })
        main.addView(header)

        main.addView(TextView(this).apply {
            text = "Place 1–${puzzle.size} per row & column. Outside numbers = visible buildings from that side."
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

        val numpad = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#16213E"))
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }
        for (n in 1..puzzle.size) numpad.addView(numBtn(n.toString()) { setValue(n) })
        numpad.addView(numBtn("✕") { setValue(0) })
        main.addView(numpad)

        root.addView(main)
    }

    private fun numBtn(label: String, onClick: () -> Unit): View {
        return Button(this).apply {
            text = label; textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#0F3460"))
            layoutParams = LinearLayout.LayoutParams(dp(40), dp(48)).apply {
                setMargins(dp(2), 0, dp(2), 0)
            }
            setOnClickListener { onClick() }
        }
    }

    private fun buildBoard(): View {
        val n = puzzle.size
        val gridSize = n + 2
        val displayW = resources.displayMetrics.widthPixels
        val pad = dp(16)
        val cellSize = ((displayW - 2 * pad) / gridSize).coerceAtMost(dp(64)).coerceAtLeast(dp(28))

        val gl = GridLayout(this).apply {
            rowCount = gridSize
            columnCount = gridSize
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER }
            layoutParams = lp
        }

        cellViews = Array(n) { Array(n) { TextView(this) } }

        for (gr in 0 until gridSize) {
            for (gc in 0 until gridSize) {
                val view: View = when {
                    // Corners
                    (gr == 0 || gr == gridSize - 1) && (gc == 0 || gc == gridSize - 1) -> View(this)
                    // Top clue row
                    gr == 0 -> clueText(puzzle.cluesTop[gc - 1])
                    // Bottom clue row
                    gr == gridSize - 1 -> clueText(puzzle.cluesBottom[gc - 1])
                    // Left clue column
                    gc == 0 -> clueText(puzzle.cluesLeft[gr - 1])
                    // Right clue column
                    gc == gridSize - 1 -> clueText(puzzle.cluesRight[gr - 1])
                    // Cell
                    else -> {
                        val r = gr - 1; val c = gc - 1
                        val tv = TextView(this).apply {
                            gravity = Gravity.CENTER
                            textSize = 18f
                            setTypeface(null, Typeface.BOLD)
                            setOnClickListener { selectCell(r, c) }
                        }
                        cellViews[r][c] = tv
                        paintCell(r, c)
                        tv
                    }
                }
                view.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(gr)
                    columnSpec = GridLayout.spec(gc)
                    width = cellSize
                    height = cellSize
                    setMargins(1, 1, 1, 1)
                }
                gl.addView(view)
            }
        }
        return gl
    }

    private fun clueText(value: Int): TextView {
        return TextView(this).apply {
            text = if (value > 0) value.toString() else ""
            setTextColor(Color.parseColor("#3B82F6"))
            textSize = 14f
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }
    }

    private fun selectCell(r: Int, c: Int) {
        if (solved || fixed[r][c]) return
        selectedRow = r; selectedCol = c
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
        val tv = cellViews[r][c]
        val v = values[r][c]
        val isSelected = (r == selectedRow && c == selectedCol)
        val isFixed = fixed[r][c]
        tv.text = if (v == 0) "" else v.toString()
        tv.setBackgroundColor(when {
            isSelected -> Color.parseColor("#FFE066")
            isFixed -> Color.parseColor("#0D2747")
            else -> Color.WHITE
        })
        tv.setTextColor(if (isFixed) Color.parseColor("#3B82F6") else Color.BLACK)
    }

    private fun visibility(line: IntArray): Int {
        var maxSeen = 0
        var count = 0
        for (h in line) {
            if (h > maxSeen) { count++; maxSeen = h }
        }
        return count
    }

    private fun checkSolution() {
        val n = puzzle.size
        for (r in 0 until n) for (c in 0 until n) {
            if (values[r][c] !in 1..n) {
                Toast.makeText(this, "Fill every cell first.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        for (r in 0 until n) {
            val seen = HashSet<Int>()
            for (c in 0 until n) if (!seen.add(values[r][c])) {
                Toast.makeText(this, "Duplicate in row ${r + 1}.", Toast.LENGTH_SHORT).show(); return
            }
        }
        for (c in 0 until n) {
            val seen = HashSet<Int>()
            for (r in 0 until n) if (!seen.add(values[r][c])) {
                Toast.makeText(this, "Duplicate in column ${c + 1}.", Toast.LENGTH_SHORT).show(); return
            }
        }
        // Visibility
        for (c in 0 until n) {
            val col = IntArray(n) { values[it][c] }
            if (puzzle.cluesTop[c] > 0 && visibility(col) != puzzle.cluesTop[c]) {
                Toast.makeText(this, "Top clue mismatch on column ${c + 1}.", Toast.LENGTH_SHORT).show(); return
            }
            if (puzzle.cluesBottom[c] > 0 && visibility(col.reversedArray()) != puzzle.cluesBottom[c]) {
                Toast.makeText(this, "Bottom clue mismatch on column ${c + 1}.", Toast.LENGTH_SHORT).show(); return
            }
        }
        for (r in 0 until n) {
            val row = values[r]
            if (puzzle.cluesLeft[r] > 0 && visibility(row) != puzzle.cluesLeft[r]) {
                Toast.makeText(this, "Left clue mismatch on row ${r + 1}.", Toast.LENGTH_SHORT).show(); return
            }
            if (puzzle.cluesRight[r] > 0 && visibility(row.reversedArray()) != puzzle.cluesRight[r]) {
                Toast.makeText(this, "Right clue mismatch on row ${r + 1}.", Toast.LENGTH_SHORT).show(); return
            }
        }
        solved = true
        PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_SKYSCRAPER, difficulty, puzzleIndex)
        AlertDialog.Builder(this)
            .setTitle("Solved!")
            .setMessage("Skyscraper complete.")
            .setPositiveButton("Back to Menu") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun diffName(d: Int) = when (d) {
        0 -> "Easy"; 1 -> "Medium"; 2 -> "Hard"; else -> "Expert"
    }
}
