package com.logicpuzzles.mastermind

import android.graphics.Typeface
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isNotEmpty
import com.logicpuzzles.MainActivity
import com.cyberhub.logicgames.R
import com.logicpuzzles.utils.applySystemBarInsets
import com.logicpuzzles.utils.CompletionDialogs
import com.logicpuzzles.utils.gameInstructionRow
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.loadGamePuzzle
import com.logicpuzzles.utils.numberText
import com.logicpuzzles.utils.puzzleHeader
import com.logicpuzzles.utils.resetSymbolButton

class MastermindGameActivity : AppCompatActivity() {

    companion object {
        private const val UNSET = -1
    }

    private val colorValues = listOf(
        0xFF1565C0.toInt(), // Blue
        0xFFF57C00.toInt(), // Orange
        0xFF00897B.toInt(), // Green
        0xFFFDD835.toInt(), // Yellow
        0xFFC2185B.toInt(), // Magenta
        0xFF00ACC1.toInt(), // Cyan
        0xFFD32F2F.toInt(), // Red
        0xFF7E57C2.toInt(), // Violet
        0xFFF5F5F5.toInt(), // White
        0xFF212121.toInt()  // Charcoal
    )
    private val colorNames = listOf(
        "Blue",
        "Orange",
        "Green",
        "Yellow",
        "Magenta",
        "Cyan",
        "Red",
        "Violet",
        "White",
        "Charcoal"
    )
    private data class SubmittedGuess(val colors: List<Int>, val exact: Int, val misplaced: Int)

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
    private var themeSignature = 0

