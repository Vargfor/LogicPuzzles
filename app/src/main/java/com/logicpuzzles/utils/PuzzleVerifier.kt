package com.logicpuzzles.utils

import android.util.Log
import com.logicpuzzles.futoshiki.FutoshikiPuzzle
import com.logicpuzzles.futoshiki.FutoshikiPuzzles
import com.logicpuzzles.hidato.HidatoPuzzle
import com.logicpuzzles.hidato.HidatoPuzzles
import com.logicpuzzles.kakuro.KCell
import com.logicpuzzles.kakuro.KakuroPuzzle
import com.logicpuzzles.kakuro.KakuroPuzzles
import com.logicpuzzles.skyscraper.SkyscraperPuzzle
import com.logicpuzzles.skyscraper.SkyscraperPuzzles
import kotlin.math.abs

/**
 * Backtracking solvers used to verify puzzle uniqueness.
 *
 * Each solver counts solutions, capped at 2 (we only need to know "0", "1", or "≥2").
 * Returns:
 *   0 → unsolvable
 *   1 → uniquely solvable (good)
 *   2 → multiple solutions (puzzle needs more clues)
 */
object PuzzleVerifier {

    private const val CAP = 2

    // ---------- Futoshiki ----------
    fun countFutoshikiSolutions(p: FutoshikiPuzzle): Int {
        val n = p.size
        val grid = Array(n) { p.initial[it].copyOf() }
        var count = 0

        fun consistent(r: Int, c: Int, v: Int): Boolean {
            for (cc in 0 until n) if (cc != c && grid[r][cc] == v) return false
            for (rr in 0 until n) if (rr != r && grid[rr][c] == v) return false
            if (c > 0 && grid[r][c - 1] != 0) {
                val con = p.hConstraints[r][c - 1]
                if (con == 1 && grid[r][c - 1] >= v) return false
                if (con == 2 && grid[r][c - 1] <= v) return false
            }
            if (c < n - 1 && grid[r][c + 1] != 0) {
                val con = p.hConstraints[r][c]
                if (con == 1 && v >= grid[r][c + 1]) return false
                if (con == 2 && v <= grid[r][c + 1]) return false
            }
            if (r > 0 && grid[r - 1][c] != 0) {
                val con = p.vConstraints[r - 1][c]
                if (con == 1 && grid[r - 1][c] >= v) return false
                if (con == 2 && grid[r - 1][c] <= v) return false
            }
            if (r < n - 1 && grid[r + 1][c] != 0) {
                val con = p.vConstraints[r][c]
                if (con == 1 && v >= grid[r + 1][c]) return false
                if (con == 2 && v <= grid[r + 1][c]) return false
            }
            return true
        }

        fun bt(): Boolean {
            for (r in 0 until n) for (c in 0 until n) {
                if (grid[r][c] == 0) {
                    for (v in 1..n) {
                        if (consistent(r, c, v)) {
                            grid[r][c] = v
                            if (bt()) return true
                            grid[r][c] = 0
                        }
                    }
                    return false
                }
            }
            count++
            return count >= CAP
        }
        bt()
        return count
    }

