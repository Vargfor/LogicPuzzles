package com.logicpuzzles

import com.cyberhub.logicgames.R
import com.logicpuzzles.utils.difficultyNameResId
import org.junit.Assert.assertEquals
import org.junit.Test

class UiTextTest {

    @Test
    fun masterDifficultyUsesMasterLabel() {
        assertEquals(R.string.difficulty_expert, difficultyNameResId(3))
        assertEquals(R.string.difficulty_master, difficultyNameResId(4))
    }
}
