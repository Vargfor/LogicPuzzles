package com.logicpuzzles.utils

import android.content.Context
import androidx.core.content.edit
import kotlin.random.Random

class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("logic_puzzles_prefs", Context.MODE_PRIVATE)

    fun isPuzzleCompleted(type: Int, difficulty: Int, index: Int): Boolean {
        return prefs.getBoolean("completed_${type}_${difficulty}_${index}", false)
    }

    fun markPuzzleCompleted(type: Int, difficulty: Int, index: Int) {
        prefs.edit { putBoolean("completed_${type}_${difficulty}_${index}", true) }
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
        prefs.edit { clear() }
    }

    fun resetProgressAndShuffleLevels() {
        val selectedTheme = prefs.getInt("color_theme", 0)
        val previousOrders = mutableMapOf<Pair<Int, Int>, List<Int>>()
        for (type in 0 until PUZZLE_TYPES) {
            for (difficulty in 0 until DIFFICULTIES) {
                previousOrders[type to difficulty] = parseLevelOrder(type, difficulty)
                    ?: identityOrder(type, difficulty)
            }
        }

        val resetSeed = System.currentTimeMillis() xor System.nanoTime()
        prefs.edit {
            clear()
            putInt("color_theme", selectedTheme)
            for (type in 0 until PUZZLE_TYPES) {
                for (difficulty in 0 until DIFFICULTIES) {
                    val previous = previousOrders.getValue(type to difficulty)
                    val order = shuffledLevelOrder(type, difficulty, resetSeed, previous)
                    putString(levelOrderKey(type, difficulty), order.joinToString(","))
                }
            }
        }
    }

    fun getCatalogIndex(type: Int, difficulty: Int, displayIndex: Int): Int {
        val count = getPuzzleCount(type, difficulty)
        if (displayIndex !in 0 until count) return displayIndex.coerceIn(0, count - 1)
        val order = parseLevelOrder(type, difficulty) ?: return displayIndex
        return order.getOrElse(displayIndex) { displayIndex }
    }

    companion object {
        const val DIFFICULTIES = 4
        const val PUZZLE_TYPES = 10
        const val PUZZLES_PER_TYPE = 60 // total per puzzle type: 15 per difficulty x 4 difficulties
        const val EXPERT_UNLOCK_HARD_COMPLETIONS = 10
        private const val SUB_DIFFICULTY_GROUP_SIZE = 5

        /**
         * How many puzzles a given (puzzle type, difficulty) combination has.
         * All types use 15 per difficulty. Within each difficulty the first 5 levels use the
         * base size/config, levels 6-10 step up by 1 unit, and levels 11-15 step up by 2 units.
         * Difficulty jumps add 2 more units, giving a smooth overall curve.
         */
        @Suppress("UNUSED_PARAMETER")
        fun getPuzzleCount(type: Int, difficulty: Int): Int = 15

        /** Number of Hard completions required to unlock Expert. */
        @Suppress("UNUSED_PARAMETER")
        fun getUnlockThreshold(type: Int, difficulty: Int): Int =
            if (difficulty == 2) EXPERT_UNLOCK_HARD_COMPLETIONS else 0
    }

    fun isDifficultyUnlocked(type: Int, difficulty: Int): Boolean {
        return difficulty < 3 ||
                getCompletedCount(type, 2) >= EXPERT_UNLOCK_HARD_COMPLETIONS
    }

    private fun identityOrder(type: Int, difficulty: Int): List<Int> =
        (0 until getPuzzleCount(type, difficulty)).toList()

    private fun shuffledLevelOrder(
        type: Int,
        difficulty: Int,
        resetSeed: Long,
        previousOrder: List<Int>
    ): List<Int> {
        val count = getPuzzleCount(type, difficulty)
        val order = MutableList(count) { it }
        val random = Random(resetSeed + type * 10_007L + difficulty * 997L)

        for (start in 0 until count step SUB_DIFFICULTY_GROUP_SIZE) {
            val end = minOf(start + SUB_DIFFICULTY_GROUP_SIZE, count)
            val block = (start until end).toMutableList()
            block.shuffle(random)
            if (block == (start until end).toList() && block.size > 1) {
                val first = block.removeAt(0)
                block.add(first)
            }
            for ((offset, catalogIndex) in block.withIndex()) {
                order[start + offset] = catalogIndex
            }
        }

        return if (order == previousOrder && count > 1) {
            rotateEachSubDifficultyBlock(order)
        } else {
            order
        }
    }

    private fun rotateEachSubDifficultyBlock(order: List<Int>): List<Int> {
        val rotated = order.toMutableList()
        for (start in order.indices step SUB_DIFFICULTY_GROUP_SIZE) {
            val end = minOf(start + SUB_DIFFICULTY_GROUP_SIZE, order.size)
            if (end - start <= 1) continue
            val first = rotated[start]
            for (i in start until end - 1) rotated[i] = rotated[i + 1]
            rotated[end - 1] = first
        }
        return rotated
    }

    private fun parseLevelOrder(type: Int, difficulty: Int): List<Int>? {
        val count = getPuzzleCount(type, difficulty)
        val raw = prefs.getString(levelOrderKey(type, difficulty), null) ?: return null
        val values = raw.split(",").mapNotNull { it.toIntOrNull() }
        if (values.size != count) return null
        if (values.toSet() != (0 until count).toSet()) return null
        return values
    }

    private fun levelOrderKey(type: Int, difficulty: Int): String =
        "level_order_${type}_${difficulty}"
}
