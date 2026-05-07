package com.logicpuzzles.skyscraper

data class SkyscraperPuzzle(
    val size: Int,
    val initial: Array<IntArray>,
    val cluesTop: IntArray,
    val cluesBottom: IntArray,
    val cluesLeft: IntArray,
    val cluesRight: IntArray,
    val solution: Array<IntArray>? = null
)

object SkyscraperPuzzles {

    private fun visibility(line: IntArray): Int {
        var maxSeen = 0
        var count = 0
        for (h in line) {
            if (h > maxSeen) { count++; maxSeen = h }
        }
        return count
    }

    private fun build(solution: Array<IntArray>, initial: Array<IntArray>? = null): SkyscraperPuzzle {
        val n = solution.size
        val init = initial?.map { it.copyOf() }?.toTypedArray() ?: Array(n) { IntArray(n) }
        val cT = IntArray(n) { c -> visibility(IntArray(n) { r -> solution[r][c] }) }
        val cB = IntArray(n) { c -> visibility(IntArray(n) { r -> solution[n - 1 - r][c] }) }
        val cL = IntArray(n) { r -> visibility(solution[r]) }
        val cR = IntArray(n) { r -> visibility(solution[r].reversedArray()) }
        return SkyscraperPuzzle(n, init, cT, cB, cL, cR, solution.map { it.copyOf() }.toTypedArray())
    }

    private fun withDifficultyGivens(puzzle: SkyscraperPuzzle, difficulty: Int, index: Int): SkyscraperPuzzle {
        val solution = puzzle.solution ?: return puzzle
        return SkyscraperPuzzle(
            size = puzzle.size,
            initial = seededInitial(solution, givenCountFor(difficulty, puzzle.size), difficulty * 31 + index),
            cluesTop = puzzle.cluesTop.copyOf(),
            cluesBottom = puzzle.cluesBottom.copyOf(),
            cluesLeft = puzzle.cluesLeft.copyOf(),
            cluesRight = puzzle.cluesRight.copyOf(),
            solution = solution.map { it.copyOf() }.toTypedArray()
        )
    }

    private fun givenCountFor(difficulty: Int, size: Int): Int {
        val count = when (difficulty.coerceIn(0, 3)) {
            0 -> when (size) {
                4 -> 10
                5 -> 14
                else -> size * size / 2
            }
            1 -> when (size) {
                4 -> 5
                5 -> 7
                else -> size + 2
            }
            2 -> when (size) {
                5 -> 3
                6 -> 4
                else -> size / 2
            }
            else -> 0
        }
        return count.coerceIn(0, size * size)
    }

    private fun seededInitial(solution: Array<IntArray>, count: Int, seed: Int): Array<IntArray> {
        val size = solution.size
        val initial = Array(size) { IntArray(size) }
        if (count <= 0) return initial

        val selected = HashSet<Int>()
        var placed = 0

        fun addCell(r: Int, c: Int) {
            if (placed >= count) return
            val key = r * size + c
            if (selected.add(key)) {
                initial[r][c] = solution[r][c]
                placed++
            }
        }

        if (count >= size) {
            for (r in 0 until size) addCell(r, positiveMod(r * 2 + seed, size))
            for (c in 0 until size) addCell(positiveMod(c * 3 + seed + 1, size), c)
        }

        val cells = (0 until size * size).sortedWith(
            compareBy<Int> { seededCellScore(it, seed, size) }.thenBy { it }
        )
        for (cell in cells) {
            addCell(cell / size, cell % size)
            if (placed >= count) break
        }

        return initial
    }

    private fun positiveMod(value: Int, modulus: Int): Int = ((value % modulus) + modulus) % modulus

    private fun seededCellScore(cell: Int, seed: Int, size: Int): Int {
        val r = cell / size
        val c = cell % size
        val mixed = (seed.toLong() + 1L) * 1_103_515_245L +
            (r.toLong() + 1L) * 73_856_093L +
            (c.toLong() + 1L) * 19_349_663L
        return ((mixed xor (mixed ushr 32)) and 0x7fff_ffffL).toInt()
    }

