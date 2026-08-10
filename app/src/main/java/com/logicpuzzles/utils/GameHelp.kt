package com.logicpuzzles.utils

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.cyberhub.logicgames.R
import com.logicpuzzles.MainActivity

internal data class GameHelpContent(
    @StringRes val puzzleNameResId: Int,
    @StringRes val goalResId: Int,
    @StringRes val controlsResId: Int,
    @StringRes val tipResId: Int
)

internal data class GameHelpExtraSection(
    @StringRes val titleResId: Int,
    val viewFactory: () -> View
)

internal fun gameHelpContent(puzzleType: Int): GameHelpContent = when (puzzleType) {
    MainActivity.TYPE_NONOGRAM -> GameHelpContent(
        R.string.puzzle_nonogram,
        R.string.help_nonogram_goal,
        R.string.help_nonogram_controls,
        R.string.help_nonogram_tip
    )
    MainActivity.TYPE_MASTERMIND -> GameHelpContent(
        R.string.puzzle_mastermind,
        R.string.help_mastermind_goal,
        R.string.help_mastermind_controls,
        R.string.help_mastermind_tip
    )
    MainActivity.TYPE_LIGHTS_OUT -> GameHelpContent(
        R.string.puzzle_lights_out,
        R.string.help_lights_out_goal,
        R.string.help_lights_out_controls,
        R.string.help_lights_out_tip
    )
    MainActivity.TYPE_KAKURO -> GameHelpContent(
        R.string.puzzle_kakuro,
        R.string.help_kakuro_goal,
        R.string.help_kakuro_controls,
        R.string.help_kakuro_tip
    )
    MainActivity.TYPE_LOGIC_GRID -> GameHelpContent(
        R.string.puzzle_logic_grid,
        R.string.help_logic_grid_goal,
        R.string.help_logic_grid_controls,
        R.string.help_logic_grid_tip
    )
    MainActivity.TYPE_SLITHERLINK -> GameHelpContent(
        R.string.puzzle_slitherlink,
        R.string.help_slitherlink_goal,
        R.string.help_slitherlink_controls,
        R.string.help_slitherlink_tip
    )
    MainActivity.TYPE_NURIKABE -> GameHelpContent(
        R.string.puzzle_nurikabe,
        R.string.help_nurikabe_goal,
        R.string.help_nurikabe_controls,
        R.string.help_nurikabe_tip
    )
    MainActivity.TYPE_HIDATO -> GameHelpContent(
        R.string.puzzle_hidato,
        R.string.help_hidato_goal,
        R.string.help_hidato_controls,
        R.string.help_hidato_tip
    )
    MainActivity.TYPE_FUTOSHIKI -> GameHelpContent(
        R.string.puzzle_futoshiki,
        R.string.help_futoshiki_goal,
        R.string.help_futoshiki_controls,
        R.string.help_futoshiki_tip
    )
    MainActivity.TYPE_SKYSCRAPER -> GameHelpContent(
        R.string.puzzle_skyscraper,
        R.string.help_skyscraper_goal,
        R.string.help_skyscraper_controls,
        R.string.help_skyscraper_tip
    )
    else -> throw IllegalArgumentException("Unknown puzzle type: $puzzleType")
}

internal fun Context.gameInstructionRow(
    puzzleType: Int,
    instruction: CharSequence,
    textSizeSp: Float = 12f,
    horizontalPaddingDp: Int = 12,
    topPaddingDp: Int = 0,
    bottomPaddingDp: Int = 8,
    extraHelpSection: GameHelpExtraSection? = null
): LinearLayout {
    val palette = ThemeManager.currentPalette(this)
    val density = resources.displayMetrics.density
    fun dp(value: Int): Int = (value * density).toInt()

    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(
            dp(horizontalPaddingDp),
            dp(topPaddingDp),
            dp(horizontalPaddingDp),
            dp(bottomPaddingDp)
        )
        addView(TextView(this@gameInstructionRow).apply {
            text = instruction
            textSize = textSizeSp
            setTextColor(palette.textSecondary)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        })
        addView(Button(this@gameInstructionRow).apply {
            text = "i"
            setAllCaps(false)
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setTextColor(palette.textPrimary)
            contentDescription = getString(
                R.string.action_how_to_play,
                getString(gameHelpContent(puzzleType).puzzleNameResId)
            )
            gravity = Gravity.CENTER
            minWidth = 0
            minimumWidth = 0
            minHeight = 0
            minimumHeight = 0
            setPadding(0, 0, 0, 0)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(4).toFloat()
                setColor(palette.surfaceStrong)
                setStroke(dp(1).coerceAtLeast(1), palette.gridLine)
            }
            layoutParams = LinearLayout.LayoutParams(dp(36), dp(36)).apply {
                marginStart = dp(8)
            }
            setOnClickListener { showGameHelpDialog(puzzleType, extraHelpSection) }
        })
    }
}

internal fun Context.showGameHelpDialog(
    puzzleType: Int,
    extraSection: GameHelpExtraSection? = null
) {
    val help = gameHelpContent(puzzleType)
    val palette = ThemeManager.currentPalette(this)
    val density = resources.displayMetrics.density
    fun dp(value: Int): Int = (value * density).toInt()

    val content = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(20), dp(8), dp(20), dp(8))
        setBackgroundColor(palette.background)
    }

    fun addSectionTitle(@StringRes titleResId: Int) {
        content.addView(TextView(this).apply {
            text = getString(titleResId)
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            setTextColor(palette.accent)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = if (content.childCount == 0) 0 else dp(16)
            }
        })
    }

    fun addSection(@StringRes titleResId: Int, @StringRes bodyResId: Int) {
        addSectionTitle(titleResId)
        content.addView(TextView(this).apply {
            text = getString(bodyResId)
            textSize = 14f
            setTextColor(palette.textPrimary)
            setPadding(0, dp(4), 0, 0)
        })
    }

    addSection(R.string.help_section_goal, help.goalResId)
    addSection(R.string.help_section_controls, help.controlsResId)
    extraSection?.let { section ->
        addSectionTitle(section.titleResId)
        content.addView(section.viewFactory())
    }
    addSection(R.string.help_section_tip, help.tipResId)

    AlertDialog.Builder(this)
        .setTitle(getString(R.string.help_dialog_title, getString(help.puzzleNameResId)))
        .setView(ScrollView(this).apply { addView(content) })
        .setPositiveButton(R.string.close, null)
        .show()
}
