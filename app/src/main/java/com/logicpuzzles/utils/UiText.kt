package com.logicpuzzles.utils

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
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

fun View.applySystemBarInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
        insets
    }
}