    // ---------- Hidato ----------
    fun countHidatoSolutions(p: HidatoPuzzle): Int {
        val rows = p.rows; val cols = p.cols
        val maxN = p.maxNumber
        val grid = Array(rows) { p.initial[it].copyOf() }
        val numToPos = HashMap<Int, Pair<Int, Int>>()
        for (r in 0 until rows) for (c in 0 until cols) {
            if (grid[r][c] > 0) numToPos[grid[r][c]] = r to c
        }
        var count = 0

        fun adj(a: Pair<Int, Int>, b: Pair<Int, Int>): Boolean =
            abs(a.first - b.first) <= 1 && abs(a.second - b.second) <= 1 && a != b

        fun bt(num: Int, last: Pair<Int, Int>?): Boolean {
            if (num > maxN) {
                count++
                return count >= CAP
            }
            val target = numToPos[num]
            if (target != null) {
                if (last == null || adj(last, target)) {
                    return bt(num + 1, target)
                }
                return false
            }
            val candidates: List<Pair<Int, Int>> = if (last == null) {
                val list = ArrayList<Pair<Int, Int>>()
                for (r in 0 until rows) for (c in 0 until cols) {
                    if (grid[r][c] == 0) list.add(r to c)
                }
                list
            } else {
                val list = ArrayList<Pair<Int, Int>>()
                for (dr in -1..1) for (dc in -1..1) {
                    if (dr == 0 && dc == 0) continue
                    val nr = last.first + dr; val nc = last.second + dc
                    if (nr in 0 until rows && nc in 0 until cols && grid[nr][nc] == 0) {
                        list.add(nr to nc)
                    }
                }
                list
            }
            for ((r, c) in candidates) {
                grid[r][c] = num
                if (bt(num + 1, r to c)) {
                    grid[r][c] = 0
                    return true
                }
                grid[r][c] = 0
            }
            return false
        }
        bt(1, null)
        return count
    }

    // ---------- Skyscraper ----------
    fun countSkyscraperSolutions(p: SkyscraperPuzzle): Int {
        val n = p.size
        val grid = Array(n) { p.initial[it].copyOf() }
        var count = 0

        fun visibility(line: IntArray): Int {
            var max = 0; var c = 0
            for (h in line) if (h > max) { c++; max = h }
            return c
        }

        fun isComplete(): Boolean {
            for (r in 0 until n) for (c in 0 until n) if (grid[r][c] == 0) return false
            return true
        }

        fun verifyClues(): Boolean {
            for (c in 0 until n) {
                val col = IntArray(n) { grid[it][c] }
                if (p.cluesTop[c] > 0 && visibility(col) != p.cluesTop[c]) return false
                if (p.cluesBottom[c] > 0 && visibility(col.reversedArray()) != p.cluesBottom[c]) return false
            }
            for (r in 0 until n) {
                if (p.cluesLeft[r] > 0 && visibility(grid[r]) != p.cluesLeft[r]) return false
                if (p.cluesRight[r] > 0 && visibility(grid[r].reversedArray()) != p.cluesRight[r]) return false
            }
            return true
        }

        fun consistent(r: Int, c: Int, v: Int): Boolean {
            for (cc in 0 until n) if (cc != c && grid[r][cc] == v) return false
            for (rr in 0 until n) if (rr != r && grid[rr][c] == v) return false
            return true
        }

        fun bt(): Boolean {
            for (r in 0 until n) for (c in 0 until n) {
                if (grid[r][c] == 0) {
                    for (v in 1..n) {
                        if (consistent(r, c, v)) {
                            grid[r][c] = v
                            if (bt()) return true
                            grid[r][c] = 0
                        }
                    }
                    return false
                }
            }
            if (verifyClues()) {
                count++
                return count >= CAP
            }
            return false
        }
        bt()
        return count
    }

