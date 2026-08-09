package com.logicpuzzles.utils

import android.content.Context
import androidx.core.content.edit
import androidx.core.graphics.toColorInt

data class AppPalette(
    val id: Int,
    val name: String,
    val background: Int,
    val surface: Int,
    val surfaceStrong: Int,
    val textPrimary: Int,
    val textSecondary: Int,
    val accent: Int,
    val accentText: Int,
    val button: Int,
    val buttonText: Int,
    val success: Int,
    val warning: Int,
    val danger: Int,
    val cellEmpty: Int,
    val cellText: Int,
    val cellFilled: Int,
    val cellFilledText: Int,
    val cellSelected: Int,
    val cellSelectedText: Int,
    val cellFixed: Int,
    val cellFixedText: Int,
    val shadedCell: Int,
    val gridLine: Int,
    val locked: Int,
    val lockedText: Int,
    val puzzleAccents: IntArray,
    val difficultyAccents: IntArray
)

data class ThemeColorSpec(
    val key: String,
    val label: String,
    val group: String
)

data class CustomThemeSlot(
    val slot: Int,
    val palette: AppPalette
)

object ThemeManager {
    private const val PREF_NAME = "logic_puzzles_prefs"
    private const val KEY_THEME = "color_theme"
    private const val KEY_CUSTOM_PREFIX = "custom_theme_"
    const val CUSTOM_THEME_SLOTS = 3
    const val CUSTOM_THEME_BASE_ID = 100

    private fun color(hex: String): Int = hex.toColorInt()
    private fun colors(vararg hex: String): IntArray = hex.map(::color).toIntArray()

    private fun darkPalette(
        id: Int,
        name: String,
        accent: String,
        button: String,
        success: String,
        warning: String,
        danger: String,
        cellFilled: String,
        cellFixed: String,
        puzzleAccents: IntArray,
        difficultyAccents: IntArray
    ): AppPalette = AppPalette(
        id = id,
        name = name,
        background = color("#111116"),
        surface = color("#171820"),
        surfaceStrong = color("#25252E"),
        textPrimary = color("#D8DEEA"),
        textSecondary = color("#8F96A8"),
        accent = color(accent),
        accentText = color("#08101C"),
        button = color(button),
        buttonText = color("#FFFFFF"),
        success = color(success),
        warning = color(warning),
        danger = color(danger),
        cellEmpty = color("#171820"),
        cellText = color("#79A9F2"),
        cellFilled = color(cellFilled),
        cellFilledText = color("#EAF3FF"),
        cellSelected = color("#074C86"),
        cellSelectedText = color("#EAF3FF"),
        cellFixed = color(cellFixed),
        cellFixedText = color("#9AA3B5"),
        shadedCell = color(cellFilled),
        gridLine = color("#050509"),
        locked = color("#0B0B10"),
        lockedText = color("#6E7484"),
        puzzleAccents = puzzleAccents,
        difficultyAccents = difficultyAccents
    )

    private fun lightPalette(
        id: Int,
        name: String,
        accent: String,
        button: String,
        success: String,
        warning: String,
        danger: String,
        cellFilled: String,
        cellFixed: String,
        puzzleAccents: IntArray,
        difficultyAccents: IntArray
    ): AppPalette = AppPalette(
        id = id,
        name = name,
        background = color("#EDF3FA"),
        surface = color("#FFFFFF"),
        surfaceStrong = color("#DDE7F3"),
        textPrimary = color("#122033"),
        textSecondary = color("#5A6678"),
        accent = color(accent),
        accentText = color("#FFFFFF"),
        button = color(button),
        buttonText = color("#FFFFFF"),
        success = color(success),
        warning = color(warning),
        danger = color(danger),
        cellEmpty = color("#FFFFFF"),
        cellText = color("#1A2B40"),
        cellFilled = color(cellFilled),
        cellFilledText = color("#FFFFFF"),
        cellSelected = color("#CFE4FF"),
        cellSelectedText = color("#102033"),
        cellFixed = color(cellFixed),
        cellFixedText = color("#27364A"),
        shadedCell = color(cellFilled),
        gridLine = color("#7C8798"),
        locked = color("#CFD8E5"),
        lockedText = color("#5B6574"),
        puzzleAccents = puzzleAccents,
        difficultyAccents = difficultyAccents
    )

