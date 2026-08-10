package com.logicpuzzles.nurikabe

import com.logicpuzzles.utils.PuzzleBoardSpecs
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil
import kotlin.random.Random

// numbers[r][c] = 0 (no clue), >0 (island size clue)
data class NurikabePuzzle(
    val rows: Int,
    val cols: Int,
    val numbers: Array<IntArray>,
    val solutionShaded: Array<BooleanArray>? = null
)

object NurikabePuzzles {
    private val cache = ConcurrentHashMap<Pair<Int, Int>, NurikabePuzzle>()

    private fun build(side: Int, difficulty: Int, index: Int): NurikabePuzzle {
        val random = Random(40_000 + difficulty * 10_009 + index * 173)
        val (maxRows, maxCols) = when (difficulty) {
            0 -> 2 to 2
            1 -> 2 to 3
            2 -> 3 to 3
            3 -> 3 to 4
            else -> 3 to 5
        }
        val rowSegments = segmentLengths(side, maxRows, random)
        val colSegments = segmentLengths(side, maxCols, random)
        val islandIds = Array(side) { IntArray(side) }
        val rowRanges = segmentRanges(rowSegments)
        val colRanges = segmentRanges(colSegments)

        var nextId = 1
        var protectedId = 0
        val targetMax = maxRows * maxCols
        for (rows in rowRanges) {
            for (cols in colRanges) {
                val id = nextId++
                for (r in rows) for (c in cols) islandIds[r][c] = id
                if (protectedId == 0 && rows.count() * cols.count() == targetMax) protectedId = id
            }
        }

        addIrregularWaterFingers(islandIds, protectedId, difficulty, random)
        return puzzleFromSolution(islandIds, random)
    }

    private fun segmentLengths(side: Int, maxLength: Int, random: Random): IntArray {
        val segmentCount = ceil((side + 1).toFloat() / (maxLength + 1)).toInt()
        val activeCells = side - (segmentCount - 1)
        val lengths = IntArray(segmentCount) { 1 }
        var remaining = activeCells - segmentCount
        val order = lengths.indices.toMutableList()
        while (remaining > 0) {
            order.shuffle(random)
            for (i in order) {
                if (remaining == 0) break
                if (lengths[i] < maxLength) {
                    lengths[i]++
                    remaining--
                }
            }
        }
        if (lengths.none { it == maxLength }) {
            val donor = lengths.indices.firstOrNull { lengths[it] > 1 }
            val receiver = lengths.indices.maxBy { lengths[it] }
            if (donor != null && lengths[receiver] < maxLength) {
                lengths[donor]--
                lengths[receiver]++
            }
        }
        lengths.shuffle(random)
        return lengths
    }

    private fun segmentRanges(lengths: IntArray): List<IntRange> {
        var start = 0
        return lengths.map { length ->
            val range = start until start + length
            start += length + 1
            range
        }
    }

    private fun addIrregularWaterFingers(
        islandIds: Array<IntArray>,
        protectedId: Int,
        difficulty: Int,
        random: Random
    ) {
        if (difficulty == 0) return
        val rows = islandIds.size
        val cols = islandIds[0].size
        val candidates = buildList {
            for (r in 0 until rows) for (c in 0 until cols) {
                val id = islandIds[r][c]
                if (id == 0 || id == protectedId) continue
                val touchesWater = DIRECTIONS.any { (dr, dc) ->
                    val nr = r + dr
                    val nc = c + dc
                    nr in 0 until rows && nc in 0 until cols && islandIds[nr][nc] == 0
                }
                if (touchesWater) add(r to c)
            }
        }.shuffled(random)

        val target = (difficulty * rows / 3).coerceAtMost(candidates.size)
        var carved = 0
        for ((r, c) in candidates) {
            if (carved >= target) break
            val id = islandIds[r][c]
            islandIds[r][c] = 0
            if (islandConnected(islandIds, id) && noTwoByTwoWater(islandIds)) {
                carved++
            } else {
                islandIds[r][c] = id
            }
        }
    }

    private fun islandConnected(ids: Array<IntArray>, id: Int): Boolean {
        val cells = buildList {
            for (r in ids.indices) for (c in ids[r].indices) if (ids[r][c] == id) add(r to c)
        }
        if (cells.isEmpty()) return false
        val seen = HashSet<Pair<Int, Int>>()
        val queue = ArrayDeque<Pair<Int, Int>>()
        queue.add(cells.first())
        seen.add(cells.first())
        while (queue.isNotEmpty()) {
            val (r, c) = queue.removeFirst()
            for ((dr, dc) in DIRECTIONS) {
                val next = r + dr to c + dc
                if (next.first in ids.indices && next.second in ids[0].indices &&
                    ids[next.first][next.second] == id && seen.add(next)
                ) queue.add(next)
            }
        }
        return seen.size == cells.size
    }

    private fun noTwoByTwoWater(ids: Array<IntArray>): Boolean {
        for (r in 0 until ids.size - 1) for (c in 0 until ids[0].size - 1) {
            if (ids[r][c] == 0 && ids[r + 1][c] == 0 &&
                ids[r][c + 1] == 0 && ids[r + 1][c + 1] == 0
            ) return false
        }
        return true
    }

    private fun puzzleFromSolution(islandIds: Array<IntArray>, random: Random): NurikabePuzzle {
        val rows = islandIds.size
        val cols = islandIds[0].size
        val cells = LinkedHashMap<Int, MutableList<Pair<Int, Int>>>()
        for (r in 0 until rows) for (c in 0 until cols) {
            val id = islandIds[r][c]
            if (id > 0) cells.getOrPut(id) { mutableListOf() }.add(r to c)
        }

        val numbers = Array(rows) { IntArray(cols) }
        for (islandCells in cells.values) {
            val clue = islandCells[random.nextInt(islandCells.size)]
            numbers[clue.first][clue.second] = islandCells.size
        }
        val shaded = Array(rows) { r -> BooleanArray(cols) { c -> islandIds[r][c] == 0 } }
        return NurikabePuzzle(rows, cols, numbers, shaded)
    }

    private val DIRECTIONS = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

    fun get(difficulty: Int, index: Int): NurikabePuzzle {
        val safeDifficulty = difficulty.coerceIn(0, 4)
        val maxIndex = when (safeDifficulty) { 0 -> 14; 1 -> 24; 2 -> 34; 3 -> 44; else -> 54 }
        val safeIndex = index.coerceIn(0, maxIndex)
        return cache.getOrPut(safeDifficulty to safeIndex) {
            build(PuzzleBoardSpecs.largeSquareSide(safeDifficulty), safeDifficulty, safeIndex)
        }
    }
}
