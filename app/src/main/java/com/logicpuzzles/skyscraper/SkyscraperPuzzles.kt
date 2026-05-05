package com.logicpuzzles.skyscraper

data class SkyscraperPuzzle(
    val size: Int,
    val initial: Array<IntArray>,
    val cluesTop: IntArray,
    val cluesBottom: IntArray,
    val cluesLeft: IntArray,
    val cluesRight: IntArray
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
        val init = initial ?: Array(n) { IntArray(n) }
        val cT = IntArray(n) { c -> visibility(IntArray(n) { r -> solution[r][c] }) }
        val cB = IntArray(n) { c -> visibility(IntArray(n) { r -> solution[n - 1 - r][c] }) }
        val cL = IntArray(n) { r -> visibility(solution[r]) }
        val cR = IntArray(n) { r -> visibility(solution[r].reversedArray()) }
        return SkyscraperPuzzle(n, init, cT, cB, cL, cR)
    }

    private fun grid(rows: Array<IntArray>): Array<IntArray> = rows
    private fun row(vararg v: Int): IntArray = v

    // ----- Easy 4x4 -----
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
        build(grid(arrayOf(row(4,3,1,2), row(2,1,3,4), row(1,2,4,3), row(3,4,2,1))))
    )

    // ----- Medium 4x4 (with one or two pre-filled cells) -----
    private val MEDIUM = EASY  // reuse easy for now; visibility clues alone are typically enough

    // ----- Hard 5x5 -----
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
        build(grid(arrayOf(row(5,3,1,4,2), row(3,1,4,2,5), row(1,4,2,5,3), row(4,2,5,3,1), row(2,5,3,1,4))))
    )

    private val EXPERT = HARD  // reuse for now

    fun get(difficulty: Int, index: Int): SkyscraperPuzzle {
        val pool = when (difficulty) {
            0 -> EASY; 1 -> MEDIUM; 2 -> HARD; else -> EXPERT
        }
        return pool[index.coerceIn(0, pool.size - 1)]
    }
}