    private fun grid(rows: Array<IntArray>): Array<IntArray> = rows
    private fun row(vararg v: Int): IntArray = v

    // EASY: levels 1-10 = 4x4, levels 11-15 = 5x5 bridge
    private val EASY = listOf(
        build(grid(arrayOf(row(2,1,4,3), row(4,3,2,1), row(1,4,3,2), row(3,2,1,4)))),
        build(grid(arrayOf(row(1,2,3,4), row(2,1,4,3), row(3,4,1,2), row(4,3,2,1)))),
        build(grid(arrayOf(row(3,1,4,2), row(4,2,3,1), row(1,3,2,4), row(2,4,1,3)))),
        build(grid(arrayOf(row(4,1,2,3), row(3,2,1,4), row(2,3,4,1), row(1,4,3,2)))),
        build(grid(arrayOf(row(2,4,1,3), row(3,1,4,2), row(1,3,2,4), row(4,2,3,1)))),
        build(grid(arrayOf(row(1,3,2,4), row(2,4,1,3), row(3,1,4,2), row(4,2,3,1)))),
        build(grid(arrayOf(row(4,2,3,1), row(1,3,2,4), row(2,4,1,3), row(3,1,4,2)))),
        build(grid(arrayOf(row(3,4,1,2), row(4,3,2,1), row(2,1,4,3), row(1,2,3,4)))),
        build(grid(arrayOf(row(2,3,4,1), row(4,1,2,3), row(1,2,3,4), row(3,4,1,2)))),
        build(grid(arrayOf(row(4,3,1,2), row(2,1,3,4), row(1,2,4,3), row(3,4,2,1)))),
        // 11-15: 5x5 bridge (+1 size step)
        build(grid(arrayOf(row(1,2,3,4,5), row(3,4,5,1,2), row(5,1,2,3,4), row(2,3,4,5,1), row(4,5,1,2,3)))),
        build(grid(arrayOf(row(1,3,5,4,2), row(3,5,4,2,1), row(5,4,2,1,3), row(4,2,1,3,5), row(2,1,3,5,4)))),
        build(grid(arrayOf(row(1,5,4,3,2), row(5,4,3,2,1), row(4,3,2,1,5), row(3,2,1,5,4), row(2,1,5,4,3)))),
        build(grid(arrayOf(row(2,5,3,4,1), row(5,3,4,1,2), row(3,4,1,2,5), row(4,1,2,5,3), row(1,2,5,3,4)))),
        build(grid(arrayOf(row(3,5,1,4,2), row(5,1,4,2,3), row(1,4,2,3,5), row(4,2,3,5,1), row(2,3,5,1,4))))
    )

    // MEDIUM: levels 1-10 = 4x4 (different grids from Easy), levels 11-15 = 5x5
    private val MEDIUM = listOf(
        build(grid(arrayOf(row(1,4,2,3), row(2,3,1,4), row(3,2,4,1), row(4,1,3,2)))),
        build(grid(arrayOf(row(2,3,4,1), row(1,4,3,2), row(4,1,2,3), row(3,2,1,4)))),
        build(grid(arrayOf(row(3,2,1,4), row(4,1,2,3), row(1,4,3,2), row(2,3,4,1)))),
        build(grid(arrayOf(row(4,3,2,1), row(3,4,1,2), row(2,1,4,3), row(1,2,3,4)))),
        build(grid(arrayOf(row(1,2,4,3), row(3,4,2,1), row(2,1,3,4), row(4,3,1,2)))),
        build(grid(arrayOf(row(2,1,3,4), row(4,3,1,2), row(1,2,4,3), row(3,4,2,1)))),
        build(grid(arrayOf(row(3,4,2,1), row(1,2,4,3), row(4,3,1,2), row(2,1,3,4)))),
        build(grid(arrayOf(row(4,1,3,2), row(2,3,1,4), row(3,2,4,1), row(1,4,2,3)))),
        build(grid(arrayOf(row(1,3,4,2), row(4,2,3,1), row(2,4,1,3), row(3,1,2,4)))),
        build(grid(arrayOf(row(2,4,3,1), row(3,1,4,2), row(4,2,1,3), row(1,3,2,4)))),
        // 11-15: 5x5
        build(grid(arrayOf(row(2,3,4,5,1), row(3,4,5,1,2), row(4,5,1,2,3), row(5,1,2,3,4), row(1,2,3,4,5)))),
        build(grid(arrayOf(row(4,1,5,3,2), row(1,5,3,2,4), row(5,3,2,4,1), row(3,2,4,1,5), row(2,4,1,5,3)))),
        build(grid(arrayOf(row(5,2,1,4,3), row(2,1,4,3,5), row(1,4,3,5,2), row(4,3,5,2,1), row(3,5,2,1,4)))),
        build(grid(arrayOf(row(1,5,2,4,3), row(5,2,4,3,1), row(2,4,3,1,5), row(4,3,1,5,2), row(3,1,5,2,4)))),
        build(grid(arrayOf(row(3,2,5,4,1), row(2,5,4,1,3), row(5,4,1,3,2), row(4,1,3,2,5), row(1,3,2,5,4))))
    )

