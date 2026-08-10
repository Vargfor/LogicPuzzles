package com.logicpuzzles.utils

import android.content.Context
import androidx.core.content.edit
import kotlin.random.Random

class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("logic_puzzles_prefs", Context.MODE_PRIVATE)

    fun isPuzzleCompleted(type: Int, difficulty: Int, index: Int): Boolean {
        return prefs.getBoolean(completionKey(type, difficulty, index), false)
    }

    fun markPuzzleCompleted(type: Int, difficulty: Int, index: Int) {
        prefs.edit { putBoolean(completionKey(type, difficulty, index), true) }
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

    fun isSkyscraperBuildingsEnabled(): Boolean =
        prefs.getBoolean(KEY_SKYSCRAPER_BUILDINGS, true)

    fun setSkyscraperBuildingsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_SKYSCRAPER_BUILDINGS, enabled) }
    }

    fun isDeveloperUnlockAllLevelsAvailable(): Boolean =
        DEVELOPER_UNLOCK_ALL_LEVELS_AVAILABLE

    fun isDeveloperUnlockAllLevelsEnabled(): Boolean =
        isDeveloperUnlockAllLevelsAvailable() &&
            prefs.getBoolean(KEY_DEVELOPER_UNLOCK_ALL_LEVELS, false)

    fun setDeveloperUnlockAllLevelsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_DEVELOPER_UNLOCK_ALL_LEVELS, enabled) }
    }

    fun resetProgressAndShuffleLevels() {
        resetProgressAndShuffleLevels((0 until PUZZLE_TYPES).toSet())
    }

    fun resetProgressAndShuffleLevels(selectedTypes: Set<Int>) {
        val validTypes = selectedTypes.filter { it in 0 until PUZZLE_TYPES }.toSet()
        if (validTypes.isEmpty()) return
        val resetSeed = System.currentTimeMillis() xor System.nanoTime()
        prefs.edit {
            for (type in validTypes) {
                for (difficulty in 0 until DIFFICULTIES) {
                    for (index in 0 until getPuzzleCount(type, difficulty)) {
                        remove(completionKey(type, difficulty, index))
                    }
                    val previous = parseLevelOrder(type, difficulty) ?: identityOrder(type, difficulty)
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
        const val DIFFICULTIES = 5
        const val PUZZLE_TYPES = 10
        const val PUZZLES_PER_TYPE = 175 // 15+25+35+45+55 across 5 difficulties
        const val EXPERT_UNLOCK_HARD_COMPLETIONS = 10
        const val MASTER_UNLOCK_EXPERT_COMPLETIONS = 10
        private const val SUB_DIFFICULTY_GROUP_SIZE = 5
        private const val KEY_SKYSCRAPER_BUILDINGS = "skyscraper_buildings_enabled"
        private const val KEY_DEVELOPER_UNLOCK_ALL_LEVELS = "developer_unlock_all_levels_enabled"
        private const val DEVELOPER_UNLOCK_ALL_LEVELS_AVAILABLE = false

        @Suppress("UNUSED_PARAMETER")
        fun getPuzzleCount(type: Int, difficulty: Int): Int = when (difficulty) {
            0 -> 15; 1 -> 25; 2 -> 35; 3 -> 45; else -> 55
        }

        @Suppress("UNUSED_PARAMETER")
        fun getUnlockThreshold(type: Int, difficulty: Int): Int = when (difficulty) {
            3 -> EXPERT_UNLOCK_HARD_COMPLETIONS
            4 -> MASTER_UNLOCK_EXPERT_COMPLETIONS
            else -> 0
        }
    }

    fun isDifficultyUnlocked(type: Int, difficulty: Int): Boolean = when {
        difficulty < 3 -> true
        difficulty == 3 -> getCompletedCount(type, 2) >= EXPERT_UNLOCK_HARD_COMPLETIONS
        difficulty == 4 -> getCompletedCount(type, 3) >= MASTER_UNLOCK_EXPERT_COMPLETIONS
        else -> true
    }

    fun isLevelUnlocked(type: Int, difficulty: Int, index: Int): Boolean {
        if (index !in 0 until getPuzzleCount(type, difficulty)) return false
        if (isDeveloperUnlockAllLevelsEnabled()) return true
        return isDifficultyUnlocked(type, difficulty) &&
            (index == 0 || isPuzzleCompleted(type, difficulty, index - 1))
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

    private fun completionKey(type: Int, difficulty: Int, index: Int): String =
        "completed_${type}_${difficulty}_${index}"

    private fun levelOrderKey(type: Int, difficulty: Int): String =
        "level_order_${type}_${difficulty}"

    private fun isRetainedSettingKey(key: String): Boolean =
        key == "color_theme" ||
            key == KEY_SKYSCRAPER_BUILDINGS ||
            key == KEY_DEVELOPER_UNLOCK_ALL_LEVELS ||
            key.startsWith("custom_theme_")
}
