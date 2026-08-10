package com.logicpuzzles.futoshiki

import com.logicpuzzles.utils.PuzzleBoardSpecs
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil
import kotlin.random.Random

// hConstraints[r][c]: 0=none, 1=left<right, 2=left>right
// vConstraints[r][c]: 0=none, 1=top<bottom, 2=top>bottom
data class FutoshikiPuzzle(
    val size: Int,
    val boxRows: Int,
    val boxCols: Int,
    val initial: Array<IntArray>,
    val hConstraints: Array<IntArray>,
    val vConstraints: Array<IntArray>,
    val solution: Array<IntArray>? = null
)

object FutoshikiPuzzles {
    private val cache = ConcurrentHashMap<Pair<Int, Int>, FutoshikiPuzzle>()

    private fun build(difficulty: Int, index: Int): FutoshikiPuzzle {
        val spec = PuzzleBoardSpecs.futoshikiSpec(difficulty)
        val size = spec.size
        val random = Random(20_000 + difficulty * 10_007 + index * 431 + size * 19)
        val solution = sudokuSolution(size, spec.boxRows, spec.boxCols, random)
        val initial = Array(size) { IntArray(size) }
        val hConstraints = Array(size) { IntArray(size - 1) }
        val vConstraints = Array(size - 1) { IntArray(size) }

        val givenRatio = floatArrayOf(0.22f, 0.17f, 0.14f, 0.115f, 0.095f)[difficulty]
        val givenCount = ceil(size * size * givenRatio).toInt()
        for (position in revealPattern(size, givenCount, random)) {
            initial[position / size][position % size] = solution[position / size][position % size]
        }

        val inequalityRatio = floatArrayOf(0.20f, 0.22f, 0.24f, 0.26f, 0.28f)[difficulty]
        val edges = buildList {
            for (r in 0 until size) for (c in 0 until size - 1) add(Edge(true, r, c))
            for (r in 0 until size - 1) for (c in 0 until size) add(Edge(false, r, c))
        }.shuffled(random)
        val constraintCount = ceil(edges.size * inequalityRatio).toInt()
        for (edge in edges.take(constraintCount)) {
            if (edge.horizontal) {
                hConstraints[edge.r][edge.c] =
                    if (solution[edge.r][edge.c] < solution[edge.r][edge.c + 1]) 1 else 2
            } else {
                vConstraints[edge.r][edge.c] =
                    if (solution[edge.r][edge.c] < solution[edge.r + 1][edge.c]) 1 else 2
            }
        }

        return FutoshikiPuzzle(
            size = size,
            boxRows = spec.boxRows,
            boxCols = spec.boxCols,
            initial = initial,
            hConstraints = hConstraints,
            vConstraints = vConstraints,
            solution = solution
        )
    }

    private fun sudokuSolution(
        size: Int,
        boxRows: Int,
        boxCols: Int,
        random: Random
    ): Array<IntArray> {
        val rowOrder = groupedOrder(size, boxRows, random)
        val colOrder = groupedOrder(size, boxCols, random)
        val symbols = (1..size).toList().shuffled(random)

        fun pattern(r: Int, c: Int): Int =
            (boxCols * (r % boxRows) + r / boxRows + c) % size

        return Array(size) { r ->
            IntArray(size) { c -> symbols[pattern(rowOrder[r], colOrder[c])] }
        }
    }

    private fun groupedOrder(size: Int, groupSize: Int, random: Random): List<Int> =
        (0 until size / groupSize).toList().shuffled(random).flatMap { group ->
            (0 until groupSize).toList().shuffled(random).map { offset -> group * groupSize + offset }
        }

    private fun revealPattern(
        size: Int,
        count: Int,
        random: Random
    ): Set<Int> = (0 until size * size).toList().shuffled(random).take(count).toSet()

    private data class Edge(val horizontal: Boolean, val r: Int, val c: Int)

    fun get(difficulty: Int, index: Int): FutoshikiPuzzle {
        val safeDifficulty = difficulty.coerceIn(0, 4)
        val maxIndex = when (safeDifficulty) { 0 -> 14; 1 -> 24; 2 -> 34; 3 -> 44; else -> 54 }
        val safeIndex = index.coerceIn(0, maxIndex)
        return cache.getOrPut(safeDifficulty to safeIndex) { build(safeDifficulty, safeIndex) }
    }
}
