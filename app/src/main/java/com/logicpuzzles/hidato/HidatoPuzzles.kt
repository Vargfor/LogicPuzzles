package com.logicpuzzles.hidato

// initial[r][c]: 0 = empty, -1 = blocked (no cell), >0 = pre-filled
data class HidatoPuzzle(
    val rows: Int,
    val cols: Int,
    val initial: Array<IntArray>,
    val maxNumber: Int
)

object HidatoPuzzles {

    /**
     * Encoding: rows separated by '/'. Each row uses comma-separated tokens.
     * '.' = empty, '#' = blocked, integer = pre-filled.
     */
    private fun build(text: String): HidatoPuzzle {
        val rowsStr = text.split("/")
        val rows = rowsStr.size
        val cols = rowsStr[0].split(",").size
        val grid = Array(rows) { r ->
            val tokens = rowsStr[r].split(",")
            IntArray(cols) { c ->
                when (val t = tokens[c].trim()) {
                    "." -> 0
                    "#" -> -1
                    else -> t.toInt()
                }
            }
        }
        var maxNum = 0
        for (r in 0 until rows) for (c in 0 until cols) {
            if (grid[r][c] != -1) maxNum++
        }
        return HidatoPuzzle(rows, cols, grid, maxNum)
    }

    // Easy 3x3 — only 5 puzzles since 3x3 Hidato is genuinely trivial.
    // Verified unique by hand using "corners + center" anchor pattern (1 at corner, 9 at center).
    // Each odd-anchor pair has exactly one connecting empty cell, forcing the path.
    private val EASY = listOf(
        build("1,.,3/.,9,./7,.,5"),
        build("3,.,1/.,9,./5,.,7"),
        build("3,.,5/.,9,./1,.,7"),
        build("5,.,7/.,9,./3,.,1"),
        build("1,.,7/.,9,./3,.,5")
    )

    // Medium 4x4 — dense anchor patterns derived from a snake path.
    // Snake: 1,2,3,4 / 8,7,6,5 / 9,10,11,12 / 16,15,14,13
    // We anchor row corners + a middle cell to constrain the path tightly.
    private val MEDIUM = listOf(
        build("1,.,.,4/8,.,.,5/9,.,.,12/16,.,.,13"),
        build("1,2,.,4/.,7,.,5/9,10,.,12/16,.,.,13"),
        build("1,.,3,4/8,.,6,5/9,.,11,12/16,.,14,13"),
        build("1,.,.,16/2,.,.,15/3,.,.,14/4,.,.,13"),
        build("16,.,.,1/.,.,.,2/.,.,.,3/13,.,.,4"),
        build("1,2,3,4/.,.,.,5/.,.,.,12/16,.,.,13"),
        build("1,.,.,4/.,.,.,5/9,.,.,12/16,15,14,13"),
        build("1,.,.,4/.,7,6,./.,.,.,./16,15,.,13"),
        build("1,.,3,./8,.,.,5/.,10,.,12/16,.,14,."),
        build("1,2,.,./.,.,.,5/9,.,.,./16,15,14,.")
    )

    // Hard 5x5 — full 25-cell paths with many anchors. 15 puzzles to compensate for fewer easies.
    // Snake-style reference: 1,2,3,4,5 / 10,9,8,7,6 / 11,12,13,14,15 / 20,19,18,17,16 / 21,22,23,24,25
    private val HARD = listOf(
        build("1,.,.,.,5/.,.,.,.,6/11,.,.,.,15/.,.,.,.,16/21,.,.,.,25"),
        build("1,2,.,.,5/.,.,.,.,6/11,.,13,.,15/.,.,.,.,16/21,.,.,.,25"),
        build("1,.,.,.,5/10,.,.,.,6/11,.,.,.,15/20,.,.,.,16/21,.,.,.,25"),
        build("1,.,.,.,25/.,.,.,.,./.,.,13,.,./.,.,.,.,./5,.,.,.,21"),
        build("1,.,3,.,5/.,9,.,7,./11,.,13,.,15/.,19,.,17,./21,.,23,.,25"),
        build("25,.,.,.,1/.,.,.,.,./.,.,13,.,./.,.,.,.,./5,.,.,.,21"),
        build("1,2,3,4,5/10,.,.,.,6/11,.,.,.,15/20,.,.,.,16/21,22,23,24,25"),
        build("1,.,.,.,5/.,.,8,.,./.,12,13,14,./.,.,18,.,./21,.,.,.,25"),
        build("1,.,.,.,./.,9,.,.,./.,.,13,.,./.,.,.,17,./.,.,.,.,25"),
        build("1,.,.,.,5/10,.,.,.,6/.,.,13,.,./20,.,.,.,16/21,.,.,.,25"),
        // Five additional puzzles to balance the reduced Easy count.
        build("1,.,.,.,./.,.,.,.,./.,.,13,.,./.,.,.,.,./.,.,.,.,25"),
        build("1,.,.,.,5/6,.,.,.,10/11,.,.,.,15/16,.,.,.,20/21,.,.,.,25"),
        build("1,.,3,.,5/.,.,.,.,./11,.,13,.,15/.,.,.,.,./21,.,23,.,25"),
        build("1,.,.,.,./.,9,10,.,./.,.,.,.,./.,.,17,18,./.,.,.,.,25"),
        build(".,.,.,.,1/.,.,.,.,./.,.,13,.,./.,.,.,.,./25,.,.,.,.")
    )

    // Expert 5x5 with blocked cells (~21 cells used). Path 1..21 anchors.
    private val EXPERT = listOf(
        build("1,.,.,.,./.,.,#,.,./.,.,.,.,./.,.,#,.,./.,.,.,.,21"),
        build("1,.,.,.,./.,#,.,#,./.,.,.,.,./.,#,.,#,./.,.,.,.,21"),
        build(".,.,1,.,./.,.,.,.,./.,.,.,.,./.,.,.,.,./.,.,21,.,."),
        build("1,.,#,.,./.,.,.,.,./#,.,.,.,#/.,.,.,.,./.,.,#,.,21"),
        build(".,.,.,.,./.,1,#,.,./.,.,.,.,./.,.,#,21,./.,.,.,.,."),
        build("1,2,.,.,./.,#,.,#,./.,.,.,.,./.,#,.,#,./.,.,.,.,21"),
        build("21,.,.,.,1/.,.,#,.,./.,.,.,.,./.,.,#,.,./.,.,.,.,."),
        build("1,.,.,.,./.,#,.,#,./.,.,21,.,./.,#,.,#,./.,.,.,.,."),
        build(".,.,.,.,21/.,.,#,.,./.,.,.,.,./.,.,#,.,./1,.,.,.,."),
        build("1,.,.,.,./.,.,.,.,./#,.,#,.,#/.,.,.,.,./.,.,.,.,21")
    )

    fun get(difficulty: Int, index: Int): HidatoPuzzle {
        val pool = when (difficulty) {
            0 -> EASY; 1 -> MEDIUM; 2 -> HARD; else -> EXPERT
        }
        return pool[index.coerceIn(0, pool.size - 1)]
    }
}
