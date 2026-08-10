package com.logicpuzzles.utils

internal object PuzzleBoardSpecs {
    private val largeSquareSides = intArrayOf(7, 9, 11, 13, 15)
    private val slitherlinkSides = intArrayOf(5, 9, 11, 13, 15)
    private val hidatoSides = intArrayOf(5, 7, 9, 13, 15)

    fun largeSquareSide(difficulty: Int): Int =
        largeSquareSides[difficulty.coerceIn(0, largeSquareSides.lastIndex)]

    fun slitherlinkSide(difficulty: Int): Int =
        slitherlinkSides[difficulty.coerceIn(0, slitherlinkSides.lastIndex)]

    fun hidatoSide(difficulty: Int): Int =
        hidatoSides[difficulty.coerceIn(0, hidatoSides.lastIndex)]

    fun futoshikiSpec(difficulty: Int): FutoshikiGridSpec = when (difficulty.coerceIn(0, 4)) {
        0 -> FutoshikiGridSpec(size = 4, boxRows = 2, boxCols = 2)
        1 -> FutoshikiGridSpec(size = 6, boxRows = 2, boxCols = 3)
        2 -> FutoshikiGridSpec(size = 9, boxRows = 3, boxCols = 3)
        3 -> FutoshikiGridSpec(size = 12, boxRows = 3, boxCols = 4)
        else -> FutoshikiGridSpec(size = 16, boxRows = 4, boxCols = 4)
    }
}

internal data class FutoshikiGridSpec(
    val size: Int,
    val boxRows: Int,
    val boxCols: Int
)
