package com.logicpuzzles.skyscraper

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
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
import androidx.core.content.ContextCompat
import com.logicpuzzles.MainActivity
import com.cyberhub.logicgames.R
import com.logicpuzzles.utils.applySystemBarInsets
import com.logicpuzzles.utils.CompletionDialogs
import com.logicpuzzles.utils.GameHelpExtraSection
import com.logicpuzzles.utils.gameInstructionRow
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.ZoomableBoardHost
import com.logicpuzzles.utils.loadGamePuzzle
import com.logicpuzzles.utils.numberText
import com.logicpuzzles.utils.puzzleHeader
import com.logicpuzzles.utils.resetSymbolButton
import kotlin.math.roundToInt

class SkyscraperGameActivity : AppCompatActivity() {

    private companion object {
        const val EMPTY_LOT = -1
        const val ORIGINAL_BUILDING_MIN_HEIGHT = 100f
        const val ORIGINAL_BUILDING_FLOOR_HEIGHT = 42f
    }

    private var difficulty = 0
    private var puzzleIndex = 0
    private lateinit var puzzle: SkyscraperPuzzle
    private lateinit var values: Array<IntArray>
    private lateinit var fixed: Array<BooleanArray>
    private lateinit var cellViews: Array<Array<TextView>>
    private var selectedRow = -1
    private var selectedCol = -1
    private var solved = false
    private var showBuildingIcons = true
    private var boardCellSize = 48
    private var themeSignature = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        findViewById<View>(R.id.game_root).applySystemBarInsets()

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)
        val prefs = PrefsManager(this)
        showBuildingIcons = prefs.isSkyscraperBuildingsEnabled()
        val catalogIndex = prefs.getCatalogIndex(MainActivity.TYPE_SKYSCRAPER, difficulty, puzzleIndex)
        loadGamePuzzle(MainActivity.TYPE_SKYSCRAPER, "Skyscraper d=$difficulty i=$puzzleIndex", {
            SkyscraperPuzzles.get(difficulty, catalogIndex)
        }) { loaded ->
            puzzle = loaded
            val n = puzzle.size
            values = Array(n) { puzzle.initial[it].copyOf() }
            fixed = Array(n) { r -> BooleanArray(n) { c -> puzzle.initial[r][c] != 0 } }
            buildUi()
        }
    }

    override fun onResume() {
        super.onResume()
        val enabled = PrefsManager(this).isSkyscraperBuildingsEnabled()
        if (::puzzle.isInitialized && themeSignature != 0 && ThemeManager.paletteSignature(this) != themeSignature) {
            showBuildingIcons = enabled
            buildUi()
            return
        }
        if (enabled != showBuildingIcons) {
            showBuildingIcons = enabled
            if (::cellViews.isInitialized) repaintCells()
        }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val palette = ThemeManager.currentPalette(this)
        themeSignature = ThemeManager.paletteSignature(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_SKYSCRAPER)
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
        header.addView(resetSymbolButton { resetPuzzle() })
        header.addView(TextView(this).apply {
            text = puzzleHeader(R.string.puzzle_skyscraper, difficulty, puzzleIndex)
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

        main.addView(gameInstructionRow(
            MainActivity.TYPE_SKYSCRAPER,
            if (puzzle.emptyLotsPerLine > 0) {
                resources.getQuantityString(
                    R.plurals.instruction_skyscraper_with_empty_lots,
                    puzzle.emptyLotsPerLine,
                    puzzle.maxHeight,
                    puzzle.emptyLotsPerLine
                )
            } else {
                getString(R.string.instruction_skyscraper, puzzle.maxHeight)
            },
            extraHelpSection = GameHelpExtraSection(R.string.skyscraper_display_mode) {
                buildDisplayModeSelector()
            }
        ))

        main.addView(
            ZoomableBoardHost(this, MainActivity.TYPE_SKYSCRAPER) { zoom -> buildBoard(zoom) },
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        )

        main.addView(buildNumpad(palette.surface))

        root.addView(main)
    }

    private fun buildDisplayModeSelector(): View {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_SKYSCRAPER)
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(6), 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        lateinit var buildingsButton: Button
        lateinit var numbersButton: Button

        fun style(button: Button, selected: Boolean) {
            button.isSelected = selected
            button.setTextColor(if (selected) palette.accentText else palette.textSecondary)
            button.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(4).toFloat()
                setColor(if (selected) accent else palette.surfaceStrong)
                setStroke(dp(1).coerceAtLeast(1), if (selected) accent else palette.gridLine)
            }
        }

        fun refresh() {
            style(buildingsButton, showBuildingIcons)
            style(numbersButton, !showBuildingIcons)
        }

        fun modeButton(label: String, enabled: Boolean): Button = Button(this).apply {
            text = label
            setAllCaps(false)
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            minWidth = 0
            minimumWidth = 0
            minHeight = 0
            minimumHeight = 0
            setPadding(dp(6), 0, dp(6), 0)
            layoutParams = LinearLayout.LayoutParams(0, dp(38), 1f).apply {
                marginStart = dp(4)
            }
            setOnClickListener {
                setBuildingDisplay(enabled)
                refresh()
            }
        }

        buildingsButton = modeButton(getString(R.string.skyscraper_display_buildings_short), true).apply {
            (layoutParams as LinearLayout.LayoutParams).marginStart = 0
        }
        numbersButton = modeButton(getString(R.string.skyscraper_display_numbers), false)
        row.addView(buildingsButton)
        row.addView(numbersButton)
        refresh()
        return row
    }

    private fun setBuildingDisplay(enabled: Boolean) {
        if (showBuildingIcons == enabled) return
        showBuildingIcons = enabled
        PrefsManager(this).setSkyscraperBuildingsEnabled(enabled)
        if (::cellViews.isInitialized) repaintCells()
    }

    private fun numBtn(label: String, onClick: () -> Unit): View {
        val palette = ThemeManager.currentPalette(this)
        return Button(this).apply {
            text = label; textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(palette.buttonText)
            setBackgroundColor(palette.button)
            minWidth = 0
            minimumWidth = 0
            layoutParams = LinearLayout.LayoutParams(0, dp(48), 1f).apply {
                setMargins(dp(2), 0, dp(2), 0)
            }
            setOnClickListener { onClick() }
        }
    }

    private fun buildNumpad(backgroundColor: Int): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(backgroundColor)
        setPadding(dp(8), dp(8), dp(8), dp(8))
        val useSingleLandscapeRow = puzzle.maxHeight > 9 &&
            resources.displayMetrics.widthPixels > resources.displayMetrics.heightPixels
        if (useSingleLandscapeRow) {
            addView(LinearLayout(this@SkyscraperGameActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                for (value in 1..puzzle.maxHeight) {
                    addView(numBtn(value.toString()) { setValue(value) })
                }
                addView(numBtn("X") { setValue(EMPTY_LOT) })
            })
            return@apply
        }

        addView(LinearLayout(this@SkyscraperGameActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            for (value in 1..minOf(9, puzzle.maxHeight)) addView(numBtn(value.toString()) { setValue(value) })
            if (puzzle.maxHeight <= 9) {
                val clearOrEmpty = if (puzzle.emptyLotsPerLine > 0) EMPTY_LOT else 0
                addView(numBtn("X") { setValue(clearOrEmpty) })
            }
        })
        if (puzzle.maxHeight > 9) {
            addView(LinearLayout(this@SkyscraperGameActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(6) }
                for (value in 10..puzzle.maxHeight) addView(numBtn(value.toString()) { setValue(value) })
                addView(numBtn("X") { setValue(EMPTY_LOT) })
            })
        }
    }

    private fun buildBoard(zoom: Float): View {
        val n = puzzle.size
        val gridSize = n + 2
        val displayW = resources.displayMetrics.widthPixels
        val displayH = resources.displayMetrics.heightPixels
        val pad = dp(16)
        val overview = minOf(displayW - 2 * pad, (displayH * 0.56f).toInt()) / gridSize
        val cellSize = (overview * zoom).toInt().coerceIn(dp(18), dp(96))
        boardCellSize = cellSize

        val gl = GridLayout(this).apply {
            rowCount = gridSize
            columnCount = gridSize
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER }
            layoutParams = lp
        }

        cellViews = Array(n) { Array(n) { TextView(this) } }

        for (gr in 0 until gridSize) {
            for (gc in 0 until gridSize) {
                val view: View = when {
                    // Corners
                    (gr == 0 || gr == gridSize - 1) && (gc == 0 || gc == gridSize - 1) -> View(this)
                    // Top clue row
                    gr == 0 -> clueText(puzzle.cluesTop[gc - 1])
                    // Bottom clue row
                    gr == gridSize - 1 -> clueText(puzzle.cluesBottom[gc - 1])
                    // Left clue column
                    gc == 0 -> clueText(puzzle.cluesLeft[gr - 1])
                    // Right clue column
                    gc == gridSize - 1 -> clueText(puzzle.cluesRight[gr - 1])
                    // Cell
                    else -> {
                        val r = gr - 1; val c = gc - 1
                        val tv = TextView(this).apply {
                            gravity = Gravity.CENTER
                            textSize = (18f * zoom).coerceIn(10f, 28f)
                            setTypeface(null, Typeface.BOLD)
                            includeFontPadding = false
                            setForegroundGravity(Gravity.CENTER)
                            setOnClickListener { selectCell(r, c) }
                        }
                        cellViews[r][c] = tv
                        paintCell(r, c)
                        tv
                    }
                }
                view.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(gr)
                    columnSpec = GridLayout.spec(gc)
                    width = cellSize
                    height = cellSize
                    setMargins(1, 1, 1, 1)
                }
                gl.addView(view)
            }
        }
        return gl
    }

    private fun clueText(value: Int): TextView {
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_SKYSCRAPER)
        return TextView(this).apply {
            text = if (value > 0) numberText(value) else ""
            setTextColor(accent)
            textSize = 14f
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }
    }

    private fun selectCell(r: Int, c: Int) {
        if (solved || fixed[r][c]) return
        selectedRow = r; selectedCol = c
        for (rr in 0 until puzzle.size) for (cc in 0 until puzzle.size) paintCell(rr, cc)
    }

    private fun setValue(v: Int) {
        if (solved) return
        if (selectedRow < 0 || selectedCol < 0) return
        if (fixed[selectedRow][selectedCol]) return
        values[selectedRow][selectedCol] = if (
            v == EMPTY_LOT && values[selectedRow][selectedCol] == EMPTY_LOT
        ) {
            0
        } else {
            v
        }
        paintCell(selectedRow, selectedCol)
    }

    private fun paintCell(r: Int, c: Int) {
        val palette = ThemeManager.currentPalette(this)
        val tv = cellViews[r][c]
        val v = values[r][c]
        val isSelected = (r == selectedRow && c == selectedCol)
        val isFixed = fixed[r][c]
        tv.setBackgroundColor(when {
            isSelected -> palette.cellSelected
            isFixed -> palette.cellFixed
            else -> palette.cellEmpty
        })
        val foregroundColor = when {
            isSelected -> palette.cellSelectedText
            isFixed -> palette.cellFixedText
            else -> palette.cellText
        }
        tv.setTextColor(foregroundColor)
        if (showBuildingIcons && v in 1..16) {
            tv.text = ""
            tv.setCompoundDrawables(null, null, null, null)
            tv.foreground = buildingIcon(v, foregroundColor)
        } else {
            tv.foreground = null
            tv.setCompoundDrawables(null, null, null, null)
            tv.text = when {
                v == EMPTY_LOT -> "X"
                v == 0 -> ""
                else -> numberText(v)
            }
        }
    }

    private fun repaintCells() {
        for (r in 0 until puzzle.size) for (c in 0 until puzzle.size) paintCell(r, c)
    }

    private fun buildingIcon(value: Int, color: Int): Drawable? {
        val heightValue = value.coerceIn(1, 16)
        val source = ContextCompat.getDrawable(this, buildingDrawable(heightValue))?.mutate() ?: return null
        source.setTint(color)

        val maxIconSize = (boardCellSize - dp(4)).coerceAtLeast(dp(20))
        val sourceHeight = originalBuildingHeight(heightValue)
        val tallestHeight = originalBuildingHeight(puzzle.maxHeight.coerceIn(1, 16))
        val minDisplayHeight = maxIconSize * 0.48f
        val displayProgress = if (tallestHeight == ORIGINAL_BUILDING_MIN_HEIGHT) {
            1f
        } else {
            (sourceHeight - ORIGINAL_BUILDING_MIN_HEIGHT) /
                (tallestHeight - ORIGINAL_BUILDING_MIN_HEIGHT)
        }

        val displayHeight =
            (minDisplayHeight + (maxIconSize - minDisplayHeight) * displayProgress).roundToInt()

        return CellBuildingDrawable(
            source = source,
            intrinsicWidth = maxIconSize,
            intrinsicHeight = displayHeight.coerceAtLeast(1)
        )
    }

    private fun buildingDrawable(value: Int): Int = when (value.coerceIn(1, 16)) {
        1 -> R.drawable.skyscraper_1
        2 -> R.drawable.skyscraper_2
        3 -> R.drawable.skyscraper_3
        4 -> R.drawable.skyscraper_4
        5 -> R.drawable.skyscraper_5
        6 -> R.drawable.skyscraper_6
        7 -> R.drawable.skyscraper_7
        8 -> R.drawable.skyscraper_8
        9 -> R.drawable.skyscraper_9
        10 -> R.drawable.skyscraper_10
        11 -> R.drawable.skyscraper_11
        12 -> R.drawable.skyscraper_12
        13 -> R.drawable.skyscraper_13
        14 -> R.drawable.skyscraper_14
        15 -> R.drawable.skyscraper_15
        else -> R.drawable.skyscraper_16
    }

    private fun originalBuildingHeight(value: Int): Float =
        ORIGINAL_BUILDING_MIN_HEIGHT + ORIGINAL_BUILDING_FLOOR_HEIGHT * (value.coerceIn(1, 16) - 1)

    private class CellBuildingDrawable(
        private val source: Drawable,
        private val intrinsicWidth: Int,
        private val intrinsicHeight: Int
    ) : Drawable() {
        private val sourceBounds = Rect()

        override fun draw(canvas: Canvas) {
            val drawingBounds = bounds
            if (drawingBounds.isEmpty) return

            val drawWidth = intrinsicWidth.coerceAtMost(drawingBounds.width()).coerceAtLeast(1)
            val drawHeight = intrinsicHeight.coerceAtMost(drawingBounds.height()).coerceAtLeast(1)
            val left = drawingBounds.left + (drawingBounds.width() - drawWidth) / 2
            val top = drawingBounds.top + (drawingBounds.height() - drawHeight) / 2

            sourceBounds.set(left, top, left + drawWidth, top + drawHeight)
            source.setBounds(sourceBounds)
            source.draw(canvas)
        }

        override fun setAlpha(alpha: Int) {
            source.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            source.colorFilter = colorFilter
        }

        @Suppress("OVERRIDE_DEPRECATION")
        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

        override fun getIntrinsicWidth(): Int = intrinsicWidth

        override fun getIntrinsicHeight(): Int = intrinsicHeight
    }

    private fun visibility(line: IntArray): Int {
        var maxSeen = 0
        var count = 0
        for (h in line) {
            if (h > maxSeen) { count++; maxSeen = h }
        }
        return count
    }

    private fun resetPuzzle() {
        solved = false
        selectedRow = -1
        selectedCol = -1
        values = Array(puzzle.size) { puzzle.initial[it].copyOf() }
        for (r in 0 until puzzle.size) for (c in 0 until puzzle.size) paintCell(r, c)
    }

    private fun checkSolution() {
        val n = puzzle.size
        val maxHeight = puzzle.maxHeight
        for (r in 0 until n) for (c in 0 until n) {
            val value = values[r][c]
            if (value == 0) {
                Toast.makeText(this, "Fill every cell or mark it as an empty lot.", Toast.LENGTH_SHORT).show()
                return
            }
            if (value != EMPTY_LOT && value !in 1..maxHeight) {
                Toast.makeText(this, "Use numbers from 1 to $maxHeight.", Toast.LENGTH_SHORT).show()
                return
            }
            if (puzzle.emptyLotsPerLine == 0 && value == EMPTY_LOT) {
                Toast.makeText(this, "This level has no empty lots.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        if (!checkEmptyLots()) return
        if (!checkTallestHeight()) return
        if (!checkNoDuplicateHeights()) return
        // Visibility
        for (c in 0 until n) {
            val col = IntArray(n) { values[it][c] }
            if (puzzle.cluesTop[c] > 0 && visibility(col) != puzzle.cluesTop[c]) {
                Toast.makeText(this, "Top clue mismatch on column ${c + 1}.", Toast.LENGTH_SHORT).show(); return
            }
            if (puzzle.cluesBottom[c] > 0 && visibility(col.reversedArray()) != puzzle.cluesBottom[c]) {
                Toast.makeText(this, "Bottom clue mismatch on column ${c + 1}.", Toast.LENGTH_SHORT).show(); return
            }
        }
        for (r in 0 until n) {
            val row = values[r]
            if (puzzle.cluesLeft[r] > 0 && visibility(row) != puzzle.cluesLeft[r]) {
                Toast.makeText(this, "Left clue mismatch on row ${r + 1}.", Toast.LENGTH_SHORT).show(); return
            }
            if (puzzle.cluesRight[r] > 0 && visibility(row.reversedArray()) != puzzle.cluesRight[r]) {
                Toast.makeText(this, "Right clue mismatch on row ${r + 1}.", Toast.LENGTH_SHORT).show(); return
            }
        }
        solved = true
        PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_SKYSCRAPER, difficulty, puzzleIndex)
        CompletionDialogs.showSolved(
            this,
            "Solved!",
            "Skyscraper complete.",
            MainActivity.TYPE_SKYSCRAPER,
            difficulty,
            puzzleIndex,
            SkyscraperGameActivity::class.java
        )
    }

    private fun checkEmptyLots(): Boolean {
        val expected = puzzle.emptyLotsPerLine
        if (expected == 0) return true
        val n = puzzle.size
        for (r in 0 until n) {
            if (values[r].count { it == EMPTY_LOT } != expected) {
                Toast.makeText(this, "Row ${r + 1} must have $expected empty lot${if (expected == 1) "" else "s"}.", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        for (c in 0 until n) {
            var emptyCount = 0
            for (r in 0 until n) {
                if (values[r][c] == EMPTY_LOT) emptyCount++
            }
            if (emptyCount != expected) {
                Toast.makeText(this, "Column ${c + 1} must have $expected empty lot${if (expected == 1) "" else "s"}.", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun checkNoDuplicateHeights(): Boolean {
        val n = puzzle.size
        for (r in 0 until n) {
            val seen = HashSet<Int>()
            for (c in 0 until n) {
                val value = values[r][c]
                if (value > 0 && !seen.add(value)) {
                    Toast.makeText(this, "Duplicate height in row ${r + 1}.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }
        for (c in 0 until n) {
            val seen = HashSet<Int>()
            for (r in 0 until n) {
                val value = values[r][c]
                if (value > 0 && !seen.add(value)) {
                    Toast.makeText(this, "Duplicate height in column ${c + 1}.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }
        return true
    }

    private fun checkTallestHeight(): Boolean {
        val n = puzzle.size
        val tallest = puzzle.maxHeight
        if (tallest <= n) return true
        for (r in 0 until n) {
            if (values[r].count { it == tallest } != 1) {
                Toast.makeText(this, "Row ${r + 1} must include height $tallest once.", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        for (c in 0 until n) {
            var tallestCount = 0
            for (r in 0 until n) {
                if (values[r][c] == tallest) tallestCount++
            }
            if (tallestCount != 1) {
                Toast.makeText(this, "Column ${c + 1} must include height $tallest once.", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }
}
