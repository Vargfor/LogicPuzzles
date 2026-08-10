package com.logicpuzzles.kakuro

import kotlin.math.abs
import kotlin.random.Random

sealed class KCell {
    object Void : KCell()
    object Black : KCell()
    data class Clue(val downSum: Int = 0, val rightSum: Int = 0) : KCell() {
        init {
            require(downSum in 0..45) { "Kakuro down clue must be in 1..45, or 0 when absent" }
            require(rightSum in 0..45) { "Kakuro right clue must be in 1..45, or 0 when absent" }
        }
    }
    object White : KCell()
}

data class KakuroPuzzle(
    val grid: Array<Array<KCell>>,
    val initial: Array<IntArray>? = null,
    val solution: Array<IntArray>? = null
) {
    val rows: Int get() = grid.size
    val cols: Int get() = grid[0].size
    fun initialAt(r: Int, c: Int): Int = initial?.getOrNull(r)?.getOrNull(c) ?: 0
    fun solutionAt(r: Int, c: Int): Int = solution?.getOrNull(r)?.getOrNull(c) ?: 0
}

object KakuroPuzzles {
    private val LEVEL_COUNTS = intArrayOf(15, 25, 35, 45, 55)
    private val BOARD_SIDES = intArrayOf(7, 9, 11, 13, 15)
    private val MINIMUM_WHITES = intArrayOf(16, 24, 34, 52, 76)
    private val MAXIMUM_WHITES = intArrayOf(24, 38, 54, 74, 96)

    private const val MAX_GENERATION_ATTEMPTS = 2_000
    private const val MAX_SOLUTION_SEARCH_NODES = 250_000
    private const val MAXIMUM_HELPERS = 5

    private val KNOWN_GOOD_ATTEMPTS = arrayOf(
        intArrayOf(5, 1, 1, 1, 2, 0, 2, 0, 0, 0, 0, 0, 3, 0, 1),
        intArrayOf(2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 0, 2, 0, 0, 0, 1, 0, 0, 0, 0, 2, 0),
        intArrayOf(4, 1, 0, 4, 2, 0, 0, 1, 1, 2, 0, 2, 2, 1, 0, 1, 4, 0, 0, 0, 4, 0, 12, 2, 0, 1, 1, 1, 0, 0, 0, 2, 2, 2, 0),
        intArrayOf(4, 2, 8, 17, 3, 15, 8, 3, 15, 4, 24, 7, 18, 70, 2, 10, 0, 1, 12, 4, 14, 4, 9, 0, 13, 9, 4, 38, 3, 1, 4, 2, 10, 0, 10, 0, 5, 18, 22, 3, 3, 1, 7, 2, 5),
        intArrayOf(131, 97, 16, 260, 288, 277, 67, 238, 342, 241, 356, 12, 86, 50, 61, 41, 110, 15, 56, 655, 83, 23, 68, 8, 93, 46, 35, 17, 267, 5, 113, 168, 161, 151, 45, 219, 27, 86, 68, 106, 172, 51, 5, 146, 54, 154, 23, 824, 7, 11, 49, 12, 149, 75, 66)
    )

    private val DIRECTIONS = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
    private val caches = Array(LEVEL_COUNTS.size) { difficulty ->
        arrayOfNulls<KakuroPuzzle>(LEVEL_COUNTS[difficulty])
    }
    private val acceptedAttempts = Array(LEVEL_COUNTS.size) { difficulty ->
        IntArray(LEVEL_COUNTS[difficulty]) { -1 }
    }
    private val generationDiagnostics = HashMap<String, Int>()

    @Synchronized
    fun get(difficulty: Int, index: Int): KakuroPuzzle {
        val safeDifficulty = difficulty.coerceIn(0, LEVEL_COUNTS.lastIndex)
        val safeIndex = index.coerceIn(0, LEVEL_COUNTS[safeDifficulty] - 1)
        return caches[safeDifficulty][safeIndex]
            ?: generatePuzzle(safeDifficulty, safeIndex).also {
                caches[safeDifficulty][safeIndex] = it
            }
    }

    private fun generatePuzzle(difficulty: Int, index: Int): KakuroPuzzle {
        generationDiagnostics.clear()
        val knownAttempt = KNOWN_GOOD_ATTEMPTS[difficulty][index]
        buildCandidate(difficulty, mixedSeed(difficulty, index, knownAttempt))?.let { puzzle ->
            acceptedAttempts[difficulty][index] = knownAttempt
            return puzzle
        }

        for (attempt in 0 until MAX_GENERATION_ATTEMPTS) {
            if (attempt == knownAttempt) continue
            val seed = mixedSeed(difficulty, index, attempt)
            buildCandidate(difficulty, seed)?.let { puzzle ->
                acceptedAttempts[difficulty][index] = attempt
                return puzzle
            }
        }
        error("Unable to generate unique Kakuro puzzle d=$difficulty i=$index $generationDiagnostics")
    }

