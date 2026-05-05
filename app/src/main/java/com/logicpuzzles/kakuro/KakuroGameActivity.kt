package com.logicpuzzles.kakuro

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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.logicpuzzles.MainActivity
import com.logicpuzzles.R
import com.logicpuzzles.utils.PrefsManager

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)

        puzzle = KakuroPuzzles.get(difficulty, puzzleIndex)
        values = Array(puzzle.rows) { r -> IntArray(puzzle.cols) { c -> puzzle.initialAt(r, c) } }
        fixed = Array(puzzle.rows) { r -> BooleanArray(puzzle.cols) { c -> puzzle.initialAt(r, c) > 0 } }
        cellViews = Array(puzzle.rows) { arrayOfNulls<View>(puzzle.cols) }

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
            text = "Kakuro • ${diffName(difficulty)} #${puzzleIndex + 1}"
            setTextColor(Color.WHITE)
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(Button(this).apply {
            text = "Check"
            textSize = 12f
            setBackgroundColor(Color.parseColor("#6BCB77"))
            setTextColor(Color.BLACK)
            setOnClickListener { checkSolution() }
        })
        main.addView(header)

        main.addView(TextView(this).apply {
            text = "Tap a white cell, then a digit. Runs must sum to the clue with no repeats."
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

        // Numpad
        val numpad = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#16213E"))
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
        return Button(this).apply {
            text = label
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#0F3460"))
            layoutParams = LinearLayout.LayoutParams(dp(32), dp(40)).apply {
                setMargins(dp(2), 0, dp(2), 0)
            }
            setOnClickListener { onClick() }
        }
    }

    private fun buildBoard(): View {
        val displayW = resources.displayMetrics.widthPixels
        val pad = dp(8)
        val cellSize = ((displayW - pad * 2) / puzzle.cols).coerceAtMost(dp(56)).coerceAtLeast(dp(28))

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
                val cellView = makeCell(puzzle.grid[r][c], r, c, cellSize)
                val gp = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(r)
                    columnSpec = GridLayout.spec(c)
                    width = cellSize
                    height = cellSize
                }
                cellView.layoutParams = gp
                gridLayout.addView(cellView)
                cellViews[r][c] = cellView
            }
        }
        return gridLayout
    }

    private fun makeCell(cell: KCell, r: Int, c: Int, size: Int): View {
        return when (cell) {
            is KCell.Black -> View(this).apply {
                setBackgroundColor(Color.BLACK)
            }
            is KCell.Clue -> {
                val frame = FrameLayout(this).apply {
                    setBackgroundColor(Color.parseColor("#0D1B2A"))
                }
                if (cell.rightSum > 0) {
                    val tv = TextView(this).apply {
                        text = cell.rightSum.toString()
                        setTextColor(Color.parseColor("#6BCB77"))
                        textSize = 11f
                        setTypeface(null, Typeface.BOLD)
                        setPadding(0, dp(2), dp(4), 0)
                    }
                    val lp = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply { gravity = Gravity.TOP or Gravity.END }
                    frame.addView(tv, lp)
                }
                if (cell.downSum > 0) {
                    val tv = TextView(this).apply {
                        text = cell.downSum.toString()
                        setTextColor(Color.parseColor("#FFD93D"))
                        textSize = 11f
                        setTypeface(null, Typeface.BOLD)
                        setPadding(dp(4), 0, 0, dp(2))
                    }
                    val lp = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply { gravity = Gravity.BOTTOM or Gravity.START }
                    frame.addView(tv, lp)
                }
                // Diagonal divider line via background drawable would be ideal; keep simple.
                frame
            }
            is KCell.White -> {
                val tv = TextView(this).apply {
                    gravity = Gravity.CENTER
                    textSize = 18f
                    setTypeface(null, Typeface.BOLD)
                    setBackgroundColor(Color.WHITE)
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
        val view = cellViews[r][c] as? TextView ?: return
        if (puzzle.grid[r][c] !is KCell.White) return
        val isSelected = (r == selectedRow && c == selectedCol)
        val isFixed = fixed[r][c]
        val v = values[r][c]
        view.text = if (v == 0) "" else v.toString()
        view.setBackgroundColor(when {
            isSelected -> Color.parseColor("#FFE066")
            isFixed -> Color.parseColor("#E5F4EE")
            else -> Color.WHITE
        })
        view.setTextColor(if (isFixed) Color.parseColor("#0E7C5C") else Color.BLACK)
    }

    private fun checkSolution() {
        // Check every white cell is filled
        for (r in 0 until puzzle.rows) for (c in 0 until puzzle.cols) {
            if (puzzle.grid[r][c] is KCell.White && values[r][c] == 0) {
                Toast.makeText(this, "Fill every cell first.", Toast.LENGTH_SHORT).show()
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
        AlertDialog.Builder(this)
            .setTitle("Solved!")
            .setMessage("All runs sum correctly.")
            .setPositiveButton("Back to Menu") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun diffName(d: Int) = when (d) {
        0 -> "Easy"; 1 -> "Medium"; 2 -> "Hard"; else -> "Expert"
    }
}
