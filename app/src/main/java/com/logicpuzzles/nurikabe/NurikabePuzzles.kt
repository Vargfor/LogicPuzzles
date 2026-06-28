package com.logicpuzzles.nurikabe

import kotlin.random.Random

// numbers[r][c] = 0 (no clue), >0 (island size clue)
data class NurikabePuzzle(
    val rows: Int,
    val cols: Int,
    val numbers: Array<IntArray>,
    val solutionShaded: Array<BooleanArray>? = null
)

object NurikabePuzzles {

    private fun build(rows: Int, cols: Int, difficulty: Int, index: Int): NurikabePuzzle {
        val random = Random(40_000 + difficulty * 1_009 + index * 173)
        val targetMaxSize = targetMaxIslandSize(difficulty, index)
        repeat(500) {
            val islands = seedSingleCellIslands(rows, cols, difficulty, index, random)
            growIslands(islands, difficulty, index, random)
            growByReplacingBlockers(islands, difficulty, index, random)
            removeSmallIslands(islands, difficulty, random)
            ensureIslandAtLeast(islands, targetMaxSize, random)
            if (isPlayableSolution(islands) && matchesIslandSizeProfile(islands, difficulty, index)) {
                return puzzleFromSolution(islands, random)
            }
        }

        val fallback = seedSingleCellIslands(rows, cols, difficulty, index, Random(75_000 + rows * 31 + index))
        growIslands(fallback, difficulty, index, Random(91_000 + cols * 43 + index))
        growByReplacingBlockers(fallback, difficulty, index, Random(93_000 + rows * 47 + index))
        ensureIslandAtLeast(fallback, targetMaxSize, Random(97_000 + rows * 59 + index))
        return puzzleFromSolution(fallback, Random(101_000 + index))
    }

    private fun targetMaxIslandSize(difficulty: Int, index: Int): Int = when (difficulty) {
        0 -> if (index >= 10) 3 else 2
        1 -> if (index >= 10) 4 else 3
        2 -> if (index >= 10) 6 else 5
        3 -> if (Random(122_000 + index * 97).nextBoolean()) 8 else 9
        else -> when {
            index >= 25 -> 12
            index >= 10 -> 11
            else        -> 10
        }
    }

    private fun matchesIslandSizeProfile(islandIds: Array<IntArray>, difficulty: Int, index: Int): Boolean {
        val sizes = islandSizes(islandIds).values
        val maxSize = targetMaxIslandSize(difficulty, index)
        return sizes.isNotEmpty() &&
            sizes.all { it in 1..maxSize } &&
            sizes.any { it == maxSize }
    }

