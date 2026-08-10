package com.logicpuzzles

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.logicpuzzles.utils.PrefsManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PrefsManagerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        PrefsManager(context).clearAll()
    }

    @Test
    fun selectiveResetOnlyClearsSelectedPuzzleTypes() {
        val prefs = PrefsManager(context)
        prefs.markPuzzleCompleted(MainActivity.TYPE_KAKURO, 0, 0)
        prefs.markPuzzleCompleted(MainActivity.TYPE_HIDATO, 0, 0)
        val hidatoOrderBefore = catalogOrder(prefs, MainActivity.TYPE_HIDATO, 0)

        prefs.resetProgressAndShuffleLevels(setOf(MainActivity.TYPE_KAKURO))

        assertFalse(prefs.isPuzzleCompleted(MainActivity.TYPE_KAKURO, 0, 0))
        assertTrue(prefs.isPuzzleCompleted(MainActivity.TYPE_HIDATO, 0, 0))
        assertNotEquals(identityOrder(MainActivity.TYPE_KAKURO, 0), catalogOrder(prefs, MainActivity.TYPE_KAKURO, 0))
        assertEquals(hidatoOrderBefore, catalogOrder(prefs, MainActivity.TYPE_HIDATO, 0))
    }

    @Test
    fun fullResetClearsAllPuzzleTypesButKeepsSettings() {
        val prefs = PrefsManager(context)
        prefs.markPuzzleCompleted(MainActivity.TYPE_KAKURO, 0, 0)
        prefs.markPuzzleCompleted(MainActivity.TYPE_HIDATO, 0, 0)
        prefs.setSkyscraperBuildingsEnabled(false)
        prefs.setDeveloperUnlockAllLevelsEnabled(true)

        prefs.resetProgressAndShuffleLevels()

        assertFalse(prefs.isPuzzleCompleted(MainActivity.TYPE_KAKURO, 0, 0))
        assertFalse(prefs.isPuzzleCompleted(MainActivity.TYPE_HIDATO, 0, 0))
        assertFalse(prefs.isSkyscraperBuildingsEnabled())
        assertFalse(prefs.isDeveloperUnlockAllLevelsEnabled())
        assertTrue(savedDeveloperUnlockPreference())
        assertNotEquals(identityOrder(MainActivity.TYPE_KAKURO, 0), catalogOrder(prefs, MainActivity.TYPE_KAKURO, 0))
        assertNotEquals(identityOrder(MainActivity.TYPE_HIDATO, 0), catalogOrder(prefs, MainActivity.TYPE_HIDATO, 0))
    }

    @Test
    fun skyscraperDisplayModePersistsAcrossManagerInstances() {
        val prefs = PrefsManager(context)

        assertTrue(prefs.isSkyscraperBuildingsEnabled())
        prefs.setSkyscraperBuildingsEnabled(false)
        assertFalse(PrefsManager(context).isSkyscraperBuildingsEnabled())
        PrefsManager(context).setSkyscraperBuildingsEnabled(true)
        assertTrue(prefs.isSkyscraperBuildingsEnabled())
    }

    @Test
    fun emptySelectionDoesNothing() {
        val prefs = PrefsManager(context)
        prefs.markPuzzleCompleted(MainActivity.TYPE_KAKURO, 0, 0)
        val orderBefore = catalogOrder(prefs, MainActivity.TYPE_KAKURO, 0)

        prefs.resetProgressAndShuffleLevels(emptySet())

        assertTrue(prefs.isPuzzleCompleted(MainActivity.TYPE_KAKURO, 0, 0))
        assertEquals(orderBefore, catalogOrder(prefs, MainActivity.TYPE_KAKURO, 0))
    }

    @Test
    fun levelUnlocksFollowProgressionWhenDeveloperUnlockIsOff() {
        val prefs = PrefsManager(context)

        assertTrue(prefs.isLevelUnlocked(MainActivity.TYPE_KAKURO, 0, 0))
        assertFalse(prefs.isLevelUnlocked(MainActivity.TYPE_KAKURO, 0, 1))
        assertFalse(prefs.isLevelUnlocked(MainActivity.TYPE_KAKURO, 3, 0))

        prefs.markPuzzleCompleted(MainActivity.TYPE_KAKURO, 0, 0)

        assertTrue(prefs.isLevelUnlocked(MainActivity.TYPE_KAKURO, 0, 1))
    }

    @Test
    fun disabledDeveloperUnlockDoesNotBypassProgression() {
        val prefs = PrefsManager(context)
        val lastMasterLevel = PrefsManager.getPuzzleCount(MainActivity.TYPE_KAKURO, 4) - 1

        assertFalse(prefs.isDeveloperUnlockAllLevelsAvailable())
        assertFalse(prefs.isLevelUnlocked(MainActivity.TYPE_KAKURO, 4, lastMasterLevel))
        assertFalse(prefs.isPuzzleCompleted(MainActivity.TYPE_KAKURO, 4, lastMasterLevel))

        prefs.setDeveloperUnlockAllLevelsEnabled(true)

        assertTrue(savedDeveloperUnlockPreference())
        assertFalse(prefs.isDeveloperUnlockAllLevelsEnabled())
        assertFalse(prefs.isLevelUnlocked(MainActivity.TYPE_KAKURO, 4, lastMasterLevel))
        assertFalse(prefs.isPuzzleCompleted(MainActivity.TYPE_KAKURO, 4, lastMasterLevel))
        assertFalse(prefs.isLevelUnlocked(MainActivity.TYPE_KAKURO, 4, lastMasterLevel + 1))
    }

    private fun savedDeveloperUnlockPreference(): Boolean =
        context.getSharedPreferences("logic_puzzles_prefs", Context.MODE_PRIVATE)
            .getBoolean("developer_unlock_all_levels_enabled", false)

    private fun catalogOrder(prefs: PrefsManager, type: Int, difficulty: Int): List<Int> =
        (0 until PrefsManager.getPuzzleCount(type, difficulty)).map { index ->
            prefs.getCatalogIndex(type, difficulty, index)
        }

    private fun identityOrder(type: Int, difficulty: Int): List<Int> =
        (0 until PrefsManager.getPuzzleCount(type, difficulty)).toList()
}
