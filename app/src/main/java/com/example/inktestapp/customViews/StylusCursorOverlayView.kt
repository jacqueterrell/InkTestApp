package com.example.inktestapp.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.inktestapp.R

class StylusCursorOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var cursorX: Float = -1f
    private var cursorY: Float = -1f
    private var cursorDrawable: Drawable? = null


    fun updateCursor(x: Float, y: Float, isErasing: Boolean) {
        cursorX = x
        cursorY = y
        setCursorDrawable(isErasing)
        invalidate()
    }

    fun hideCursor() {
        cursorX = -1f
        cursorY = -1f
        invalidate()
    }

    fun setCursorDrawable(isErasing: Boolean) {
        if (isErasing) {
            cursorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_eraser)
            cursorDrawable?.setTint(ContextCompat.getColor(context, R.color.light_gray))
        } else {
            cursorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_stylus)
            cursorDrawable?.setTint(ContextCompat.getColor(context, R.color.teal_700))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (cursorX >= 0 && cursorY >= 0) {

            cursorDrawable?.setBounds(
                (cursorX - 16).toInt(),
                (cursorY - 16).toInt(),
                (cursorX + 16).toInt(),
                (cursorY + 16).toInt()
            )
            cursorDrawable?.draw(canvas)
        }
    }
}