package com.logicpuzzles

import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.PuzzleVerifier

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

    private data class PuzzleInfo(val type: Int, val name: String, val desc: String, val color: String)

    private val puzzleTypes = listOf(
        PuzzleInfo(TYPE_NONOGRAM,     "Nonogram",     "Fill the grid using number clues",       "#4ECDC4"),
        PuzzleInfo(TYPE_MASTERMIND,   "Mastermind",   "Crack the secret color code",            "#E94560"),
        PuzzleInfo(TYPE_LIGHTS_OUT,   "Lights Out",   "Toggle lights to turn them all off",     "#FFD93D"),
        PuzzleInfo(TYPE_KAKURO,       "Kakuro",       "Fill digits so each run hits its sum",   "#6BCB77"),
        PuzzleInfo(TYPE_LOGIC_GRID,   "Logic Grid",   "Match clues to find who owns what",      "#A855F7"),
        PuzzleInfo(TYPE_SLITHERLINK,  "Slitherlink",  "Draw a single closed loop using clues",  "#F59E0B"),
        PuzzleInfo(TYPE_NURIKABE,     "Nurikabe",     "Shade cells to form islands & a river",  "#06B6D4"),
        PuzzleInfo(TYPE_HIDATO,       "Hidato",       "Fill consecutive numbers along a path",  "#EC4899"),
        PuzzleInfo(TYPE_FUTOSHIKI,    "Futoshiki",    "Sudoku-style with < and > inequalities", "#10B981"),
        PuzzleInfo(TYPE_SKYSCRAPER,   "Skyscraper",   "Place buildings using visibility clues", "#3B82F6")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_support).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://buymeacoffee.com/vargfor")))
        }

        findViewById<Button>(R.id.btn_reset_progress).apply {
            setOnClickListener { confirmReset() }
            // Long-press = run puzzle uniqueness verifier (results to logcat under "PuzzleVerifier")
            setOnLongClickListener {
                Thread { PuzzleVerifier.verifyAll() }.start()
                android.widget.Toast.makeText(
                    this@MainActivity,
                    "Verifying puzzles… check logcat (tag: PuzzleVerifier)",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                true
            }
        }
    }

    private fun confirmReset() {
        AlertDialog.Builder(this)
            .setTitle("Reset all progress?")
            .setMessage("This will clear every completed puzzle across all puzzle types. This cannot be undone.")
            .setPositiveButton("Reset") { _, _ ->
                PrefsManager(this).clearAll()
                buildCards()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        buildCards()
    }

    private fun buildCards() {
        val prefs = PrefsManager(this)
        val container = findViewById<LinearLayout>(R.id.puzzle_container)
        container.removeAllViews()

        val density = resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()

        for (info in puzzleTypes) {
            val card = CardView(this).apply {
                radius = 12f * density
                cardElevation = 4f * density
                setCardBackgroundColor(Color.parseColor("#16213E"))
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
                setBackgroundColor(Color.parseColor(info.color))
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
            val titleText = if (total > 0) "${info.name}  ★$total/${PrefsManager.PUZZLES_PER_TYPE}" else info.name

            val title = TextView(this).apply {
                text = titleText
                setTextColor(Color.WHITE)
                textSize = 20f
                setTypeface(null, Typeface.BOLD)
            }
            val desc = TextView(this).apply {
                text = info.desc
                setTextColor(Color.parseColor("#A0A0C0"))
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
                setTextColor(Color.parseColor(info.color))
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
