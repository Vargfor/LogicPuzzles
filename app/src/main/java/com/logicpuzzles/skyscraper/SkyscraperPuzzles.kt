package com.logicpuzzles.skyscraper

import com.logicpuzzles.utils.PuzzleBoardSpecs
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil
import kotlin.random.Random

data class SkyscraperPuzzle(
    val size: Int,
    val initial: Array<IntArray>,
    val cluesTop: IntArray,
    val cluesBottom: IntArray,
    val cluesLeft: IntArray,
    val cluesRight: IntArray,
    val solution: Array<IntArray>? = null,
    val maxHeight: Int = size,
    val emptyLotsPerLine: Int = 0
)

object SkyscraperPuzzles {
    private val cache = ConcurrentHashMap<Pair<Int, Int>, SkyscraperPuzzle>()

    private fun build(size: Int, difficulty: Int, index: Int): SkyscraperPuzzle {
        val random = Random(60_000 + difficulty * 10_009 + index * 211 + size * 31)
        val solution = latinSolution(size, random)
        val maxHeight = if (difficulty == 0) size else size + 1
        val emptyLots = intArrayOf(0, 1, 1, 2, 3)[difficulty]

        if (difficulty > 0) {
            val permutationStep = coprimeSteps(size)[random.nextInt(coprimeSteps(size).size)]
            val shift = random.nextInt(size)
            val tallestCols = IntArray(size) { r -> positiveMod(r * permutationStep + shift, size) }
            for (r in 0 until size) solution[r][tallestCols[r]] = maxHeight

            val offsets = (1 until size).toList().shuffled(random).take(emptyLots)
            for (offset in offsets) {
                for (r in 0 until size) {
                    solution[r][positiveMod(tallestCols[r] + offset, size)] = 0
                }
            }
        }

        val cluesTop = IntArray(size) { c -> visibility(IntArray(size) { r -> solution[r][c] }) }
        val cluesBottom = IntArray(size) { c -> visibility(IntArray(size) { r -> solution[size - 1 - r][c] }) }
        val cluesLeft = IntArray(size) { r -> visibility(solution[r]) }
        val cluesRight = IntArray(size) { r -> visibility(solution[r].reversedArray()) }
        hideClues(cluesTop, cluesBottom, cluesLeft, cluesRight, difficulty, random)

        val givenRatio = floatArrayOf(0.42f, 0.28f, 0.18f, 0.10f, 0.06f)[difficulty]
        val nonEmpty = buildList {
            for (r in 0 until size) for (c in 0 until size) {
                if (solution[r][c] > 0) add(r * size + c)
            }
        }.shuffled(random)
        val givenCount = ceil(nonEmpty.size * givenRatio).toInt()
        val initial = Array(size) { IntArray(size) }
        for (position in nonEmpty.take(givenCount)) {
            initial[position / size][position % size] = solution[position / size][position % size]
        }

        return SkyscraperPuzzle(
            size = size,
            initial = initial,
            cluesTop = cluesTop,
            cluesBottom = cluesBottom,
            cluesLeft = cluesLeft,
            cluesRight = cluesRight,
            solution = solution,
            maxHeight = maxHeight,
            emptyLotsPerLine = emptyLots
        )
    }

    private fun latinSolution(size: Int, random: Random): Array<IntArray> {
        val stepOptions = coprimeSteps(size)
        val step = stepOptions[random.nextInt(stepOptions.size)]
        val rowOrder = (0 until size).toList().shuffled(random)
        val colOrder = (0 until size).toList().shuffled(random)
        val symbols = (1..size).toList().shuffled(random)
        return Array(size) { r ->
            IntArray(size) { c -> symbols[(rowOrder[r] * step + colOrder[c]) % size] }
        }
    }

    private fun coprimeSteps(size: Int): IntArray =
        (1 until size).filter { gcd(it, size) == 1 }.toIntArray()

    private fun gcd(a: Int, b: Int): Int {
        var x = a
        var y = b
        while (y != 0) {
            val next = x % y
            x = y
            y = next
        }
        return x
    }

    private fun hideClues(
        top: IntArray,
        bottom: IntArray,
        left: IntArray,
        right: IntArray,
        difficulty: Int,
        random: Random
    ) {
        val keepRatio = floatArrayOf(1f, 0.88f, 0.75f, 0.62f, 0.50f)[difficulty]
        for (side in arrayOf(top, bottom, left, right)) {
            val keep = ceil(side.size * keepRatio).toInt().coerceAtLeast(1)
            val visible = side.indices.toList().shuffled(random).take(keep).toSet()
            for (i in side.indices) if (i !in visible) side[i] = 0
        }
    }

    private fun visibility(line: IntArray): Int {
        var maxSeen = 0
        var count = 0
        for (height in line) {
            if (height > maxSeen) {
                maxSeen = height
                count++
            }
        }
        return count
    }

    private fun positiveMod(value: Int, modulus: Int): Int = ((value % modulus) + modulus) % modulus

    fun get(difficulty: Int, index: Int): SkyscraperPuzzle {
        val safeDifficulty = difficulty.coerceIn(0, 4)
        val maxIndex = when (safeDifficulty) { 0 -> 14; 1 -> 24; 2 -> 34; 3 -> 44; else -> 54 }
        val safeIndex = index.coerceIn(0, maxIndex)
        return cache.getOrPut(safeDifficulty to safeIndex) {
            build(PuzzleBoardSpecs.largeSquareSide(safeDifficulty), safeDifficulty, safeIndex)
        }
    }
}
