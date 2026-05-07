package com.logicpuzzles

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.PuzzleVerifier
import com.logicpuzzles.utils.ThemeManager

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PUZZLE_TYPE = "puzzle_type"
        const val EXTRA_DIFFICULTY = "difficulty"
        const val EXTRA_PUZZLE_INDEX = "puzzle_index"

        const val TYPE_NONOGRAM = 0
        const val TYPE_MASTERMIND = 1
        const val TYPE_LIGHTS_OUT = 2
        const val TYPE_KAKURO = 3
        const val TYPE_LOGIC_GRID = 4
        const val TYPE_SLITHERLINK = 5
        const val TYPE_NURIKABE = 6
        const val TYPE_HIDATO = 7
        const val TYPE_FUTOSHIKI = 8
        const val TYPE_SKYSCRAPER = 9
    }

    private data class PuzzleInfo(
        val type: Int,
        @StringRes val nameResId: Int,
        @StringRes val descResId: Int
    )

    private val puzzleTypes = listOf(
        PuzzleInfo(TYPE_NONOGRAM, R.string.puzzle_nonogram, R.string.desc_nonogram),
        PuzzleInfo(TYPE_MASTERMIND, R.string.puzzle_mastermind, R.string.desc_mastermind),
        PuzzleInfo(TYPE_LIGHTS_OUT, R.string.puzzle_lights_out, R.string.desc_lights_out),
        PuzzleInfo(TYPE_KAKURO, R.string.puzzle_kakuro, R.string.desc_kakuro),
        PuzzleInfo(TYPE_LOGIC_GRID, R.string.puzzle_logic_grid, R.string.desc_logic_grid),
        PuzzleInfo(TYPE_SLITHERLINK, R.string.puzzle_slitherlink, R.string.desc_slitherlink),
        PuzzleInfo(TYPE_NURIKABE, R.string.puzzle_nurikabe, R.string.desc_nurikabe),
        PuzzleInfo(TYPE_HIDATO, R.string.puzzle_hidato, R.string.desc_hidato),
        PuzzleInfo(TYPE_FUTOSHIKI, R.string.puzzle_futoshiki, R.string.desc_futoshiki),
        PuzzleInfo(TYPE_SKYSCRAPER, R.string.puzzle_skyscraper, R.string.desc_skyscraper)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_support).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, "https://buymeacoffee.com/vargfor".toUri()))
        }

        findViewById<Button>(R.id.btn_settings).apply {
            setOnClickListener { showSettings() }
            // Long-press = run puzzle uniqueness verifier (results to logcat under "PuzzleVerifier")
            setOnLongClickListener {
                Thread { PuzzleVerifier.verifyAll() }.start()
                android.widget.Toast.makeText(
                    this@MainActivity,
                    getString(R.string.verifying_puzzles),
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                true
            }
        }
    }

    private fun showSettings() {
        val palette = ThemeManager.currentPalette(this)
        val density = resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(palette.background)
            setPadding(dp(18), dp(8), dp(18), dp(4))
        }
        content.addView(TextView(this).apply {
            text = getString(R.string.color_theme)
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            setTextColor(palette.textPrimary)
            setPadding(0, 0, 0, dp(8))
        })

        var dialog: AlertDialog? = null
        for (option in ThemeManager.palettes) {
            val selected = option.id == ThemeManager.selectedThemeId(this)
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(12), dp(10), dp(12), dp(10))
                background = roundedDrawable(
                    if (selected) palette.surfaceStrong else palette.surface,
                    palette.gridLine,
                    dp(8).toFloat()
                )
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(8) }
                setOnClickListener {
                    ThemeManager.setTheme(this@MainActivity, option.id)
                    dialog?.dismiss()
                    buildCards()
                }
            }
            row.addView(View(this).apply {
                background = roundedDrawable(option.accent, option.gridLine, dp(12).toFloat())
                layoutParams = LinearLayout.LayoutParams(dp(24), dp(24)).apply { marginEnd = dp(10) }
            })
            row.addView(TextView(this).apply {
                text = if (selected) getString(R.string.theme_selected, option.name) else option.name
                textSize = 14f
                setTextColor(palette.textPrimary)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            content.addView(row)
        }

        content.addView(Button(this).apply {
            text = getString(R.string.reset_progress_shuffle)
            textSize = 13f
            backgroundTintList = ColorStateList.valueOf(palette.danger)
            setTextColor(palette.buttonText)
            setOnClickListener {
                dialog?.dismiss()
                confirmReset()
            }
        })

        dialog = AlertDialog.Builder(this)
            .setTitle(R.string.settings)
            .setView(content)
            .setNegativeButton(R.string.close, null)
            .create()
        dialog.show()
    }

    private fun roundedDrawable(fill: Int, stroke: Int, radius: Float): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radius
            setColor(fill)
            setStroke(1, stroke)
        }

    private fun confirmReset() {
        AlertDialog.Builder(this)
            .setTitle(R.string.reset_all_progress_title)
            .setMessage(R.string.reset_all_progress_message)
            .setPositiveButton(R.string.reset) { _, _ ->
                PrefsManager(this).resetProgressAndShuffleLevels()
                buildCards()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        buildCards()
    }

    private fun buildCards() {
        val palette = ThemeManager.currentPalette(this)
        val prefs = PrefsManager(this)
        val container = findViewById<LinearLayout>(R.id.puzzle_container)
        container.removeAllViews()
        findViewById<View>(R.id.main_root).setBackgroundColor(palette.background)
        findViewById<TextView>(R.id.main_title).setTextColor(palette.textPrimary)
        findViewById<TextView>(R.id.main_subtitle).setTextColor(palette.textSecondary)
        findViewById<Button>(R.id.btn_support).apply {
            text = getString(R.string.support)
            backgroundTintList = ColorStateList.valueOf(palette.warning)
            setTextColor(palette.cellText)
        }
        findViewById<Button>(R.id.btn_settings).apply {
            backgroundTintList = ColorStateList.valueOf(palette.button)
            setTextColor(palette.buttonText)
        }

        val density = resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()

        for (info in puzzleTypes) {
            val puzzleName = getString(info.nameResId)
            val card = CardView(this).apply {
                radius = 12f * density
                cardElevation = 4f * density
                setCardBackgroundColor(palette.surface)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, dp(12))
                }
                layoutParams = lp
            }

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(20), dp(20), dp(20), dp(20))
            }

            val accent = View(this).apply {
                setBackgroundColor(ThemeManager.puzzleAccent(this@MainActivity, info.type))
                layoutParams = LinearLayout.LayoutParams(dp(4), dp(40)).apply {
                    marginEnd = dp(12)
                }
            }
            row.addView(accent)

            val textCol = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val total = prefs.getTotalCompleted(info.type)
            val titleText = if (total > 0) {
                getString(R.string.puzzle_completion_title, puzzleName, total, PrefsManager.PUZZLES_PER_TYPE)
            } else {
                puzzleName
            }

            val title = TextView(this).apply {
                text = titleText
                setTextColor(palette.textPrimary)
                textSize = 20f
                setTypeface(null, Typeface.BOLD)
            }
            val desc = TextView(this).apply {
                text = getString(info.descResId)
                setTextColor(palette.textSecondary)
                textSize = 13f
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(4) }
                layoutParams = lp
            }
            textCol.addView(title)
            textCol.addView(desc)
            row.addView(textCol)

            val arrow = TextView(this).apply {
                text = "▶"
                setTextColor(ThemeManager.puzzleAccent(this@MainActivity, info.type))
                textSize = 18f
            }
            row.addView(arrow)

            card.addView(row)

            card.setOnClickListener {
                val intent = Intent(this, PuzzleMenuActivity::class.java)
                intent.putExtra(EXTRA_PUZZLE_TYPE, info.type)
                startActivity(intent)
            }

            container.addView(card)
        }
    }
}
