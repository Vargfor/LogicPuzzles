package com.logicpuzzles.logicgrid

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.logicpuzzles.MainActivity
import com.cyberhub.logicgames.R
import com.logicpuzzles.utils.applySystemBarInsets
import com.logicpuzzles.utils.CompletionDialogs
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.puzzleHeader
import com.logicpuzzles.utils.resetSymbolButton

class LogicGridGameActivity : AppCompatActivity() {

    private var difficulty = 0
    private var puzzleIndex = 0
    private lateinit var puzzle: LogicGridPuzzle
    // marks[catA][itemA][catB][itemB] = 0 (empty), 1 (yes), -1 (no)
    private lateinit var marks: Array<Array<Array<IntArray>>>
    private var solved = false
    private var themeSignature = 0

    private val markCells = mutableListOf<MarkCell>()
    private data class MarkCell(val catA: Int, val itemA: Int, val catB: Int, val itemB: Int, val view: TextView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        findViewById<View>(R.id.game_root).applySystemBarInsets()

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)
        val catalogIndex = PrefsManager(this).getCatalogIndex(MainActivity.TYPE_LOGIC_GRID, difficulty, puzzleIndex)
        puzzle = LogicGridPuzzles.get(difficulty, catalogIndex)

        val nCats = puzzle.categories.size
        val nItems = puzzle.items[0].size
        marks = Array(nCats) { Array(nItems) { Array(nCats) { IntArray(nItems) } } }

