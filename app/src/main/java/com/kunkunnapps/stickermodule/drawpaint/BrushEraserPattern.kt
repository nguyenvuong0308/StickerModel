package com.kunkunnapps.stickermodule.drawpaint

import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode

class BrushEraserPattern: BrushPattern() {
    private var drawPath: Path = Path()
    init {
        drawPaint.apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            isDither = true
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = drawSize
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    override fun touchDown(touchX: Float, touchY: Float) {
        drawPath.moveTo(touchX, touchY)
    }

    override fun touchMove(touchX: Float, touchY: Float) {
        drawPath.lineTo(touchX, touchY)
        drawCanvas?.drawPath(drawPath, drawPaint)
    }

    override fun touchUp(touchX: Float, touchY: Float) {
        drawCanvas?.drawPath(drawPath, drawPaint)
        drawPath.reset()
    }
}