package com.logicpuzzles.kakuro

sealed class KCell {
    object Black : KCell()
    data class Clue(val downSum: Int = 0, val rightSum: Int = 0) : KCell()
    object White : KCell()
}

data class KakuroPuzzle(
    val grid: Array<Array<KCell>>,
    val initial: Array<IntArray>? = null
) {
    val rows: Int get() = grid.size
    val cols: Int get() = grid[0].size
    fun initialAt(r: Int, c: Int): Int = initial?.getOrNull(r)?.getOrNull(c) ?: 0
}

object KakuroPuzzles {
    private fun b() = KCell.Black
    private fun c(d: Int = 0, r: Int = 0) = KCell.Clue(d, r)
    private fun w() = KCell.White

    // EASY — 2x2 / 2x3 / 3x2 white area
    private val EASY = listOf(
        // 1: 2x2 Cols (4,6) Rows (7,3)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=4),  c(d=6)),
            arrayOf<KCell>(c(r=7),    w(),     w()),
            arrayOf<KCell>(c(r=3),    w(),     w())
        )),
        // 2: 2x2 Cols (7,5) Rows (4,8)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=7),  c(d=5)),
            arrayOf<KCell>(c(r=4),    w(),     w()),
            arrayOf<KCell>(c(r=8),    w(),     w())
        )),
        // 3: 2x3 Cols (3,7,4) Rows (6,8)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=3),  c(d=7),  c(d=4)),
            arrayOf<KCell>(c(r=6),    w(),     w(),     w()),
            arrayOf<KCell>(c(r=8),    w(),     w(),     w())
        )),
        // 4: 3x2 Cols (6,9) Rows (4,8,3)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=6),  c(d=9)),
            arrayOf<KCell>(c(r=4),    w(),     w()),
            arrayOf<KCell>(c(r=8),    w(),     w()),
            arrayOf<KCell>(c(r=3),    w(),     w())
        )),
        // 5: 2x2 Cols (9,7) Rows (8,8)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=9),  c(d=7)),
            arrayOf<KCell>(c(r=8),    w(),     w()),
            arrayOf<KCell>(c(r=8),    w(),     w())
        )),
        // 6: 2x2 Cols (5,7) Rows (4,8)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=5),  c(d=7)),
            arrayOf<KCell>(c(r=4),    w(),     w()),
            arrayOf<KCell>(c(r=8),    w(),     w())
        )),
        // 7: 2x2 Cols (8,9) Rows (10,7)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=8),  c(d=9)),
            arrayOf<KCell>(c(r=10),   w(),     w()),
            arrayOf<KCell>(c(r=7),    w(),     w())
        )),
        // 8: 2x2 Cols (7,8) Rows (5,10)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=7),  c(d=8)),
            arrayOf<KCell>(c(r=5),    w(),     w()),
            arrayOf<KCell>(c(r=10),   w(),     w())
        )),
        // 9: 3x2 Cols (10,11) Rows (8,5,8)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=10), c(d=11)),
            arrayOf<KCell>(c(r=8),    w(),     w()),
            arrayOf<KCell>(c(r=5),    w(),     w()),
            arrayOf<KCell>(c(r=8),    w(),     w())
        )),
        // 10: 2x3 Cols (9,11,10) Rows (16,14)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=9),  c(d=11), c(d=10)),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=14),   w(),     w(),     w())
        ))
    ) + listOf(
        // 11-15: 3x3 white areas (+1 size step from Easy base)
        // 11: Rows (6,15,24) Cols (12,15,18)  grid 1,2,3/4,5,6/7,8,9
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=12), c(d=15), c(d=18)),
            arrayOf<KCell>(c(r=6),    w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w())
        )),
        // 12: Rows (9,15,21) Cols (11,13,21)  grid 1,3,5/2,6,7/8,4,9
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=11), c(d=13), c(d=21)),
            arrayOf<KCell>(c(r=9),    w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w())
        )),
        // 13: Rows (12,18,15) Cols (10,12,23)  grid 2,4,6/3,7,8/5,1,9
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=10), c(d=12), c(d=23)),
            arrayOf<KCell>(c(r=12),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w())
        )),
        // 14: Rows (12,16,17) Cols (14,14,17)  grid 3,1,8/5,9,2/6,4,7
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=14), c(d=14), c(d=17)),
            arrayOf<KCell>(c(r=12),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=17),   w(),     w(),     w())
        )),
        // 15: Rows (15,15,15) Cols (6,19,20) with anchor  grid 1,5,9/2,6,7/3,8,4
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=6),  c(d=19), c(d=20)),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w())
        ), initial = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 1, 0, 0),
            intArrayOf(0, 2, 0, 0),
            intArrayOf(0, 3, 0, 0)
        ))
    )

    // MEDIUM — 3x3 Latin squares with various sums.
    // Latin-square-only Kakuros (all sums equal) have many solutions, so we add
    // anchor cells to force uniqueness. Anchors at (1,1)=1, (1,2)=2, (2,1)=2 force
    // a 3x3 of {1,2,3} into the unique square 1,2,3 / 2,3,1 / 3,1,2.
    private val MEDIUM = listOf(
        // 1: All sums = 6  ({1,2,3}) with 3 anchors
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=6),  c(d=6),  c(d=6)),
            arrayOf<KCell>(c(r=6),    w(),     w(),     w()),
            arrayOf<KCell>(c(r=6),    w(),     w(),     w()),
            arrayOf<KCell>(c(r=6),    w(),     w(),     w())
        ), initial = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 1, 2, 0),
            intArrayOf(0, 2, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )),
        // 2: Mixed sums (8,10,16 / 14,11,9)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=8),  c(d=10), c(d=16)),
            arrayOf<KCell>(c(r=14),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=11),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=9),    w(),     w(),     w())
        )),
        // 3: Mixed sums (18,6,9 / 12,15,6)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=18), c(d=6),  c(d=9)),
            arrayOf<KCell>(c(r=12),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=6),    w(),     w(),     w())
        )),
        // 4: Mixed sums (8,16,15 / 18,7,14)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=8),  c(d=16), c(d=15)),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=7),    w(),     w(),     w()),
            arrayOf<KCell>(c(r=14),   w(),     w(),     w())
        )),
        // 5: Mixed sums (10,17,11 / 15,11,12)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=10), c(d=17), c(d=11)),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=11),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=12),   w(),     w(),     w())
        )),
        // 6: All sums = 9  ({2,3,4}) with anchors
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=9),  c(d=9),  c(d=9)),
            arrayOf<KCell>(c(r=9),    w(),     w(),     w()),
            arrayOf<KCell>(c(r=9),    w(),     w(),     w()),
            arrayOf<KCell>(c(r=9),    w(),     w(),     w())
        ), initial = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 2, 3, 0),
            intArrayOf(0, 3, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )),
        // 7: All sums = 12 ({3,4,5} or {1,4,7})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=12), c(d=12), c(d=12)),
            arrayOf<KCell>(c(r=12),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=12),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=12),   w(),     w(),     w())
        )),
        // 8: All sums = 15 ({4,5,6} or {2,5,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=15), c(d=15), c(d=15)),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w())
        )),
        // 9: All sums = 18 ({3,6,9} or {5,6,7})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=18), c(d=18), c(d=18)),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w())
        )),
        // 10: All sums = 21 ({4,8,9} or {6,7,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=21), c(d=21), c(d=21)),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w())
        ))
    ) + listOf(
        // 11-15: 4x4 white areas (+1 size step from Medium base)
        // 11: Rows (10,26,10,27) Cols (17,19,17,20)  grid 1,2,3,4/5,6,7,8/2,4,1,3/9,7,6,5
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=17), c(d=19), c(d=17), c(d=20)),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w())
        )),
        // 12: Rows (10,26,10,28) Cols (19,19,18,18)  grid 2,1,4,3/6,5,8,7/3,4,1,2/8,9,5,6
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=19), c(d=19), c(d=18), c(d=18)),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w())
        )),
        // 13: Rows (16,20,24,20) Cols (10,18,26,26)  grid 1,3,5,7/2,4,6,8/3,5,7,9/4,6,8,2
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=10), c(d=18), c(d=26), c(d=26)),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w())
        )),
        // 14: Rows (10,26,10,23) Cols (19,17,20,13)  grid 4,3,2,1/8,7,6,5/1,2,3,4/6,5,9,3
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=19), c(d=17), c(d=20), c(d=13)),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w())
        )),
        // 15: Rows (16,21,22,20) Cols (18,22,19,20)  grid 3,7,1,5/6,4,9,2/8,2,5,7/1,9,4,6
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=18), c(d=22), c(d=19), c(d=20)),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w())
        ))
    ) + listOf(
        // 16-25: more 4x4 white areas
        // 16: sum 10 ({1,2,3,4})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=10), c(d=10), c(d=10), c(d=10)),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w())
        )),
        // 17: sum 16 ({1,2,4,9} or {1,3,4,8} etc.)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=16), c(d=16), c(d=16), c(d=16)),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w())
        )),
        // 18: sum 28 ({4,7,8,9} or {5,6,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=28), c(d=28), c(d=28), c(d=28)),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w())
        )),
        // 19: sum 13 ({1,2,3,7} or {1,2,4,6})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=13), c(d=13), c(d=13), c(d=13)),
            arrayOf<KCell>(c(r=13),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=13),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=13),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=13),   w(),     w(),     w(),     w())
        )),
        // 20: sum 27 ({3,7,8,9} or {4,6,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=27), c(d=27), c(d=27), c(d=27)),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w())
        )),
        // 21: sum 11 ({1,2,3,5})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=11), c(d=11), c(d=11), c(d=11)),
            arrayOf<KCell>(c(r=11),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=11),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=11),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=11),   w(),     w(),     w(),     w())
        )),
        // 22: sum 29 ({5,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=29), c(d=29), c(d=29), c(d=29)),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w())
        )),
        // 23: sum 17 ({1,2,5,9} or {1,3,4,9} or {2,3,4,8} etc.)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=17), c(d=17), c(d=17), c(d=17)),
            arrayOf<KCell>(c(r=17),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=17),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=17),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=17),   w(),     w(),     w(),     w())
        )),
        // 24: sum 23 ({2,4,8,9} or {3,5,6,9} etc.)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=23), c(d=23), c(d=23), c(d=23)),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w())
        )),
        // 25: sum 26 ({5,6,7,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=26), c(d=26), c(d=26), c(d=26)),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w())
        ))
    )

    // HARD — 4x4 Latin squares (rows/cols sum to a constant per puzzle)
    private val HARD = listOf(
        // 1: sum 10 ({1,2,3,4})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=10), c(d=10), c(d=10), c(d=10)),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=10),   w(),     w(),     w(),     w())
        )),
        // 2: sum 26 ({5,6,7,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=26), c(d=26), c(d=26), c(d=26)),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w())
        )),
        // 3: sum 18 ({1,2,7,8} or {3,4,5,6})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=18), c(d=18), c(d=18), c(d=18)),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w())
        )),
        // 4: sum 15 ({1,2,3,9} etc.)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=15), c(d=15), c(d=15), c(d=15)),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w())
        )),
        // 5: sum 12 (very tight)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=12), c(d=12), c(d=12), c(d=12)),
            arrayOf<KCell>(c(r=12),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=12),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=12),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=12),   w(),     w(),     w(),     w())
        )),
        // 6: sum 14 ({2,3,4,5})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=14), c(d=14), c(d=14), c(d=14)),
            arrayOf<KCell>(c(r=14),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=14),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=14),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=14),   w(),     w(),     w(),     w())
        )),
        // 7: sum 22 ({4,5,6,7} or {1,5,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=22), c(d=22), c(d=22), c(d=22)),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w())
        )),
        // 8: sum 30 ({6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=30), c(d=30), c(d=30), c(d=30)),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w())
        )),
        // 9: sum 20 ({2,4,6,8} or {1,3,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=20), c(d=20), c(d=20), c(d=20)),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w())
        )),
        // 10: sum 24 ({3,5,7,9} or {2,6,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=24), c(d=24), c(d=24), c(d=24)),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w())
        ))
    ) + listOf(
        // 11-15: 5x5 white areas (+1 size step from Hard base)
        // 11: all sums = 15 ({1,2,3,4,5})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=15), c(d=15), c(d=15), c(d=15), c(d=15)),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w())
        )),
        // 12: all sums = 35 ({5,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=35), c(d=35), c(d=35), c(d=35), c(d=35)),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w())
        )),
        // 13: all sums = 20 ({2,3,4,5,6})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=20), c(d=20), c(d=20), c(d=20), c(d=20)),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w())
        )),
        // 14: all sums = 30 ({4,5,6,7,8} or {3,5,6,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=30), c(d=30), c(d=30), c(d=30), c(d=30)),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w())
        )),
        // 15: all sums = 25 ({3,4,5,6,7} or {1,3,5,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=25), c(d=25), c(d=25), c(d=25), c(d=25)),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w())
        ))
    ) + listOf(
        // 16-35: more 5x5 white areas
        // 16: sum 16 ({1,2,3,4,6})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=16), c(d=16), c(d=16), c(d=16), c(d=16)),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w(),     w())
        )),
        // 17: sum 34 ({4,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=34), c(d=34), c(d=34), c(d=34), c(d=34)),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w())
        )),
        // 18: sum 17 ({1,2,3,4,7} or {1,2,3,5,6})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=17), c(d=17), c(d=17), c(d=17), c(d=17)),
            arrayOf<KCell>(c(r=17),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=17),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=17),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=17),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=17),   w(),     w(),     w(),     w(),     w())
        )),
        // 19: sum 33 ({3,6,7,8,9} or {4,5,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=33), c(d=33), c(d=33), c(d=33), c(d=33)),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w())
        )),
        // 20: sum 18 ({1,2,3,4,8} or {1,2,3,5,7})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=18), c(d=18), c(d=18), c(d=18), c(d=18)),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w(),     w())
        )),
        // 21: sum 32 ({3,5,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=32), c(d=32), c(d=32), c(d=32), c(d=32)),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w())
        )),
        // 22: sum 19 ({1,2,3,4,9} or {1,2,3,6,7})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=19), c(d=19), c(d=19), c(d=19), c(d=19)),
            arrayOf<KCell>(c(r=19),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=19),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=19),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=19),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=19),   w(),     w(),     w(),     w(),     w())
        )),
        // 23: sum 31 ({3,4,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=31), c(d=31), c(d=31), c(d=31), c(d=31)),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w())
        )),
        // 24: sum 21 ({1,2,3,6,9} or {1,2,4,5,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=21), c(d=21), c(d=21), c(d=21), c(d=21)),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w())
        )),
        // 25: sum 29 ({2,5,6,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=29), c(d=29), c(d=29), c(d=29), c(d=29)),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w())
        )),
        // 26: sum 22 ({1,2,4,6,9} or {1,2,4,7,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=22), c(d=22), c(d=22), c(d=22), c(d=22)),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w())
        )),
        // 27: sum 28 ({1,4,6,8,9} or {2,4,5,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=28), c(d=28), c(d=28), c(d=28), c(d=28)),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w())
        )),
        // 28: sum 23 ({1,2,4,7,9} or {1,2,5,6,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=23), c(d=23), c(d=23), c(d=23), c(d=23)),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w(),     w())
        )),
        // 29: sum 27 ({1,3,6,8,9} or {2,3,6,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=27), c(d=27), c(d=27), c(d=27), c(d=27)),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w())
        )),
        // 30: sum 24 ({1,2,4,8,9} or {1,3,4,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=24), c(d=24), c(d=24), c(d=24), c(d=24)),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w())
        )),
        // 31: sum 26 ({1,3,5,8,9} or {2,4,5,7,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=26), c(d=26), c(d=26), c(d=26), c(d=26)),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w())
        )),
        // 32: sum 15 ({1,2,3,4,5})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=15), c(d=15), c(d=15), c(d=15), c(d=15)),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w())
        )),
        // 33: sum 35 ({5,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=35), c(d=35), c(d=35), c(d=35), c(d=35)),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w())
        )),
        // 34: sum 20 ({2,3,4,5,6})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=20), c(d=20), c(d=20), c(d=20), c(d=20)),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w())
        )),
        // 35: sum 30 ({4,5,6,7,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=30), c(d=30), c(d=30), c(d=30), c(d=30)),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w())
        ))
    )

    // EXPERT — 5x5 Latin squares (rows/cols sum to a constant per puzzle)
    private val EXPERT = listOf(
        // 1: sum 15 ({1,2,3,4,5})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=15), c(d=15), c(d=15), c(d=15), c(d=15)),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=15),   w(),     w(),     w(),     w(),     w())
        )),
        // 2: sum 20 ({2,3,4,5,6})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=20), c(d=20), c(d=20), c(d=20), c(d=20)),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=20),   w(),     w(),     w(),     w(),     w())
        )),
        // 3: sum 35 ({5,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=35), c(d=35), c(d=35), c(d=35), c(d=35)),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w())
        )),
        // 4: sum 25 ({3,4,5,6,7} or {1,3,5,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=25), c(d=25), c(d=25), c(d=25), c(d=25)),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w())
        )),
        // 5: sum 30 ({4,5,6,7,8} or {2,4,6,9,?}: limited)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=30), c(d=30), c(d=30), c(d=30), c(d=30)),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w())
        )),
        // 6: sum 18 ({1,2,3,4,8} etc.)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=18), c(d=18), c(d=18), c(d=18), c(d=18)),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=18),   w(),     w(),     w(),     w(),     w())
        )),
        // 7: sum 22
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=22), c(d=22), c(d=22), c(d=22), c(d=22)),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w())
        )),
        // 8: sum 28
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=28), c(d=28), c(d=28), c(d=28), c(d=28)),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w())
        )),
        // 9: sum 32
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=32), c(d=32), c(d=32), c(d=32), c(d=32)),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w())
        )),
        // 10: sum 16 (tight: {1,2,3,4,6} sums 16; {1,2,3,5,5} repeats)
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=16), c(d=16), c(d=16), c(d=16), c(d=16)),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=16),   w(),     w(),     w(),     w(),     w())
        ))
    ) + listOf(
        // 11-15: 6x6 white areas (+1 size step from Expert base)
        // 11: all sums = 21 ({1,2,3,4,5,6})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=21), c(d=21), c(d=21), c(d=21), c(d=21), c(d=21)),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 12: all sums = 39 ({4,5,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39)),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 13: all sums = 27 ({1,2,3,5,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=27), c(d=27), c(d=27), c(d=27), c(d=27), c(d=27)),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 14: all sums = 33 ({3,4,5,6,7,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=33), c(d=33), c(d=33), c(d=33), c(d=33), c(d=33)),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 15: all sums = 30 ({1,3,5,6,7,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=30), c(d=30), c(d=30), c(d=30), c(d=30), c(d=30)),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w())
        ))
    ) + listOf(
        // 16-45: more 6x6 white areas
        // 16: sum 22 ({1,2,3,4,5,7})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=22), c(d=22), c(d=22), c(d=22), c(d=22), c(d=22)),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 17: sum 38 ({3,5,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38)),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 18: sum 24 ({1,2,3,4,5,9} or {1,2,3,4,6,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=24), c(d=24), c(d=24), c(d=24), c(d=24), c(d=24)),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 19: sum 36 ({1,5,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36)),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 20: sum 25 ({1,2,3,4,6,9} or {1,2,3,5,6,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=25), c(d=25), c(d=25), c(d=25), c(d=25), c(d=25)),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 21: sum 35 ({1,4,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=35), c(d=35), c(d=35), c(d=35), c(d=35), c(d=35)),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 22: sum 26 ({1,2,3,4,7,9} or {1,2,4,5,6,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=26), c(d=26), c(d=26), c(d=26), c(d=26), c(d=26)),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 23: sum 34 ({1,4,5,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=34), c(d=34), c(d=34), c(d=34), c(d=34), c(d=34)),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 24: sum 28 ({1,2,4,5,7,9} or {1,2,3,6,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=28), c(d=28), c(d=28), c(d=28), c(d=28), c(d=28)),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 25: sum 32 ({2,3,5,6,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=32), c(d=32), c(d=32), c(d=32), c(d=32), c(d=32)),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 26: sum 21 ({1,2,3,4,5,6})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=21), c(d=21), c(d=21), c(d=21), c(d=21), c(d=21)),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=21),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 27: sum 39 ({4,5,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39)),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 28: sum 29 ({1,2,4,5,8,9} or {1,3,4,5,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=29), c(d=29), c(d=29), c(d=29), c(d=29), c(d=29)),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 29: sum 31 ({2,3,4,6,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=31), c(d=31), c(d=31), c(d=31), c(d=31), c(d=31)),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 30: sum 30 ({1,3,5,6,7,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=30), c(d=30), c(d=30), c(d=30), c(d=30), c(d=30)),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 31: sum 23 ({1,2,3,4,5,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=23), c(d=23), c(d=23), c(d=23), c(d=23), c(d=23)),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=23),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 32: sum 37 ({2,5,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37)),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 33: sum 27 ({2,3,4,5,6,7})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=27), c(d=27), c(d=27), c(d=27), c(d=27), c(d=27)),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=27),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 34: sum 33 ({3,4,5,6,7,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=33), c(d=33), c(d=33), c(d=33), c(d=33), c(d=33)),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 35: sum 22 ({1,2,3,4,5,7})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=22), c(d=22), c(d=22), c(d=22), c(d=22), c(d=22)),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=22),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 36: sum 38 ({3,5,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38)),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 37: sum 24 ({1,2,3,4,5,9} or {1,2,3,4,6,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=24), c(d=24), c(d=24), c(d=24), c(d=24), c(d=24)),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=24),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 38: sum 36 ({1,5,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36)),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 39: sum 25 ({1,2,3,4,6,9} or {1,2,3,5,6,8})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=25), c(d=25), c(d=25), c(d=25), c(d=25), c(d=25)),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=25),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 40: sum 35 ({1,4,6,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=35), c(d=35), c(d=35), c(d=35), c(d=35), c(d=35)),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 41: sum 26 ({1,2,3,4,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=26), c(d=26), c(d=26), c(d=26), c(d=26), c(d=26)),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=26),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 42: sum 34 ({1,4,5,7,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=34), c(d=34), c(d=34), c(d=34), c(d=34), c(d=34)),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 43: sum 28 ({1,2,4,5,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=28), c(d=28), c(d=28), c(d=28), c(d=28), c(d=28)),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 44: sum 32 ({2,3,5,6,7,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=32), c(d=32), c(d=32), c(d=32), c(d=32), c(d=32)),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 45: sum 29 ({1,2,4,5,8,9})
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=29), c(d=29), c(d=29), c(d=29), c(d=29), c(d=29)),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w())
        ))
    )

    // MASTER: levels 1-25 = 7x7, levels 26-55 = 8x8
    private val MASTER = listOf(
        // 1-25: 7x7 white areas
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=28), c(d=28), c(d=28), c(d=28), c(d=28), c(d=28), c(d=28)),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42)),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=35), c(d=35), c(d=35), c(d=35), c(d=35), c(d=35), c(d=35)),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=30), c(d=30), c(d=30), c(d=30), c(d=30), c(d=30), c(d=30)),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40)),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=29), c(d=29), c(d=29), c(d=29), c(d=29), c(d=29), c(d=29)),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=29),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=41), c(d=41), c(d=41), c(d=41), c(d=41), c(d=41), c(d=41)),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=31), c(d=31), c(d=31), c(d=31), c(d=31), c(d=31), c(d=31)),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39)),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=32), c(d=32), c(d=32), c(d=32), c(d=32), c(d=32), c(d=32)),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38)),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=33), c(d=33), c(d=33), c(d=33), c(d=33), c(d=33), c(d=33)),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=33),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37)),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=34), c(d=34), c(d=34), c(d=34), c(d=34), c(d=34), c(d=34)),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=34),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36)),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=28), c(d=28), c(d=28), c(d=28), c(d=28), c(d=28), c(d=28)),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=28),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42)),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=35), c(d=35), c(d=35), c(d=35), c(d=35), c(d=35), c(d=35)),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=35),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=30), c(d=30), c(d=30), c(d=30), c(d=30), c(d=30), c(d=30)),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=30),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40)),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=31), c(d=31), c(d=31), c(d=31), c(d=31), c(d=31), c(d=31)),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=31),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39)),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=32), c(d=32), c(d=32), c(d=32), c(d=32), c(d=32), c(d=32)),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=32),   w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        // 26-55: 8x8 white areas
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36)),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=44), c(d=44), c(d=44), c(d=44), c(d=44), c(d=44), c(d=44), c(d=44)),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40)),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37)),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=43), c(d=43), c(d=43), c(d=43), c(d=43), c(d=43), c(d=43), c(d=43)),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38)),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42)),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39)),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=41), c(d=41), c(d=41), c(d=41), c(d=41), c(d=41), c(d=41), c(d=41)),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36)),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=44), c(d=44), c(d=44), c(d=44), c(d=44), c(d=44), c(d=44), c(d=44)),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40)),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37)),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=43), c(d=43), c(d=43), c(d=43), c(d=43), c(d=43), c(d=43), c(d=43)),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38)),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42)),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39)),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=41), c(d=41), c(d=41), c(d=41), c(d=41), c(d=41), c(d=41), c(d=41)),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36)),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=44), c(d=44), c(d=44), c(d=44), c(d=44), c(d=44), c(d=44), c(d=44)),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40)),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37)),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=43), c(d=43), c(d=43), c(d=43), c(d=43), c(d=43), c(d=43), c(d=43)),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38), c(d=38)),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=38),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42), c(d=42)),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=42),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39), c(d=39)),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=39),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=41), c(d=41), c(d=41), c(d=41), c(d=41), c(d=41), c(d=41), c(d=41)),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=41),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36), c(d=36)),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=36),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=44), c(d=44), c(d=44), c(d=44), c(d=44), c(d=44), c(d=44), c(d=44)),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=44),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40), c(d=40)),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=40),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=43), c(d=43), c(d=43), c(d=43), c(d=43), c(d=43), c(d=43), c(d=43)),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=43),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        )),
        KakuroPuzzle(arrayOf(
            arrayOf<KCell>(b(),       c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37), c(d=37)),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w()),
            arrayOf<KCell>(c(r=37),   w(),     w(),     w(),     w(),     w(),     w(),     w(),     w())
        ))
    )

    fun get(difficulty: Int, index: Int): KakuroPuzzle {
        val pool = when (difficulty) {
            0 -> EASY; 1 -> MEDIUM; 2 -> HARD; 3 -> EXPERT; else -> MASTER
        }
        return pool[index.coerceIn(0, pool.size - 1)]
    }
}