    // HARD: levels 1-10 = 5x5, levels 11-15 = 6x6 bridge
    private val HARD = listOf(
        build(grid(arrayOf(row(1,2,3,4,5), row(2,3,4,5,1), row(3,4,5,1,2), row(4,5,1,2,3), row(5,1,2,3,4)))),
        build(grid(arrayOf(row(2,3,4,5,1), row(3,4,5,1,2), row(4,5,1,2,3), row(5,1,2,3,4), row(1,2,3,4,5)))),
        build(grid(arrayOf(row(5,4,3,2,1), row(4,3,2,1,5), row(3,2,1,5,4), row(2,1,5,4,3), row(1,5,4,3,2)))),
        build(grid(arrayOf(row(1,3,5,2,4), row(3,5,2,4,1), row(5,2,4,1,3), row(2,4,1,3,5), row(4,1,3,5,2)))),
        build(grid(arrayOf(row(2,4,1,3,5), row(4,1,3,5,2), row(1,3,5,2,4), row(3,5,2,4,1), row(5,2,4,1,3)))),
        build(grid(arrayOf(row(3,1,5,2,4), row(1,5,2,4,3), row(5,2,4,3,1), row(2,4,3,1,5), row(4,3,1,5,2)))),
        build(grid(arrayOf(row(4,2,5,3,1), row(2,5,3,1,4), row(5,3,1,4,2), row(3,1,4,2,5), row(1,4,2,5,3)))),
        build(grid(arrayOf(row(1,4,2,5,3), row(4,2,5,3,1), row(2,5,3,1,4), row(5,3,1,4,2), row(3,1,4,2,5)))),
        build(grid(arrayOf(row(2,1,3,5,4), row(1,3,5,4,2), row(3,5,4,2,1), row(5,4,2,1,3), row(4,2,1,3,5)))),
        build(grid(arrayOf(row(5,3,1,4,2), row(3,1,4,2,5), row(1,4,2,5,3), row(4,2,5,3,1), row(2,5,3,1,4)))),
        // 11-15: 6x6 bridge (+1 size step)
        // Each 6x6 grid is a cyclic left-rotation Latin square: row[i] = rotate(row[0], left by i)
        build(grid(arrayOf(row(1,2,3,4,5,6), row(2,3,4,5,6,1), row(3,4,5,6,1,2), row(4,5,6,1,2,3), row(5,6,1,2,3,4), row(6,1,2,3,4,5)))),
        build(grid(arrayOf(row(1,2,3,4,5,6), row(6,1,2,3,4,5), row(5,6,1,2,3,4), row(4,5,6,1,2,3), row(3,4,5,6,1,2), row(2,3,4,5,6,1)))),
        build(grid(arrayOf(row(1,3,5,2,4,6), row(3,5,2,4,6,1), row(5,2,4,6,1,3), row(2,4,6,1,3,5), row(4,6,1,3,5,2), row(6,1,3,5,2,4)))),
        build(grid(arrayOf(row(1,4,2,5,3,6), row(4,2,5,3,6,1), row(2,5,3,6,1,4), row(5,3,6,1,4,2), row(3,6,1,4,2,5), row(6,1,4,2,5,3)))),
        build(grid(arrayOf(row(2,5,4,1,6,3), row(5,4,1,6,3,2), row(4,1,6,3,2,5), row(1,6,3,2,5,4), row(6,3,2,5,4,1), row(3,2,5,4,1,6))))
    )

