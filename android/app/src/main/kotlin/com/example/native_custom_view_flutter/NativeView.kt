// NativeDialView.kt

package com.example.native_custom_view_flutter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.view.View
import io.flutter.plugin.platform.PlatformView
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

    fun next() = when (this) {
        OFF -> LOW
        LOW -> MEDIUM
        MEDIUM -> HIGH
        HIGH -> OFF
    }
}

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35

class NativeDialView(
    private val context: Context,
    private val id: Int,
    private val creationParams: Map<String?, Any?>?
) : PlatformView {

    private var radius = 0.0f
    private var fanSpeed = FanSpeed.OFF
    private val pointPosition: PointF = PointF(0.0f, 0.0f)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }
    private var fanSpeedLowColor = Color.parseColor("#FFEB3B")
    private var fanSpeedMediumColor = Color.parseColor("#CDDC39")
    private var fanSeedMaxColor = Color.parseColor("#009688")
    private var view: View

    init {
        view = object : View(context) {
            init {
                isClickable = true
            }

            override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
                radius = (min(width, height) / 2.0 * 0.8).toFloat()
            }

            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)
                paint.color = when (fanSpeed) {
                    FanSpeed.OFF -> Color.GRAY
                    FanSpeed.LOW -> fanSpeedLowColor
                    FanSpeed.MEDIUM -> fanSpeedMediumColor
                    FanSpeed.HIGH -> fanSeedMaxColor
                }

                canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)

                val markerRadius = radius + RADIUS_OFFSET_INDICATOR
                pointPosition.computeXYForSpeed(fanSpeed, markerRadius, width, height)
                paint.color = Color.BLACK
                canvas.drawCircle(pointPosition.x, pointPosition.y, radius / 12, paint)

                val labelRadius = radius + RADIUS_OFFSET_LABEL
                for (i in FanSpeed.values()) {
                    pointPosition.computeXYForSpeed(i, labelRadius, width, height)
                    val label = resources.getString(i.label)
                    canvas.drawText(label, pointPosition.x, pointPosition.y, paint)
                }
            }

            override fun performClick(): Boolean {
                if (super.performClick()) return true

                fanSpeed = fanSpeed.next()
                contentDescription = resources.getString(fanSpeed.label)
                invalidate()
                return true
            }
        }
    }

    override fun getView(): View {
        return view
    }

    override fun dispose() {}

    private fun PointF.computeXYForSpeed(
        pos: FanSpeed,
        radius: Float,
        width: Int,
        height: Int
    ) {
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }
}
