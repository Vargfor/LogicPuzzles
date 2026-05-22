package com.logicpuzzles.mastermind

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
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
import com.logicpuzzles.MainActivity
import com.cyberhub.logicgames.R
import com.logicpuzzles.utils.CompletionDialogs
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.numberText
import com.logicpuzzles.utils.puzzleHeader

class MastermindGameActivity : AppCompatActivity() {

    companion object {
        private const val UNSET = -1
    }

    private val colorValues = listOf(
        0xFF0072B2.toInt(), // Blue
        0xFFE69F00.toInt(), // Orange
        0xFF009E73.toInt(), // Teal
        0xFFF0E442.toInt(), // Yellow
        0xFFCC79A7.toInt(), // Purple
        0xFF6E6E6E.toInt(), // Gray
        0xFFD55E00.toInt(), // Vermilion
        0xFF56B4E9.toInt()  // Sky blue
    )
    private val colorNames = listOf("Blue", "Orange", "Teal", "Yellow", "Purple", "Gray", "Vermilion", "Sky Blue")

    private var difficulty = 0
    private var puzzleIndex = 0
    private var positions = 4
    private var numColors = 4
    private var maxGuesses = 10
    private var allowDuplicates = false

    private lateinit var secret: List<Int>
    private var currentGuess = mutableListOf<Int>()
    private var selectedSlot = 0
    private var guessesUsed = 0
    private var gameOver = false

