package com.logicpuzzles.futoshiki

import kotlin.random.Random

// hConstraints[r][c] = constraint between cell (r,c) and (r,c+1): 0=none, 1=<, 2=>
// vConstraints[r][c] = constraint between cell (r,c) and (r+1,c): 0=none, 1=top<bottom, 2=top>bottom
data class FutoshikiPuzzle(
    val size: Int,
    val initial: Array<IntArray>,
    val hConstraints: Array<IntArray>,
    val vConstraints: Array<IntArray>,
    val solution: Array<IntArray>? = null
)

object FutoshikiPuzzles {

    private fun latinSolution(size: Int, variant: Int): Array<IntArray> {
        val random = Random(10_000 + size * 97 + variant * 313)
        val rowOrder = (0 until size).toList().shuffled(random)
        val colOrder = (0 until size).toList().shuffled(random)
        val digitOrder = (1..size).toList().shuffled(random)
        val step = coprimeSteps(size)[variant % coprimeSteps(size).size]
        return Array(size) { r ->
            IntArray(size) { c ->
                val base = (rowOrder[r] + colOrder[c] * step) % size
                digitOrder[base]
            }
        }
    }

    private fun coprimeSteps(size: Int): IntArray = when (size) {
        4 -> intArrayOf(1, 3)
        5 -> intArrayOf(1, 2, 3, 4)
        6 -> intArrayOf(1, 5)
        else -> intArrayOf(1)
    }

    private fun build(size: Int, difficulty: Int, index: Int): FutoshikiPuzzle {
        val variant = difficulty * 31 + index
        val random = Random(20_000 + variant * 431 + size * 19)
        val solution = latinSolution(size, variant)
        val initial = Array(size) { IntArray(size) }
        val hConstraints = Array(size) { IntArray(size - 1) }
        val vConstraints = Array(size - 1) { IntArray(size) }

        val revealCount = when (difficulty) {
            0 -> if (size == 4) 7 else 8
            1 -> 6
            2 -> 5
            3 -> 4
            else -> 5  // Master 6×6: slightly more givens relative to grid size
        }
        for (pos in revealPattern(size, revealCount, random)) {
            val r = pos / size
            val c = pos % size
            initial[r][c] = solution[r][c]
        }

        val constraintCount = when (difficulty) {
            0 -> if (size == 4) 3 else 5
            1 -> 8
            2 -> 11
            3 -> 14
            else -> 18  // Master 6×6: denser constraints
        }
        val edges = mutableListOf<Edge>()
        for (r in 0 until size) {
            for (c in 0 until size - 1) {
                edges.add(Edge(horizontal = true, r = r, c = c))
            }
        }
        for (r in 0 until size - 1) {
            for (c in 0 until size) {
                edges.add(Edge(horizontal = false, r = r, c = c))
            }
        }
        for (edge in edges.shuffled(random).take(constraintCount.coerceAtMost(edges.size))) {
            if (edge.horizontal) {
                hConstraints[edge.r][edge.c] =
                    if (solution[edge.r][edge.c] < solution[edge.r][edge.c + 1]) 1 else 2
            } else {
                vConstraints[edge.r][edge.c] =
                    if (solution[edge.r][edge.c] < solution[edge.r + 1][edge.c]) 1 else 2
            }
        }

        return FutoshikiPuzzle(size, initial, hConstraints, vConstraints, solution.map { it.copyOf() }.toTypedArray())
    }

    private data class Edge(val horizontal: Boolean, val r: Int, val c: Int)

    private fun revealPattern(size: Int, count: Int, random: Random): Set<Int> {
        val selected = LinkedHashSet<Int>()
        val rows = (0 until size).toList().shuffled(random)
        for (r in rows.take(count.coerceAtMost(size))) {
            selected.add(r * size + random.nextInt(size))
        }

        val all = (0 until size * size).toMutableList()
        all.shuffle(random)
        for (pos in all) {
            if (selected.size >= count) break
            selected.add(pos)
        }
        return selected
    }

    fun get(difficulty: Int, index: Int): FutoshikiPuzzle {
        val safeDifficulty = difficulty.coerceIn(0, 4)
        val maxIndex = when (safeDifficulty) { 0 -> 14; 1 -> 24; 2 -> 34; 3 -> 44; else -> 54 }
        val safeIndex = index.coerceIn(0, maxIndex)
        val size = when (safeDifficulty) {
            0 -> 4; 1, 2, 3 -> 5; else -> 6
        }
        return build(size, safeDifficulty, safeIndex)
    }
}