        buildUi()
    }

    override fun onResume() {
        super.onResume()
        if (themeSignature != 0 && ThemeManager.paletteSignature(this) != themeSignature) {
            buildUi()
        }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val palette = ThemeManager.currentPalette(this)
        themeSignature = ThemeManager.paletteSignature(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_LOGIC_GRID)
        val root = findViewById<FrameLayout>(R.id.game_root)
        root.removeAllViews()
        root.setBackgroundColor(palette.background)
        markCells.clear()

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
            text = puzzleHeader(R.string.puzzle_logic_grid, difficulty, puzzleIndex)
            setTextColor(palette.textPrimary)
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
        })
        content.addView(TextView(this).apply {
            text = puzzle.title
            setTextColor(accent)
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
            setTextColor(palette.textSecondary)
            textSize = 13f
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(4); bottomMargin = dp(12) }
            layoutParams = lp
        })

        // Clues card
        content.addView(buildCluesCard())

        content.addView(buildPairGridSection())

        scroll.addView(content)
        main.addView(scroll)

        // Action bar
        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(palette.surface)
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }
        actions.addView(resetSymbolButton { resetMarks() }.apply {
            layoutParams = LinearLayout.LayoutParams(dp(48), LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { marginEnd = dp(8) }
        })
        actions.addView(Button(this).apply {
            text = getString(R.string.action_check_solution)
            setBackgroundColor(accent)
            setTextColor(palette.buttonText)
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener { checkSolution() }
        })
        main.addView(actions)

        root.addView(main)
        repaintAll()
    }

    private fun buildCluesCard(): View {
        val palette = ThemeManager.currentPalette(this)
        val card = CardView(this).apply {
            radius = dp(8).toFloat()
            setCardBackgroundColor(palette.surface)
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
            text = getString(R.string.clues)
            setTextColor(palette.warning)
            setTypeface(null, Typeface.BOLD)
            textSize = 14f
        })
        for (clue in puzzle.clues) {
            box.addView(TextView(this).apply {
                text = clue
                setTextColor(palette.textPrimary)
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

    private fun buildPairGridSection(): View {
        val nCats = puzzle.categories.size
        val pairs = mutableListOf<Pair<Int, Int>>()
        for (a in 0 until nCats) {
            for (b in a + 1 until nCats) {
                pairs.add(a to b)
            }
        }

        val columns = if (pairs.size > 1) 2 else 1
        val gap = dp(8)
        val availableWidth = resources.displayMetrics.widthPixels - dp(24)
        val sheetWidth = if (columns == 2) {
            (availableWidth - gap) / 2
        } else {
            availableWidth
        }

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            pairs.chunked(columns).forEach { rowPairs ->
                val row = LinearLayout(this@LogicGridGameActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { bottomMargin = gap }
                }

                rowPairs.forEachIndexed { index, pair ->
                    row.addView(buildPairGrid(pair.first, pair.second, sheetWidth).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        ).apply {
                            if (index > 0) marginStart = gap
                        }
                    })
                }

                if (rowPairs.size < columns) {
                    row.addView(View(this@LogicGridGameActivity).apply {
                        layoutParams = LinearLayout.LayoutParams(0, 1, 1f).apply {
                            marginStart = gap
                        }
                    })
                }

                addView(row)
            }
        }
    }

    private fun buildPairGrid(catA: Int, catB: Int, maxSheetWidth: Int): View {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_LOGIC_GRID)
        val sheet = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedDrawable(palette.surface, palette.gridLine, dp(8).toFloat())
        }

        sheet.addView(TextView(this).apply {
            text = getString(R.string.logic_grid_pair_header, puzzle.categories[catA], puzzle.categories[catB])
            setTextColor(accent)
            setTypeface(null, Typeface.BOLD)
            textSize = 11f
            gravity = Gravity.CENTER
            setSingleLine(true)
            ellipsize = TextUtils.TruncateAt.END
            setPadding(dp(4), dp(6), dp(4), dp(6))
            setBackgroundColor(palette.surfaceStrong)
        })

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(6), dp(4), dp(4))
        }

        val itemsA = puzzle.items[catA]
        val itemsB = puzzle.items[catB]
        val n = itemsA.size

        val labelW = (maxSheetWidth * 0.3f).toInt().coerceIn(dp(42), dp(62))
        val cellSize = ((maxSheetWidth - labelW - dp(18)) / n).coerceIn(dp(24), dp(40))
        val labelTextSize = if (cellSize <= dp(28)) 8f else 9f
        val markTextSize = if (cellSize <= dp(28)) 13f else 15f

        val grid = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(palette.surfaceStrong)
        }

        // Header row (B items as columns)
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        headerRow.addView(TextView(this).apply {
            setBackgroundColor(palette.surfaceStrong)
            layoutParams = gridCellParams(labelW, cellSize)
        })
        for (j in 0 until n) {
            headerRow.addView(TextView(this).apply {
                text = itemsB[j]
                setTextColor(palette.textPrimary)
                textSize = labelTextSize
                gravity = Gravity.CENTER
                setTypeface(null, Typeface.BOLD)
                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
                includeFontPadding = false
                setPadding(dp(1), 0, dp(1), 0)
                setBackgroundColor(palette.surfaceStrong)
                layoutParams = gridCellParams(cellSize, cellSize)
            })
        }
        grid.addView(headerRow)

        // Each row: A item label + N cells
        for (i in 0 until n) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            row.addView(TextView(this).apply {
                text = itemsA[i]
                setTextColor(palette.textPrimary)
                textSize = labelTextSize
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER_VERTICAL or Gravity.START
                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
                includeFontPadding = false
                setPadding(dp(3), 0, dp(2), 0)
                setBackgroundColor(palette.surfaceStrong)
                layoutParams = gridCellParams(labelW, cellSize)
            })
            for (j in 0 until n) {
                val cell = TextView(this).apply {
                    gravity = Gravity.CENTER
                    textSize = markTextSize
                    setTypeface(null, Typeface.BOLD)
                    includeFontPadding = false
                    setBackgroundColor(palette.cellEmpty)
                    layoutParams = gridCellParams(cellSize, cellSize)
                    setOnClickListener { cycleMark(catA, i, catB, j) }
                }
                row.addView(cell)
                markCells.add(MarkCell(catA, i, catB, j, cell))
            }
            grid.addView(row)
        }

        box.addView(grid)
        sheet.addView(box)
        return sheet
    }

    private fun gridCellParams(width: Int, height: Int): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(width, height).apply {
            setMargins(1, 1, 1, 1)
        }

    private fun roundedDrawable(fill: Int, stroke: Int, radius: Float): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radius
            setColor(fill)
            setStroke(dp(1), stroke)
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
        val palette = ThemeManager.currentPalette(this)
        for (mc in markCells) {
            val v = marks[mc.catA][mc.itemA][mc.catB][mc.itemB]
            mc.view.text = when (v) {
                1 -> "✓"
                -1 -> "✗"
                else -> ""
            }
            mc.view.setBackgroundColor(when (v) {
                1 -> palette.cellFilled
                else -> palette.cellEmpty
            })
            mc.view.setTextColor(when (v) {
                1 -> palette.cellFilledText
                -1 -> palette.danger
                else -> palette.cellText
            })
        }
    }

    private fun resetMarks() {
        solved = false
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
        val expectedMatches = HashSet<String>()
        for (entry in puzzle.solution) {
            for (a in 0 until nCats) {
                for (b in a + 1 until nCats) {
                    val itemA = entry[a]
                    val itemB = entry[b]
                    expectedMatches.add(matchKey(a, itemA, b, itemB))
                    if (marks[a][itemA][b][itemB] != 1) {
                        Toast.makeText(this, "Not all matches marked yet.", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
            }
        }
        for (a in 0 until nCats) {
            for (b in a + 1 until nCats) {
                for (itemA in puzzle.items[a].indices) {
                    for (itemB in puzzle.items[b].indices) {
                        if (marks[a][itemA][b][itemB] == 1 &&
                            matchKey(a, itemA, b, itemB) !in expectedMatches
                        ) {
                            Toast.makeText(this, "Some marked matches are incorrect.", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                }
            }
        }
        solved = true
        PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_LOGIC_GRID, difficulty, puzzleIndex)
        CompletionDialogs.showSolved(
            this,
            "Solved!",
            "Logic grid solved.",
            MainActivity.TYPE_LOGIC_GRID,
            difficulty,
            puzzleIndex,
            LogicGridGameActivity::class.java
        )
    }

    private fun matchKey(catA: Int, itemA: Int, catB: Int, itemB: Int): String =
        "$catA:$itemA:$catB:$itemB"
}
