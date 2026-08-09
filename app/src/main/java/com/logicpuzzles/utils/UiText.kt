package com.logicpuzzles.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cyberhub.logicgames.R

fun Context.difficultyName(difficulty: Int): String = getString(
    when (difficulty) {
        0 -> R.string.difficulty_easy
        1 -> R.string.difficulty_medium
        2 -> R.string.difficulty_hard
        else -> R.string.difficulty_expert
    }
)

fun Context.puzzleHeader(
    @StringRes puzzleNameResId: Int,
    difficulty: Int,
    puzzleIndex: Int
): String = getString(
    R.string.puzzle_header,
    getString(puzzleNameResId),
    difficultyName(difficulty),
    puzzleIndex + 1
)

fun Context.numberText(value: Int): String = getString(R.string.number_value, value)

fun Context.resetSymbolButton(onClick: () -> Unit): Button {
    val palette = ThemeManager.currentPalette(this)
    val density = resources.displayMetrics.density
    fun dp(value: Int): Int = (value * density).toInt()
    val resetIcon = ContextCompat.getDrawable(this, R.drawable.ic_reset)?.mutate()?.let { drawable ->
        DrawableCompat.wrap(drawable).apply {
            DrawableCompat.setTint(this, palette.buttonText)
        }
    }

    return Button(this).apply {
        text = ""
        contentDescription = getString(R.string.action_reset_puzzle)
        setTextColor(palette.buttonText)
        setBackgroundColor(palette.button)
        gravity = Gravity.CENTER
        if (resetIcon != null) {
            foreground = resetIcon
            setForegroundGravity(Gravity.CENTER)
        }
        minWidth = dp(44)
        minimumWidth = dp(44)
        minHeight = dp(40)
        minimumHeight = dp(40)
        setPadding(0, 0, 0, 0)
        layoutParams = LinearLayout.LayoutParams(dp(44), LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            marginEnd = dp(8)
        }
        setOnClickListener { onClick() }
    }
}

fun View.applySystemBarInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
        insets
    }
}