    val palettes = listOf(
        darkPalette(
            id = 0,
            name = "Dark",
            accent = "#79A9F2",
            button = "#114D86",
            success = "#48D6C1",
            warning = "#F3C766",
            danger = "#FF6B8A",
            cellFilled = "#0F4F8F",
            cellFixed = "#24242C",
            puzzleAccents = colors(
                "#79A9F2", "#9FC5FF", "#48D6C1", "#F3C766", "#7FB3FF",
                "#B8A7FF", "#A0D1FF", "#8DD9D0", "#FF9E7A", "#8CBFFF"
            ),
            difficultyAccents = colors("#79A9F2", "#48D6C1", "#F3C766", "#FF9E7A", "#FF6B8A")
        ),
        lightPalette(
            id = 1,
            name = "Light",
            accent = "#1D6FCB",
            button = "#1D6FCB",
            success = "#007E72",
            warning = "#8D6200",
            danger = "#B13E66",
            cellFilled = "#1D6FCB",
            cellFixed = "#DDE7F3",
            puzzleAccents = colors(
                "#1D6FCB", "#3A7BD5", "#007E72", "#8D6200", "#2D76C8",
                "#6B63B5", "#2084B7", "#118678", "#B65C36", "#B13E66"
            ),
            difficultyAccents = colors("#1D6FCB", "#007E72", "#8D6200", "#B65C36", "#B13E66")
        ),
        darkPalette(
            id = 2,
            name = "Trichromacy",
            accent = "#0072B2",
            button = "#0072B2",
            success = "#00796B",
            warning = "#A16207",
            danger = "#B0005A",
            cellFilled = "#1F2937",
            cellFixed = "#E0F2FE",
            puzzleAccents = colors(
                "#0072B2", "#D55E00", "#009E73", "#7B3294", "#A16207",
                "#2F5F8F", "#8B5CF6", "#006A6A", "#994F00", "#B0005A"
            ),
            difficultyAccents = colors("#0072B2", "#D55E00", "#7B3294", "#1F2937", "#A50026")
        ),
        darkPalette(
            id = 3,
            name = "Dichromacy",
            accent = "#005AB5",
            button = "#005AB5",
            success = "#006A6A",
            warning = "#9C6500",
            danger = "#8F2D7A",
            cellFilled = "#102A43",
            cellFixed = "#E0ECFF",
            puzzleAccents = colors(
                "#005AB5", "#DC8F00", "#006A6A", "#8F2D7A", "#4B5563",
                "#7A5C00", "#004B6B", "#6D5A8D", "#3B6F6A", "#9C6500"
            ),
            difficultyAccents = colors("#005AB5", "#DC8F00", "#8F2D7A", "#4B5563", "#8B0000")
        ),
        darkPalette(
            id = 4,
            name = "Protanopia",
            accent = "#005AB5",
            button = "#005AB5",
            success = "#006A6A",
            warning = "#9C6500",
            danger = "#7A3E9D",
            cellFilled = "#102A43",
            cellFixed = "#E0ECFF",
            puzzleAccents = colors(
                "#005AB5", "#DC8F00", "#007C91", "#7A3E9D", "#5D5A88",
                "#8A5A00", "#004B6B", "#006A6A", "#3F4A7A", "#99582A"
            ),
            difficultyAccents = colors("#005AB5", "#DC8F00", "#7A3E9D", "#334155", "#7B1818")
        ),
        darkPalette(
            id = 5,
            name = "Deuteranopia",
            accent = "#004C99",
            button = "#004C99",
            success = "#006A6A",
            warning = "#9C6500",
            danger = "#8F2D7A",
            cellFilled = "#111827",
            cellFixed = "#DCEEFF",
            puzzleAccents = colors(
                "#004C99", "#C77500", "#006A6A", "#8F2D7A", "#5D5A88",
                "#7A5C00", "#2F5F8F", "#4B5563", "#007C91", "#9A2E73"
            ),
            difficultyAccents = colors("#004C99", "#C77500", "#8F2D7A", "#4B5563", "#8B1A1A")
        ),
        darkPalette(
            id = 6,
            name = "Tritanopia",
            accent = "#C2185B",
            button = "#8F2D56",
            success = "#006A6A",
            warning = "#A14A00",
            danger = "#7B1E3D",
            cellFilled = "#201A1E",
            cellFixed = "#F4E3EA",
            puzzleAccents = colors(
                "#C2185B", "#006A6A", "#A14A00", "#6E3B6E", "#4B5563",
                "#8A3A00", "#007C91", "#7B1E3D", "#5A4A66", "#9A2E73"
            ),
            difficultyAccents = colors("#C2185B", "#A14A00", "#006A6A", "#201A1E", "#6B2737")
        )
    )

