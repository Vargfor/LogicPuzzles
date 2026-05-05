package com.logicpuzzles.utils

import android.content.Context
import com.logicpuzzles.MainActivity

class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("logic_puzzles_prefs", Context.MODE_PRIVATE)

    fun isPuzzleCompleted(type: Int, difficulty: Int, index: Int): Boolean {
        return prefs.getBoolean("completed_${type}_${difficulty}_${index}", false)
    }

    fun markPuzzleCompleted(type: Int, difficulty: Int, index: Int) {
        prefs.edit().putBoolean("completed_${type}_${difficulty}_${index}", true).apply()
    }

    fun getCompletedCount(type: Int, difficulty: Int): Int {
        var count = 0
        for (i in 0 until getPuzzleCount(type, difficulty)) {
            if (isPuzzleCompleted(type, difficulty, i)) count++
        }
        return count
    }

    fun getTotalCompleted(type: Int): Int {
        var total = 0
        for (d in 0 until DIFFICULTIES) {
            total += getCompletedCount(type, d)
        }
        return total
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        const val DIFFICULTIES = 4
        const val PUZZLES_PER_TYPE = 40 // total per puzzle type, distributed across difficulties

        /**
         * How many puzzles a given (puzzle type, difficulty) combination has.
         * Most types use 10 per difficulty, but some redistribute (e.g. Hidato has fewer
         * easy puzzles since 3x3 is trivial, and more hard ones to compensate).
         * Sums to PUZZLES_PER_TYPE for every type.
         */
        fun getPuzzleCount(type: Int, difficulty: Int): Int = when (type) {
            MainActivity.TYPE_HIDATO -> when (difficulty) {
                0 -> 5      // 3x3 — only a few trivial easies
                1 -> 10     // 4x4
                2 -> 15     // 5x5 — extra puzzles here to balance
                else -> 10  // 5x5 with blocks
            }
            else -> 10
        }

        /** Number of completions in difficulty D required to unlock difficulty D+1. */
        fun getUnlockThreshold(type: Int, difficulty: Int): Int =
            (getPuzzleCount(type, difficulty) / 2).coerceAtLeast(1)
    }
}
