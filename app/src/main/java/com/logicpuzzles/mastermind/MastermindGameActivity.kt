package com.logicpuzzles.mastermind

import android.graphics.Color
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
import com.logicpuzzles.R
import com.logicpuzzles.utils.PrefsManager
import kotlin.random.Random

class MastermindGameActivity : AppCompatActivity() {

    companion object {
        private const val UNSET = -1
    }

    private val colorValues = listOf(
        0xFFE94560.toInt(), // Red
        0xFF4ECDC4.toInt(), // Teal
        0xFFFFD93D.toInt(), // Yellow
        0xFF6BCB77.toInt(), // Green
        0xFFA855F7.toInt(), // Purple
        0xFFFF6B35.toInt(), // Orange
        0xFF00B4D8.toInt(), // Blue
        0xFFFF8FAB.toInt()  // Pink
    )
    private val colorNames = listOf("Red", "Teal", "Yellow", "Green", "Purple", "Orange", "Blue", "Pink")

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
        secret = generateSecret()
        currentGuess = MutableList(positions) { UNSET }

        buildUi()
    }

    private fun configureLevel() {
        when (difficulty) {
            0 -> { positions = 4; numColors = 4; maxGuesses = 10; allowDuplicates = false }
            1 -> { positions = 4; numColors = 6; maxGuesses = 9;  allowDuplicates = true  }
            2 -> { positions = 5; numColors = 6; maxGuesses = 9;  allowDuplicates = true  }
            else -> { positions = 5; numColors = 8; maxGuesses = 8; allowDuplicates = true }
        }
    }

    private fun generateSecret(): List<Int> {
        val seed = (difficulty * 1000L + puzzleIndex) * 31337L + 9001L
        val rng = Random(seed)
        return if (allowDuplicates) {
            List(positions) { rng.nextInt(numColors) }
        } else {
            (0 until numColors).toMutableList().apply { shuffle(rng) }.take(positions)
        }
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

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(8))
        }
        header.addView(TextView(this).apply {
            text = "Mastermind • ${diffName(difficulty)} #${puzzleIndex + 1}"
            setTextColor(Color.WHITE)
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
            text = "Tap a slot then a color. ● = right color & spot, ○ = right color, wrong spot."
            setTextColor(Color.parseColor("#A0A0C0"))
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
            setBackgroundColor(Color.parseColor("#16213E"))
        }
        main.addView(pickerContainer)

        // Submit button
        main.addView(Button(this).apply {
            text = "Submit Guess"
            setBackgroundColor(Color.parseColor("#E94560"))
            setTextColor(Color.WHITE)
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
        val left = maxGuesses - guessesUsed
        guessesLeftText.text = "Guesses: $left"
        guessesLeftText.setTextColor(when {
            left <= 2 -> Color.parseColor("#F44336")
            left <= 4 -> Color.parseColor("#FF9800")
            else -> Color.parseColor("#4CAF50")
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
                background = circleDrawable(colorValues[i], false)
                setOnClickListener { selectColor(i) }
            }
            pickerContainer.addView(v)
        }
    }

    private fun buildCurrentGuessRow() {
        slotViews.clear()
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(8) }
            setBackgroundColor(Color.parseColor("#1A2040"))
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        row.addView(TextView(this).apply {
            text = "${guessesUsed + 1}"
            textSize = 14f
            setTextColor(Color.parseColor("#AAAACC"))
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
                emptySlotDrawable(i == selectedSlot)
            } else {
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
            text = "${blacks}● ${whites}○"
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
        })

        guessesUsed++
        if (blacks == positions) {
            gameOver = true
            PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_MASTERMIND, difficulty, puzzleIndex)
            AlertDialog.Builder(this)
                .setTitle("Code Cracked!")
                .setMessage("Solved in $guessesUsed ${if (guessesUsed == 1) "guess" else "guesses"}.")
                .setPositiveButton("Back to Menu") { _, _ -> finish() }
                .setCancelable(false)
                .show()
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
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            if (selected) setStroke(4, Color.WHITE)
            else setStroke(2, Color.parseColor("#44FFFFFF"))
        }
    }

    private fun emptySlotDrawable(selected: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#2A2A4A"))
            if (selected) setStroke(3, Color.WHITE)
            else setStroke(1, Color.parseColor("#555577"))
        }
    }

    private fun diffName(d: Int) = when (d) {
        0 -> "Easy"; 1 -> "Medium"; 2 -> "Hard"; else -> "Expert"
    }
}
