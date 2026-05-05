package com.logicpuzzles.logicgrid

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.logicpuzzles.MainActivity
import com.logicpuzzles.R
import com.logicpuzzles.utils.PrefsManager

class LogicGridGameActivity : AppCompatActivity() {

    private var difficulty = 0
    private var puzzleIndex = 0
    private lateinit var puzzle: LogicGridPuzzle
    // marks[catA][itemA][catB][itemB] = 0 (empty), 1 (yes), -1 (no)
    private lateinit var marks: Array<Array<Array<IntArray>>>
    private var solved = false

    private val markCells = mutableListOf<MarkCell>()
    private data class MarkCell(val catA: Int, val itemA: Int, val catB: Int, val itemB: Int, val view: TextView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)
        puzzle = LogicGridPuzzles.get(difficulty, puzzleIndex)

        val nCats = puzzle.categories.size
        val nItems = puzzle.items[0].size
        marks = Array(nCats) { Array(nItems) { Array(nCats) { IntArray(nItems) } } }

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

        val scroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }

        // Title
        content.addView(TextView(this).apply {
            text = "Logic Grid • ${diffName(difficulty)} #${puzzleIndex + 1}"
            setTextColor(Color.WHITE)
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
        })
        content.addView(TextView(this).apply {
            text = puzzle.title
            setTextColor(Color.parseColor("#A855F7"))
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
            layoutParams = lp
        })
        content.addView(TextView(this).apply {
            text = puzzle.description
            setTextColor(Color.parseColor("#A0A0C0"))
            textSize = 13f
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(4); bottomMargin = dp(12) }
            layoutParams = lp
        })

        // Clues card
        content.addView(buildCluesCard())

        // Pair grids
        val nCats = puzzle.categories.size
        for (a in 0 until nCats) {
            for (b in a + 1 until nCats) {
                content.addView(buildPairGrid(a, b))
            }
        }

        scroll.addView(content)
        main.addView(scroll)

        // Action bar
        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor("#16213E"))
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }
        actions.addView(Button(this).apply {
            text = "Check Solution"
            setBackgroundColor(Color.parseColor("#A855F7"))
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                .apply { marginEnd = dp(4) }
            setOnClickListener { checkSolution() }
        })
        actions.addView(Button(this).apply {
            text = "Reset"
            setBackgroundColor(Color.parseColor("#0F3460"))
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                .apply { marginStart = dp(4) }
            setOnClickListener { resetMarks() }
        })
        main.addView(actions)

        root.addView(main)
    }

    private fun buildCluesCard(): View {
        val card = CardView(this).apply {
            radius = dp(8).toFloat()
            setCardBackgroundColor(Color.parseColor("#16213E"))
            cardElevation = 2f
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(12) }
            layoutParams = lp
        }
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }
        box.addView(TextView(this).apply {
            text = "Clues"
            setTextColor(Color.parseColor("#FFD93D"))
            setTypeface(null, Typeface.BOLD)
            textSize = 14f
        })
        for (clue in puzzle.clues) {
            box.addView(TextView(this).apply {
                text = clue
                setTextColor(Color.WHITE)
                textSize = 13f
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(4) }
                layoutParams = lp
            })
        }
        card.addView(box)
        return card
    }

    private fun buildPairGrid(catA: Int, catB: Int): View {
        val card = CardView(this).apply {
            radius = dp(8).toFloat()
            setCardBackgroundColor(Color.parseColor("#16213E"))
            cardElevation = 2f
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(12) }
            layoutParams = lp
        }

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), dp(12), dp(8), dp(12))
        }

        box.addView(TextView(this).apply {
            text = "${puzzle.categories[catA]} ↔ ${puzzle.categories[catB]}"
            setTextColor(Color.parseColor("#A855F7"))
            setTypeface(null, Typeface.BOLD)
            textSize = 13f
            setPadding(dp(4), 0, 0, dp(8))
        })

        val itemsA = puzzle.items[catA]
        val itemsB = puzzle.items[catB]
        val n = itemsA.size

        // Compute cell width from screen
        val displayW = resources.displayMetrics.widthPixels
        val cellSize = ((displayW - dp(80)) / (n + 1)).coerceAtMost(dp(64)).coerceAtLeast(dp(40))
        val labelW = dp(80)

        // Header row (B items as columns)
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        headerRow.addView(View(this).apply {
            layoutParams = LinearLayout.LayoutParams(labelW, cellSize)
        })
        for (j in 0 until n) {
            headerRow.addView(TextView(this).apply {
                text = itemsB[j]
                setTextColor(Color.WHITE)
                textSize = 11f
                gravity = Gravity.CENTER
                setTypeface(null, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(cellSize, cellSize)
            })
        }
        box.addView(headerRow)

        // Each row: A item label + N cells
        for (i in 0 until n) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            row.addView(TextView(this).apply {
                text = itemsA[i]
                setTextColor(Color.WHITE)
                textSize = 11f
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(4), 0, dp(4), 0)
                layoutParams = LinearLayout.LayoutParams(labelW, cellSize)
            })
            for (j in 0 until n) {
                val cell = TextView(this).apply {
                    gravity = Gravity.CENTER
                    textSize = 18f
                    setTypeface(null, Typeface.BOLD)
                    setBackgroundColor(Color.parseColor("#0F3460"))
                    layoutParams = LinearLayout.LayoutParams(cellSize, cellSize)
                        .apply { setMargins(1, 1, 1, 1) }
                    setOnClickListener { cycleMark(catA, i, catB, j) }
                }
                row.addView(cell)
                markCells.add(MarkCell(catA, i, catB, j, cell))
            }
            box.addView(row)
        }

        card.addView(box)
        return card
    }

    private fun cycleMark(catA: Int, itemA: Int, catB: Int, itemB: Int) {
        if (solved) return
        val cur = marks[catA][itemA][catB][itemB]
        val next = when (cur) {
            0 -> 1
            1 -> -1
            else -> 0
        }
        marks[catA][itemA][catB][itemB] = next
        marks[catB][itemB][catA][itemA] = next
        repaintAll()
    }

    private fun repaintAll() {
        for (mc in markCells) {
            val v = marks[mc.catA][mc.itemA][mc.catB][mc.itemB]
            mc.view.text = when (v) {
                1 -> "✓"
                -1 -> "✗"
                else -> ""
            }
            mc.view.setTextColor(when (v) {
                1 -> Color.parseColor("#6BCB77")
                -1 -> Color.parseColor("#E94560")
                else -> Color.WHITE
            })
        }
    }

    private fun resetMarks() {
        if (solved) return
        for (a in marks.indices)
            for (i in marks[a].indices)
                for (b in marks[a][i].indices)
                    for (j in marks[a][i][b].indices)
                        marks[a][i][b][j] = 0
        repaintAll()
    }

    private fun checkSolution() {
        // For each pair (catA < catB), each entry in solution must have YES at the right cell
        val nCats = puzzle.categories.size
        for (entry in puzzle.solution) {
            for (a in 0 until nCats) {
                for (b in a + 1 until nCats) {
                    val itemA = entry[a]
                    val itemB = entry[b]
                    if (marks[a][itemA][b][itemB] != 1) {
                        Toast.makeText(this, "Not all matches marked yet.", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
            }
        }
        solved = true
        PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_LOGIC_GRID, difficulty, puzzleIndex)
        AlertDialog.Builder(this)
            .setTitle("Solved!")
            .setMessage("Logic grid solved.")
            .setPositiveButton("Back to Menu") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun diffName(d: Int) = when (d) {
        0 -> "Easy"; 1 -> "Medium"; 2 -> "Hard"; else -> "Expert"
    }
}
