package com.logicpuzzles

import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.gameHelpContent
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GameHelpContentTest {
    @Test
    fun everyPuzzleTypeHasCompleteHelpContent() {
        for (puzzleType in 0 until PrefsManager.PUZZLE_TYPES) {
            val help = gameHelpContent(puzzleType)
            val resources = listOf(
                help.puzzleNameResId,
                help.goalResId,
                help.controlsResId,
                help.tipResId
            )

            resources.forEach { resourceId ->
                assertNotEquals(0, resourceId)
            }
        }
    }

    @Test
    fun unknownPuzzleTypeIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            gameHelpContent(PrefsManager.PUZZLE_TYPES)
        }
    }
}
