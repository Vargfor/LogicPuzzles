package com.logicpuzzles.slitherlink

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.logicpuzzles.MainActivity
import com.cyberhub.logicgames.R
import com.logicpuzzles.utils.CompletionDialogs
import com.logicpuzzles.utils.PrefsManager
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.numberText
import com.logicpuzzles.utils.puzzleHeader
import java.util.ArrayDeque

class SlitherlinkGameActivity : AppCompatActivity() {

    private var difficulty = 0
    private var puzzleIndex = 0
    private lateinit var puzzle: SlitherlinkPuzzle
    private lateinit var hEdges: Array<BooleanArray>  // (rows+1) x cols
    private lateinit var vEdges: Array<BooleanArray>  // rows x (cols+1)
    private lateinit var hEdgeViews: Array<Array<View?>>
    private lateinit var vEdgeViews: Array<Array<View?>>
    private var solved = false
    private var zoomLevel = 1.0f
    private lateinit var boardContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0)
        puzzleIndex = intent.getIntExtra(MainActivity.EXTRA_PUZZLE_INDEX, 0)
        val catalogIndex = PrefsManager(this).getCatalogIndex(MainActivity.TYPE_SLITHERLINK, difficulty, puzzleIndex)
        puzzle = SlitherlinkPuzzles.get(difficulty, catalogIndex)

        hEdges = Array(puzzle.rows + 1) { BooleanArray(puzzle.cols) }
        vEdges = Array(puzzle.rows) { BooleanArray(puzzle.cols + 1) }
        hEdgeViews = Array(puzzle.rows + 1) { arrayOfNulls<View>(puzzle.cols) }
        vEdgeViews = Array(puzzle.rows) { arrayOfNulls<View>(puzzle.cols + 1) }

        buildUi()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun dp(v: Float) = (v * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_SLITHERLINK)
        val root = findViewById<FrameLayout>(R.id.game_root)
        root.removeAllViews()
        root.setBackgroundColor(palette.background)

        val main = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(8))
        }
        header.addView(TextView(this).apply {
            text = puzzleHeader(R.string.puzzle_slitherlink, difficulty, puzzleIndex)
            setTextColor(palette.textPrimary); textSize = 16f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(zoomButton("−") {
            zoomLevel = (zoomLevel / 1.2f).coerceAtLeast(0.6f)
            refreshBoard()
        })
        header.addView(zoomButton("+") {
            zoomLevel = (zoomLevel * 1.2f).coerceAtMost(3.0f)
            refreshBoard()
        })
        header.addView(Button(this).apply {
            text = getString(R.string.action_check); textSize = 11f
            setBackgroundColor(accent)
            setTextColor(palette.accentText)
            setOnClickListener { checkSolution() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { marginStart = dp(4) }
        })
        main.addView(header)

        main.addView(TextView(this).apply {
            text = getString(R.string.instruction_slitherlink)
            setTextColor(palette.textSecondary)
            textSize = 12f
            setPadding(dp(12), 0, dp(12), dp(8))
        })

        // 2D scrollable board container
        val verticalScroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        val horizontalScroll = HorizontalScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        boardContainer = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        boardContainer.addView(buildBoard())
        horizontalScroll.addView(boardContainer)
        verticalScroll.addView(horizontalScroll)
        main.addView(verticalScroll)

        root.addView(main)
    }

    private fun zoomButton(label: String, onClick: () -> Unit): View {
        val palette = ThemeManager.currentPalette(this)
        return Button(this).apply {
            text = label
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setBackgroundColor(palette.button)
            setTextColor(palette.buttonText)
            minWidth = dp(36)
            minHeight = dp(36)
            setPadding(dp(8), 0, dp(8), 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { marginStart = dp(4) }
            setOnClickListener { onClick() }
        }
    }

    private fun refreshBoard() {
        boardContainer.removeAllViews()
        // Reset view caches before rebuild
        hEdgeViews = Array(puzzle.rows + 1) { arrayOfNulls<View>(puzzle.cols) }
        vEdgeViews = Array(puzzle.rows) { arrayOfNulls<View>(puzzle.cols + 1) }
        boardContainer.addView(buildBoard())
    }

    private fun buildBoard(): View {
        val palette = ThemeManager.currentPalette(this)
        val rows = puzzle.rows; val cols = puzzle.cols
        val displayW = resources.displayMetrics.widthPixels
        val pad = dp(16)
        val maxBoardW = displayW - 2 * pad
        // edgeSize = cellSize / 3 (thicker than before)
        // total width = cols*cellSize + (cols+1)*cellSize/3 = cellSize * (3*cols + cols + 1) / 3
        // = cellSize * (4*cols + 1) / 3
        // cellSize = maxBoardW * 3 / (4*cols + 1)
        val baseCellSize = (maxBoardW * 3 / (4 * cols + 1)).coerceAtLeast(dp(36))
        val cellSize = (baseCellSize * zoomLevel).toInt().coerceAtLeast(dp(28))
        val edgeSize = (cellSize / 3).coerceAtLeast(dp(10))

        val gl = GridLayout(this).apply {
            rowCount = 2 * rows + 1
            columnCount = 2 * cols + 1
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = lp
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setBackgroundColor(palette.cellEmpty)
        }

        for (gr in 0 until 2 * rows + 1) {
            for (gc in 0 until 2 * cols + 1) {
                val w = if (gc % 2 == 0) edgeSize else cellSize
                val h = if (gr % 2 == 0) edgeSize else cellSize
                val view: View = when {
                    gr % 2 == 0 && gc % 2 == 0 -> {
                        View(this).apply { setBackgroundColor(palette.cellFilled) }
                    }
                    gr % 2 == 0 && gc % 2 == 1 -> {
                        val r = gr / 2; val c = gc / 2
                        val v = View(this).apply {
                            setOnClickListener {
                                hEdges[r][c] = !hEdges[r][c]
                                paintHEdge(r, c)
                            }
                        }
                        hEdgeViews[r][c] = v
                        v
                    }
                    gr % 2 == 1 && gc % 2 == 0 -> {
                        val r = gr / 2; val c = gc / 2
                        val v = View(this).apply {
                            setOnClickListener {
                                vEdges[r][c] = !vEdges[r][c]
                                paintVEdge(r, c)
                            }
                        }
                        vEdgeViews[r][c] = v
                        v
                    }
                    else -> {
                        val r = gr / 2; val c = gc / 2
                        val clue = puzzle.clues[r][c]
                        TextView(this).apply {
                            text = if (clue >= 0) numberText(clue) else ""
                            setTextColor(palette.cellText)
                            textSize = (cellSize / resources.displayMetrics.density / 3.5f)
                                .coerceAtLeast(12f)
                                .coerceAtMost(22f)
                            setTypeface(null, Typeface.BOLD)
                            gravity = Gravity.CENTER
                            setBackgroundColor(palette.cellEmpty)
                        }
                    }
                }
                view.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(gr)
                    columnSpec = GridLayout.spec(gc)
                    width = w
                    height = h
                }
                gl.addView(view)
            }
        }

        for (r in 0..rows) for (c in 0 until cols) paintHEdge(r, c)
        for (r in 0 until rows) for (c in 0..cols) paintVEdge(r, c)

        return gl
    }

    private fun paintHEdge(r: Int, c: Int) {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_SLITHERLINK)
        hEdgeViews[r][c]?.setBackgroundColor(
            if (hEdges[r][c]) accent else palette.gridLine
        )
    }

    private fun paintVEdge(r: Int, c: Int) {
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, MainActivity.TYPE_SLITHERLINK)
        vEdgeViews[r][c]?.setBackgroundColor(
            if (vEdges[r][c]) accent else palette.gridLine
        )
    }

    private fun checkSolution() {
        val rows = puzzle.rows; val cols = puzzle.cols

        for (r in 0 until rows) for (c in 0 until cols) {
            val clue = puzzle.clues[r][c]
            if (clue < 0) continue
            var count = 0
            if (hEdges[r][c]) count++
            if (hEdges[r + 1][c]) count++
            if (vEdges[r][c]) count++
            if (vEdges[r][c + 1]) count++
            if (count != clue) {
                Toast.makeText(this, "Clue mismatch at row ${r + 1}, col ${c + 1}.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val degree = Array(rows + 1) { IntArray(cols + 1) }
        for (r in 0..rows) for (c in 0 until cols) {
            if (hEdges[r][c]) {
                degree[r][c]++
                degree[r][c + 1]++
            }
        }
        for (r in 0 until rows) for (c in 0..cols) {
            if (vEdges[r][c]) {
                degree[r][c]++
                degree[r + 1][c]++
            }
        }
        for (r in 0..rows) for (c in 0..cols) {
            if (degree[r][c] != 0 && degree[r][c] != 2) {
                Toast.makeText(this, "Loop must not branch.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        var sr = -1; var sc = -1
        outer@ for (r in 0..rows) for (c in 0..cols) {
            if (degree[r][c] > 0) { sr = r; sc = c; break@outer }
        }
        if (sr == -1) {
            Toast.makeText(this, "No loop drawn.", Toast.LENGTH_SHORT).show()
            return
        }

        var totalUsed = 0
        for (r in 0..rows) for (c in 0..cols) if (degree[r][c] > 0) totalUsed++

        val visited = Array(rows + 1) { BooleanArray(cols + 1) }
        val q = ArrayDeque<Pair<Int, Int>>()
        q.add(sr to sc); visited[sr][sc] = true
        var visitedCount = 1
        while (q.isNotEmpty()) {
            val (r, c) = q.removeFirst()
            if (r > 0 && vEdges[r - 1][c] && !visited[r - 1][c]) {
                visited[r - 1][c] = true; q.add(r - 1 to c); visitedCount++
            }
            if (r < rows && vEdges[r][c] && !visited[r + 1][c]) {
                visited[r + 1][c] = true; q.add(r + 1 to c); visitedCount++
            }
            if (c > 0 && hEdges[r][c - 1] && !visited[r][c - 1]) {
                visited[r][c - 1] = true; q.add(r to c - 1); visitedCount++
            }
            if (c < cols && hEdges[r][c] && !visited[r][c + 1]) {
                visited[r][c + 1] = true; q.add(r to c + 1); visitedCount++
            }
        }

        if (visitedCount != totalUsed) {
            Toast.makeText(this, "Edges form multiple loops.", Toast.LENGTH_SHORT).show()
            return
        }

        solved = true
        PrefsManager(this).markPuzzleCompleted(MainActivity.TYPE_SLITHERLINK, difficulty, puzzleIndex)
        CompletionDialogs.showSolved(
            this,
            "Solved!",
            "Slitherlink complete.",
            MainActivity.TYPE_SLITHERLINK,
            difficulty,
            puzzleIndex,
            SlitherlinkGameActivity::class.java
        )
    }
}