    @Synchronized
    internal fun usedKnownGoodAttemptForTesting(difficulty: Int, index: Int): Boolean =
        acceptedAttempts[difficulty][index] == KNOWN_GOOD_ATTEMPTS[difficulty][index]

    private fun buildCandidate(difficulty: Int, seed: Int): KakuroPuzzle? {
        val random = Random(seed)
        val side = BOARD_SIDES[difficulty]
        val white = generateClassicMask(side, difficulty, random)
        if (!isStrongTopology(white, difficulty)) return reject("topology")

        val solution = generateSolution(white, random) ?: return reject("solution")
        val grid = deriveGrid(white, solution)
        if (!hasRequiredClues(grid, side)) return reject("clues")

        val puzzle = KakuroPuzzle(grid = grid, solution = solution)
        val initial = strategicStarterGrid(
            puzzle = puzzle,
            minimumCount = difficultyStarterCount(difficulty),
            seed = seed
        ) ?: return reject("helpers")
        return puzzle.copy(initial = initial)
    }

    private fun reject(reason: String): KakuroPuzzle? {
        generationDiagnostics[reason] = generationDiagnostics.getOrDefault(reason, 0) + 1
        return null
    }

    private fun generateClassicMask(
        side: Int,
        difficulty: Int,
        random: Random
    ): Array<BooleanArray> {
        val moduleSide = (side - 1) / 2
        val modules = Array(moduleSide) { BooleanArray(moduleSide) { true } }
        val minimumModules = (MINIMUM_WHITES[difficulty] + 3) / 4
        val maximumModules = MAXIMUM_WHITES[difficulty] / 4
        val targetModules = random.nextInt(minimumModules, maximumModules + 1)
        var moduleCount = moduleSide * moduleSide

        while (moduleCount > targetModules) {
            val candidates = ArrayList<Pair<Pair<Int, Int>, Int>>()
            for (r in modules.indices) {
                for (c in modules[r].indices) {
                    if (!modules[r][c]) continue
                    if (modules[r].count { it } <= 1) continue
                    if (modules.indices.count { row -> modules[row][c] } <= 1) continue

                    val horizontalLength = moduleRunLength(modules, r, c, 0, 1)
                    val verticalLength = moduleRunLength(modules, r, c, 1, 0)
                    modules[r][c] = false
                    val valid = whiteCellsConnected(
                        modules,
                        firstWhiteCell(modules) ?: (r to c)
                    )
                    modules[r][c] = true
                    if (!valid) continue

                    var score = random.nextInt(1_000)
                    if (horizontalLength > 4) score += 12_000
                    if (verticalLength > 4) score += 12_000
                    val blocked = modules.indices.flatMap { row ->
                        modules[row].indices.mapNotNull { col ->
                            if (!modules[row][col]) row to col else null
                        }
                    }
                    if (blocked.isNotEmpty()) {
                        score += blocked.minOf { (row, col) ->
                            abs(r - row) + abs(c - col)
                        } * 500
                    }
                    candidates.add((r to c) to score)
                }
            }

            val selected = candidates.maxByOrNull { it.second }?.first ?: break
            modules[selected.first][selected.second] = false
            moduleCount--
        }

        val white = Array(side) { BooleanArray(side) }
        for (moduleRow in modules.indices) {
            for (moduleCol in modules[moduleRow].indices) {
                if (!modules[moduleRow][moduleCol]) continue
                val top = 1 + moduleRow * 2
                val left = 1 + moduleCol * 2
                for (dr in 0..1) {
                    for (dc in 0..1) white[top + dr][left + dc] = true
                }
            }
        }
        varyCellPattern(white, difficulty, random)
        return white
    }