    private fun seedSingleCellIslands(
        rows: Int,
        cols: Int,
        difficulty: Int,
        index: Int,
        random: Random
    ): Array<IntArray> {
        val islandIds = Array(rows) { IntArray(cols) }
        val uncoveredWindows = mutableSetOf<Pair<Int, Int>>()
        for (r in 0 until rows - 1) {
            for (c in 0 until cols - 1) uncoveredWindows.add(r to c)
        }

        var nextId = 1
        while (uncoveredWindows.isNotEmpty()) {
            val candidates = mutableListOf<Pair<Pair<Int, Int>, Int>>()
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if (!canPlaceNewIsland(islandIds, r, c)) continue
                    val score = coveredWindows(r, c, rows, cols).count { it in uncoveredWindows }
                    if (score > 0) candidates.add((r to c) to score)
                }
            }
            if (candidates.isEmpty()) return islandIds
            val bestScore = candidates.maxOf { it.second }
            val eligible = candidates.filter { it.second >= (bestScore - 1).coerceAtLeast(1) }
            val chosen = eligible[random.nextInt(eligible.size)].first
            islandIds[chosen.first][chosen.second] = nextId++
            uncoveredWindows.removeAll(coveredWindows(chosen.first, chosen.second, rows, cols).toSet())
        }

        val targetClues = (rows * cols * when (difficulty) {
            0 -> 0.38f
            1 -> 0.32f
            2 -> 0.27f
            3 -> 0.23f
            else -> 0.19f
        }).toInt() + (index % 3) - 1

        val cells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until rows) for (c in 0 until cols) cells.add(r to c)
        cells.shuffle(random)

        var clueCount = islandSizes(islandIds).size
        for ((r, c) in cells) {
            if (clueCount >= targetClues) break
            if (canPlaceNewIsland(islandIds, r, c)) {
                islandIds[r][c] = nextId++
                clueCount++
            }
        }
        return islandIds
    }

    private fun growIslands(islandIds: Array<IntArray>, difficulty: Int, index: Int, random: Random) {
        val rows = islandIds.size
        val cols = islandIds[0].size
        val maxSize = targetMaxIslandSize(difficulty, index)
        val targetExtraCells = (rows * cols * when (difficulty) {
            0 -> if (index >= 10) 0.14f else 0.10f
            1 -> if (index >= 10) 0.22f else 0.16f
            2 -> if (index >= 10) 0.34f else 0.28f
            3 -> 0.42f
            else -> 0.48f
        }).toInt()

        var added = 0
        repeat(rows * cols * 4) {
            if (added >= targetExtraCells) return
            val sizes = islandSizes(islandIds)
            val candidates = mutableListOf<GrowCandidate>()
            for ((id, size) in sizes) {
                if (size >= maxSize) continue
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        if (canGrowInto(islandIds, id, r, c)) candidates.add(GrowCandidate(id, r, c))
                    }
                }
            }
            if (candidates.isEmpty()) return
            val candidate = candidates[random.nextInt(candidates.size)]
            islandIds[candidate.r][candidate.c] = candidate.id
            if (blackCellsConnected(islandIds)) {
                added++
            } else {
                islandIds[candidate.r][candidate.c] = 0
            }
        }
    }

    private fun growByReplacingBlockers(islandIds: Array<IntArray>, difficulty: Int, index: Int, random: Random) {
        val rows = islandIds.size
        val cols = islandIds[0].size
        val maxSize = targetMaxIslandSize(difficulty, index)
        val steps = when (difficulty) {
            0 -> if (index >= 10) rows else 2
            1 -> if (index >= 10) rows + cols else rows
            2 -> if (index >= 10) rows + cols + 8 else rows + cols + 4
            3 -> rows * cols
            else -> rows * cols + rows * cols / 2
        }

        repeat(steps) {
            val sizes = islandSizes(islandIds)
            val cellsByIsland = islandCells(islandIds)
            val candidates = mutableListOf<GrowCandidate>()
            for ((id, cells) in cellsByIsland) {
                if ((sizes[id] ?: 0) >= maxSize) continue
                for ((r, c) in cells) {
                    for ((dr, dc) in DIRECTIONS) {
                        val nr = r + dr
                        val nc = c + dc
                        if (nr in 0 until rows && nc in 0 until cols && islandIds[nr][nc] == 0) {
                            candidates.add(GrowCandidate(id, nr, nc))
                        }
                    }
                }
            }
            candidates.shuffle(random)

            for (candidate in candidates) {
                val currentSizes = islandSizes(islandIds)
                if ((currentSizes[candidate.id] ?: 0) >= maxSize) continue
                val blockers = adjacentIslandIds(islandIds, candidate.r, candidate.c)
                    .filter { it != candidate.id }
                    .filter { (currentSizes[it] ?: 0) <= difficulty + 1 }
                    .toSet()
                val removed = blockers.associateWith { id -> islandCells(islandIds).getValue(id) }

                for ((_, cells) in removed) {
                    for ((r, c) in cells) islandIds[r][c] = 0
                }
                islandIds[candidate.r][candidate.c] = candidate.id
                if (isPlayableSolution(islandIds)) return@repeat

                islandIds[candidate.r][candidate.c] = 0
                for ((id, cells) in removed) {
                    for ((r, c) in cells) islandIds[r][c] = id
                }
            }
        }
    }

    private fun removeSmallIslands(islandIds: Array<IntArray>, difficulty: Int, random: Random) {
        val removals = when (difficulty) {
            0 -> 0; 1 -> 1; 2 -> 2; 3 -> 3; else -> 4
        }
        repeat(removals) {
            val singletons = islandCells(islandIds)
                .filter { (_, cells) -> cells.size == 1 }
                .map { it.value.first() }
                .shuffled(random)
            for ((r, c) in singletons) {
                val oldId = islandIds[r][c]
                islandIds[r][c] = 0
                if (noTwoByTwoShaded(islandIds) && blackCellsConnected(islandIds)) return@repeat
                islandIds[r][c] = oldId
            }
        }
    }

    private fun ensureIslandAtLeast(islandIds: Array<IntArray>, targetSize: Int, random: Random): Boolean {
        repeat(islandIds.size * islandIds[0].size * targetSize) {
            val sizes = islandSizes(islandIds)
            if (sizes.values.any { it >= targetSize }) return true

            val cellsByIsland = islandCells(islandIds)
            val candidates = mutableListOf<GrowCandidate>()
            val idsBySize = sizes.entries
                .filter { it.value < targetSize }
                .sortedByDescending { it.value }
                .map { it.key }
            for (id in idsBySize) {
                for ((r, c) in cellsByIsland[id].orEmpty()) {
                    for ((dr, dc) in DIRECTIONS) {
                        val nr = r + dr
                        val nc = c + dc
                        if (nr in islandIds.indices && nc in islandIds[0].indices && islandIds[nr][nc] == 0) {
                            candidates.add(GrowCandidate(id, nr, nc))
                        }
                    }
                }
            }
            candidates.shuffle(random)

            var grew = false
            for (candidate in candidates) {
                val snapshot = copyIslandIds(islandIds)
                val currentSizes = islandSizes(islandIds)
                val blockers = adjacentIslandIds(islandIds, candidate.r, candidate.c)
                    .filter { it != candidate.id }
                    .filter { (currentSizes[it] ?: 0) <= targetSize }
                    .toSet()
                val cellsByBlocker = islandCells(islandIds)
                for (blocker in blockers) {
                    for ((r, c) in cellsByBlocker[blocker].orEmpty()) islandIds[r][c] = 0
                }
                islandIds[candidate.r][candidate.c] = candidate.id
                val newSize = islandSizes(islandIds)[candidate.id] ?: 0
                if (newSize <= targetSize && isPlayableSolution(islandIds)) {
                    grew = true
                    break
                }
                restoreIslandIds(islandIds, snapshot)
            }
            if (!grew) return false
        }
        return islandSizes(islandIds).values.any { it >= targetSize }
    }

    private fun copyIslandIds(islandIds: Array<IntArray>): Array<IntArray> =
        Array(islandIds.size) { r -> islandIds[r].copyOf() }

    private fun restoreIslandIds(target: Array<IntArray>, source: Array<IntArray>) {
        for (r in target.indices) source[r].copyInto(target[r])
    }

    private data class GrowCandidate(val id: Int, val r: Int, val c: Int)

    private fun canPlaceNewIsland(islandIds: Array<IntArray>, r: Int, c: Int): Boolean {
        if (islandIds[r][c] != 0) return false
        for ((dr, dc) in DIRECTIONS) {
            val nr = r + dr
            val nc = c + dc
            if (nr in islandIds.indices && nc in islandIds[0].indices && islandIds[nr][nc] != 0) return false
        }
        return true
    }

    private fun canGrowInto(islandIds: Array<IntArray>, id: Int, r: Int, c: Int): Boolean {
        if (islandIds[r][c] != 0) return false
        var touchesIsland = false
        for ((dr, dc) in DIRECTIONS) {
            val nr = r + dr
            val nc = c + dc
            if (nr !in islandIds.indices || nc !in islandIds[0].indices) continue
            val neighbor = islandIds[nr][nc]
            if (neighbor == id) touchesIsland = true
            if (neighbor != 0 && neighbor != id) return false
        }
        return touchesIsland
    }

    private fun adjacentIslandIds(islandIds: Array<IntArray>, r: Int, c: Int): Set<Int> {
        val ids = mutableSetOf<Int>()
        for ((dr, dc) in DIRECTIONS) {
            val nr = r + dr
            val nc = c + dc
            if (nr in islandIds.indices && nc in islandIds[0].indices && islandIds[nr][nc] > 0) {
                ids.add(islandIds[nr][nc])
            }
        }
        return ids
    }

    private fun coveredWindows(r: Int, c: Int, rows: Int, cols: Int): List<Pair<Int, Int>> {
        val windows = ArrayList<Pair<Int, Int>>(4)
        for (wr in r - 1..r) {
            for (wc in c - 1..c) {
                if (wr in 0 until rows - 1 && wc in 0 until cols - 1) windows.add(wr to wc)
            }
        }
        return windows
    }

    private fun puzzleFromSolution(islandIds: Array<IntArray>, random: Random): NurikabePuzzle {
        val rows = islandIds.size
        val cols = islandIds[0].size
        val numbers = Array(rows) { IntArray(cols) }
        for (cells in islandCells(islandIds).values) {
            val clueCell = cells[random.nextInt(cells.size)]
            numbers[clueCell.first][clueCell.second] = cells.size
        }
        val shaded = Array(rows) { r -> BooleanArray(cols) { c -> islandIds[r][c] == 0 } }
        return NurikabePuzzle(rows, cols, numbers, shaded)
    }

    private fun isPlayableSolution(islandIds: Array<IntArray>): Boolean {
        return islandSizes(islandIds).isNotEmpty() &&
            noAdjacentDifferentIslands(islandIds) &&
            noTwoByTwoShaded(islandIds) &&
            blackCellsConnected(islandIds)
    }

    private fun noAdjacentDifferentIslands(islandIds: Array<IntArray>): Boolean {
        val rows = islandIds.size
        val cols = islandIds[0].size
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val id = islandIds[r][c]
                if (id == 0) continue
                for ((dr, dc) in DIRECTIONS) {
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in 0 until rows && nc in 0 until cols) {
                        val neighbor = islandIds[nr][nc]
                        if (neighbor != 0 && neighbor != id) return false
                    }
                }
            }
        }
        return true
    }

    private fun noTwoByTwoShaded(islandIds: Array<IntArray>): Boolean {
        val rows = islandIds.size
        val cols = islandIds[0].size
        for (r in 0 until rows - 1) {
            for (c in 0 until cols - 1) {
                if (islandIds[r][c] == 0 &&
                    islandIds[r + 1][c] == 0 &&
                    islandIds[r][c + 1] == 0 &&
                    islandIds[r + 1][c + 1] == 0
                ) {
                    return false
                }
            }
        }
        return true
    }

    private fun blackCellsConnected(islandIds: Array<IntArray>): Boolean {
        val rows = islandIds.size
        val cols = islandIds[0].size
        var start: Pair<Int, Int>? = null
        var total = 0
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (islandIds[r][c] == 0) {
                    total++
                    if (start == null) start = r to c
                }
            }
        }
        val first = start ?: return false
        val seen = Array(rows) { BooleanArray(cols) }
        val q = ArrayDeque<Pair<Int, Int>>()
        q.add(first)
        seen[first.first][first.second] = true
        var reached = 0
        while (q.isNotEmpty()) {
            val (r, c) = q.removeFirst()
            reached++
            for ((dr, dc) in DIRECTIONS) {
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until rows && nc in 0 until cols && islandIds[nr][nc] == 0 && !seen[nr][nc]) {
                    seen[nr][nc] = true
                    q.add(nr to nc)
                }
            }
        }
        return reached == total
    }

    private fun islandSizes(islandIds: Array<IntArray>): Map<Int, Int> {
        val sizes = LinkedHashMap<Int, Int>()
        for (row in islandIds) {
            for (id in row) {
                if (id > 0) sizes[id] = (sizes[id] ?: 0) + 1
            }
        }
        return sizes
    }

    private fun islandCells(islandIds: Array<IntArray>): Map<Int, List<Pair<Int, Int>>> {
        val cells = LinkedHashMap<Int, MutableList<Pair<Int, Int>>>()
        for (r in islandIds.indices) {
            for (c in islandIds[r].indices) {
                val id = islandIds[r][c]
                if (id > 0) cells.getOrPut(id) { mutableListOf() }.add(r to c)
            }
        }
        return cells
    }

    private val DIRECTIONS = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
    private val cache = mutableMapOf<Pair<Int, Int>, NurikabePuzzle>()

    fun get(difficulty: Int, index: Int): NurikabePuzzle {
        val safeDifficulty = difficulty.coerceIn(0, 4)
        val maxIndex = when (safeDifficulty) { 0 -> 14; 1 -> 24; 2 -> 34; 3 -> 44; else -> 54 }
        val safeIndex = index.coerceIn(0, maxIndex)
        val size = when (safeDifficulty) {
            0 -> 5; 1 -> 6; 2 -> 7; 3 -> 8; else -> 9
        }
        return cache.getOrPut(safeDifficulty to safeIndex) {
            build(size, size, safeDifficulty, safeIndex)
        }
    }
}
