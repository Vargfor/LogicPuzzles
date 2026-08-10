package com.logicpuzzles.mastermind

import kotlin.math.roundToInt

internal data class MastermindLayoutMetrics(
    val compactRows: Boolean,
    val stackControls: Boolean,
    val boardWidthPx: Int,
    val pegSizePx: Int,
    val pegMarginPx: Int,
    val feedbackWidthPx: Int,
    val pickerColumns: Int,
    val swatchSizePx: Int
) {
    companion object {
        fun calculate(
            availableWidthPx: Int,
            density: Float,
            positions: Int,
            colors: Int
        ): MastermindLayoutMetrics {
            val safeDensity = density.coerceAtLeast(0.1f)
            fun dp(value: Int): Int = (value * safeDensity).roundToInt()

            val boardWidth = minOf(availableWidthPx, dp(720)).coerceAtLeast(dp(240))
            val widePegMargin = dp(3)
            val wideRequired = dp(16 + 28 + 104) + positions * (dp(44) + widePegMargin * 2)
            val compactRows = boardWidth < wideRequired
            val pegMargin = dp(if (compactRows) 2 else 3)
            val reservedWidth = dp(16 + 28) + positions * pegMargin * 2 +
                if (compactRows) 0 else dp(104)
            val pegSize = ((boardWidth - reservedWidth) / positions)
                .coerceIn(dp(24), dp(44))

            val stackControls = availableWidthPx < dp(600)
            val pickerColumns = if (colors > 6) 5 else colors
            val pickerWidth = if (stackControls) {
                availableWidthPx - dp(16)
            } else {
                availableWidthPx - dp(16 + 176 + 8)
            }.coerceAtLeast(dp(140))
            val swatchSize = (pickerWidth / pickerColumns - dp(8)).coerceIn(dp(28), dp(44))

            return MastermindLayoutMetrics(
                compactRows = compactRows,
                stackControls = stackControls,
                boardWidthPx = boardWidth,
                pegSizePx = pegSize,
                pegMarginPx = pegMargin,
                feedbackWidthPx = dp(104),
                pickerColumns = pickerColumns,
                swatchSizePx = swatchSize
            )
        }
    }
}
