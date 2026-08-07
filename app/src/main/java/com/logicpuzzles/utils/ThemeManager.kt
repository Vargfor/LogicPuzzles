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

object ThemeManager {
    private const val PREF_NAME = "logic_puzzles_prefs"
    private const val KEY_THEME = "color_theme"

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
        background = color("#020617"),
        surface = color("#111827"),
        surfaceStrong = color("#334155"),
        textPrimary = color("#F8FAFC"),
        textSecondary = color("#E2E8F0"),
        accent = color(accent),
        accentText = color("#07111F"),
        button = color(button),
        buttonText = color("#FFFFFF"),
        success = color(success),
        warning = color(warning),
        danger = color(danger),
        cellEmpty = color("#FFFFFF"),
        cellText = color("#020617"),
        cellFilled = color(cellFilled),
        cellFilledText = color("#FFFFFF"),
        cellSelected = color("#FFD43B"),
        cellSelectedText = color("#020617"),
        cellFixed = color(cellFixed),
        cellFixedText = color("#07111F"),
        shadedCell = color(cellFilled),
        gridLine = color("#94A3B8"),
        locked = color("#1E293B"),
        lockedText = color("#CBD5E1"),
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
        background = color("#D8E2EE"),
        surface = color("#FFFFFF"),
        surfaceStrong = color("#CBD5E1"),
        textPrimary = color("#111827"),
        textSecondary = color("#334155"),
        accent = color(accent),
        accentText = color("#FFFFFF"),
        button = color(button),
        buttonText = color("#FFFFFF"),
        success = color(success),
        warning = color(warning),
        danger = color(danger),
        cellEmpty = color("#FFFFFF"),
        cellText = color("#111827"),
        cellFilled = color(cellFilled),
        cellFilledText = color("#FFFFFF"),
        cellSelected = color("#FFD43B"),
        cellSelectedText = color("#020617"),
        cellFixed = color(cellFixed),
        cellFixedText = color("#020617"),
        shadedCell = color(cellFilled),
        gridLine = color("#475569"),
        locked = color("#CBD5E1"),
        lockedText = color("#334155"),
        puzzleAccents = puzzleAccents,
        difficultyAccents = difficultyAccents
    )

    val palettes = listOf(
        darkPalette(
            id = 0,
            name = "Dark",
            accent = "#4EA3F1",
            button = "#1C5FDB",
            success = "#35C2B5",
            warning = "#F2C94C",
            danger = "#FF6B9A",
            cellFilled = "#111827",
            cellFixed = "#BFE6FF",
            puzzleAccents = colors(
                "#56B4E9", "#E69F00", "#40B0A6", "#CC79A7", "#F2C94C",
                "#8EA4FF", "#FF8C42", "#B5A1FF", "#2EC4B6", "#FF6B9A"
            ),
            difficultyAccents = colors("#56B4E9", "#E69F00", "#CC79A7", "#F2C94C", "#D62828")
        ),
        lightPalette(
            id = 1,
            name = "Light",
            accent = "#005AB5",
            button = "#005AB5",
            success = "#006A6A",
            warning = "#8A5A00",
            danger = "#9A2E73",
            cellFilled = "#111827",
            cellFixed = "#B7D7FF",
            puzzleAccents = colors(
                "#005AB5", "#A16207", "#006A6A", "#7A3E9D", "#8A5A00",
                "#2F5F8F", "#99582A", "#5D5A88", "#24706B", "#9A2E73"
            ),
            difficultyAccents = colors("#005AB5", "#A16207", "#7A3E9D", "#111827", "#9A0000")
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

    fun currentPalette(context: Context): AppPalette {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val id = prefs.getInt(KEY_THEME, 0)
        return palettes.firstOrNull { it.id == id } ?: palettes.first()
    }

    fun selectedThemeId(context: Context): Int = currentPalette(context).id

    fun setTheme(context: Context, id: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit { putInt(KEY_THEME, id) }
    }

    fun puzzleAccent(context: Context, type: Int): Int {
        val palette = currentPalette(context)
        return palette.puzzleAccents[type.coerceIn(0, palette.puzzleAccents.lastIndex)]
    }

    fun difficultyAccent(context: Context, difficulty: Int): Int {
        val palette = currentPalette(context)
        return palette.difficultyAccents[difficulty.coerceIn(0, palette.difficultyAccents.lastIndex)]
    }
}