    val normalPalettes = palettes.filter { it.id in 0..1 }
    val colorblindPalettes = palettes.filter { it.id >= 2 }

    val editableColorSpecs = listOf(
        ThemeColorSpec("background", "App background", "App surfaces"),
        ThemeColorSpec("surface", "Panel surface", "App surfaces"),
        ThemeColorSpec("surface_strong", "Selected panel", "App surfaces"),
        ThemeColorSpec("text_primary", "Primary text", "Text"),
        ThemeColorSpec("text_secondary", "Secondary text", "Text"),
        ThemeColorSpec("accent", "General accent", "Controls"),
        ThemeColorSpec("accent_text", "Accent text", "Controls"),
        ThemeColorSpec("button", "Button fill", "Controls"),
        ThemeColorSpec("button_text", "Button text", "Controls"),
        ThemeColorSpec("success", "Success feedback", "Feedback"),
        ThemeColorSpec("warning", "Warning feedback", "Feedback"),
        ThemeColorSpec("danger", "Danger feedback", "Feedback"),
        ThemeColorSpec("cell_empty", "Empty cell", "Puzzle cells"),
        ThemeColorSpec("cell_text", "Cell text", "Puzzle cells"),
        ThemeColorSpec("cell_filled", "Filled cell", "Puzzle cells"),
        ThemeColorSpec("cell_filled_text", "Filled cell text", "Puzzle cells"),
        ThemeColorSpec("cell_selected", "Selected cell", "Puzzle cells"),
        ThemeColorSpec("cell_selected_text", "Selected cell text", "Puzzle cells"),
        ThemeColorSpec("cell_fixed", "Given cell", "Puzzle cells"),
        ThemeColorSpec("cell_fixed_text", "Given cell text", "Puzzle cells"),
        ThemeColorSpec("shaded_cell", "Shaded cell", "Puzzle cells"),
        ThemeColorSpec("grid_line", "Grid lines", "Puzzle cells"),
        ThemeColorSpec("locked", "Locked item", "Puzzle cells"),
        ThemeColorSpec("locked_text", "Locked item text", "Puzzle cells"),
        ThemeColorSpec("puzzle_0", "Nonogram accent", "Game accents"),
        ThemeColorSpec("puzzle_1", "Mastermind accent", "Game accents"),
        ThemeColorSpec("puzzle_2", "Lights Out accent", "Game accents"),
        ThemeColorSpec("puzzle_3", "Kakuro accent", "Game accents"),
        ThemeColorSpec("puzzle_4", "Logic Grid accent", "Game accents"),
        ThemeColorSpec("puzzle_5", "Slitherlink accent", "Game accents"),
        ThemeColorSpec("puzzle_6", "Nurikabe accent", "Game accents"),
        ThemeColorSpec("puzzle_7", "Hidato accent", "Game accents"),
        ThemeColorSpec("puzzle_8", "Futoshiki accent", "Game accents"),
        ThemeColorSpec("puzzle_9", "Skyscraper accent", "Game accents"),
        ThemeColorSpec("difficulty_0", "Easy difficulty", "Difficulty accents"),
        ThemeColorSpec("difficulty_1", "Medium difficulty", "Difficulty accents"),
        ThemeColorSpec("difficulty_2", "Hard difficulty", "Difficulty accents"),
        ThemeColorSpec("difficulty_3", "Expert difficulty", "Difficulty accents"),
        ThemeColorSpec("difficulty_4", "Master difficulty", "Difficulty accents")
    )

