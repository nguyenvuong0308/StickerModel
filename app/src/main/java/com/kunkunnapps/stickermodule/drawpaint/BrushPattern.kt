package com.kunkunnapps.stickermodule.drawpaint

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

abstract class BrushPattern {
    open var drawBitmap: Bitmap? = null
    var drawCanvas: Canvas? = null
    var drawSize = 50f
        set(value) {
            field = value
            drawPaint.strokeWidth = value
        }
    var drawColor = Color.RED
    protected var drawPaint: Paint = Paint()

    abstract fun touchDown(touchX: Float, touchY: Float)
    abstract fun touchMove(touchX: Float, touchY: Float)
    abstract fun touchUp(touchX: Float, touchY: Float)
}