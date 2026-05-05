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
    )

    fun get(difficulty: Int, index: Int): KakuroPuzzle {
        val pool = when (difficulty) {
            0 -> EASY; 1 -> MEDIUM; 2 -> HARD; else -> EXPERT
        }
        return pool[index.coerceIn(0, pool.size - 1)]
    }
}
