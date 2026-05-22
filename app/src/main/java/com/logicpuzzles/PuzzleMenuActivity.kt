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
import com.cyberhub.logicgames.R
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
import com.logicpuzzles.utils.ThemeManager

class PuzzleMenuActivity : AppCompatActivity() {

    private val diffNameResIds = listOf(
        R.string.difficulty_easy,
        R.string.difficulty_medium,
        R.string.difficulty_hard,
        R.string.difficulty_expert
    )
    private var puzzleType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_menu)

        puzzleType = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_TYPE, 0)

        val (typeName, typeDesc) = when (puzzleType) {
            MainActivity.TYPE_NONOGRAM -> R.string.puzzle_nonogram to R.string.menu_desc_nonogram
            MainActivity.TYPE_MASTERMIND -> R.string.puzzle_mastermind to R.string.menu_desc_mastermind
            MainActivity.TYPE_LIGHTS_OUT -> R.string.puzzle_lights_out to R.string.menu_desc_lights_out
            MainActivity.TYPE_KAKURO -> R.string.puzzle_kakuro to R.string.menu_desc_kakuro
            MainActivity.TYPE_LOGIC_GRID -> R.string.puzzle_logic_grid to R.string.menu_desc_logic_grid
            MainActivity.TYPE_SLITHERLINK -> R.string.puzzle_slitherlink to R.string.menu_desc_slitherlink
            MainActivity.TYPE_NURIKABE -> R.string.puzzle_nurikabe to R.string.menu_desc_nurikabe
            MainActivity.TYPE_HIDATO -> R.string.puzzle_hidato to R.string.menu_desc_hidato
            MainActivity.TYPE_FUTOSHIKI -> R.string.puzzle_futoshiki to R.string.menu_desc_futoshiki
            else -> R.string.puzzle_skyscraper to R.string.menu_desc_skyscraper
        }

        findViewById<TextView>(R.id.menu_title).setText(typeName)
        findViewById<TextView>(R.id.menu_subtitle).setText(typeDesc)
    }

    override fun onResume() {
        super.onResume()
        buildPuzzleCards()
    }

    private fun buildPuzzleCards() {
        val palette = ThemeManager.currentPalette(this)
        val prefs = PrefsManager(this)
        val container = findViewById<LinearLayout>(R.id.difficulty_container)
        container.removeAllViews()
        findViewById<android.view.View>(R.id.puzzle_menu_root).setBackgroundColor(palette.background)
        findViewById<TextView>(R.id.menu_title).setTextColor(palette.textPrimary)
        findViewById<TextView>(R.id.menu_subtitle).setTextColor(palette.textSecondary)

        val density = resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()

        for ((diffIndex, diffNameResId) in diffNameResIds.withIndex()) {
            val header = TextView(this).apply {
                text = getString(diffNameResId)
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(ThemeManager.difficultyAccent(this@PuzzleMenuActivity, diffIndex))
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
                    val difficultyUnlocked = prefs.isDifficultyUnlocked(puzzleType, diffIndex)
                    val prevDone = puzzleIndex == 0 ||
                            prefs.isPuzzleCompleted(puzzleType, diffIndex, puzzleIndex - 1)
                    val isUnlocked = difficultyUnlocked && prevDone

                    val card = CardView(this).apply {
                        radius = 12f * density
                        val lp = LinearLayout.LayoutParams(0, dp(48)).apply {
                            weight = 1f
                            marginStart = if (colIdx == 0) 0 else dp(6)
                        }
                        layoutParams = lp
                        setCardBackgroundColor(when {
                            isCompleted -> ThemeManager.difficultyAccent(this@PuzzleMenuActivity, diffIndex)
                            isUnlocked  -> palette.surface
                            else        -> palette.locked
                        })
                        cardElevation = if (isUnlocked) 4f * density else 0f
                    }

                    val tv = TextView(this).apply {
                        text = if (isCompleted) {
                            getString(R.string.completed_star)
                        } else {
                            getString(R.string.number_value, puzzleIndex + 1)
                        }
                        textSize = 16f
                        gravity = Gravity.CENTER
                        setTypeface(null, Typeface.BOLD)
                        setTextColor(when {
                            isCompleted -> Color.WHITE
                            isUnlocked  -> palette.textPrimary
                            else        -> palette.lockedText
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