    private lateinit var rowsContainer: LinearLayout
    private lateinit var pickerContainer: LinearLayout
    private lateinit var guessesLeftText: TextView
    private lateinit var scrollView: ScrollView
    private val slotViews = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)
        configureLevel()
        currentGuess = MutableList(positions) { UNSET }

        buildUi()
    }

    private fun configureLevel() {
        val catalogIndex = PrefsManager(this).getCatalogIndex(MainActivity.TYPE_MASTERMIND, difficulty, puzzleIndex)
        val level = MastermindData.levelFor(difficulty, catalogIndex)
        positions = level.positions
        numColors = level.numColors
        maxGuesses = level.maxGuesses
        allowDuplicates = level.allowDuplicates
        secret = level.secret
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_MASTERMIND)
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

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(8))
        }
        header.addView(TextView(this).apply {
            text = puzzleHeader(R.string.puzzle_mastermind, difficulty, puzzleIndex)
            setTextColor(palette.textPrimary)
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        guessesLeftText = TextView(this).apply {
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
        }
        header.addView(guessesLeftText)
        main.addView(header)

        main.addView(TextView(this).apply {
            text = getString(R.string.instruction_mastermind)
            setTextColor(palette.textSecondary)
            textSize = 12f
            setPadding(dp(12), 0, dp(12), dp(8))
        })

        scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        rowsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }
        scrollView.addView(rowsContainer)
        main.addView(scrollView)

        // Color picker
        pickerContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setBackgroundColor(palette.surface)
        }
        main.addView(pickerContainer)

        // Submit button
        main.addView(Button(this).apply {
            text = getString(R.string.action_submit_guess)
            setBackgroundColor(accent)
            setTextColor(palette.buttonText)
            setTypeface(null, Typeface.BOLD)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(dp(8), dp(4), dp(8), dp(8)) }
            layoutParams = lp
            setOnClickListener { submitGuess() }
        })

        root.addView(main)

        updateGuessesLeft()
        buildColorPicker()
        buildCurrentGuessRow()
    }

    private fun updateGuessesLeft() {
        val palette = ThemeManager.currentPalette(this)
        val left = maxGuesses - guessesUsed
        guessesLeftText.text = getString(R.string.guesses_count, left)
        guessesLeftText.setTextColor(when {
            left <= 2 -> palette.danger
            left <= 4 -> palette.warning
            else -> palette.success
        })
    }

    private fun buildColorPicker() {
        pickerContainer.removeAllViews()
        val size = dp(40)
        val margin = dp(4)
        for (i in 0 until numColors) {
            val v = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginStart = margin; marginEnd = margin
                }
                contentDescription = colorNames[i]
                background = circleDrawable(colorValues[i], false)
                setOnClickListener { selectColor(i) }
            }
            pickerContainer.addView(v)
        }
    }

    private fun buildCurrentGuessRow() {
        val palette = ThemeManager.currentPalette(this)
        slotViews.clear()
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(8) }
            setBackgroundColor(palette.surfaceStrong)
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        row.addView(TextView(this).apply {
            text = numberText(guessesUsed + 1)
            textSize = 14f
            setTextColor(palette.textSecondary)
            layoutParams = LinearLayout.LayoutParams(dp(28), LinearLayout.LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER
        })

        val slotsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        for (i in 0 until positions) {
            val slot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(dp(36), dp(36)).apply {
                    marginStart = dp(3); marginEnd = dp(3)
                }
                background = emptySlotDrawable(i == selectedSlot)
                tag = i
                contentDescription = "Slot ${i + 1}: empty"
                setOnClickListener { selectSlot(i) }
            }
            slotViews.add(slot)
            slotsContainer.addView(slot)
        }
        row.addView(slotsContainer)

        // Feedback placeholder
        row.addView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dp(56), LinearLayout.LayoutParams.WRAP_CONTENT)
        })

        rowsContainer.addView(row)
        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun selectSlot(idx: Int) {
        if (gameOver) return
        selectedSlot = idx
        refreshSlots()
    }

    private fun selectColor(colorIdx: Int) {
        if (gameOver) return
        currentGuess[selectedSlot] = colorIdx
        if (selectedSlot < positions - 1) selectedSlot++
        refreshSlots()
    }

    private fun refreshSlots() {
        for (i in slotViews.indices) {
            val color = currentGuess[i]
            slotViews[i].background = if (color == UNSET) {
                slotViews[i].contentDescription = "Slot ${i + 1}: empty"
                emptySlotDrawable(i == selectedSlot)
            } else {
                slotViews[i].contentDescription = "Slot ${i + 1}: ${colorNames[color]}"
                circleDrawable(colorValues[color], i == selectedSlot)
            }
        }
    }

    private fun submitGuess() {
        if (gameOver) return
        if (currentGuess.any { it == UNSET }) {
            Toast.makeText(this, "Fill all slots first", Toast.LENGTH_SHORT).show()
            return
        }
        val (blacks, whites) = checkGuess(secret, currentGuess)
        slotViews.forEach { it.setOnClickListener(null) }
        // Replace placeholder feedback
        val row = rowsContainer.getChildAt(rowsContainer.childCount - 1) as LinearLayout
        val feedback = row.getChildAt(2) as LinearLayout
        feedback.removeAllViews()
        feedback.addView(TextView(this).apply {
            text = getString(R.string.mastermind_feedback, blacks, whites)
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            setTextColor(ThemeManager.currentPalette(this@MastermindGameActivity).textPrimary)
        })

        guessesUsed++
        if (blacks == positions) {
            gameOver = true
            PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_MASTERMIND, difficulty, puzzleIndex)
            CompletionDialogs.showSolved(
                this,
                "Code Cracked!",
                "Solved in $guessesUsed ${if (guessesUsed == 1) "guess" else "guesses"}.",
                MainActivity.TYPE_MASTERMIND,
                difficulty,
                puzzleIndex,
                MastermindGameActivity::class.java
            )
            return
        }
        if (guessesUsed >= maxGuesses) {
            gameOver = true
            val secretStr = secret.joinToString(" ") { colorNames[it] }
            AlertDialog.Builder(this)
                .setTitle("Out of Guesses")
                .setMessage("The code was: $secretStr")
                .setPositiveButton("Back to Menu") { _, _ -> finish() }
                .setCancelable(false)
                .show()
            return
        }
        currentGuess = MutableList(positions) { UNSET }
        selectedSlot = 0
        updateGuessesLeft()
        buildCurrentGuessRow()
    }

    private fun checkGuess(secret: List<Int>, guess: List<Int>): Pair<Int, Int> {
        var blacks = 0
        val secretLeft = IntArray(colorValues.size)
        val guessLeft = IntArray(colorValues.size)
        for (i in secret.indices) {
            if (secret[i] == guess[i]) blacks++
            else {
                secretLeft[secret[i]]++
                guessLeft[guess[i]]++
            }
        }
        var whites = 0
        for (c in 0 until colorValues.size) whites += minOf(secretLeft[c], guessLeft[c])
        return blacks to whites
    }

    private fun circleDrawable(color: Int, selected: Boolean): GradientDrawable {
        val palette = ThemeManager.currentPalette(this)
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            if (selected) setStroke(4, palette.textPrimary)
            else setStroke(2, palette.gridLine)
        }
    }

    private fun emptySlotDrawable(selected: Boolean): GradientDrawable {
        val palette = ThemeManager.currentPalette(this)
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(palette.surface)
            if (selected) setStroke(3, palette.textPrimary)
            else setStroke(1, palette.gridLine)
        }
    }
}
