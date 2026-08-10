package com.logicpuzzles.kakuro

/** Counts Kakuro solutions up to [limit] using intersecting run constraints. */
object KakuroSolver {

    private val preferredCombinationCache = HashMap<Pair<Int, Int>, IntArray>()

    @Synchronized
    fun uniqueCombinationMasks(length: Int): IntArray =
        preferredCombinationMasks(length, 1)

    @Synchronized
    fun preferredCombinationMasks(length: Int, maximumCombinationsForSum: Int): IntArray =
        preferredCombinationCache.getOrPut(length to maximumCombinationsForSum) {
            if (length !in 2..9) return@getOrPut IntArray(0)
            val masksBySum = Array(46) { ArrayList<Int>() }
            fun collect(nextDigit: Int, digitsLeft: Int, sum: Int, mask: Int) {
                if (digitsLeft == 0) {
                    masksBySum[sum].add(mask)
                    return
                }
                for (digit in nextDigit..9) {
                    collect(digit + 1, digitsLeft - 1, sum + digit, mask or (1 shl digit))
                }
            }
            collect(1, length, 0, 0)
            masksBySum
                .asSequence()
                .filter { it.size in 1..maximumCombinationsForSum }
                .flatten()
                .distinct()
                .toList()
                .toIntArray()
        }.copyOf()

