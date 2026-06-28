package com.logicpuzzles.slitherlink

import kotlin.math.roundToInt
import kotlin.random.Random

// clues[r][c] = -1 (no clue) or 0..3
data class SlitherlinkPuzzle(
    val rows: Int,
    val cols: Int,
    val clues: Array<IntArray>,
    val solutionHEdges: Array<BooleanArray>? = null,
    val solutionVEdges: Array<BooleanArray>? = null
)

object SlitherlinkPuzzles {

    private fun build(rows: Int, cols: Int, difficulty: Int, index: Int): SlitherlinkPuzzle {
        val random = Random(50_000 + difficulty * 1_013 + index * 193)
        val fillRatio = when (difficulty) {
            0 -> 0.34f
            1 -> 0.42f
            2 -> 0.50f
            3 -> 0.56f
            else -> 0.62f
        }
        val inside = randomShape(rows, cols, (rows * cols * fillRatio).roundToInt(), random)

        val hEdges = Array(rows + 1) { BooleanArray(cols) }
        val vEdges = Array(rows) { BooleanArray(cols + 1) }

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (!inside[r][c]) continue
                if (r == 0 || !inside[r - 1][c]) hEdges[r][c] = true
                if (r == rows - 1 || !inside[r + 1][c]) hEdges[r + 1][c] = true
                if (c == 0 || !inside[r][c - 1]) vEdges[r][c] = true
                if (c == cols - 1 || !inside[r][c + 1]) vEdges[r][c + 1] = true
            }
        }

        val clues = Array(rows) { r ->
            IntArray(cols) { c ->
                edgeCount(hEdges, vEdges, r, c)
            }
        }
        hideClues(clues, difficulty, random)
        return SlitherlinkPuzzle(
            rows,
            cols,
            clues,
            solutionHEdges = hEdges.map { it.copyOf() }.toTypedArray(),
            solutionVEdges = vEdges.map { it.copyOf() }.toTypedArray()
        )
    }

    private fun randomShape(
        rows: Int,
        cols: Int,
        targetCells: Int,
        random: Random
    ): Array<BooleanArray> {
        val inside = Array(rows) { BooleanArray(cols) }
        val startR = rows / 2 + random.nextInt(-1, 2).coerceIn(-rows / 2, rows / 2)
        val startC = cols / 2 + random.nextInt(-1, 2).coerceIn(-cols / 2, cols / 2)
        inside[startR.coerceIn(0, rows - 1)][startC.coerceIn(0, cols - 1)] = true
        var count = 1

        while (count < targetCells.coerceIn(4, rows * cols - 1)) {
            val frontier = ArrayList<Pair<Int, Int>>()
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if (inside[r][c]) continue
                    if (ORTHOGONAL.any { (dr, dc) ->
                            val nr = r + dr
                            val nc = c + dc
                            nr in 0 until rows && nc in 0 until cols && inside[nr][nc]
                        }
                    ) {
                        frontier.add(r to c)
                    }
                }
            }
            if (frontier.isEmpty()) break
            val next = frontier[random.nextInt(frontier.size)]
            inside[next.first][next.second] = true
            count++
        }

        fillHoles(inside)
        return inside
    }

    private fun fillHoles(inside: Array<BooleanArray>) {
        val rows = inside.size
        val cols = inside[0].size
        val outside = Array(rows) { BooleanArray(cols) }
        val q = ArrayDeque<Pair<Int, Int>>()

        fun addOutside(r: Int, c: Int) {
            if (r !in 0 until rows || c !in 0 until cols || inside[r][c] || outside[r][c]) return
            outside[r][c] = true
            q.add(r to c)
        }

        for (r in 0 until rows) {
            addOutside(r, 0)
            addOutside(r, cols - 1)
        }
        for (c in 0 until cols) {
            addOutside(0, c)
            addOutside(rows - 1, c)
        }

        while (q.isNotEmpty()) {
            val (r, c) = q.removeFirst()
            for ((dr, dc) in ORTHOGONAL) {
                addOutside(r + dr, c + dc)
            }
        }

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (!inside[r][c] && !outside[r][c]) {
                    inside[r][c] = true
                }
            }
        }
    }

    private fun edgeCount(
        hEdges: Array<BooleanArray>,
        vEdges: Array<BooleanArray>,
        r: Int,
        c: Int
    ): Int {
        var count = 0
        if (hEdges[r][c]) count++
        if (hEdges[r + 1][c]) count++
        if (vEdges[r][c]) count++
        if (vEdges[r][c + 1]) count++
        return count
    }

    private fun hideClues(clues: Array<IntArray>, difficulty: Int, random: Random) {
        val rows = clues.size
        val cols = clues[0].size
        val keepRatio = when (difficulty) {
            0 -> 0.82f
            1 -> 0.70f
            2 -> 0.60f
            3 -> 0.52f
            else -> 0.44f
        }
        val cells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until rows) for (c in 0 until cols) cells.add(r to c)
        cells.shuffle(random)
        val keepTarget = (rows * cols * keepRatio).roundToInt().coerceIn(rows + cols, rows * cols)
        val keep = cells.take(keepTarget).toMutableSet()
        for (r in 0 until rows) keep.add(r to random.nextInt(cols))
        for (c in 0 until cols) keep.add(random.nextInt(rows) to c)

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if ((r to c) !in keep) clues[r][c] = -1
            }
        }
    }

    private val ORTHOGONAL = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

    fun get(difficulty: Int, index: Int): SlitherlinkPuzzle {
        val safeDifficulty = difficulty.coerceIn(0, 4)
        val maxIndex = when (safeDifficulty) { 0 -> 14; 1 -> 24; 2 -> 34; 3 -> 44; else -> 54 }
        val safeIndex = index.coerceIn(0, maxIndex)
        val size = when (safeDifficulty) {
            0 -> 5; 1 -> 6; 2 -> 7; 3 -> 8; else -> 9
        }
        return build(size, size, safeDifficulty, safeIndex)
    }
}