    fun currentPalette(context: Context): AppPalette {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val id = prefs.getInt(KEY_THEME, 0)
        customThemeFromId(context, id)?.let { return it }
        return palettes.firstOrNull { it.id == id } ?: palettes.first()
    }

    fun paletteSignature(context: Context): Int =
        paletteColors(currentPalette(context)).values.fold(17) { hash, value ->
            hash * 31 + value
        }

    fun selectedThemeId(context: Context): Int =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(KEY_THEME, 0)

    fun setTheme(context: Context, id: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit { putInt(KEY_THEME, id) }
    }

    fun customThemeId(slot: Int): Int = CUSTOM_THEME_BASE_ID + slot

    fun customThemeSlots(context: Context): List<CustomThemeSlot> =
        (0 until CUSTOM_THEME_SLOTS).mapNotNull { slot ->
            customThemeAt(context, slot)?.let { CustomThemeSlot(slot, it) }
        }

    fun customThemeAt(context: Context, slot: Int): AppPalette? {
        if (slot !in 0 until CUSTOM_THEME_SLOTS) return null
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(customThemeNameKey(slot), null)?.trim().orEmpty()
        if (name.isEmpty()) return null
        val colors = editableColorSpecs.associate { spec ->
            spec.key to prefs.getInt(customThemeColorKey(slot, spec.key), defaultColorFor(spec.key))
        }
        return createCustomPalette(customThemeId(slot), name, colors)
    }

