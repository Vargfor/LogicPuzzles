package com.logicpuzzles.hidato

import com.logicpuzzles.utils.PuzzleBoardSpecs
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt
import kotlin.random.Random

// initial[r][c]: 0 = empty, -1 = blocked (no cell), >0 = pre-filled
data class HidatoPuzzle(
    val rows: Int,
    val cols: Int,
    val initial: Array<IntArray>,
    val maxNumber: Int,
    val solution: Array<IntArray>? = null
)

object HidatoPuzzles {
    private val cache = ConcurrentHashMap<Pair<Int, Int>, HidatoPuzzle>()

    private fun build(side: Int, difficulty: Int, index: Int): HidatoPuzzle {
        val random = Random(30_000 + difficulty * 10_007 + index * 131)
        val activeCount = activeCount(side, difficulty, index)
        val path = irregularSerpentinePath(side, activeCount, random)
        val solution = Array(side) { IntArray(side) { -1 } }
        val initial = Array(side) { IntArray(side) { -1 } }

        for ((position, cell) in path.withIndex()) {
            solution[cell.first][cell.second] = position + 1
            initial[cell.first][cell.second] = 0
        }

        val revealRatio = floatArrayOf(0.58f, 0.44f, 0.34f, 0.27f, 0.22f)[difficulty]
        val revealCount = (path.size * revealRatio).roundToInt().coerceIn(4, path.size)
        for (number in anchorPattern(path.size, revealCount, random)) {
            val (r, c) = path[number - 1]
            initial[r][c] = number
        }

        return HidatoPuzzle(side, side, initial, path.size, solution)
    }

    private fun activeCount(side: Int, difficulty: Int, index: Int): Int {
        val range = when (difficulty) {
            0 -> side * side..side * side
            1 -> 42..47
            2 -> 68..78
            3 -> 84..94
            else -> 90..99
        }
        return range.first + positiveMod(index * 17 + difficulty * 11, range.last - range.first + 1)
    }

    private fun irregularSerpentinePath(
        side: Int,
        targetCells: Int,
        random: Random
    ): List<Pair<Int, Int>> {
        if (targetCells == side * side) return fullSnake(side)

        val lengths = IntArray(side) { if (it == 0) side else 2 }
        var remaining = targetCells - lengths.sum()
        val expandableRows = (1 until side).toMutableList()
        while (remaining > 0) {
            expandableRows.shuffle(random)
            for (r in expandableRows) {
                if (remaining == 0) break
                if (lengths[r] < side) {
                    lengths[r]++
                    remaining--
                }
            }
        }

        val starts = IntArray(side)
        val ends = IntArray(side)
        starts[0] = 0
        ends[0] = side - 1
        for (r in 1 until side) {
            val length = lengths[r]
            if ((r - 1) % 2 == 0) {
                val anchor = (ends[r - 1] + random.nextInt(-1, 2)).coerceIn(length - 1, side - 1)
                ends[r] = anchor
                starts[r] = anchor - length + 1
            } else {
                val anchor = (starts[r - 1] + random.nextInt(-1, 2)).coerceIn(0, side - length)
                starts[r] = anchor
                ends[r] = anchor + length - 1
            }
        }

        return buildList(targetCells) {
            for (r in 0 until side) {
                val columns = if (r % 2 == 0) starts[r]..ends[r] else ends[r] downTo starts[r]
                for (c in columns) add(r to c)
            }
        }
    }

    private fun fullSnake(side: Int): List<Pair<Int, Int>> = buildList(side * side) {
        for (r in 0 until side) {
            val columns = if (r % 2 == 0) 0 until side else side - 1 downTo 0
            for (c in columns) add(r to c)
        }
    }

    private fun anchorPattern(maxNumber: Int, revealCount: Int, random: Random): Set<Int> {
        val anchors = linkedSetOf(1, maxNumber)
        val interiorCount = revealCount - anchors.size
        val spacing = maxNumber.toFloat() / (interiorCount + 1)
        for (i in 1..interiorCount) {
            val center = (i * spacing).roundToInt().coerceIn(2, maxNumber - 1)
            anchors.add((center + random.nextInt(-2, 3)).coerceIn(2, maxNumber - 1))
        }
        for (number in (2 until maxNumber).toList().shuffled(random)) {
            if (anchors.size >= revealCount) break
            anchors.add(number)
        }
        return anchors
    }

    private fun positiveMod(value: Int, modulus: Int): Int = ((value % modulus) + modulus) % modulus

    fun get(difficulty: Int, index: Int): HidatoPuzzle {
        val safeDifficulty = difficulty.coerceIn(0, 4)
        val maxIndex = when (safeDifficulty) { 0 -> 14; 1 -> 24; 2 -> 34; 3 -> 44; else -> 54 }
        val safeIndex = index.coerceIn(0, maxIndex)
        return cache.getOrPut(safeDifficulty to safeIndex) {
            build(PuzzleBoardSpecs.hidatoSide(safeDifficulty), safeDifficulty, safeIndex)
        }
    }
}
