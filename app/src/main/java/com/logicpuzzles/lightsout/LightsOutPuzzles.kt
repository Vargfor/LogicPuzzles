package com.logicpuzzles.lightsout

import kotlin.random.Random

data class LightsOutPuzzle(val size: Int, val initial: Array<BooleanArray>)

object LightsOutPuzzles {
    private const val LEVELS = 15

    private val EASY by lazy { buildLevels(0) }
    private val MEDIUM by lazy { buildLevels(1) }
    private val HARD by lazy { buildLevels(2) }
    private val EXPERT by lazy { buildLevels(3) }

    private fun buildLevels(difficulty: Int): List<LightsOutPuzzle> =
        List(LEVELS) { index ->
            val size = sizeFor(difficulty, index)
            val pressCount = targetPressCount(difficulty, index)
            buildFromPresses(size, pressCells(size, pressCount, difficulty, index))
        }

    private fun sizeFor(difficulty: Int, index: Int): Int = when (difficulty) {
        0 -> 5
        1 -> if (index < 10) 5 else 6
        2 -> if (index < 10) 6 else 7
        else -> if (index < 10) 7 else 8
    }

    private fun targetPressCount(difficulty: Int, index: Int): Int = when (difficulty) {
        0 -> 1 + (index % 3) + if (index >= 10) 1 else 0
        1 -> 5 + (index % 4) + if (index >= 10) 1 else 0
        2 -> 9 + (index % 5) + if (index >= 10) 2 else 0
        else -> 14 + (index % 7) + if (index >= 10) 4 else 0
    }

    private fun pressCells(size: Int, count: Int, difficulty: Int, index: Int): List<Pair<Int, Int>> {
        val cells = (0 until size * size).toMutableList()
        cells.shuffle(Random(50_000 + difficulty * 10_007 + index * 997 + size * 53))
        return cells.take(count).map { cell -> cell / size to cell % size }
    }

    private fun buildFromPresses(size: Int, presses: List<Pair<Int, Int>>): LightsOutPuzzle {
        val grid = Array(size) { BooleanArray(size) }
        for ((r, c) in presses) toggle(grid, r, c)
        return LightsOutPuzzle(size, grid)
    }

    private fun toggle(grid: Array<BooleanArray>, r: Int, c: Int) {
        grid[r][c] = !grid[r][c]
        if (r > 0) grid[r - 1][c] = !grid[r - 1][c]
        if (r < grid.size - 1) grid[r + 1][c] = !grid[r + 1][c]
        if (c > 0) grid[r][c - 1] = !grid[r][c - 1]
        if (c < grid.size - 1) grid[r][c + 1] = !grid[r][c + 1]
    }

    fun get(difficulty: Int, index: Int): LightsOutPuzzle {
        val pool = when (difficulty) {
            0 -> EASY
            1 -> MEDIUM
            2 -> HARD
            else -> EXPERT
        }
        return pool[index.coerceIn(0, pool.size - 1)]
    }
}
