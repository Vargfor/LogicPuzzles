package com.logicpuzzles

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SweepGradient
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cyberhub.logicgames.R
import com.logicpuzzles.utils.AppPalette
import com.logicpuzzles.utils.ThemeColorSpec
import com.logicpuzzles.utils.ThemeManager
import com.logicpuzzles.utils.applySystemBarInsets
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CustomThemeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CUSTOM_THEME_SLOT = "custom_theme_slot"
    }

    private val draftColors = linkedMapOf<String, Int>()
    private val swatches = mutableMapOf<String, View>()
    private var themeName = ""
    private var editSlot: Int? = null
    private lateinit var nameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestedSlot = intent.getIntExtra(EXTRA_CUSTOM_THEME_SLOT, -1)
        val savedPalette = if (requestedSlot in 0 until ThemeManager.CUSTOM_THEME_SLOTS) {
            ThemeManager.customThemeAt(this, requestedSlot)
        } else {
            null
        }
        editSlot = if (savedPalette != null) requestedSlot else null

        val sourcePalette = savedPalette ?: ThemeManager.palettes.first()
        themeName = if (savedPalette != null) {
            savedPalette.name
        } else {
            getString(R.string.custom_theme_default_name)
        }
        draftColors.putAll(ThemeManager.paletteColors(sourcePalette))
        buildUi()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun buildUi() {
        val palette = draftPalette()
        swatches.clear()

        val root = FrameLayout(this).apply {
            setBackgroundColor(palette.background)
            applySystemBarInsets()
        }

        val scroll = ScrollView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(18))
        }
        scroll.addView(content)

        content.addView(headerRow(palette))
        content.addView(nameField(palette))

        ThemeManager.editableColorSpecs.groupBy { it.group }.forEach { (group, specs) ->
            content.addView(groupTitle(group, palette))
            specs.forEach { spec ->
                content.addView(colorRoleRow(spec, palette))
            }
        }

        content.addView(actionRow(palette))
        root.addView(scroll)
        setContentView(root)
    }

    private fun headerRow(palette: AppPalette): View =
        LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, dp(12))

            addView(Button(this@CustomThemeActivity).apply {
                text = getString(R.string.custom_theme_back)
                textSize = 13f
                backgroundTintList = ColorStateList.valueOf(palette.surfaceStrong)
                setTextColor(palette.textPrimary)
                setOnClickListener { finish() }
            })

            addView(TextView(this@CustomThemeActivity).apply {
                text = getString(R.string.custom_theme_title)
                textSize = 22f
                setTypeface(null, Typeface.BOLD)
                setTextColor(palette.textPrimary)
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    marginStart = dp(12)
                }
            })
        }

    private fun nameField(palette: AppPalette): View {
        nameInput = EditText(this).apply {
            setText(themeName)
            hint = getString(R.string.custom_theme_name)
            textSize = 16f
            setSingleLine(true)
            setTextColor(palette.textPrimary)
            setHintTextColor(palette.textSecondary)
            backgroundTintList = ColorStateList.valueOf(palette.accent)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(14)
            }
        }
        return nameInput
    }

    private fun groupTitle(group: String, palette: AppPalette): TextView =
        TextView(this).apply {
            text = group
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            setTextColor(palette.textSecondary)
            setPadding(0, dp(16), 0, dp(8))
        }

    private fun colorRoleRow(spec: ThemeColorSpec, palette: AppPalette): View {
        val currentColor = draftColors.getValue(spec.key)
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = roundedDrawable(palette.surface, palette.gridLine, dp(8).toFloat())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
            setOnClickListener { showColorPicker(spec) }
        }

        val swatch = View(this).apply {
            background = roundedDrawable(currentColor, palette.gridLine, dp(6).toFloat())
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(48)).apply {
                marginEnd = dp(12)
            }
            setOnClickListener { showColorPicker(spec) }
        }
        swatches[spec.key] = swatch
        row.addView(swatch)

        row.addView(TextView(this).apply {
            text = spec.label
            textSize = 14f
            setTextColor(palette.textPrimary)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })

        return row
    }

    private fun actionRow(palette: AppPalette): View =
        LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, dp(16), 0, 0)

            addView(actionButton(getString(R.string.custom_theme_save), palette.button, palette.buttonText) {
                saveTheme()
            }.apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            })
        }

    private fun actionButton(label: String, fill: Int, textColor: Int, onClick: () -> Unit): Button =
        Button(this).apply {
            text = label
            textSize = 13f
            backgroundTintList = ColorStateList.valueOf(fill)
            setTextColor(textColor)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener { onClick() }
        }

    private fun showColorPicker(spec: ThemeColorSpec) {
        val dialogPalette = ThemeManager.currentPalette(this)
        var workingColor = draftColors.getValue(spec.key)
        val alpha = Color.alpha(workingColor)
        val hsv = FloatArray(3)
        Color.colorToHSV(workingColor, hsv)
        hsv[1] = 0.5f
        hsv[2] = 0.5f
        workingColor = Color.HSVToColor(alpha, hsv)

        val preview = View(this).apply {
            background = roundedDrawable(workingColor, dialogPalette.gridLine, dp(8).toFloat())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(56)
            ).apply {
                bottomMargin = dp(14)
            }
        }

        val wheel = ColorWheelView(this).apply {
            hue = hsv[0]
            saturation = hsv[1]
            brightness = hsv[2]
            onHueChanged = { selectedHue ->
                hsv[0] = selectedHue
                workingColor = Color.HSVToColor(alpha, hsv)
                preview.background = roundedDrawable(workingColor, dialogPalette.gridLine, dp(8).toFloat())
            }
            layoutParams = LinearLayout.LayoutParams(dp(220), dp(220)).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = dp(10)
            }
        }

        val saturation = SeekBar(this).apply {
            max = 100
            progress = (hsv[1] * 100f).toInt()
            progressTintList = ColorStateList.valueOf(dialogPalette.accent)
            thumbTintList = ColorStateList.valueOf(dialogPalette.accent)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    hsv[1] = progress / 100f
                    wheel.saturation = hsv[1]
                    workingColor = Color.HSVToColor(alpha, hsv)
                    preview.background = roundedDrawable(workingColor, dialogPalette.gridLine, dp(8).toFloat())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })
        }

        val brightness = SeekBar(this).apply {
            max = 100
            progress = (hsv[2] * 100f).toInt()
            progressTintList = ColorStateList.valueOf(dialogPalette.accent)
            thumbTintList = ColorStateList.valueOf(dialogPalette.accent)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    hsv[2] = progress / 100f
                    wheel.brightness = hsv[2]
                    workingColor = Color.HSVToColor(alpha, hsv)
                    preview.background = roundedDrawable(workingColor, dialogPalette.gridLine, dp(8).toFloat())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(8), dp(18), dp(4))
            setBackgroundColor(dialogPalette.background)
            addView(preview)
            addView(wheel)
            addView(TextView(this@CustomThemeActivity).apply {
                text = getString(R.string.color_saturation)
                textSize = 13f
                setTypeface(null, Typeface.BOLD)
                setTextColor(dialogPalette.textSecondary)
                setPadding(0, 0, 0, dp(4))
            })
            addView(saturation)
            addView(TextView(this@CustomThemeActivity).apply {
                text = getString(R.string.color_brightness)
                textSize = 13f
                setTypeface(null, Typeface.BOLD)
                setTextColor(dialogPalette.textSecondary)
                setPadding(0, dp(10), 0, dp(4))
            })
            addView(brightness)
        }

        AlertDialog.Builder(this)
            .setTitle(spec.label)
            .setView(content)
            .setPositiveButton(R.string.apply) { _, _ ->
                draftColors[spec.key] = workingColor
                swatches[spec.key]?.background = roundedDrawable(workingColor, dialogPalette.gridLine, dp(6).toFloat())
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun saveTheme() {
        updateName()
        editSlot?.let { slot ->
            saveThemeInto(slot)
            return
        }
        val availableSlot = ThemeManager.firstAvailableCustomSlot(this)
        if (availableSlot != null) {
            saveThemeInto(availableSlot)
        } else {
            showOverrideSelection()
        }
    }

    private fun showOverrideSelection() {
        val labels = Array(ThemeManager.CUSTOM_THEME_SLOTS) { slot ->
            ThemeManager.customThemeAt(this, slot)?.name ?: "Custom ${slot + 1}"
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.custom_theme_override_title)
            .setItems(labels) { _, which -> saveThemeInto(which) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun saveThemeInto(slot: Int) {
        ThemeManager.saveCustomTheme(this, slot, themeName, draftColors)
        Toast.makeText(this, R.string.custom_theme_saved, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun updateName() {
        themeName = nameInput.text.toString().trim().ifEmpty { getString(R.string.custom_theme_default_name) }
    }

    private fun draftPalette(): AppPalette =
        ThemeManager.createCustomPalette(
            editSlot?.let { ThemeManager.customThemeId(it) } ?: ThemeManager.CUSTOM_THEME_BASE_ID,
            themeName,
            draftColors
        )

    private fun roundedDrawable(fill: Int, stroke: Int, radius: Float): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radius
            setColor(fill)
            setStroke(1, stroke)
        }

    private class ColorWheelView(context: Context) : View(context) {
        var hue: Float = 0f
            set(value) {
                field = value
                invalidate()
            }
        var saturation: Float = 1f
            set(value) {
                field = value.coerceIn(0f, 1f)
                invalidate()
            }
        var brightness: Float = 1f
            set(value) {
                field = value.coerceIn(0f, 1f)
                invalidate()
            }
        var onHueChanged: ((Float) -> Unit)? = null

        private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 28f * resources.displayMetrics.density
        }
        private val markerFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.WHITE
        }
        private val markerStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f * resources.displayMetrics.density
            color = Color.BLACK
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            ringPaint.shader = SweepGradient(
                w / 2f,
                h / 2f,
                intArrayOf(
                    Color.RED,
                    Color.YELLOW,
                    Color.GREEN,
                    Color.CYAN,
                    Color.BLUE,
                    Color.MAGENTA,
                    Color.RED
                ),
                null
            )
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val centerX = width / 2f
            val centerY = height / 2f
            val radius = min(width, height) / 2f - ringPaint.strokeWidth
            canvas.drawCircle(centerX, centerY, radius, ringPaint)

            val angle = Math.toRadians(hue.toDouble())
            val markerX = centerX + cos(angle).toFloat() * radius
            val markerY = centerY + sin(angle).toFloat() * radius
            markerFill.color = Color.HSVToColor(floatArrayOf(hue, saturation, brightness))
            canvas.drawCircle(markerX, markerY, 10f * resources.displayMetrics.density, markerFill)
            canvas.drawCircle(markerX, markerY, 10f * resources.displayMetrics.density, markerStroke)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            return when (event.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    updateHueFromTouch(event)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    updateHueFromTouch(event)
                    performClick()
                    true
                }
                else -> true
            }
        }

        override fun performClick(): Boolean {
            super.performClick()
            return true
        }

        private fun updateHueFromTouch(event: MotionEvent) {
            val centerX = width / 2f
            val centerY = height / 2f
            val angle = atan2(event.y - centerY, event.x - centerX)
            hue = ((Math.toDegrees(angle.toDouble()) + 360.0) % 360.0).toFloat()
            onHueChanged?.invoke(hue)
        }
    }
}
