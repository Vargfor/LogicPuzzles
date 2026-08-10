package com.logicpuzzles.utils

import kotlin.math.abs

internal class DragPaintSession<T> {
    private val visited = HashSet<T>()
    private var active = false
    private var targetState = false

    fun begin(targetState: Boolean) {
        active = true
        this.targetState = targetState
        visited.clear()
    }

    fun visit(target: T): Boolean? {
        if (!active || !visited.add(target)) return null
        return targetState
    }

    fun end() {
        active = false
        visited.clear()
    }
}

internal fun interpolatedGridCells(
    fromRow: Int,
    fromCol: Int,
    toRow: Int,
    toCol: Int
): List<Pair<Int, Int>> {
    val cells = ArrayList<Pair<Int, Int>>()
    var row = fromRow
    var col = fromCol
    val deltaCol = abs(toCol - fromCol)
    val stepCol = if (fromCol < toCol) 1 else -1
    val deltaRow = -abs(toRow - fromRow)
    val stepRow = if (fromRow < toRow) 1 else -1
    var error = deltaCol + deltaRow

    while (true) {
        cells.add(row to col)
        if (row == toRow && col == toCol) break
        val doubled = error * 2
        if (doubled >= deltaRow) {
            error += deltaRow
            col += stepCol
        }
        if (doubled <= deltaCol) {
            error += deltaCol
            row += stepRow
        }
    }
    return cells
}

internal data class SlitherlinkEdgeTarget(
    val horizontal: Boolean,
    val row: Int,
    val col: Int
)

internal object SlitherlinkEdgeHitTester {
    fun hit(
        x: Float,
        y: Float,
        rows: Int,
        cols: Int,
        cellSize: Int,
        edgeSize: Int,
        padding: Int
    ): SlitherlinkEdgeTarget? {
        val gridCol = axisIndex(x - padding, cols, cellSize, edgeSize) ?: return null
        val gridRow = axisIndex(y - padding, rows, cellSize, edgeSize) ?: return null
        return when {
            gridRow % 2 == 0 && gridCol % 2 == 1 ->
                SlitherlinkEdgeTarget(horizontal = true, row = gridRow / 2, col = gridCol / 2)
            gridRow % 2 == 1 && gridCol % 2 == 0 ->
                SlitherlinkEdgeTarget(horizontal = false, row = gridRow / 2, col = gridCol / 2)
            else -> null
        }
    }

    private fun axisIndex(position: Float, cells: Int, cellSize: Int, edgeSize: Int): Int? {
        if (position < 0f) return null
        val period = edgeSize + cellSize
        val block = (position / period).toInt()
        val offset = position - block * period
        return when {
            block < cells && offset < edgeSize -> block * 2
            block < cells -> block * 2 + 1
            block == cells && offset < edgeSize -> cells * 2
            else -> null
        }
    }
}