    private fun varyCellPattern(
        white: Array<BooleanArray>,
        difficulty: Int,
        random: Random
    ) {
        val side = white.size
        val targetChanges = side / 2 + random.nextInt(side / 2 + 1)
        var changes = 0
        var attempts = 0
        while (changes < targetChanges && attempts < side * side * 6) {
            attempts++
            val r = random.nextInt(1, side)
            val c = random.nextInt(1, side)
            white[r][c] = !white[r][c]

            val count = white.sumOf { row -> row.count { it } }
            val valid = count in MINIMUM_WHITES[difficulty]..MAXIMUM_WHITES[difficulty] &&
                allRunsHaveValidLength(white) &&
                (1 until side).all { row -> (1 until side).any { col -> white[row][col] } } &&
                (1 until side).all { col -> (1 until side).any { row -> white[row][col] } } &&
                whiteCellsConnected(white, firstWhiteCell(white) ?: (r to c))
            if (valid) {
                changes++
            } else {
                white[r][c] = !white[r][c]
            }
        }
    }

    private fun firstWhiteCell(white: Array<BooleanArray>): Pair<Int, Int>? {
        for (r in white.indices) {
            for (c in white[r].indices) {
                if (white[r][c]) return r to c
            }
        }
        return null
    }

    private fun moduleRunLength(
        modules: Array<BooleanArray>,
        row: Int,
        col: Int,
        dr: Int,
        dc: Int
    ): Int {
        var length = 1
        var r = row - dr
        var c = col - dc
        while (r in modules.indices && c in modules[r].indices && modules[r][c]) {
            length++
            r -= dr
            c -= dc
        }
        r = row + dr
        c = col + dc
        while (r in modules.indices && c in modules[r].indices && modules[r][c]) {
            length++
            r += dr
            c += dc
        }
        return length
    }

    private fun isStrongTopology(white: Array<BooleanArray>, difficulty: Int): Boolean {
        val side = BOARD_SIDES[difficulty]
        if (white.size != side || white.any { it.size != side }) return false
        if (white[0].any { it }) return false
        if (white.any { row -> row[0] }) return false

        val cells = ArrayList<Pair<Int, Int>>()
        for (r in white.indices) {
            for (c in white[r].indices) {
                if (white[r][c]) cells.add(r to c)
            }
        }
        if (cells.size !in MINIMUM_WHITES[difficulty]..MAXIMUM_WHITES[difficulty]) return false
        if (!allRunsHaveValidLength(white)) return false
        if (!whiteCellsConnected(white, cells.firstOrNull() ?: return false)) return false
        if ((1 until side).any { r -> (1 until side).none { c -> white[r][c] } }) return false
        if ((1 until side).any { c -> (1 until side).none { r -> white[r][c] } }) return false
        if (diagonalConcentration(cells, mainDiagonal = true) > 55) return false
        if (diagonalConcentration(cells, mainDiagonal = false) > 55) return false

        var horizontalRuns = 0
        var verticalRuns = 0
        for (r in 1 until side) {
            for (c in 1 until side) {
                if (white[r][c] && !white[r][c - 1]) horizontalRuns++
                if (white[r][c] && !white[r - 1][c]) verticalRuns++
            }
        }
        return horizontalRuns >= side - 1 && verticalRuns >= side - 1
    }

    private fun diagonalConcentration(
        cells: List<Pair<Int, Int>>,
        mainDiagonal: Boolean
    ): Int {
        val keys = cells.map { (r, c) -> if (mainDiagonal) r - c else r + c }
        var maximum = 0
        for (center in keys.minOrNull()!!..keys.maxOrNull()!!) {
            maximum = maxOf(maximum, keys.count { abs(it - center) <= 1 })
        }
        return maximum * 100 / cells.size
    }

    private fun allRunsHaveValidLength(white: Array<BooleanArray>): Boolean {
        fun validLine(length: Int): Boolean = length == 0 || length in 2..9

        for (r in white.indices) {
            var length = 0
            for (c in white[r].indices) {
                if (white[r][c]) {
                    length++
                } else {
                    if (!validLine(length)) return false
                    length = 0
                }
            }
            if (!validLine(length)) return false
        }
        for (c in white[0].indices) {
            var length = 0
            for (r in white.indices) {
                if (white[r][c]) {
                    length++
                } else {
                    if (!validLine(length)) return false
                    length = 0
                }
            }
            if (!validLine(length)) return false
        }
        return true
    }

    private fun whiteCellsConnected(white: Array<BooleanArray>, start: Pair<Int, Int>): Boolean {
        val seen = HashSet<Pair<Int, Int>>()
        val queue = ArrayDeque<Pair<Int, Int>>()
        seen.add(start)
        queue.add(start)
        while (queue.isNotEmpty()) {
            val (r, c) = queue.removeFirst()
            for ((dr, dc) in DIRECTIONS) {
                val nextRow = r + dr
                val nextCol = c + dc
                val next = nextRow to nextCol
                if (nextRow in white.indices &&
                    nextCol in white[nextRow].indices &&
                    white[nextRow][nextCol] &&
                    seen.add(next)
                ) {
                    queue.add(next)
                }
            }
        }
        return seen.size == white.sumOf { row -> row.count { it } }
    }