    fun countSolutions(
        puzzle: KakuroPuzzle,
        initial: Array<IntArray>? = null,
        limit: Int = 2,
        maxSearchNodes: Int = Int.MAX_VALUE,
        solutionCollector: MutableList<Array<IntArray>>? = null
    ): Int {
        val rows = puzzle.rows
        val cols = puzzle.cols
        val grid = Array(rows) { r ->
            IntArray(cols) { c -> initial?.getOrNull(r)?.getOrNull(c) ?: 0 }
        }

        data class Run(val cells: List<Pair<Int, Int>>, val sum: Int)

        val runs = ArrayList<Run>()
        val cellRuns = Array(rows) { Array(cols) { ArrayList<Int>(2) } }

        fun addRun(startRow: Int, startCol: Int, dr: Int, dc: Int, sum: Int): Boolean {
            if (sum !in 1..45) return false
            val cells = ArrayList<Pair<Int, Int>>()
            var r = startRow
            var c = startCol
            while (r in 0 until rows && c in 0 until cols && puzzle.grid[r][c] is KCell.White) {
                cells.add(r to c)
                r += dr
                c += dc
            }
            if (cells.size !in 2..9) return false
            val runIndex = runs.size
            runs.add(Run(cells, sum))
            for ((cellRow, cellCol) in cells) cellRuns[cellRow][cellCol].add(runIndex)
            return true
        }

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cell = puzzle.grid[r][c]
                if (cell is KCell.Clue) {
                    if (cell.rightSum > 0 && !addRun(r, c + 1, 0, 1, cell.rightSum)) return 0
                    if (cell.downSum > 0 && !addRun(r + 1, c, 1, 0, cell.downSum)) return 0
                }
            }
        }
        if (runs.isEmpty()) return 0

        val whiteCells = ArrayList<Pair<Int, Int>>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val value = grid[r][c]
                if (puzzle.grid[r][c] is KCell.White) {
                    if (value !in 0..9 || cellRuns[r][c].size != 2) return 0
                    whiteCells.add(r to c)
                } else if (value != 0) {
                    return 0
                }
            }
        }

        val combinationCache = HashMap<Pair<Int, Int>, IntArray>()
        fun combinations(length: Int, sum: Int): IntArray =
            combinationCache.getOrPut(length to sum) {
                val result = ArrayList<Int>()
                fun collect(nextDigit: Int, digitsLeft: Int, sumLeft: Int, mask: Int) {
                    if (digitsLeft == 0) {
                        if (sumLeft == 0) result.add(mask)
                        return
                    }
                    for (digit in nextDigit..9) {
                        if (digit > sumLeft) break
                        collect(digit + 1, digitsLeft - 1, sumLeft - digit, mask or (1 shl digit))
                    }
                }
                collect(1, length, sum, 0)
                result.toIntArray()
            }

        val combinationsByRun = runs.map { run -> combinations(run.cells.size, run.sum) }
        if (combinationsByRun.any { it.isEmpty() }) return 0

        val openCells = IntArray(runs.size) { runs[it].cells.size }
        val usedDigits = IntArray(runs.size)

        fun runCanContain(runIndex: Int, requiredMask: Int): Boolean =
            combinationsByRun[runIndex].any { combination -> combination and requiredMask == requiredMask }

        fun availableDigits(runIndex: Int): Int {
            val used = usedDigits[runIndex]
            var available = 0
            for (combination in combinationsByRun[runIndex]) {
                if (combination and used == used) available = available or combination
            }
            return available and used.inv()
        }

        fun applyValue(r: Int, c: Int, value: Int): Boolean {
            val mask = 1 shl value
            for (runIndex in cellRuns[r][c]) {
                if (usedDigits[runIndex] and mask != 0) return false
                if (!runCanContain(runIndex, usedDigits[runIndex] or mask)) return false
            }
            for (runIndex in cellRuns[r][c]) {
                usedDigits[runIndex] = usedDigits[runIndex] or mask
                openCells[runIndex]--
            }
            grid[r][c] = value
            return true
        }

        fun removeValue(r: Int, c: Int, value: Int) {
            val mask = (1 shl value).inv()
            for (runIndex in cellRuns[r][c]) {
                usedDigits[runIndex] = usedDigits[runIndex] and mask
                openCells[runIndex]++
            }
            grid[r][c] = 0
        }

        for ((r, c) in whiteCells) {
            val value = grid[r][c]
            if (value > 0) {
                grid[r][c] = 0
                if (!applyValue(r, c, value)) return 0
            }
        }
        if (runs.indices.any { !runCanContain(it, usedDigits[it]) }) return 0

        var count = 0
        val solutionLimit = limit.coerceAtLeast(1)
        var searchNodes = 0

        fun search(): Boolean {
            if (count >= solutionLimit) return true
            searchNodes++
            if (searchNodes > maxSearchNodes) {
                count = solutionLimit
                return true
            }

            var bestRow = -1
            var bestCol = -1
            var bestCandidates = IntArray(0)

            for ((r, c) in whiteCells) {
                if (grid[r][c] != 0) continue
                var candidateMask = ALL_DIGITS_MASK
                for (runIndex in cellRuns[r][c]) {
                    candidateMask = candidateMask and availableDigits(runIndex)
                }
                if (candidateMask == 0) return false
                val candidateCount = Integer.bitCount(candidateMask)
                if (bestRow == -1 || candidateCount < bestCandidates.size) {
                    bestRow = r
                    bestCol = c
                    bestCandidates = (1..9).filter { candidateMask and (1 shl it) != 0 }.toIntArray()
                    if (bestCandidates.size == 1) break
                }
            }

            if (bestRow == -1) {
                if (runs.indices.all { runIndex ->
                        openCells[runIndex] == 0 && combinationsByRun[runIndex].any {
                            it == usedDigits[runIndex]
                        }
                    }
                ) {
                    count++
                    solutionCollector?.add(Array(grid.size) { grid[it].copyOf() })
                }
                return count >= solutionLimit
            }

            for (value in bestCandidates) {
                if (!applyValue(bestRow, bestCol, value)) continue
                if (search()) {
                    removeValue(bestRow, bestCol, value)
                    return true
                }
                removeValue(bestRow, bestCol, value)
            }
            return false
        }

        search()
        return count
    }

    fun findSolutions(
        puzzle: KakuroPuzzle,
        initial: Array<IntArray>? = null,
        limit: Int = 2,
        maxSearchNodes: Int = Int.MAX_VALUE
    ): List<Array<IntArray>> {
        val solutions = ArrayList<Array<IntArray>>()
        val count = countSolutions(
            puzzle = puzzle,
            initial = initial,
            limit = limit,
            maxSearchNodes = maxSearchNodes,
            solutionCollector = solutions
        )
        return if (count == solutions.size) solutions else emptyList()
    }

    private const val ALL_DIGITS_MASK = 0b11_1111_1110
}