    // EXPERT: levels 1-10 = 5x5 (different from Hard), levels 11-15 = 6x6
    private val EXPERT = listOf(
        build(grid(arrayOf(row(1,3,5,4,2), row(3,5,4,2,1), row(5,4,2,1,3), row(4,2,1,3,5), row(2,1,3,5,4)))),
        build(grid(arrayOf(row(1,5,4,3,2), row(5,4,3,2,1), row(4,3,2,1,5), row(3,2,1,5,4), row(2,1,5,4,3)))),
        build(grid(arrayOf(row(2,5,3,4,1), row(5,3,4,1,2), row(3,4,1,2,5), row(4,1,2,5,3), row(1,2,5,3,4)))),
        build(grid(arrayOf(row(3,5,1,4,2), row(5,1,4,2,3), row(1,4,2,3,5), row(4,2,3,5,1), row(2,3,5,1,4)))),
        build(grid(arrayOf(row(4,5,2,3,1), row(5,2,3,1,4), row(2,3,1,4,5), row(3,1,4,5,2), row(1,4,5,2,3)))),
        build(grid(arrayOf(row(4,2,1,5,3), row(2,1,5,3,4), row(1,5,3,4,2), row(5,3,4,2,1), row(3,4,2,1,5)))),
        build(grid(arrayOf(row(5,1,3,4,2), row(1,3,4,2,5), row(3,4,2,5,1), row(4,2,5,1,3), row(2,5,1,3,4)))),
        build(grid(arrayOf(row(3,4,1,5,2), row(4,1,5,2,3), row(1,5,2,3,4), row(5,2,3,4,1), row(2,3,4,1,5)))),
        build(grid(arrayOf(row(5,2,4,3,1), row(2,4,3,1,5), row(4,3,1,5,2), row(3,1,5,2,4), row(1,5,2,4,3)))),
        build(grid(arrayOf(row(1,4,3,5,2), row(4,3,5,2,1), row(3,5,2,1,4), row(5,2,1,4,3), row(2,1,4,3,5)))),
        // 11-15: 6x6
        build(grid(arrayOf(row(3,6,4,1,5,2), row(6,4,1,5,2,3), row(4,1,5,2,3,6), row(1,5,2,3,6,4), row(5,2,3,6,4,1), row(2,3,6,4,1,5)))),
        build(grid(arrayOf(row(4,1,6,2,5,3), row(1,6,2,5,3,4), row(6,2,5,3,4,1), row(2,5,3,4,1,6), row(5,3,4,1,6,2), row(3,4,1,6,2,5)))),
        build(grid(arrayOf(row(5,2,6,3,1,4), row(2,6,3,1,4,5), row(6,3,1,4,5,2), row(3,1,4,5,2,6), row(1,4,5,2,6,3), row(4,5,2,6,3,1)))),
        build(grid(arrayOf(row(6,3,5,2,4,1), row(3,5,2,4,1,6), row(5,2,4,1,6,3), row(2,4,1,6,3,5), row(4,1,6,3,5,2), row(1,6,3,5,2,4)))),
        build(grid(arrayOf(row(4,6,1,5,3,2), row(6,1,5,3,2,4), row(1,5,3,2,4,6), row(5,3,2,4,6,1), row(3,2,4,6,1,5), row(2,4,6,1,5,3))))
    )

    fun get(difficulty: Int, index: Int): SkyscraperPuzzle {
        val pool = when (difficulty) {
            0 -> EASY; 1 -> MEDIUM; 2 -> HARD; else -> EXPERT
        }
        return withDifficultyGivens(pool[index.coerceIn(0, pool.size - 1)], difficulty, index)
    }
}