    private fun generateSolution(
        white: Array<BooleanArray>,
        random: Random
    ): Array<IntArray>? {
        val rows = white.size
        val cols = white[0].size
        val cellRuns = Array(rows) { Array(cols) { IntArray(2) { -1 } } }
        val runLengths = ArrayList<Int>()

        for (r in 0 until rows) {
            var c = 0
            while (c < cols) {
                if (!white[r][c]) {
                    c++
                    continue
                }
                val start = c
                while (c < cols && white[r][c]) c++
                val runIndex = runLengths.size
                runLengths.add(c - start)
                for (runCol in start until c) cellRuns[r][runCol][0] = runIndex
            }
        }
        for (c in 0 until cols) {
            var r = 0
            while (r < rows) {
                if (!white[r][c]) {
                    r++
                    continue
                }
                val start = r
                while (r < rows && white[r][c]) r++
                val runIndex = runLengths.size
                runLengths.add(r - start)
                for (runRow in start until r) cellRuns[runRow][c][1] = runIndex
            }
        }

        val domains = runLengths.map { length ->
            KakuroSolver.preferredCombinationMasks(length, maximumCombinationsForSum = 2)
        }
        if (domains.any { it.isEmpty() }) return null

        val cells = ArrayList<Pair<Int, Int>>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (white[r][c]) cells.add(r to c)
            }
        }
        val scanOrder = cells.shuffled(random)
        val solution = Array(rows) { IntArray(cols) }
        val usedDigits = IntArray(runLengths.size)
        var searchNodes = 0

        fun availableDigits(runIndex: Int): Int {
            val used = usedDigits[runIndex]
            var available = 0
            for (domain in domains[runIndex]) {
                if (domain and used == used) available = available or domain
            }
            return available and used.inv()
        }

        fun fill(remaining: Int): Boolean {
            if (remaining == 0) return true
            searchNodes++
            if (searchNodes > MAX_SOLUTION_SEARCH_NODES) return false

            var bestRow = -1
            var bestCol = -1
            var bestMask = 0
            var bestCount = 10
            for ((r, c) in scanOrder) {
                if (solution[r][c] != 0) continue
                val horizontal = cellRuns[r][c][0]
                val vertical = cellRuns[r][c][1]
                if (horizontal < 0 || vertical < 0) return false
                val candidateMask = availableDigits(horizontal) and availableDigits(vertical)
                val candidateCount = Integer.bitCount(candidateMask)
                if (candidateCount == 0) return false
                if (candidateCount < bestCount) {
                    bestRow = r
                    bestCol = c
                    bestMask = candidateMask
                    bestCount = candidateCount
                    if (candidateCount == 1) break
                }
            }

            val candidates = (1..9)
                .filter { bestMask and (1 shl it) != 0 }
                .shuffled(random)
            for (value in candidates) {
                val mask = 1 shl value
                val horizontal = cellRuns[bestRow][bestCol][0]
                val vertical = cellRuns[bestRow][bestCol][1]
                solution[bestRow][bestCol] = value
                usedDigits[horizontal] = usedDigits[horizontal] or mask
                usedDigits[vertical] = usedDigits[vertical] or mask
                if (fill(remaining - 1)) return true
                usedDigits[horizontal] = usedDigits[horizontal] and mask.inv()
                usedDigits[vertical] = usedDigits[vertical] and mask.inv()
                solution[bestRow][bestCol] = 0
            }
            return false
        }

        return if (fill(cells.size)) solution else null
    }

    private fun deriveGrid(
        white: Array<BooleanArray>,
        solution: Array<IntArray>
    ): Array<Array<KCell>> =
        Array(white.size) { r ->
            Array<KCell>(white[r].size) { c ->
                if (white[r][c]) {
                    KCell.White
                } else {
                    val right = runSum(r, c + 1, 0, 1, white, solution)
                    val down = runSum(r + 1, c, 1, 0, white, solution)
                    if (right.length >= 2 || down.length >= 2) {
                        KCell.Clue(
                            downSum = if (down.length >= 2) down.sum else 0,
                            rightSum = if (right.length >= 2) right.sum else 0
                        )
                    } else {
                        KCell.Black
                    }
                }
            }
        }

    private fun runSum(
        startRow: Int,
        startCol: Int,
        dr: Int,
        dc: Int,
        white: Array<BooleanArray>,
        solution: Array<IntArray>
    ): RunSum {
        var r = startRow
        var c = startCol
        var length = 0
        var sum = 0
        while (r in white.indices && c in white[r].indices && white[r][c]) {
            length++
            sum += solution[r][c]
            r += dr
            c += dc
        }
        return RunSum(length, sum)
    }

    private fun hasRequiredClues(grid: Array<Array<KCell>>, side: Int): Boolean {
        var clueCells = 0
        var internalClues = 0
        var dualClues = 0
        var twoDigitClues = 0
        for (r in grid.indices) {
            for (c in grid[r].indices) {
                val cell = grid[r][c]
                if (cell !is KCell.Clue) continue
                clueCells++
                if (r > 0 && c > 0) internalClues++
                if (cell.downSum > 0 && cell.rightSum > 0) dualClues++
                for (sum in intArrayOf(cell.downSum, cell.rightSum)) {
                    if (sum !in 0..45) return false
                    if (sum >= 10) twoDigitClues++
                }
            }
        }
        return clueCells >= side &&
            internalClues >= maxOf(2, side / 3) &&
            dualClues > 0 &&
            twoDigitClues > 0
    }

    private fun strategicStarterGrid(
        puzzle: KakuroPuzzle,
        minimumCount: Int,
        seed: Int
    ): Array<IntArray>? {
        val solution = puzzle.solution ?: return null
        val initial = Array(solution.size) { IntArray(solution[0].size) }
        val available = ArrayList<Pair<Int, Int>>()
        for (r in solution.indices) {
            for (c in solution[r].indices) {
                if (solution[r][c] > 0) available.add(r to c)
            }
        }
        if (available.isEmpty()) return null

        val selected = ArrayList<Pair<Int, Int>>()
        while (selected.size < MAXIMUM_HELPERS) {
            val alternatives = KakuroSolver.findSolutions(
                puzzle = puzzle,
                initial = initial,
                limit = 8,
                maxSearchNodes = 100_000
            )
            if (alternatives.size == 1) break
            if (alternatives.size < 2) return null

            val disagreementCounts = available
                .filter { it !in selected }
                .associateWith { (r, c) ->
                    alternatives.count { candidate -> candidate[r][c] != solution[r][c] }
                }
            val maximumDisagreement = disagreementCounts.values.maxOrNull() ?: return null
            val ambiguityCells = disagreementCounts.filterValues { count ->
                count == maximumDisagreement && count > 0
            }.keys.toList()
            if (ambiguityCells.isEmpty()) return null
            val helper = pickSpreadCell(ambiguityCells, selected, seed)
            selected.add(helper)
            initial[helper.first][helper.second] = solution[helper.first][helper.second]
        }

        while (selected.size < minimumCount && selected.size < available.size) {
            val next = pickSpreadCell(available.filter { it !in selected }, selected, seed)
            selected.add(next)
            initial[next.first][next.second] = solution[next.first][next.second]
        }
        return if (KakuroSolver.countSolutions(
                puzzle = puzzle,
                initial = initial,
                limit = 2,
                maxSearchNodes = 250_000
            ) == 1
        ) initial else null
    }

    private fun pickSpreadCell(
        candidates: List<Pair<Int, Int>>,
        selected: List<Pair<Int, Int>>,
        seed: Int
    ): Pair<Int, Int> =
        candidates.maxWithOrNull(
            compareBy<Pair<Int, Int>>(
                { candidate ->
                    if (selected.isEmpty()) 0 else selected.minOf { chosen ->
                        abs(candidate.first - chosen.first) + abs(candidate.second - chosen.second)
                    }
                },
                { candidate ->
                    (candidate.first * 73 + candidate.second * 37 + seed).positiveMod(10_007)
                }
            )
        ) ?: error("No Kakuro helper candidate")

    private fun difficultyStarterCount(difficulty: Int): Int =
        when (difficulty.coerceIn(0, 4)) {
            0 -> 5
            1 -> 4
            2 -> 3
            3 -> 2
            else -> 1
        }

    private fun mixedSeed(difficulty: Int, index: Int, attempt: Int): Int {
        var value = difficulty * 0x1f123bb5 + index * 0x6d2b79f5 + attempt * 0x45d9f3b
        value = value xor (value ushr 16)
        value *= 0x45d9f3b
        value = value xor (value ushr 16)
        return value
    }

    private fun Int.positiveMod(mod: Int): Int {
        val value = this % mod
        return if (value < 0) value + mod else value
    }

    private data class RunSum(val length: Int, val sum: Int)
}