    fun firstAvailableCustomSlot(context: Context): Int? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return (0 until CUSTOM_THEME_SLOTS).firstOrNull { slot ->
            !prefs.contains(customThemeNameKey(slot))
        }
    }

    fun saveCustomTheme(context: Context, slot: Int, name: String, colors: Map<String, Int>): AppPalette {
        val safeSlot = slot.coerceIn(0, CUSTOM_THEME_SLOTS - 1)
        val safeName = name.trim().ifEmpty { "Custom ${safeSlot + 1}" }
        val palette = createCustomPalette(customThemeId(safeSlot), safeName, colors)
        val storedColors = paletteColors(palette)

        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit {
            putString(customThemeNameKey(safeSlot), safeName)
            editableColorSpecs.forEach { spec ->
                putInt(customThemeColorKey(safeSlot, spec.key), storedColors.getValue(spec.key))
            }
            putInt(KEY_THEME, palette.id)
        }
        return palette
    }

    fun deleteCustomTheme(context: Context, slot: Int) {
        if (slot !in 0 until CUSTOM_THEME_SLOTS) return
        val deletedThemeId = customThemeId(slot)
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val wasSelected = prefs.getInt(KEY_THEME, 0) == deletedThemeId
        prefs.edit {
            remove(customThemeNameKey(slot))
            editableColorSpecs.forEach { spec ->
                remove(customThemeColorKey(slot, spec.key))
            }
            if (wasSelected) putInt(KEY_THEME, 0)
        }
    }

    fun createCustomPalette(id: Int, name: String, colors: Map<String, Int>): AppPalette {
        val base = palettes.first()
        fun c(key: String, fallback: Int): Int = colors[key] ?: fallback
        return AppPalette(
            id = id,
            name = name,
            background = c("background", base.background),
            surface = c("surface", base.surface),
            surfaceStrong = c("surface_strong", base.surfaceStrong),
            textPrimary = c("text_primary", base.textPrimary),
            textSecondary = c("text_secondary", base.textSecondary),
            accent = c("accent", base.accent),
            accentText = c("accent_text", base.accentText),
            button = c("button", base.button),
            buttonText = c("button_text", base.buttonText),
            success = c("success", base.success),
            warning = c("warning", base.warning),
            danger = c("danger", base.danger),
            cellEmpty = c("cell_empty", base.cellEmpty),
            cellText = c("cell_text", base.cellText),
            cellFilled = c("cell_filled", base.cellFilled),
            cellFilledText = c("cell_filled_text", base.cellFilledText),
            cellSelected = c("cell_selected", base.cellSelected),
            cellSelectedText = c("cell_selected_text", base.cellSelectedText),
            cellFixed = c("cell_fixed", base.cellFixed),
            cellFixedText = c("cell_fixed_text", base.cellFixedText),
            shadedCell = c("shaded_cell", base.shadedCell),
            gridLine = c("grid_line", base.gridLine),
            locked = c("locked", base.locked),
            lockedText = c("locked_text", base.lockedText),
            puzzleAccents = IntArray(base.puzzleAccents.size) { index ->
                c("puzzle_$index", base.puzzleAccents[index])
            },
            difficultyAccents = IntArray(base.difficultyAccents.size) { index ->
                c("difficulty_$index", base.difficultyAccents[index])
            }
        )
    }

    fun paletteColors(palette: AppPalette): MutableMap<String, Int> =
        linkedMapOf<String, Int>().apply {
            put("background", palette.background)
            put("surface", palette.surface)
            put("surface_strong", palette.surfaceStrong)
            put("text_primary", palette.textPrimary)
            put("text_secondary", palette.textSecondary)
            put("accent", palette.accent)
            put("accent_text", palette.accentText)
            put("button", palette.button)
            put("button_text", palette.buttonText)
            put("success", palette.success)
            put("warning", palette.warning)
            put("danger", palette.danger)
            put("cell_empty", palette.cellEmpty)
            put("cell_text", palette.cellText)
            put("cell_filled", palette.cellFilled)
            put("cell_filled_text", palette.cellFilledText)
            put("cell_selected", palette.cellSelected)
            put("cell_selected_text", palette.cellSelectedText)
            put("cell_fixed", palette.cellFixed)
            put("cell_fixed_text", palette.cellFixedText)
            put("shaded_cell", palette.shadedCell)
            put("grid_line", palette.gridLine)
            put("locked", palette.locked)
            put("locked_text", palette.lockedText)
            palette.puzzleAccents.forEachIndexed { index, color -> put("puzzle_$index", color) }
            palette.difficultyAccents.forEachIndexed { index, color -> put("difficulty_$index", color) }
        }

    fun puzzleAccent(context: Context, type: Int): Int {
        val palette = currentPalette(context)
        return palette.puzzleAccents[type.coerceIn(0, palette.puzzleAccents.lastIndex)]
    }

    fun difficultyAccent(context: Context, difficulty: Int): Int {
        val palette = currentPalette(context)
        return palette.difficultyAccents[difficulty.coerceIn(0, palette.difficultyAccents.lastIndex)]
    }

    private fun customThemeFromId(context: Context, id: Int): AppPalette? {
        val slot = id - CUSTOM_THEME_BASE_ID
        return customThemeAt(context, slot)
    }

    private fun defaultColorFor(key: String): Int =
        paletteColors(palettes.first()).getValue(key)

    private fun customThemeNameKey(slot: Int): String =
        "$KEY_CUSTOM_PREFIX${slot}_name"

    private fun customThemeColorKey(slot: Int, colorKey: String): String =
        "$KEY_CUSTOM_PREFIX${slot}_$colorKey"
}
