package com.logicpuzzles.futoshiki

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.logicpuzzles.MainActivity
import com.logicpuzzles.R
import com.logicpuzzles.utils.PrefsManager

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)
        puzzle = FutoshikiPuzzles.get(difficulty, puzzleIndex)

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
            text = "Futoshiki • ${diffName(difficulty)} #${puzzleIndex + 1}"
            setTextColor(Color.WHITE); textSize = 18f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(Button(this).apply {
            text = "Check"; textSize = 12f
            setBackgroundColor(Color.parseColor("#10B981"))
            setTextColor(Color.BLACK)
            setOnClickListener { checkSolution() }
        })
        main.addView(header)

        main.addView(TextView(this).apply {
            text = "Each row & column must contain 1–${puzzle.size}. Honor the < > inequalities."
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
        val displayW = resources.displayMetrics.widthPixels
        val pad = dp(16)
        val totalUnits = n + (n - 1) * 0.4f
        val cellSize = ((displayW - 2 * pad) / totalUnits).toInt()
        val ineqSize = (cellSize * 0.4f).toInt()

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
                val tv = TextView(this).apply {
                    gravity = Gravity.CENTER
                    textSize = 18f
                    setTypeface(null, Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(cellSize, cellSize).apply {
                        setMargins(1, 1, 1, 1)
                    }
                    setOnClickListener { selectCell(r, c) }
                }
                cellViews[r][c] = tv
                row.addView(tv)
                paintCell(r, c)

                if (c < n - 1) {
                    val sym = when (puzzle.hConstraints[r][c]) { 1 -> "<"; 2 -> ">"; else -> "" }
                    row.addView(TextView(this).apply {
                        text = sym; gravity = Gravity.CENTER
                        textSize = 14f; setTypeface(null, Typeface.BOLD)
                        setTextColor(Color.parseColor("#10B981"))
                        layoutParams = LinearLayout.LayoutParams(ineqSize, cellSize)
                    })
                }
            }
            board.addView(row)

            if (r < n - 1) {
                val ineqRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                for (c in 0 until n) {
                    val sym = when (puzzle.vConstraints[r][c]) { 1 -> "∧"; 2 -> "∨"; else -> "" }
                    ineqRow.addView(TextView(this).apply {
                        text = sym; gravity = Gravity.CENTER
                        textSize = 14f; setTypeface(null, Typeface.BOLD)
                        setTextColor(Color.parseColor("#10B981"))
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
        tv.setTextColor(when {
            isFixed -> Color.parseColor("#10B981")
            else -> Color.BLACK
        })
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
        AlertDialog.Builder(this)
            .setTitle("Solved!")
            .setMessage("Futoshiki complete.")
            .setPositiveButton("Back to Menu") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun diffName(d: Int) = when (d) {
        0 -> "Easy"; 1 -> "Medium"; 2 -> "Hard"; else -> "Expert"
    }
}
