package com.logicpuzzles

import com.logicpuzzles.futoshiki.FutoshikiBoardGeometry
import com.logicpuzzles.mastermind.MastermindLayoutMetrics
import com.logicpuzzles.mastermind.MastermindRules
import com.logicpuzzles.mastermind.MastermindScore
import com.logicpuzzles.utils.DragPaintSession
import com.logicpuzzles.utils.SlitherlinkEdgeHitTester
import com.logicpuzzles.utils.SlitherlinkEdgeTarget
import com.logicpuzzles.utils.interpolatedGridCells
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleInteractionRulesTest {
    @Test
    fun futoshikiZoomChangesBoardAndContentAsOneGeometry() {
        val overview = FutoshikiBoardGeometry.calculate(1_000, 16, 0.75f, 2.5f)
        val normal = FutoshikiBoardGeometry.calculate(1_000, 16, 1f, 2.5f)
        val enlarged = FutoshikiBoardGeometry.calculate(1_000, 16, 1.25f, 2.5f)

        assertTrue(overview.cellSizePx < normal.cellSizePx)
        assertTrue(normal.cellSizePx < enlarged.cellSizePx)
        assertTrue(overview.inequalitySizePx < normal.inequalitySizePx)
        assertTrue(normal.inequalitySizePx < enlarged.inequalitySizePx)
        assertTrue(overview.fontSizeSp < normal.fontSizeSp)
        assertTrue(normal.fontSizeSp < enlarged.fontSizeSp)
        assertEquals(normal.cellSizePx * 0.75f, overview.cellSizePx.toFloat(), 1.1f)
        assertEquals(normal.cellSizePx * 1.25f, enlarged.cellSizePx.toFloat(), 1.1f)
    }

    @Test
    fun mastermindLayoutAccountsForEveryPegMarginAndFeedbackBlock() {
        val density = 3f
        val phone = MastermindLayoutMetrics.calculate(
            availableWidthPx = (344 * density).toInt(),
            density = density,
            positions = 8,
            colors = 10
        )
        val phonePegLineWidth = (16 * density).toInt() + (28 * density).toInt() +
            8 * (phone.pegSizePx + phone.pegMarginPx * 2)

        assertTrue(phone.compactRows)
        assertTrue(phone.stackControls)
        assertTrue(phonePegLineWidth <= phone.boardWidthPx)
        assertTrue(
            phone.pickerColumns * (phone.swatchSizePx + (8 * density).toInt()) <=
                (344 * density).toInt()
        )

        val tablet = MastermindLayoutMetrics.calculate(
            availableWidthPx = (1_000 * 2f).toInt(),
            density = 2f,
            positions = 8,
            colors = 10
        )
        val tabletRowWidth = (16 * 2f).toInt() + (28 * 2f).toInt() + tablet.feedbackWidthPx +
            8 * (tablet.pegSizePx + tablet.pegMarginPx * 2)

        assertTrue(!tablet.compactRows)
        assertTrue(!tablet.stackControls)
        assertTrue(tabletRowWidth <= tablet.boardWidthPx)
    }

    @Test
    fun mastermindFeedbackHandlesDuplicateColorsWithoutDoubleCounting() {
        assertEquals(
            MastermindScore(exact = 1, misplaced = 2),
            MastermindRules.score(
                secret = listOf(0, 0, 1, 2),
                guess = listOf(0, 1, 0, 0)
            )
        )
        assertEquals(
            MastermindScore(exact = 4, misplaced = 0),
            MastermindRules.score(listOf(3, 2, 1, 0), listOf(3, 2, 1, 0))
        )
        assertEquals(
            MastermindScore(exact = 0, misplaced = 2),
            MastermindRules.score(listOf(0, 0, 1, 1), listOf(1, 2, 0, 2))
        )
    }

    @Test
    fun dragPaintingAppliesOneStateAndDoesNotRetoggleVisitedTargets() {
        val session = DragPaintSession<String>()
        session.begin(targetState = true)
        assertEquals(true, session.visit("a"))
        assertNull(session.visit("a"))
        assertEquals(true, session.visit("b"))
        session.end()
        assertNull(session.visit("c"))

        session.begin(targetState = false)
        assertEquals(false, session.visit("a"))
    }

    @Test
    fun dragInterpolationIncludesEveryCrossedCell() {
        assertEquals(
            listOf(0 to 0, 1 to 1, 2 to 2, 3 to 3),
            interpolatedGridCells(0, 0, 3, 3)
        )
        assertEquals(
            listOf(2 to 0, 2 to 1, 2 to 2, 2 to 3),
            interpolatedGridCells(2, 0, 2, 3)
        )
    }

    @Test
    fun slitherlinkHitTestingSeparatesEdgesFromCellsAndIntersections() {
        val horizontal = SlitherlinkEdgeHitTester.hit(
            x = 11f, y = 4f, rows = 2, cols = 2, cellSize = 10, edgeSize = 4, padding = 2
        )
        val vertical = SlitherlinkEdgeHitTester.hit(
            x = 4f, y = 11f, rows = 2, cols = 2, cellSize = 10, edgeSize = 4, padding = 2
        )
        assertEquals(SlitherlinkEdgeTarget(true, 0, 0), horizontal)
        assertEquals(SlitherlinkEdgeTarget(false, 0, 0), vertical)
        assertNull(
            SlitherlinkEdgeHitTester.hit(
                x = 4f, y = 4f, rows = 2, cols = 2, cellSize = 10, edgeSize = 4, padding = 2
            )
        )
        assertNull(
            SlitherlinkEdgeHitTester.hit(
                x = 11f, y = 11f, rows = 2, cols = 2, cellSize = 10, edgeSize = 4, padding = 2
            )
        )
    }
}
