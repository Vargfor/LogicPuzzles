package com.logicpuzzles.utils

import android.content.res.ColorStateList
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cyberhub.logicgames.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <T> AppCompatActivity.loadGamePuzzle(
    puzzleType: Int,
    logLabel: String,
    load: () -> T,
    onLoaded: (T) -> Unit
) {
    val root = findViewById<FrameLayout>(R.id.game_root)
    val density = resources.displayMetrics.density
    fun dp(value: Int): Int = (value * density).toInt()

    fun showLoading() {
        val palette = ThemeManager.currentPalette(this)
        root.removeAllViews()
        root.setBackgroundColor(palette.background)
        root.addView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(ProgressBar(this@loadGamePuzzle).apply {
                isIndeterminate = true
                indeterminateTintList = ColorStateList.valueOf(
                    ThemeManager.puzzleAccent(this@loadGamePuzzle, puzzleType)
                )
            })
            addView(TextView(this@loadGamePuzzle).apply {
                text = getString(R.string.puzzle_loading)
                setTextColor(palette.textSecondary)
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(dp(16), dp(16), dp(16), 0)
            })
        }, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))
    }

    lateinit var startLoad: () -> Unit
    fun showError(error: Throwable) {
        Log.e("PuzzleLoader", "Unable to prepare $logLabel", error)
        val palette = ThemeManager.currentPalette(this)
        val accent = ThemeManager.puzzleAccent(this, puzzleType)
        root.removeAllViews()
        root.setBackgroundColor(palette.background)
        root.addView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(24), dp(24), dp(24))
            addView(TextView(this@loadGamePuzzle).apply {
                text = getString(R.string.puzzle_load_failed)
                setTextColor(palette.textPrimary)
                textSize = 16f
                gravity = Gravity.CENTER
            })
            addView(LinearLayout(this@loadGamePuzzle).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                setPadding(0, dp(16), 0, 0)
                addView(Button(this@loadGamePuzzle).apply {
                    text = getString(R.string.back)
                    setTextColor(palette.buttonText)
                    setBackgroundColor(palette.button)
                    setOnClickListener { finish() }
                })
                addView(Button(this@loadGamePuzzle).apply {
                    text = getString(R.string.retry)
                    setTextColor(palette.accentText)
                    setBackgroundColor(accent)
                    setOnClickListener { startLoad() }
                }, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginStart = dp(12) })
            })
        }, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))
    }

    startLoad = {
        showLoading()
        lifecycleScope.launch {
            try {
                val puzzle = withContext(Dispatchers.Default) { load() }
                onLoaded(puzzle)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                showError(error)
            }
        }
    }
    startLoad()
}
