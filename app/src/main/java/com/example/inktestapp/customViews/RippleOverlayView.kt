package com.example.inktestapp.customViews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.inktestapp.R


/**
 * Adds a ripple animation each time the stylus touches the canvas
 */
class RippleOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var rippleX = 0f
    private var rippleY = 0f
    private var rippleRadius = 0f
    private var rippleAlpha = 0f
    private var showRipple = false
    private val rippleColor = ContextCompat.getColor(context, R.color.light_gray)

    private val maxRippleRadius = 50f
    private val paint = Paint().apply {
        color = rippleColor
        style = Paint.Style.FILL
    }

    private var rippleAnimator: ValueAnimator? = null

    fun triggerRipple(x: Float, y: Float) {
        rippleX = x
        rippleY = y
        showRipple = true
        rippleAnimator?.cancel()
        rippleAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 500L
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener { animator ->
                val progress = animator.animatedValue as Float
                rippleRadius = maxRippleRadius * progress
                rippleAlpha = 1f - progress
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (showRipple && rippleAlpha > 0f) {
            paint.alpha = (rippleAlpha * 255).toInt().coerceIn(0, 255)
            canvas.drawCircle(rippleX, rippleY, rippleRadius, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS &&
            event.action == MotionEvent.ACTION_DOWN
        ) {
            triggerRipple(event.x, event.y)
        }
        return false
    }

    override fun onDetachedFromWindow() {
        rippleAnimator?.cancel()
        super.onDetachedFromWindow()
    }
}