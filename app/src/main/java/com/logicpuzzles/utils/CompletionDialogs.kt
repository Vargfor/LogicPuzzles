package com.logicpuzzles.utils

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.logicpuzzles.MainActivity

object CompletionDialogs {
    fun showSolved(
        activity: AppCompatActivity,
        title: String,
        message: String,
        puzzleType: Int,
        difficulty: Int,
        puzzleIndex: Int,
        nextActivityClass: Class<out AppCompatActivity>
    ) {
        val hasNext = puzzleIndex + 1 < PrefsManager.getPuzzleCount(puzzleType, difficulty)
        val builder = AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)

        if (hasNext) {
            builder
                .setPositiveButton("Next Game") { _, _ ->
                    activity.startActivity(Intent(activity, nextActivityClass).apply {
                        putExtra(MainActivity.EXTRA_PUZZLE_TYPE, puzzleType)
                        putExtra(MainActivity.EXTRA_DIFFICULTY, difficulty)
                        putExtra(MainActivity.EXTRA_PUZZLE_INDEX, puzzleIndex + 1)
                    })
                    activity.finish()
                }
                .setNegativeButton("Back to Menu") { _, _ -> activity.finish() }
        } else {
            builder.setPositiveButton("Back to Menu") { _, _ -> activity.finish() }
        }

        builder.show()
    }
}