    // ---------- Kakuro ----------
    fun countKakuroSolutions(p: KakuroPuzzle, initial: Array<IntArray>? = null): Int {
        val rows = p.rows; val cols = p.cols
        val grid = Array(rows) { r ->
            IntArray(cols) { c ->
                initial?.getOrNull(r)?.getOrNull(c) ?: 0
            }
        }
        var count = 0

        // Build run constraints: each run has its sum and the list of cells
        data class Run(val cells: List<Pair<Int, Int>>, val sum: Int)
        val runs = ArrayList<Run>()
        for (r in 0 until rows) for (c in 0 until cols) {
            val cell = p.grid[r][c]
            if (cell is KCell.Clue) {
                if (cell.rightSum > 0) {
                    val run = ArrayList<Pair<Int, Int>>()
                    var cc = c + 1
                    while (cc < cols && p.grid[r][cc] is KCell.White) {
                        run.add(r to cc); cc++
                    }
                    runs.add(Run(run, cell.rightSum))
                }
                if (cell.downSum > 0) {
                    val run = ArrayList<Pair<Int, Int>>()
                    var rr = r + 1
                    while (rr < rows && p.grid[rr][c] is KCell.White) {
                        run.add(rr to c); rr++
                    }
                    runs.add(Run(run, cell.downSum))
                }
            }
        }
        // For each white cell, list runs it belongs to
        val cellRuns = HashMap<Pair<Int, Int>, ArrayList<Int>>()
        for ((idx, run) in runs.withIndex()) {
            for (pos in run.cells) cellRuns.getOrPut(pos) { ArrayList() }.add(idx)
        }
        val whites = ArrayList<Pair<Int, Int>>()
        for (r in 0 until rows) for (c in 0 until cols) {
            if (p.grid[r][c] is KCell.White) whites.add(r to c)
        }

        fun runOk(runIdx: Int): Boolean {
            val r = runs[runIdx]
            var sum = 0; var any = false
            val seen = HashSet<Int>()
            for ((rr, cc) in r.cells) {
                val v = grid[rr][cc]
                if (v == 0) any = true
                else {
                    if (!seen.add(v)) return false
                    sum += v
                }
            }
            return if (any) sum < r.sum else sum == r.sum
        }

        fun bt(idx: Int): Boolean {
            if (idx == whites.size) {
                count++
                return count >= CAP
            }
            val (r, c) = whites[idx]
            if (grid[r][c] != 0) return bt(idx + 1)  // pre-filled
            for (v in 1..9) {
                grid[r][c] = v
                var ok = true
                for (ri in cellRuns[r to c]!!) {
                    if (!runOk(ri)) { ok = false; break }
                }
                if (ok && bt(idx + 1)) {
                    grid[r][c] = 0
                    return true
                }
                grid[r][c] = 0
            }
            return false
        }
        bt(0)
        return count
    }

    // ---------- Bulk verification (debug helper) ----------

    /**
     * Runs every solver against every puzzle of the supported types and logs the result.
     * Call this once from MainActivity (e.g. behind a BuildConfig.DEBUG check) to spot
     * non-uniquely-solvable puzzles. Output is via Log.d under the "PuzzleVerifier" tag.
     *
     * Counts are capped at 2 (we only need to know "0", "1", or "≥2"). A count of 1 is
     * the goal; ≥2 means the puzzle has multiple valid solutions and could use more clues.
     */
    fun verifyAll(typesToCheck: Set<String> = setOf("futoshiki", "hidato", "skyscraper", "kakuro")) {
        val tag = "PuzzleVerifier"
        var solvable = 0; var unsolv = 0

        fun report(name: String, d: Int, i: Int, n: Int) {
            when (n) {
                0 -> { unsolv++; Log.w(tag, "$name [d=$d i=$i] UNSOLVABLE") }
                1 -> { solvable++; Log.d(tag, "$name [d=$d i=$i] solvable (unique)") }
                else -> { solvable++; Log.d(tag, "$name [d=$d i=$i] solvable (multiple)") }
            }
        }

        for (d in 0 until PrefsManager.DIFFICULTIES) {
            for (i in 0 until PrefsManager.getPuzzleCount(0, d)) {
                if ("futoshiki" in typesToCheck) {
                    val p = FutoshikiPuzzles.get(d, i)
                    report("Futoshiki", d, i, countFutoshikiSolutions(p))
                }
                if ("hidato" in typesToCheck) {
                    val p = HidatoPuzzles.get(d, i)
                    report("Hidato", d, i, countHidatoSolutions(p))
                }
                if ("skyscraper" in typesToCheck) {
                    val p = SkyscraperPuzzles.get(d, i)
                    report("Skyscraper", d, i, countSkyscraperSolutions(p))
                }
                if ("kakuro" in typesToCheck) {
                    val p = KakuroPuzzles.get(d, i)
                    report("Kakuro", d, i, countKakuroSolutions(p, p.initial))
                }
            }
        }
        Log.i(tag, "Done. solvable=$solvable unsolvable=$unsolv")
    }
}