    private lateinit var rowsContainer: LinearLayout
    private lateinit var pickerContainer: GridLayout
    private lateinit var guessesLeftText: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var layoutMetrics: MastermindLayoutMetrics
    private val slotViews = mutableListOf<TextView>()
    private val submittedGuesses = mutableListOf<SubmittedGuess>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        findViewById<View>(R.id.game_root).applySystemBarInsets()

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)
        val catalogIndex = PrefsManager(this).getCatalogIndex(MainActivity.TYPE_MASTERMIND, difficulty, puzzleIndex)
        loadGamePuzzle(MainActivity.TYPE_MASTERMIND, "Mastermind d=$difficulty i=$puzzleIndex", {
            MastermindData.levelFor(difficulty, catalogIndex)
        }) { level ->
            configureLevel(level)
            currentGuess = MutableList(positions) { UNSET }
            buildUi()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::secret.isInitialized && themeSignature != 0 && ThemeManager.paletteSignature(this) != themeSignature) {
            buildUi()
        }
    }

    private fun configureLevel(level: MastermindLevel) {
        positions = level.positions
        numColors = level.numColors
        maxGuesses = level.maxGuesses
        allowDuplicates = level.allowDuplicates
        secret = level.secret
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val palette = ThemeManager.currentPalette(this)
        themeSignature = ThemeManager.paletteSignature(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_MASTERMIND)
        val root = findViewById<FrameLayout>(R.id.game_root)
        root.removeAllViews()
        root.setBackgroundColor(palette.background)
        layoutMetrics = MastermindLayoutMetrics.calculate(
            availableWidthPx = resources.displayMetrics.widthPixels - dp(16),
            density = resources.displayMetrics.density,
            positions = positions,
            colors = numColors
        )

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
        header.addView(resetSymbolButton { resetPuzzle() })
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

        main.addView(gameInstructionRow(
            MainActivity.TYPE_MASTERMIND,
            getString(R.string.instruction_mastermind)
        ))
        main.addView(TextView(this).apply {
            text = getString(
                R.string.mastermind_level_details,
                positions,
                numColors,
                getString(
                    if (allowDuplicates) R.string.mastermind_duplicates_allowed
                    else R.string.mastermind_no_duplicates
                )
            )
            setTextColor(palette.textPrimary)
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(dp(12), 0, dp(12), dp(8))
        })

        scrollView = ScrollView(this).apply {
            isFillViewport = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        rowsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }
        scrollView.addView(rowsContainer)
        main.addView(scrollView)

        val controls = LinearLayout(this).apply {
            orientation = if (layoutMetrics.stackControls) {
                LinearLayout.VERTICAL
            } else {
                LinearLayout.HORIZONTAL
            }
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setBackgroundColor(palette.surface)
        }
        val pickerHost = FrameLayout(this).apply {
            layoutParams = if (layoutMetrics.stackControls) {
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            } else {
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
        }
        pickerContainer = GridLayout(this).apply {
            columnCount = layoutMetrics.pickerColumns
            rowCount = (numColors + columnCount - 1) / columnCount
            alignmentMode = GridLayout.ALIGN_BOUNDS
            useDefaultMargins = false
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        }
        pickerHost.addView(pickerContainer)
        controls.addView(pickerHost)
        controls.addView(Button(this).apply {
            text = getString(R.string.action_submit_guess)
            setBackgroundColor(accent)
            setTextColor(palette.accentText)
            setTypeface(null, Typeface.BOLD)
            layoutParams = if (layoutMetrics.stackControls) {
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply {
                    topMargin = dp(8)
                }
            } else {
                LinearLayout.LayoutParams(dp(176), dp(56)).apply { marginStart = dp(8) }
            }
            setOnClickListener { submitGuess() }
        })
        main.addView(controls)

        root.addView(main)

        updateGuessesLeft()
        buildColorPicker()
        slotViews.clear()
        submittedGuesses.forEachIndexed { index, guess ->
            buildSubmittedGuessRow(index, guess)
        }
        if (!gameOver) buildCurrentGuessRow()
    }

    private fun resetPuzzle() {
        gameOver = false
        guessesUsed = 0
        selectedSlot = 0
        currentGuess = MutableList(positions) { UNSET }
        submittedGuesses.clear()
        rowsContainer.removeAllViews()
        updateGuessesLeft()
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
        val margin = dp(4)
        val columns = layoutMetrics.pickerColumns
        val size = layoutMetrics.swatchSizePx
        for (i in 0 until numColors) {
            val v = TextView(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(i / columns)
                    columnSpec = GridLayout.spec(i % columns)
                    width = size
                    height = size
                    setMargins(margin, margin, margin, margin)
                }
                text = numberText(i + 1)
                textSize = 13f
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                setTextColor(contrastingTextColor(colorValues[i]))
                contentDescription = "Color ${i + 1}: ${colorNames[i]}"
                background = circleDrawable(colorValues[i], false)
                setOnClickListener { selectColor(i) }
            }
            pickerContainer.addView(v)
        }
    }

    private fun buildCurrentGuessRow() {
        val palette = ThemeManager.currentPalette(this)
        slotViews.clear()
        val pegSize = layoutMetrics.pegSizePx
        val row = guessRow(palette.surfaceStrong)
        val pegLine = guessLine(guessesUsed, palette.textSecondary)

        val slotsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        for (i in 0 until positions) {
            val slot = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(pegSize, pegSize).apply {
                    marginStart = layoutMetrics.pegMarginPx
                    marginEnd = layoutMetrics.pegMarginPx
                }
                gravity = Gravity.CENTER
                textSize = 12f
                setTypeface(null, Typeface.BOLD)
                background = emptySlotDrawable(i == selectedSlot)
                tag = i
                contentDescription = "Slot ${i + 1}: empty"
                setOnClickListener { selectSlot(i) }
            }
            slotViews.add(slot)
            slotsContainer.addView(slot)
        }
        pegLine.addView(slotsContainer)
        attachFeedback(row, pegLine, buildFeedbackView(null))

        rowsContainer.addView(row)
        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun buildSubmittedGuessRow(index: Int, submitted: SubmittedGuess) {
        val palette = ThemeManager.currentPalette(this)
        val pegSize = layoutMetrics.pegSizePx
        val row = guessRow(palette.surfaceStrong)
        val pegLine = guessLine(index, palette.textSecondary)

        pegLine.addView(LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            submitted.colors.forEach { color ->
                addView(TextView(this@MastermindGameActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(pegSize, pegSize).apply {
                        marginStart = layoutMetrics.pegMarginPx
                        marginEnd = layoutMetrics.pegMarginPx
                    }
                    text = numberText(color + 1)
                    textSize = 12f
                    setTypeface(null, Typeface.BOLD)
                    gravity = Gravity.CENTER
                    setTextColor(contrastingTextColor(colorValues[color]))
                    background = circleDrawable(colorValues[color], false)
                    contentDescription = "Color ${color + 1}: ${colorNames[color]}"
                })
            }
        })
        attachFeedback(row, pegLine, buildFeedbackView(submitted))

        rowsContainer.addView(row)
        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun guessRow(backgroundColor: Int): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            layoutMetrics.boardWidthPx,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            bottomMargin = dp(8)
        }
        setBackgroundColor(backgroundColor)
        setPadding(dp(8), dp(8), dp(8), dp(8))
    }

    private fun guessLine(index: Int, indexColor: Int): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        addView(TextView(this@MastermindGameActivity).apply {
            text = numberText(index + 1)
            textSize = 14f
            setTextColor(indexColor)
            layoutParams = LinearLayout.LayoutParams(dp(28), LinearLayout.LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER
        })
    }

    private fun attachFeedback(row: LinearLayout, pegLine: LinearLayout, feedback: View) {
        if (layoutMetrics.compactRows) {
            row.addView(pegLine)
            feedback.layoutParams = LinearLayout.LayoutParams(
                layoutMetrics.feedbackWidthPx,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END
                topMargin = dp(4)
            }
            row.addView(feedback)
        } else {
            pegLine.addView(feedback)
            row.addView(pegLine)
        }
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
                slotViews[i].text = ""
                slotViews[i].contentDescription = "Slot ${i + 1}: empty"
                slotViews[i].setTextColor(ThemeManager.currentPalette(this).cellText)
                emptySlotDrawable(i == selectedSlot)
            } else {
                slotViews[i].text = numberText(color + 1)
                slotViews[i].setTextColor(contrastingTextColor(colorValues[color]))
                slotViews[i].contentDescription = "Slot ${i + 1}: color ${color + 1}, ${colorNames[color]}"
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
        val score = MastermindRules.score(secret, currentGuess)
        val submitted = SubmittedGuess(currentGuess.toList(), score.exact, score.misplaced)
        submittedGuesses.add(submitted)
        if (rowsContainer.isNotEmpty()) rowsContainer.removeViewAt(rowsContainer.childCount - 1)
        buildSubmittedGuessRow(guessesUsed, submitted)
        guessesUsed++
        updateGuessesLeft()
        if (score.exact == positions) {
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
        buildCurrentGuessRow()
    }

    private fun buildFeedbackView(submitted: SubmittedGuess?): View {
        val columns = when {
            positions <= 4 -> 2
            positions <= 6 -> 3
            else -> 4
        }
        return GridLayout(this).apply {
            columnCount = columns
            rowCount = (positions + columns - 1) / columns
            alignmentMode = GridLayout.ALIGN_BOUNDS
            setPadding(dp(8), 0, dp(8), 0)
            layoutParams = LinearLayout.LayoutParams(
                layoutMetrics.feedbackWidthPx,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            contentDescription = submitted?.let {
                "${it.exact} exact positions and ${it.misplaced} correct colors in other positions"
            } ?: "Feedback appears after submitting"

            for (index in 0 until positions) {
                val state = when {
                    submitted == null -> 0
                    index < submitted.exact -> 1
                    index < submitted.exact + submitted.misplaced -> 2
                    else -> 0
                }
                addView(View(this@MastermindGameActivity).apply {
                    background = feedbackDotDrawable(state)
                    layoutParams = GridLayout.LayoutParams().apply {
                        rowSpec = GridLayout.spec(index / columns)
                        columnSpec = GridLayout.spec(index % columns)
                        width = dp(14)
                        height = dp(14)
                        setMargins(dp(3), dp(3), dp(3), dp(3))
                    }
                })
            }
        }
    }

    private fun feedbackDotDrawable(state: Int): GradientDrawable {
        val palette = ThemeManager.currentPalette(this)
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            when (state) {
                1 -> {
                    setColor(palette.success)
                    setStroke(dp(1), palette.success)
                }
                2 -> {
                    setColor(Color.TRANSPARENT)
                    setStroke(dp(2), palette.warning)
                }
                else -> {
                    setColor(palette.cellEmpty)
                    setStroke(dp(1), palette.gridLine)
                }
            }
        }
    }

    private fun contrastingTextColor(background: Int): Int {
        val luminance = (Color.red(background) * 299 + Color.green(background) * 587 +
            Color.blue(background) * 114) / 1000
        return if (luminance >= 150) Color.BLACK else Color.WHITE
    }

    private fun circleDrawable(color: Int, selected: Boolean): GradientDrawable {
        val palette = ThemeManager.currentPalette(this)
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            if (selected) setStroke(4, palette.cellSelected)
            else setStroke(2, palette.gridLine)
        }
    }

    private fun emptySlotDrawable(selected: Boolean): GradientDrawable {
        val palette = ThemeManager.currentPalette(this)
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(palette.cellEmpty)
            if (selected) setStroke(3, palette.cellSelected)
            else setStroke(1, palette.gridLine)
        }
    }
}
