package com.logicpuzzles.utils

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import kotlin.math.roundToInt

class ZoomableBoardHost(
    context: Context,
    private val puzzleType: Int,
    private val boardBuilder: (Float) -> View
) : LinearLayout(context) {
    private val boardHolder = FrameLayout(context)
    private val percentText = TextView(context)
    private var zoom = 1f

    init {
        orientation = VERTICAL
        val palette = ThemeManager.currentPalette(context)

        addView(LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(8), 0, dp(8), dp(6))
            addView(zoomButton("-", "Zoom out") { setZoom(zoom / ZOOM_FACTOR) })
            addView(percentText.apply {
                gravity = Gravity.CENTER
                textSize = 12f
                setTypeface(null, Typeface.BOLD)
                setTextColor(palette.textSecondary)
                minWidth = dp(64)
                setPadding(dp(8), 0, dp(8), 0)
                setOnClickListener { setZoom(1f) }
                contentDescription = "Reset zoom"
            })
            addView(zoomButton("+", "Zoom in") { setZoom(zoom * ZOOM_FACTOR) })
        }, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        val horizontal = HorizontalScrollView(context).apply {
            isFillViewport = true
            addView(boardHolder, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ))
            addOnLayoutChangeListener { _, left, _, right, _, _, _, _, _ ->
                boardHolder.minimumWidth = right - left
            }
        }
        addView(ScrollView(context).apply {
            isFillViewport = true
            addView(horizontal, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ))
        }, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))

        rebuild()
    }

    private fun zoomButton(label: String, description: String, action: () -> Unit): Button {
        val palette = ThemeManager.currentPalette(context)
        return Button(context).apply {
            text = label
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setTextColor(palette.buttonText)
            setBackgroundColor(ThemeManager.puzzleAccent(context, puzzleType))
            contentDescription = description
            minWidth = dp(44)
            minimumWidth = dp(44)
            minHeight = dp(40)
            minimumHeight = dp(40)
            setPadding(0, 0, 0, 0)
            setOnClickListener { action() }
        }
    }

    private fun setZoom(value: Float) {
        val next = value.coerceIn(MIN_ZOOM, MAX_ZOOM)
        if (next == zoom) return
        zoom = next
        rebuild()
    }

    private fun rebuild() {
        percentText.text = "${(zoom * 100).roundToInt()}%"
        boardHolder.removeAllViews()
        boardHolder.addView(boardBuilder(zoom))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()

    private companion object {
        const val MIN_ZOOM = 0.75f
        const val MAX_ZOOM = 2.5f
        const val ZOOM_FACTOR = 1.25f
    }
}
