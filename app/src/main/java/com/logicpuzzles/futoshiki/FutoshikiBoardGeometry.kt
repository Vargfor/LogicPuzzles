package com.logicpuzzles.futoshiki

import kotlin.math.roundToInt

internal data class FutoshikiBoardGeometry(
    val cellSizePx: Int,
    val inequalitySizePx: Int,
    val fontSizeSp: Float,
    val gridStrokePx: Float,
    val regionStrokePx: Float
) {
    companion object {
        fun calculate(
            availablePx: Int,
            size: Int,
            zoom: Float,
            density: Float
        ): FutoshikiBoardGeometry {
            val safeDensity = density.coerceAtLeast(0.1f)
            val totalUnits = size + (size - 1) * INEQUALITY_RATIO
            val cellSize = (availablePx / totalUnits * zoom).roundToInt().coerceAtLeast(1)
            val inequalitySize = (cellSize * INEQUALITY_RATIO).roundToInt().coerceAtLeast(1)
            val cellSizeDp = cellSize / safeDensity

            return FutoshikiBoardGeometry(
                cellSizePx = cellSize,
                inequalitySizePx = inequalitySize,
                fontSizeSp = (cellSizeDp * FONT_RATIO).coerceAtLeast(1f),
                gridStrokePx = (safeDensity * GRID_STROKE_DP * zoom).coerceAtLeast(1f),
                regionStrokePx = (safeDensity * REGION_STROKE_DP * zoom).coerceAtLeast(1f)
            )
        }

        private const val INEQUALITY_RATIO = 0.32f
        private const val FONT_RATIO = 0.62f
        private const val GRID_STROKE_DP = 1f
        private const val REGION_STROKE_DP = 3f
    }
}
