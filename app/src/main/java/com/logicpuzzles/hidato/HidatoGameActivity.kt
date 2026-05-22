package com.logicpuzzles.hidato

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
import androidx.appcompat.app.AppCompatActivity
import com.logicpuzzles.MainActivity
import com.cyberhub.logicgames.R
import com.logicpuzzles.utils.CompletionDialogs
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.numberText
import com.logicpuzzles.utils.puzzleHeader
import kotlin.math.abs

class HidatoGameActivity : AppCompatActivity() {

    private var difficulty = 0
    private var puzzleIndex = 0
    private lateinit var puzzle: HidatoPuzzle
    private lateinit var values: Array<IntArray>
    private lateinit var fixed: Array<BooleanArray>
    private lateinit var cellViews: Array<Array<TextView?>>
    private var selectedRow = -1
    private var selectedCol = -1
    private var solved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)
        val catalogIndex = PrefsManager(this).getCatalogIndex(MainActivity.TYPE_HIDATO, difficulty, puzzleIndex)
        puzzle = HidatoPuzzles.get(difficulty, catalogIndex)

        values = Array(puzzle.rows) { puzzle.initial[it].copyOf() }
        fixed = Array(puzzle.rows) { r ->
            BooleanArray(puzzle.cols) { c -> puzzle.initial[r][c] > 0 }
        }
        cellViews = Array(puzzle.rows) { arrayOfNulls<TextView>(puzzle.cols) }

        buildUi()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_HIDATO)
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
            text = puzzleHeader(R.string.puzzle_hidato, difficulty, puzzleIndex)
            setTextColor(palette.textPrimary); textSize = 18f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(Button(this).apply {
            text = getString(R.string.action_check); textSize = 12f
            setBackgroundColor(accent)
            setTextColor(palette.buttonText)
            setOnClickListener { checkSolution() }
        })
        main.addView(header)

        main.addView(TextView(this).apply {
            text = getString(R.string.instruction_hidato, puzzle.maxNumber)
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

        // Numpad
        main.addView(buildNumpad())

        root.addView(main)
    }

    private fun buildBoard(): View {
        val palette = ThemeManager.currentPalette(this)
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

        for (r in 0 until puzzle.rows) {
            for (c in 0 until puzzle.cols) {
                val initial = puzzle.initial[r][c]
                val cell: View = if (initial == -1) {
                    View(this).apply { setBackgroundColor(palette.locked) }
                } else {
                    TextView(this).apply {
                        gravity = Gravity.CENTER
                        textSize = 16f
                        setTypeface(null, Typeface.BOLD)
                        setOnClickListener { selectCell(r, c) }
                    }.also { cellViews[r][c] = it }
                }
                cell.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(r)
                    columnSpec = GridLayout.spec(c)
                    width = cellSize
                    height = cellSize
                    setMargins(1, 1, 1, 1)
                }
                gl.addView(cell)
                if (initial != -1) paintCell(r, c)
            }
        }
        return gl
    }

    private fun buildNumpad(): View {
        val palette = ThemeManager.currentPalette(this)
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(palette.surface)
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        // Row 1: 1 2 3 4 5 ⌫
        val row1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        for (d in 1..5) row1.addView(numpadBtn(d.toString(), false) { typeDigit(d) })
        row1.addView(numpadBtn("⌫", true) { typeDelete() })
        container.addView(row1)

        // Row 2: 6 7 8 9 0 ✓
        val row2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(6) }
            layoutParams = lp
        }
        for (d in 6..9) row2.addView(numpadBtn(d.toString(), false) { typeDigit(d) })
        row2.addView(numpadBtn("0", false) { typeDigit(0) })
        row2.addView(numpadBtn("✓", true) { typeConfirm() })
        container.addView(row2)

        return container
    }

    private fun numpadBtn(label: String, accent: Boolean, onClick: () -> Unit): View {
        val palette = ThemeManager.currentPalette(this)
        return Button(this).apply {
            text = label; textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(palette.buttonText)
            setBackgroundColor(if (accent) ThemeManager.puzzleAccent(this@HidatoGameActivity, MainActivity.TYPE_HIDATO) else palette.button)
            layoutParams = LinearLayout.LayoutParams(0, dp(48), 1f).apply {
                setMargins(dp(2), 0, dp(2), 0)
            }
            setOnClickListener { onClick() }
        }
    }

    private fun selectCell(r: Int, c: Int) {
        if (solved || fixed[r][c]) return
        if (puzzle.initial[r][c] == -1) return
        selectedRow = r; selectedCol = c
        repaintAll()
    }

    private fun typeDigit(d: Int) {
        if (solved || selectedRow < 0 || selectedCol < 0) return
        if (fixed[selectedRow][selectedCol]) return
        val cur = values[selectedRow][selectedCol]
        val newVal = when {
            cur == 0 -> d
            cur < 10 -> cur * 10 + d
            else -> d  // already 2 digits, restart
        }
        values[selectedRow][selectedCol] = if (newVal in 0..puzzle.maxNumber) newVal else d.coerceIn(0, puzzle.maxNumber)
        paintCell(selectedRow, selectedCol)
    }

    private fun typeDelete() {
        if (solved || selectedRow < 0 || selectedCol < 0) return
        if (fixed[selectedRow][selectedCol]) return
        values[selectedRow][selectedCol] = values[selectedRow][selectedCol] / 10
        paintCell(selectedRow, selectedCol)
    }

    private fun typeConfirm() {
        selectedRow = -1; selectedCol = -1
        repaintAll()
    }

    private fun repaintAll() {
        for (r in 0 until puzzle.rows) for (c in 0 until puzzle.cols) {
            if (puzzle.initial[r][c] != -1) paintCell(r, c)
        }
    }

    private fun paintCell(r: Int, c: Int) {
        val palette = ThemeManager.currentPalette(this)
        val tv = cellViews[r][c] ?: return
        val v = values[r][c]
        val isFixed = fixed[r][c]
        val isSelected = (r == selectedRow && c == selectedCol)
        tv.text = if (v == 0) "" else numberText(v)
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

    private fun checkSolution() {
        val positions = HashMap<Int, Pair<Int, Int>>()
        for (r in 0 until puzzle.rows) for (c in 0 until puzzle.cols) {
            if (puzzle.initial[r][c] == -1) continue
            val v = values[r][c]
            if (v !in 1..puzzle.maxNumber) {
                Toast.makeText(this, "Fill every cell with 1–${puzzle.maxNumber}.", Toast.LENGTH_SHORT).show()
                return
            }
            if (positions.containsKey(v)) {
                Toast.makeText(this, "Number $v used twice.", Toast.LENGTH_SHORT).show()
                return
            }
            positions[v] = r to c
        }
        for (k in 1 until puzzle.maxNumber) {
            val a = positions[k]!!
            val b = positions[k + 1]!!
            if (abs(a.first - b.first) > 1 || abs(a.second - b.second) > 1) {
                Toast.makeText(this, "$k and ${k + 1} are not adjacent.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        solved = true
        PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_HIDATO, difficulty, puzzleIndex)
        CompletionDialogs.showSolved(
            this,
            "Solved!",
            "Hidato complete.",
            MainActivity.TYPE_HIDATO,
            difficulty,
            puzzleIndex,
            HidatoGameActivity::class.java
        )
    }
}
