package com.logicpuzzles

import com.logicpuzzles.futoshiki.FutoshikiPuzzle
import com.logicpuzzles.futoshiki.FutoshikiPuzzles
import com.logicpuzzles.hidato.HidatoPuzzle
import com.logicpuzzles.hidato.HidatoPuzzles
import com.logicpuzzles.kakuro.KCell
import com.logicpuzzles.kakuro.KakuroPuzzle
import com.logicpuzzles.kakuro.KakuroPuzzles
import com.logicpuzzles.lightsout.LightsOutPuzzle
import com.logicpuzzles.lightsout.LightsOutPuzzles
import com.logicpuzzles.logicgrid.LogicGridPuzzle
import com.logicpuzzles.logicgrid.LogicGridPuzzles
import com.logicpuzzles.mastermind.MastermindData
import com.logicpuzzles.nonogram.NonogramPuzzles
import com.logicpuzzles.nurikabe.NurikabePuzzle
import com.logicpuzzles.nurikabe.NurikabePuzzles
import com.logicpuzzles.skyscraper.SkyscraperPuzzle
import com.logicpuzzles.skyscraper.SkyscraperPuzzles
import com.logicpuzzles.slitherlink.SlitherlinkPuzzle
import com.logicpuzzles.slitherlink.SlitherlinkPuzzles
import com.logicpuzzles.utils.PuzzleVerifier
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleCatalogVerificationTest {

    private data class CatalogCheck(
        val name: String,
        val signature: (difficulty: Int, index: Int) -> String,
        val sizeSignature: (difficulty: Int, index: Int) -> String,
        val minimumDistinctPerDifficulty: Int = 15
    )

    private fun missing(message: String): Nothing = throw AssertionError(message)

    @Test
    fun catalogsVaryBySeededContentNotOnlyByBoardSize() {
        val catalogs = listOf(
            CatalogCheck(
                name = "Nonogram",
                signature = { d, i -> intGridSignature(NonogramPuzzles.get(d, i)) },
                sizeSignature = { d, i -> dimensions(NonogramPuzzles.get(d, i)) }
            ),
            CatalogCheck(
                name = "Mastermind",
                signature = { d, i -> mastermindSignature(d, i) },
                sizeSignature = { d, i -> mastermindConfigSignature(d, i) }
            ),
            CatalogCheck(
                name = "Lights Out",
                signature = { d, i -> boolGridSignature(LightsOutPuzzles.get(d, i).initial) },
                sizeSignature = { d, i -> LightsOutPuzzles.get(d, i).size.toString() }
            ),
            CatalogCheck(
                name = "Kakuro",
                signature = { d, i -> kakuroSignature(KakuroPuzzles.get(d, i)) },
                sizeSignature = { d, i -> dimensions(KakuroPuzzles.get(d, i)) }
            ),
            CatalogCheck(
                name = "Logic Grid",
                signature = { d, i -> logicGridSignature(LogicGridPuzzles.get(d, i)) },
                sizeSignature = { d, i -> logicGridSizeSignature(LogicGridPuzzles.get(d, i)) }
            ),
            CatalogCheck(
                name = "Slitherlink",
                signature = { d, i -> intGridSignature(SlitherlinkPuzzles.get(d, i).clues) },
                sizeSignature = { d, i -> dimensions(SlitherlinkPuzzles.get(d, i)) }
            ),
            CatalogCheck(
                name = "Nurikabe",
                signature = { d, i -> intGridSignature(NurikabePuzzles.get(d, i).numbers) },
                sizeSignature = { d, i -> dimensions(NurikabePuzzles.get(d, i)) },
                minimumDistinctPerDifficulty = 12
            ),
            CatalogCheck(
                name = "Hidato",
                signature = { d, i -> intGridSignature(HidatoPuzzles.get(d, i).initial) },
                sizeSignature = { d, i -> dimensions(HidatoPuzzles.get(d, i)) }
            ),
            CatalogCheck(
                name = "Futoshiki",
                signature = { d, i -> futoshikiSignature(FutoshikiPuzzles.get(d, i)) },
                sizeSignature = { d, i -> FutoshikiPuzzles.get(d, i).size.toString() }
            ),
            CatalogCheck(
                name = "Skyscraper",
                signature = { d, i -> skyscraperSignature(SkyscraperPuzzles.get(d, i)) },
                sizeSignature = { d, i -> SkyscraperPuzzles.get(d, i).size.toString() },
                minimumDistinctPerDifficulty = 14
            )
        )

        for (catalog in catalogs) {
            for (difficulty in 0 until DIFFICULTIES) {
                val levelsInDifficulty = levelCount(difficulty)
                val signatures = (0 until levelsInDifficulty).map { catalog.signature(difficulty, it) }
                val sizes = (0 until levelsInDifficulty).map { catalog.sizeSignature(difficulty, it) }
                assertEquals(
                    "${catalog.name} difficulty $difficulty should expose $levelsInDifficulty levels",
                    levelsInDifficulty,
                    signatures.size
                )
                assertTrue(
                    "${catalog.name} difficulty $difficulty repeats content too often (${signatures.toSet().size}/${catalog.minimumDistinctPerDifficulty})",
                    signatures.toSet().size >= minOf(catalog.minimumDistinctPerDifficulty, levelsInDifficulty)
                )
                assertTrue(
                    "${catalog.name} difficulty $difficulty changes only board/config size",
                    signatures.toSet().size > sizes.toSet().size
                )
            }
        }

        assertNotEquals(
            "Futoshiki Hard and Expert share a size, so givens/constraints must change difficulty.",
            futoshikiDifficultyProfile(2, 0),
            futoshikiDifficultyProfile(3, 0)
        )
        assertNotEquals(
            "Hidato Hard and Expert share a size, so clue density must change difficulty.",
            hidatoDifficultyProfile(2, 0),
            hidatoDifficultyProfile(3, 0)
        )
        assertTrue(
            "Lights Out Easy should stay a short solve.",
            maxLightsOutMinimumMoves(0) <= 4
        )
        assertTrue(
            "Lights Out should require more presses as difficulty rises.",
            averageLightsOutMinimumMoves(0) < averageLightsOutMinimumMoves(1) &&
                averageLightsOutMinimumMoves(1) < averageLightsOutMinimumMoves(2) &&
                averageLightsOutMinimumMoves(2) < averageLightsOutMinimumMoves(3)
        )
        assertTrue(
            "Slitherlink should reduce shown-clue density as difficulty rises.",
            averageSlitherlinkClueDensity(3) < averageSlitherlinkClueDensity(0)
        )
        assertTrue(
            "Nurikabe should use larger islands as difficulty rises.",
            averageNurikabeClueSize(3) > averageNurikabeClueSize(0)
        )
        assertTrue(
            "Nurikabe should reduce clue density as difficulty rises.",
            averageNurikabeClueDensity(3) < averageNurikabeClueDensity(0)
        )
        assertTrue(
            "Nurikabe Expert should mix 1-8 and 1-9 island profiles.",
            expertNurikabeMaxClues().containsAll(listOf(8, 9))
        )
        assertTrue(
            "Skyscraper should reduce starting givens as difficulty rises.",
            averageSkyscraperGivenDensity(0) > averageSkyscraperGivenDensity(1) &&
                averageSkyscraperGivenDensity(1) > averageSkyscraperGivenDensity(2) &&
                averageSkyscraperGivenDensity(2) > averageSkyscraperGivenDensity(3)
        )
    }

    @Test
    fun everyLevelHasAValidSolutionPathOrSolverWitness() {
        for (difficulty in 0 until DIFFICULTIES) {
            for (index in 0 until levelCount(difficulty)) {
                assertNonogramSolutionValid(NonogramPuzzles.get(difficulty, index), "Nonogram", difficulty, index)
                assertMastermindLevelValid(difficulty, index)
                val lightsOutMoves = minimumLightsOutMoves(LightsOutPuzzles.get(difficulty, index))
                assertTrue("Lights Out [d=$difficulty i=$index] is not solvable", lightsOutMoves != null)
                assertTrue(
                    "Kakuro [d=$difficulty i=$index] has no solver witness",
                    kakuroHasSolution(KakuroPuzzles.get(difficulty, index))
                )
                assertLogicGridSolutionValid(LogicGridPuzzles.get(difficulty, index), difficulty, index)
                assertSlitherlinkSolutionValid(SlitherlinkPuzzles.get(difficulty, index), difficulty, index)
                assertNurikabeSolutionValid(NurikabePuzzles.get(difficulty, index), difficulty, index)
                assertHidatoSolutionValid(HidatoPuzzles.get(difficulty, index), difficulty, index)
                assertFutoshikiSolutionValid(FutoshikiPuzzles.get(difficulty, index), difficulty, index)
                assertSkyscraperSolutionValid(SkyscraperPuzzles.get(difficulty, index), difficulty, index)
            }
        }
    }

    private fun assertMastermindLevelValid(difficulty: Int, index: Int) {
        val level = MastermindData.levelFor(difficulty, index)
        assertTrue("Mastermind [d=$difficulty i=$index] positions", level.positions in 4..8)
        assertTrue("Mastermind [d=$difficulty i=$index] colors", level.numColors in level.positions..10)
        assertTrue("Mastermind [d=$difficulty i=$index] guesses", level.maxGuesses >= 4)
        assertEquals("Mastermind [d=$difficulty i=$index] secret length", level.positions, level.secret.size)
        assertTrue(
            "Mastermind [d=$difficulty i=$index] secret color outside picker",
            level.secret.all { it in 0 until level.numColors }
        )
        if (!level.allowDuplicates) {
            assertEquals(
                "Mastermind [d=$difficulty i=$index] duplicate in no-duplicate level",
                level.secret.size,
                level.secret.toSet().size
            )
        }
    }

    private fun assertNonogramSolutionValid(solution: Array<IntArray>, name: String, difficulty: Int, index: Int) {
        val cols = solution.firstOrNull()?.size ?: missing("$name [d=$difficulty i=$index] has no rows")
        for (row in solution) {
            assertEquals("$name [d=$difficulty i=$index] row width mismatch", cols, row.size)
            assertTrue("$name [d=$difficulty i=$index] contains non-binary cells", row.all { it == 0 || it == 1 })
            assertTrue(
                "$name [d=$difficulty i=$index] row does not match its own clues",
                lineMatchesClues(row, cluesForLine(row))
            )
        }
        for (c in 0 until cols) {
            val col = IntArray(solution.size) { r -> solution[r][c] }
            assertTrue(
                "$name [d=$difficulty i=$index] column does not match its own clues",
                lineMatchesClues(col, cluesForLine(col))
            )
        }
    }

    private fun assertLogicGridSolutionValid(puzzle: LogicGridPuzzle, difficulty: Int, index: Int) {
        assertEquals("Logic Grid [d=$difficulty i=$index] category/item mismatch", puzzle.categories.size, puzzle.items.size)
        assertTrue("Logic Grid [d=$difficulty i=$index] has no clues", puzzle.clues.isNotEmpty())
        val entries = puzzle.items.first().size
        assertEquals("Logic Grid [d=$difficulty i=$index] entry count", entries, puzzle.solution.size)
        for (entry in puzzle.solution) {
            assertEquals("Logic Grid [d=$difficulty i=$index] solution width", puzzle.categories.size, entry.size)
        }
        for (cat in puzzle.categories.indices) {
            val expected = (0 until puzzle.items[cat].size).toSet()
            val actual = puzzle.solution.map { it[cat] }.toSet()
            assertEquals("Logic Grid [d=$difficulty i=$index] category ${puzzle.categories[cat]} is not one-to-one", expected, actual)
        }
    }

    private fun assertFutoshikiSolutionValid(puzzle: FutoshikiPuzzle, difficulty: Int, index: Int) {
        val solution = puzzle.solution ?: missing("Futoshiki [d=$difficulty i=$index] has no stored solution")
        assertLatinSquare(solution, puzzle.size, "Futoshiki [d=$difficulty i=$index]")
        for (r in 0 until puzzle.size) {
            for (c in 0 until puzzle.size) {
                val given = puzzle.initial[r][c]
                if (given != 0) {
                    assertEquals("Futoshiki [d=$difficulty i=$index] given mismatch", solution[r][c], given)
                }
                if (c < puzzle.size - 1) {
                    when (puzzle.hConstraints[r][c]) {
                        1 -> assertTrue("Futoshiki [d=$difficulty i=$index] horizontal < mismatch", solution[r][c] < solution[r][c + 1])
                        2 -> assertTrue("Futoshiki [d=$difficulty i=$index] horizontal > mismatch", solution[r][c] > solution[r][c + 1])
                    }
                }
                if (r < puzzle.size - 1) {
                    when (puzzle.vConstraints[r][c]) {
                        1 -> assertTrue("Futoshiki [d=$difficulty i=$index] vertical < mismatch", solution[r][c] < solution[r + 1][c])
                        2 -> assertTrue("Futoshiki [d=$difficulty i=$index] vertical > mismatch", solution[r][c] > solution[r + 1][c])
                    }
                }
            }
        }
    }

    private fun assertHidatoSolutionValid(puzzle: HidatoPuzzle, difficulty: Int, index: Int) {
        val solution = puzzle.solution ?: missing("Hidato [d=$difficulty i=$index] has no stored solution")
        val positions = Array<Pair<Int, Int>?>(puzzle.maxNumber + 1) { null }
        for (r in 0 until puzzle.rows) {
            for (c in 0 until puzzle.cols) {
                val value = solution[r][c]
                assertTrue("Hidato [d=$difficulty i=$index] value out of range", value in 1..puzzle.maxNumber)
                positions[value] = r to c
                val given = puzzle.initial[r][c]
                if (given > 0) {
                    assertEquals("Hidato [d=$difficulty i=$index] given mismatch", value, given)
                }
            }
        }
        for (number in 1..puzzle.maxNumber) {
            assertTrue("Hidato [d=$difficulty i=$index] missing $number", positions[number] != null)
        }
        for (number in 1 until puzzle.maxNumber) {
            val a = positions[number]!!
            val b = positions[number + 1]!!
            assertTrue(
                "Hidato [d=$difficulty i=$index] $number and ${number + 1} are not adjacent",
                abs(a.first - b.first) <= 1 && abs(a.second - b.second) <= 1 && a != b
            )
        }
    }

    private fun assertSkyscraperSolutionValid(puzzle: SkyscraperPuzzle, difficulty: Int, index: Int) {
        val solution = puzzle.solution ?: missing("Skyscraper [d=$difficulty i=$index] has no stored solution")
        assertLatinSquare(solution, puzzle.size, "Skyscraper [d=$difficulty i=$index]")
        assertEquals("Skyscraper [d=$difficulty i=$index] initial row count", puzzle.size, puzzle.initial.size)
        var givens = 0
        for (r in 0 until puzzle.size) {
            assertEquals("Skyscraper [d=$difficulty i=$index] initial row width", puzzle.size, puzzle.initial[r].size)
            for (c in 0 until puzzle.size) {
                val given = puzzle.initial[r][c]
                assertTrue("Skyscraper [d=$difficulty i=$index] given out of range", given in 0..puzzle.size)
                if (given > 0) {
                    givens++
                    assertEquals("Skyscraper [d=$difficulty i=$index] given mismatch", solution[r][c], given)
                }
            }
        }
        for (c in 0 until puzzle.size) {
            val col = IntArray(puzzle.size) { r -> solution[r][c] }
            assertEquals("Skyscraper [d=$difficulty i=$index] top clue", puzzle.cluesTop[c], visibility(col))
            assertEquals("Skyscraper [d=$difficulty i=$index] bottom clue", puzzle.cluesBottom[c], visibility(col.reversedArray()))
        }
        for (r in 0 until puzzle.size) {
            assertEquals("Skyscraper [d=$difficulty i=$index] left clue", puzzle.cluesLeft[r], visibility(solution[r]))
            assertEquals("Skyscraper [d=$difficulty i=$index] right clue", puzzle.cluesRight[r], visibility(solution[r].reversedArray()))
        }
        if (difficulty == 0) {
            val minimumEasyGivens = if (puzzle.size == 4) 10 else 14
            assertTrue(
                "Skyscraper Easy [i=$index] should have enough givens to be easy",
                givens >= minimumEasyGivens
            )
            assertEquals(
                "Skyscraper Easy [i=$index] should be uniquely solvable",
                1,
                PuzzleVerifier.countSkyscraperSolutions(puzzle)
            )
        }
    }

    private fun assertSlitherlinkSolutionValid(puzzle: SlitherlinkPuzzle, difficulty: Int, index: Int) {
        val hEdges = puzzle.solutionHEdges ?: missing("Slitherlink [d=$difficulty i=$index] has no horizontal solution")
        val vEdges = puzzle.solutionVEdges ?: missing("Slitherlink [d=$difficulty i=$index] has no vertical solution")
        assertEquals("Slitherlink [d=$difficulty i=$index] h edge rows", puzzle.rows + 1, hEdges.size)
        assertEquals("Slitherlink [d=$difficulty i=$index] v edge rows", puzzle.rows, vEdges.size)

        val degree = Array(puzzle.rows + 1) { IntArray(puzzle.cols + 1) }
        val adjacency = HashMap<Pair<Int, Int>, MutableList<Pair<Int, Int>>>()
        var edgeCount = 0

        fun addEdge(a: Pair<Int, Int>, b: Pair<Int, Int>) {
            degree[a.first][a.second]++
            degree[b.first][b.second]++
            adjacency.getOrPut(a) { mutableListOf() }.add(b)
            adjacency.getOrPut(b) { mutableListOf() }.add(a)
            edgeCount++
        }

        for (r in hEdges.indices) {
            assertEquals("Slitherlink [d=$difficulty i=$index] h edge cols", puzzle.cols, hEdges[r].size)
            for (c in 0 until puzzle.cols) {
                if (hEdges[r][c]) addEdge(r to c, r to c + 1)
            }
        }
        for (r in vEdges.indices) {
            assertEquals("Slitherlink [d=$difficulty i=$index] v edge cols", puzzle.cols + 1, vEdges[r].size)
            for (c in 0..puzzle.cols) {
                if (vEdges[r][c]) addEdge(r to c, r + 1 to c)
            }
        }

        assertTrue("Slitherlink [d=$difficulty i=$index] solution has no loop edges", edgeCount > 0)
        for (r in 0..puzzle.rows) {
            for (c in 0..puzzle.cols) {
                assertTrue(
                    "Slitherlink [d=$difficulty i=$index] vertex degree must be 0 or 2",
                    degree[r][c] == 0 || degree[r][c] == 2
                )
            }
        }
        for (r in 0 until puzzle.rows) {
            for (c in 0 until puzzle.cols) {
                val clue = puzzle.clues[r][c]
                if (clue >= 0) {
                    val actual = listOf(hEdges[r][c], hEdges[r + 1][c], vEdges[r][c], vEdges[r][c + 1]).count { it }
                    assertEquals("Slitherlink [d=$difficulty i=$index] clue mismatch", clue, actual)
                }
            }
        }

        val loopVertices = adjacency.keys
        val seen = mutableSetOf<Pair<Int, Int>>()
        val queue = ArrayDeque<Pair<Int, Int>>()
        queue.add(loopVertices.first())
        seen.add(loopVertices.first())
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            for (next in adjacency[current].orEmpty()) {
                if (seen.add(next)) queue.add(next)
            }
        }
        assertEquals("Slitherlink [d=$difficulty i=$index] solution is not one closed loop", loopVertices.size, seen.size)
    }

    private fun assertNurikabeSolutionValid(puzzle: NurikabePuzzle, difficulty: Int, index: Int) {
        val shaded = puzzle.solutionShaded ?: missing("Nurikabe [d=$difficulty i=$index] has no stored solution")
        val rows = puzzle.rows
        val cols = puzzle.cols
        assertEquals("Nurikabe [d=$difficulty i=$index] solution row count", rows, shaded.size)
        for (r in 0 until rows) {
            assertEquals("Nurikabe [d=$difficulty i=$index] solution col count", cols, shaded[r].size)
            for (c in 0 until cols) {
                if (puzzle.numbers[r][c] > 0) {
                    assertTrue("Nurikabe [d=$difficulty i=$index] clue is shaded in solution", !shaded[r][c])
                }
            }
        }
        assertTrue(
            "Nurikabe [d=$difficulty i=$index] has only one-cell island clues",
            puzzle.numbers.any { row -> row.any { it > 1 } }
        )
        assertNurikabeClueProfile(puzzle, difficulty, index)

        for (r in 0 until rows - 1) {
            for (c in 0 until cols - 1) {
                assertTrue(
                    "Nurikabe [d=$difficulty i=$index] has a 2x2 shaded block",
                    !shaded[r][c] ||
                        !shaded[r + 1][c] ||
                        !shaded[r][c + 1] ||
                        !shaded[r + 1][c + 1]
                )
            }
        }
        assertNurikabeIslandsMatchClues(puzzle, shaded, difficulty, index)
        assertShadedCellsConnected(puzzle, shaded, difficulty, index)
    }

    private fun assertNurikabeIslandsMatchClues(
        puzzle: NurikabePuzzle,
        shaded: Array<BooleanArray>,
        difficulty: Int,
        index: Int
    ) {
        val visited = Array(puzzle.rows) { BooleanArray(puzzle.cols) }
        for (r in 0 until puzzle.rows) {
            for (c in 0 until puzzle.cols) {
                if (shaded[r][c] || visited[r][c]) continue
                val region = mutableListOf<Pair<Int, Int>>()
                val queue = ArrayDeque<Pair<Int, Int>>()
                queue.add(r to c)
                visited[r][c] = true
                while (queue.isNotEmpty()) {
                    val (cr, cc) = queue.removeFirst()
                    region.add(cr to cc)
                    for ((dr, dc) in ORTHOGONAL) {
                        val nr = cr + dr
                        val nc = cc + dc
                        if (nr in 0 until puzzle.rows && nc in 0 until puzzle.cols && !shaded[nr][nc] && !visited[nr][nc]) {
                            visited[nr][nc] = true
                            queue.add(nr to nc)
                        }
                    }
                }
                val clues = region.filter { (rr, cc) -> puzzle.numbers[rr][cc] > 0 }
                assertEquals("Nurikabe [d=$difficulty i=$index] island should have one clue", 1, clues.size)
                val clue = clues.first()
                assertEquals(
                    "Nurikabe [d=$difficulty i=$index] island clue size mismatch",
                    puzzle.numbers[clue.first][clue.second],
                    region.size
                )
            }
        }
    }

    private fun assertShadedCellsConnected(
        puzzle: NurikabePuzzle,
        shaded: Array<BooleanArray>,
        difficulty: Int,
        index: Int
    ) {
        var start: Pair<Int, Int>? = null
        var total = 0
        for (r in 0 until puzzle.rows) {
            for (c in 0 until puzzle.cols) {
                if (shaded[r][c]) {
                    total++
                    if (start == null) start = r to c
                }
            }
        }
        val first = start ?: missing("Nurikabe [d=$difficulty i=$index] has no shaded cells")
        val seen = Array(puzzle.rows) { BooleanArray(puzzle.cols) }
        val queue = ArrayDeque<Pair<Int, Int>>()
        queue.add(first)
        seen[first.first][first.second] = true
        var reached = 0
        while (queue.isNotEmpty()) {
            val (r, c) = queue.removeFirst()
            reached++
            for ((dr, dc) in ORTHOGONAL) {
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until puzzle.rows && nc in 0 until puzzle.cols && shaded[nr][nc] && !seen[nr][nc]) {
                    seen[nr][nc] = true
                    queue.add(nr to nc)
                }
            }
        }
        assertEquals("Nurikabe [d=$difficulty i=$index] shaded cells are disconnected", total, reached)
    }

    private fun minimumLightsOutMoves(puzzle: LightsOutPuzzle): Int? {
        val n = puzzle.size
        var best: Int? = null

        fun press(grid: Array<BooleanArray>, r: Int, c: Int) {
            grid[r][c] = !grid[r][c]
            if (r > 0) grid[r - 1][c] = !grid[r - 1][c]
            if (r < n - 1) grid[r + 1][c] = !grid[r + 1][c]
            if (c > 0) grid[r][c - 1] = !grid[r][c - 1]
            if (c < n - 1) grid[r][c + 1] = !grid[r][c + 1]
        }

        for (firstRowMask in 0 until (1 shl n)) {
            val grid = Array(n) { r -> puzzle.initial[r].copyOf() }
            var moves = 0

            for (c in 0 until n) {
                if ((firstRowMask and (1 shl c)) != 0) {
                    press(grid, 0, c)
                    moves++
                }
            }
            for (r in 1 until n) {
                for (c in 0 until n) {
                    if (grid[r - 1][c]) {
                        press(grid, r, c)
                        moves++
                    }
                }
            }
            val currentBest = best
            if (grid.all { row -> row.none { it } } && (currentBest == null || moves < currentBest)) {
                best = moves
            }
        }
        return best
    }

    private fun assertNurikabeClueProfile(puzzle: NurikabePuzzle, difficulty: Int, index: Int) {
        val clues = puzzle.numbers.flatMap { row -> row.filter { it > 0 } }
        val maxClue = clues.maxOrNull() ?: missing("Nurikabe [d=$difficulty i=$index] has no clues")
        val expectedMax = expectedNurikabeMaxClue(difficulty, index)
        if (expectedMax == null) {
            assertTrue("Nurikabe Expert [i=$index] should use a 1-8 or 1-9 profile", maxClue in 8..9)
            assertTrue("Nurikabe Expert [i=$index] clue above 9", clues.all { it in 1..9 })
        } else {
            assertEquals("Nurikabe [d=$difficulty i=$index] max clue profile", expectedMax, maxClue)
            assertTrue("Nurikabe [d=$difficulty i=$index] clue above profile", clues.all { it in 1..expectedMax })
        }
    }

    private fun expectedNurikabeMaxClue(difficulty: Int, index: Int): Int? = when (difficulty) {
        0 -> if (index >= 10) 3 else 2
        1 -> if (index >= 10) 4 else 3
        2 -> if (index >= 10) 6 else 5
        3 -> null
        else -> when {
            index >= 25 -> 10
            else -> 9
        }
    }

    private data class KakuroRun(val cells: List<Pair<Int, Int>>, val sum: Int)

    private fun kakuroHasSolution(puzzle: KakuroPuzzle): Boolean {
        val runs = kakuroRuns(puzzle)
        val assignments = runs.map { run -> kakuroAssignments(run.cells.size, run.sum) }
        val grid = Array(puzzle.rows) { r ->
            IntArray(puzzle.cols) { c -> puzzle.initialAt(r, c) }
        }
        val assigned = BooleanArray(runs.size)

        fun compatible(run: KakuroRun, assignment: IntArray): Boolean {
            for (i in run.cells.indices) {
                val (r, c) = run.cells[i]
                val current = grid[r][c]
                if (current != 0 && current != assignment[i]) return false
            }
            return true
        }

        fun search(): Boolean {
            if (assigned.all { it }) return true

            var bestRun = -1
            var bestOptions: List<IntArray> = emptyList()
            for (runIndex in runs.indices) {
                if (assigned[runIndex]) continue
                val options = assignments[runIndex].filter { compatible(runs[runIndex], it) }
                if (options.isEmpty()) return false
                if (bestRun == -1 || options.size < bestOptions.size) {
                    bestRun = runIndex
                    bestOptions = options
                }
            }

            assigned[bestRun] = true
            val run = runs[bestRun]
            for (assignment in bestOptions) {
                val changed = ArrayList<Pair<Int, Int>>()
                var ok = true
                for (i in run.cells.indices) {
                    val (r, c) = run.cells[i]
                    val current = grid[r][c]
                    if (current != 0 && current != assignment[i]) {
                        ok = false
                        break
                    }
                    if (current == 0) {
                        grid[r][c] = assignment[i]
                        changed.add(r to c)
                    }
                }
                if (ok && search()) return true
                for ((r, c) in changed) grid[r][c] = 0
            }
            assigned[bestRun] = false
            return false
        }

        return search()
    }

    private fun kakuroRuns(puzzle: KakuroPuzzle): List<KakuroRun> {
        val runs = mutableListOf<KakuroRun>()
        for (r in 0 until puzzle.rows) {
            for (c in 0 until puzzle.cols) {
                val cell = puzzle.grid[r][c]
                if (cell is KCell.Clue) {
                    if (cell.rightSum > 0) {
                        val cells = mutableListOf<Pair<Int, Int>>()
                        var cc = c + 1
                        while (cc < puzzle.cols && puzzle.grid[r][cc] is KCell.White) {
                            cells.add(r to cc)
                            cc++
                        }
                        runs.add(KakuroRun(cells, cell.rightSum))
                    }
                    if (cell.downSum > 0) {
                        val cells = mutableListOf<Pair<Int, Int>>()
                        var rr = r + 1
                        while (rr < puzzle.rows && puzzle.grid[rr][c] is KCell.White) {
                            cells.add(rr to c)
                            rr++
                        }
                        runs.add(KakuroRun(cells, cell.downSum))
                    }
                }
            }
        }
        return runs
    }

    private fun kakuroAssignments(length: Int, sum: Int): List<IntArray> {
        val result = mutableListOf<IntArray>()
        val current = IntArray(length)
        val used = BooleanArray(10)

        fun dfs(position: Int, remaining: Int) {
            if (position == length) {
                if (remaining == 0) result.add(current.copyOf())
                return
            }
            for (value in 1..9) {
                if (used[value] || value > remaining) continue
                used[value] = true
                current[position] = value
                dfs(position + 1, remaining - value)
                used[value] = false
            }
        }

        dfs(0, sum)
        return result
    }

    private fun assertLatinSquare(grid: Array<IntArray>, size: Int, label: String) {
        val expected = (1..size).toSet()
        assertEquals("$label row count", size, grid.size)
        for (r in 0 until size) {
            assertEquals("$label row width", size, grid[r].size)
            assertEquals("$label row $r", expected, grid[r].toSet())
        }
        for (c in 0 until size) {
            assertEquals("$label column $c", expected, IntArray(size) { r -> grid[r][c] }.toSet())
        }
    }

    private fun visibility(line: IntArray): Int {
        var tallest = 0
        var count = 0
        for (value in line) {
            if (value > tallest) {
                tallest = value
                count++
            }
        }
        return count
    }

    private fun cluesForLine(line: IntArray): List<Int> {
        val clues = mutableListOf<Int>()
        var run = 0
        for (cell in line) {
            if (cell == 1) {
                run++
            } else if (run > 0) {
                clues.add(run)
                run = 0
            }
        }
        if (run > 0) clues.add(run)
        return if (clues.isEmpty()) listOf(0) else clues
    }

    private fun lineMatchesClues(line: IntArray, clues: List<Int>): Boolean = cluesForLine(line) == clues

    private fun averageLightsOutMinimumMoves(difficulty: Int): Double =
        (0 until levelCount(difficulty)).map { index ->
            minimumLightsOutMoves(LightsOutPuzzles.get(difficulty, index))
                ?: missing("Lights Out [d=$difficulty i=$index] is not solvable")
        }.average()

    private fun maxLightsOutMinimumMoves(difficulty: Int): Int =
        (0 until levelCount(difficulty)).maxOf { index ->
            minimumLightsOutMoves(LightsOutPuzzles.get(difficulty, index))
                ?: missing("Lights Out [d=$difficulty i=$index] is not solvable")
        }

    private fun averageSlitherlinkClueDensity(difficulty: Int): Double =
        (0 until levelCount(difficulty)).map { index ->
            val puzzle = SlitherlinkPuzzles.get(difficulty, index)
            puzzle.clues.sumOf { row -> row.count { it >= 0 } }.toDouble() / (puzzle.rows * puzzle.cols)
        }.average()

    private fun averageNurikabeClueDensity(difficulty: Int): Double =
        (0 until levelCount(difficulty)).map { index ->
            val puzzle = NurikabePuzzles.get(difficulty, index)
            puzzle.numbers.sumOf { row -> row.count { it > 0 } }.toDouble() / (puzzle.rows * puzzle.cols)
        }.average()

    private fun averageNurikabeClueSize(difficulty: Int): Double =
        (0 until levelCount(difficulty)).flatMap { index ->
            NurikabePuzzles.get(difficulty, index).numbers.flatMap { row ->
                row.filter { it > 0 }
            }
        }.average()

    private fun expertNurikabeMaxClues(): Set<Int> =
        (0 until levelCount(3)).map { index ->
            NurikabePuzzles.get(3, index).numbers
                .flatMap { row -> row.filter { it > 0 } }
                .maxOrNull() ?: missing("Nurikabe Expert [i=$index] has no clues")
        }.toSet()

    private fun averageSkyscraperGivenDensity(difficulty: Int): Double =
        (0 until levelCount(difficulty)).map { index ->
            val puzzle = SkyscraperPuzzles.get(difficulty, index)
            puzzle.initial.sumOf { row -> row.count { it > 0 } }.toDouble() / (puzzle.size * puzzle.size)
        }.average()

    private fun futoshikiDifficultyProfile(difficulty: Int, index: Int): String {
        val puzzle = FutoshikiPuzzles.get(difficulty, index)
        val givens = puzzle.initial.sumOf { row -> row.count { it > 0 } }
        val constraints = puzzle.hConstraints.sumOf { row -> row.count { it > 0 } } +
            puzzle.vConstraints.sumOf { row -> row.count { it > 0 } }
        return "${puzzle.size}:$givens:$constraints"
    }

    private fun hidatoDifficultyProfile(difficulty: Int, index: Int): String {
        val puzzle = HidatoPuzzles.get(difficulty, index)
        val givens = puzzle.initial.sumOf { row -> row.count { it > 0 } }
        return "${puzzle.rows}x${puzzle.cols}:$givens"
    }

    private fun mastermindSignature(difficulty: Int, index: Int): String {
        val level = MastermindData.levelFor(difficulty, index)
        return "${mastermindConfigSignature(difficulty, index)}:${level.secret.joinToString(",")}"
    }

    private fun mastermindConfigSignature(difficulty: Int, index: Int): String {
        val level = MastermindData.levelFor(difficulty, index)
        return "${level.positions}:${level.numColors}:${level.maxGuesses}:${level.allowDuplicates}"
    }

    private fun futoshikiSignature(puzzle: FutoshikiPuzzle): String =
        "${puzzle.size}|${intGridSignature(puzzle.initial)}|${intGridSignature(puzzle.hConstraints)}|${intGridSignature(puzzle.vConstraints)}"

    private fun skyscraperSignature(puzzle: SkyscraperPuzzle): String =
        "${puzzle.size}|${intGridSignature(puzzle.initial)}|${puzzle.cluesTop.joinToString(",")}|${puzzle.cluesBottom.joinToString(",")}|${puzzle.cluesLeft.joinToString(",")}|${puzzle.cluesRight.joinToString(",")}"

    private fun kakuroSignature(puzzle: KakuroPuzzle): String =
        puzzle.grid.joinToString("/") { row ->
            row.joinToString(",") { cell ->
                when (cell) {
                    KCell.Black -> "B"
                    is KCell.Clue -> "C${cell.downSum}:${cell.rightSum}"
                    KCell.White -> "W"
                }
            }
        } + "|" + (puzzle.initial?.let(::intGridSignature) ?: "")

    private fun logicGridSignature(puzzle: LogicGridPuzzle): String =
        "${puzzle.title}|${puzzle.categories.joinToString(",")}|${puzzle.items.flatten().joinToString(",")}|${puzzle.solution.flatten().joinToString(",")}"

    private fun logicGridSizeSignature(puzzle: LogicGridPuzzle): String =
        "${puzzle.categories.size}:${puzzle.items.first().size}"

    private fun boolGridSignature(grid: Array<BooleanArray>): String =
        grid.joinToString("/") { row -> row.joinToString("") { if (it) "1" else "0" } }

    private fun intGridSignature(grid: Array<IntArray>): String =
        grid.joinToString("/") { row -> row.joinToString(",") }

    private fun dimensions(grid: Array<IntArray>): String = "${grid.size}x${grid.first().size}"
    private fun dimensions(puzzle: KakuroPuzzle): String = "${puzzle.rows}x${puzzle.cols}"
    private fun dimensions(puzzle: SlitherlinkPuzzle): String = "${puzzle.rows}x${puzzle.cols}"
    private fun dimensions(puzzle: NurikabePuzzle): String = "${puzzle.rows}x${puzzle.cols}"
    private fun dimensions(puzzle: HidatoPuzzle): String = "${puzzle.rows}x${puzzle.cols}"

    private companion object {
        const val DIFFICULTIES = 5
        val ORTHOGONAL = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
        val ORTHOGONAL_WITH_SELF = arrayOf(0 to 0, -1 to 0, 1 to 0, 0 to -1, 0 to 1)

        fun levelCount(difficulty: Int): Int = when (difficulty) {
            0 -> 15
            1 -> 25
            2 -> 35
            3 -> 45
            else -> 55
        }
    }
}
