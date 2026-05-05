package com.logicpuzzles

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.logicpuzzles.futoshiki.FutoshikiGameActivity
import com.logicpuzzles.hidato.HidatoGameActivity
import com.logicpuzzles.kakuro.KakuroGameActivity
import com.logicpuzzles.lightsout.LightsOutGameActivity
import com.logicpuzzles.logicgrid.LogicGridGameActivity
import com.logicpuzzles.mastermind.MastermindGameActivity
import com.logicpuzzles.nonogram.NonogramGameActivity
import com.logicpuzzles.nurikabe.NurikabeGameActivity
import com.logicpuzzles.skyscraper.SkyscraperGameActivity
import com.logicpuzzles.slitherlink.SlitherlinkGameActivity
import com.logicpuzzles.utils.PrefsManager

class PuzzleMenuActivity : AppCompatActivity() {

    private val diffNames = listOf("Easy", "Medium", "Hard", "Expert")
    private val diffColors = listOf("#4CAF50", "#FF9800", "#F44336", "#9C27B0")
    private var puzzleType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_menu)

        puzzleType = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_TYPE, 0)

        val (typeName, typeDesc) = when (puzzleType) {
            MainActivity.TYPE_NONOGRAM    -> "Nonogram" to "Tap to fill, long-press to mark"
            MainActivity.TYPE_MASTERMIND  -> "Mastermind" to "Guess the hidden color sequence"
            MainActivity.TYPE_LIGHTS_OUT  -> "Lights Out" to "Toggle lights until all are off"
            MainActivity.TYPE_KAKURO      -> "Kakuro" to "Fill digits so every run matches its clue sum"
            MainActivity.TYPE_LOGIC_GRID  -> "Logic Grid" to "Use the clues to find the unique solution"
            MainActivity.TYPE_SLITHERLINK -> "Slitherlink" to "Draw a single closed loop using number clues"
            MainActivity.TYPE_NURIKABE    -> "Nurikabe" to "Shade cells into one connected river around islands"
            MainActivity.TYPE_HIDATO      -> "Hidato" to "Fill consecutive numbers(1-50) in adjacent cells"
            MainActivity.TYPE_FUTOSHIKI   -> "Futoshiki" to "Like Sudoku but with < and > between cells"
            else                          -> "Skyscraper" to "Place 1–N so visibility clues match"
        }

        findViewById<TextView>(R.id.menu_title).text = typeName
        findViewById<TextView>(R.id.menu_subtitle).text = typeDesc
    }

    override fun onResume() {
        super.onResume()
        buildPuzzleCards()
    }

    private fun buildPuzzleCards() {
        val prefs = PrefsManager(this)
        val container = findViewById<LinearLayout>(R.id.difficulty_container)
        container.removeAllViews()

        val density = resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()

        for ((diffIndex, diffName) in diffNames.withIndex()) {
            val header = TextView(this).apply {
                text = diffName
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor(diffColors[diffIndex]))
                setPadding(0, dp(16), 0, dp(8))
            }
            container.addView(header)

            // Variable count per difficulty (e.g. Hidato has 5 easy / 15 hard).
            // Display as rows of 5 cards each; last row may be short.
            val puzzleCount = PrefsManager.getPuzzleCount(puzzleType, diffIndex)
            val numRows = (puzzleCount + 4) / 5  // ceil
            for (rowIdx in 0 until numRows) {
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = if (rowIdx > 0) dp(8) else 0 }
                }

                for (colIdx in 0 until 5) {
                    val puzzleIndex = rowIdx * 5 + colIdx
                    if (puzzleIndex >= puzzleCount) {
                        // Pad with invisible spacer so cards in earlier rows stay 1/5 wide
                        row.addView(android.view.View(this).apply {
                            layoutParams = LinearLayout.LayoutParams(0, dp(48), 1f).apply {
                                marginStart = if (colIdx == 0) 0 else dp(6)
                            }
                        })
                        continue
                    }
                    val isCompleted = prefs.isPuzzleCompleted(puzzleType, diffIndex, puzzleIndex)
                    val prevDone = puzzleIndex == 0 ||
                            prefs.isPuzzleCompleted(puzzleType, diffIndex, puzzleIndex - 1)
                    val firstOfDiff = puzzleIndex == 0
                    val prevDiffDone = diffIndex == 0 ||
                            prefs.getCompletedCount(puzzleType, diffIndex - 1) >=
                            PrefsManager.getUnlockThreshold(puzzleType, diffIndex - 1)
                    val isUnlocked = (firstOfDiff && prevDiffDone) || (!firstOfDiff && prevDone)

                    val card = CardView(this).apply {
                        radius = 12f * density
                        val lp = LinearLayout.LayoutParams(0, dp(48)).apply {
                            weight = 1f
                            marginStart = if (colIdx == 0) 0 else dp(6)
                        }
                        layoutParams = lp
                        setCardBackgroundColor(when {
                            isCompleted -> Color.parseColor(diffColors[diffIndex])
                            isUnlocked  -> Color.parseColor("#16213E")
                            else        -> Color.parseColor("#0A0A1A")
                        })
                        cardElevation = if (isUnlocked) 4f * density else 0f
                    }

                    val tv = TextView(this).apply {
                        text = if (isCompleted) "★" else "${puzzleIndex + 1}"
                        textSize = 16f
                        gravity = Gravity.CENTER
                        setTypeface(null, Typeface.BOLD)
                        setTextColor(when {
                            isCompleted -> Color.WHITE
                            isUnlocked  -> Color.WHITE
                            else        -> Color.parseColor("#444466")
                        })
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        ).apply { gravity = Gravity.CENTER }
                    }
                    card.addView(tv)

                    if (isUnlocked) {
                        card.setOnClickListener {
                            val cls = when (puzzleType) {
                                MainActivity.TYPE_NONOGRAM    -> NonogramGameActivity::class.java
                                MainActivity.TYPE_MASTERMIND  -> MastermindGameActivity::class.java
                                MainActivity.TYPE_LIGHTS_OUT  -> LightsOutGameActivity::class.java
                                MainActivity.TYPE_KAKURO      -> KakuroGameActivity::class.java
                                MainActivity.TYPE_LOGIC_GRID  -> LogicGridGameActivity::class.java
                                MainActivity.TYPE_SLITHERLINK -> SlitherlinkGameActivity::class.java
                                MainActivity.TYPE_NURIKABE    -> NurikabeGameActivity::class.java
                                MainActivity.TYPE_HIDATO      -> HidatoGameActivity::class.java
                                MainActivity.TYPE_FUTOSHIKI   -> FutoshikiGameActivity::class.java
                                else                          -> SkyscraperGameActivity::class.java
                            }
                            startActivity(Intent(this@PuzzleMenuActivity, cls).apply {
                                putExtra(MainActivity.EXTRA_PUZZLE_TYPE, puzzleType)
                                putExtra(MainActivity.EXTRA_DIFFICULTY, diffIndex)
                                putExtra(MainActivity.EXTRA_PUZZLE_INDEX, puzzleIndex)
                            })
                        }
                    }

                    row.addView(card)
                }
                container.addView(row)
            }
        }
    }
}
