package com.logicpuzzles.mastermind

import kotlin.random.Random

data class MastermindLevel(
    val positions: Int,
    val numColors: Int,
    val maxGuesses: Int,
    val allowDuplicates: Boolean,
    val secret: List<Int>
)

object MastermindData {
    private data class LevelConfig(
        val positions: Int,
        val numColors: Int,
        val maxGuesses: Int,
        val allowDuplicates: Boolean
    )

    fun levelFor(difficulty: Int, index: Int): MastermindLevel {
        val safeDifficulty = difficulty.coerceIn(0, 3)
        val safeIndex = index.coerceIn(0, 14)
        val config = configFor(safeDifficulty, safeIndex)
        return MastermindLevel(
            positions = config.positions,
            numColors = config.numColors,
            maxGuesses = config.maxGuesses,
            allowDuplicates = config.allowDuplicates,
            secret = generateSecret(safeDifficulty, safeIndex, config)
        )
    }

    private fun configFor(difficulty: Int, index: Int): LevelConfig {
        val group = index / 5
        return when (difficulty) {
            0 -> when (group) {
                0 -> LevelConfig(positions = 4, numColors = 4, maxGuesses = 12, allowDuplicates = false)
                1 -> LevelConfig(positions = 4, numColors = 5, maxGuesses = 10, allowDuplicates = false)
                else -> LevelConfig(positions = 4, numColors = 6, maxGuesses = 9, allowDuplicates = false)
            }
            1 -> when (group) {
                0 -> LevelConfig(positions = 4, numColors = 6, maxGuesses = 9, allowDuplicates = true)
                1 -> LevelConfig(positions = 5, numColors = 6, maxGuesses = 9, allowDuplicates = true)
                else -> LevelConfig(positions = 5, numColors = 7, maxGuesses = 8, allowDuplicates = true)
            }
            2 -> when (group) {
                0 -> LevelConfig(positions = 5, numColors = 7, maxGuesses = 8, allowDuplicates = true)
                1 -> LevelConfig(positions = 5, numColors = 8, maxGuesses = 8, allowDuplicates = true)
                else -> LevelConfig(positions = 6, numColors = 8, maxGuesses = 7, allowDuplicates = true)
            }
            else -> when (group) {
                0 -> LevelConfig(positions = 6, numColors = 8, maxGuesses = 7, allowDuplicates = true)
                1 -> LevelConfig(positions = 6, numColors = 8, maxGuesses = 6, allowDuplicates = true)
                else -> LevelConfig(positions = 6, numColors = 8, maxGuesses = 5, allowDuplicates = true)
            }
        }
    }

    private fun generateSecret(difficulty: Int, index: Int, config: LevelConfig): List<Int> {
        val group = index / 5
        val indexInGroup = index % 5
        val colorMap = (0 until config.numColors).toMutableList().apply {
            shuffle(Random(90_001 + difficulty * 811 + group * 37))
        }

        return if (config.allowDuplicates) {
            val total = power(config.numColors, config.positions)
            var ordinal = ((difficulty + 1L) * 104_729L + (index + 1L) * 7_919L + group * 1_543L)
                .floorMod(total)
            List(config.positions) { position ->
                val raw = (ordinal % config.numColors).toInt()
                ordinal /= config.numColors
                colorMap[(raw + position + group) % config.numColors]
            }
        } else {
            val total = permutationCount(config.numColors, config.positions)
            val ordinal = ((difficulty + 1) * 997 + group * 59 + indexInGroup * 353).floorMod(total)
            nthPermutation(config.numColors, config.positions, ordinal).map { colorMap[it] }
        }
    }

    private fun nthPermutation(size: Int, length: Int, ordinal: Int): List<Int> {
        val values = (0 until size).toMutableList()
        val result = ArrayList<Int>(length)
        var remainingOrdinal = ordinal
        for (position in 0 until length) {
            val blockSize = permutationCount(size - position - 1, length - position - 1)
            val selected = if (blockSize == 0) 0 else remainingOrdinal / blockSize
            remainingOrdinal = if (blockSize == 0) 0 else remainingOrdinal % blockSize
            result.add(values.removeAt(selected))
        }
        return result
    }

    private fun permutationCount(size: Int, length: Int): Int {
        var result = 1
        for (value in size - length + 1..size) result *= value.coerceAtLeast(1)
        return result
    }

    private fun power(base: Int, exponent: Int): Long {
        var result = 1L
        repeat(exponent) { result *= base }
        return result
    }

    private fun Int.floorMod(modulus: Int): Int {
        val value = this % modulus
        return if (value < 0) value + modulus else value
    }

    private fun Long.floorMod(modulus: Long): Long {
        val value = this % modulus
        return if (value < 0) value + modulus else value
    }
}
