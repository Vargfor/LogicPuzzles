package com.logicpuzzles.hidato

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

    private fun build(rows: Int, cols: Int, difficulty: Int, index: Int): HidatoPuzzle {
        val random = Random(30_000 + difficulty * 997 + index * 131)
        val path = randomPath(rows, cols, random)
        val maxNumber = path.size
        val initial = Array(rows) { IntArray(cols) }

        val revealCount = when (difficulty) {
            0 -> (maxNumber * 0.62f).roundToInt()
            1 -> (maxNumber * 0.44f).roundToInt()
            2 -> (maxNumber * 0.34f).roundToInt()
            else -> (maxNumber * 0.28f).roundToInt()
        }.coerceIn(4, maxNumber)

        val solution = Array(rows) { IntArray(cols) }
        for ((position, cell) in path.withIndex()) {
            solution[cell.first][cell.second] = position + 1
        }

        for (number in anchorPattern(maxNumber, revealCount, random)) {
            val cell = path[number - 1]
            initial[cell.first][cell.second] = number
        }
        return HidatoPuzzle(rows, cols, initial, maxNumber, solution)
    }

    private fun randomPath(rows: Int, cols: Int, random: Random): List<Pair<Int, Int>> {
        repeat(80) { attempt ->
            val attemptRandom = Random(random.nextInt() + attempt * 7_919)
            val visited = Array(rows) { BooleanArray(cols) }
            val path = ArrayList<Pair<Int, Int>>(rows * cols)
            val start = attemptRandom.nextInt(rows) to attemptRandom.nextInt(cols)

            fun onwardCount(r: Int, c: Int): Int {
                var count = 0
                for (dr in -1..1) for (dc in -1..1) {
                    if (dr == 0 && dc == 0) continue
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in 0 until rows && nc in 0 until cols && !visited[nr][nc]) {
                        count++
                    }
                }
                return count
            }

            fun dfs(r: Int, c: Int): Boolean {
                visited[r][c] = true
                path.add(r to c)
                if (path.size == rows * cols) return true

                val candidates = ArrayList<Pair<Pair<Int, Int>, Int>>()
                for (dr in -1..1) for (dc in -1..1) {
                    if (dr == 0 && dc == 0) continue
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in 0 until rows && nc in 0 until cols && !visited[nr][nc]) {
                        candidates.add((nr to nc) to attemptRandom.nextInt(1_000))
                    }
                }
                candidates.sortWith(
                    compareBy<Pair<Pair<Int, Int>, Int>> { onwardCount(it.first.first, it.first.second) }
                        .thenBy { it.second }
                )

                for ((cell, _) in candidates) {
                    if (dfs(cell.first, cell.second)) return true
                }

                path.removeAt(path.lastIndex)
                visited[r][c] = false
                return false
            }

            if (dfs(start.first, start.second)) return path
        }

        return snakeFallback(rows, cols)
    }

    private fun anchorPattern(maxNumber: Int, revealCount: Int, random: Random): Set<Int> {
        val anchors = linkedSetOf(1, maxNumber)
        val interiorCount = revealCount - anchors.size
        val spacing = maxNumber.toFloat() / (interiorCount + 1)
        for (i in 1..interiorCount) {
            val center = (i * spacing).roundToInt().coerceIn(2, maxNumber - 1)
            val jitter = random.nextInt(-2, 3)
            anchors.add((center + jitter).coerceIn(2, maxNumber - 1))
        }

        val remaining = (2 until maxNumber).toMutableList()
        remaining.shuffle(random)
        for (number in remaining) {
            if (anchors.size >= revealCount) break
            anchors.add(number)
        }
        return anchors
    }

    private fun snakeFallback(rows: Int, cols: Int): List<Pair<Int, Int>> {
        val path = ArrayList<Pair<Int, Int>>(rows * cols)
        for (r in 0 until rows) {
            val range = if (r % 2 == 0) 0 until cols else cols - 1 downTo 0
            for (c in range) path.add(r to c)
        }
        return path
    }

    fun get(difficulty: Int, index: Int): HidatoPuzzle {
        val safeDifficulty = difficulty.coerceIn(0, 3)
        val safeIndex = index.coerceIn(0, 14)
        val size = when (safeDifficulty) {
            0 -> 3
            1 -> 4
            else -> 5
        }
        return build(size, size, safeDifficulty, safeIndex)
    }
}
