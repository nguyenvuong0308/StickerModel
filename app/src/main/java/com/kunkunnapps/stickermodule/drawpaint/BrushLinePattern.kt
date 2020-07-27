package com.kunkunnapps.stickermodule.drawpaint

import android.R.attr.x
import android.R.attr.y
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import java.util.*
import kotlin.math.abs


class BrushLinePattern : BrushPattern() {
    private val points: MutableList<PointF> = ArrayList()
    private val mPath: Path = Path()
    private var mX = 0f
    private  var mY = 0f
    private val TOUCH_TOLERANCE = 4f
    init {
        drawPaint.apply {
            isAntiAlias = true
            isDither = true
            color = drawColor
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = drawSize
        }
    }

    override fun touchDown(touchX: Float, touchY: Float) {
        mPath.reset()
        mPath.moveTo(touchX, touchY);
        mX = touchX
        mY = touchY
        drawCanvas?.drawPath(mPath, drawPaint)
    }

    override fun touchMove(touchX: Float, touchY: Float) {
        val dx = abs(touchX - mX)
        val dy = abs(touchY - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (touchX + mX) / 2, (touchY + mY) / 2)
            mX = touchX
            mY = touchY
        }
        drawCanvas?.drawPath(mPath, drawPaint)
    }

    override fun touchUp(touchX: Float, touchY: Float) {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        drawCanvas?.drawPath(mPath, drawPaint)
        // kill this so we don't double draw
        mPath.reset()
    }

    private fun stroke(points: List<PointF>) {
        var p1 = points[0]
        var p2 = points[1]
        val path = Path()
        path.moveTo(p1.x, p1.y)
        for (i in 1 until points.size) {
            // we pick the point between pi+1 & pi+2 as the
            // end point and p1 as our control point
            val midPoint = midPointBtw(p1, p2)
            path.quadTo(p1.x, p1.y, midPoint.x, midPoint.y)
            p1 = points[i]
            if (i + 1 < points.size) p2 = points[i + 1]
        }
        // Draw last line as a straight line while
        // we wait for the next point to be able to calculate
        // the bezier control point
        path.lineTo(p1.x, p1.y)
        drawCanvas!!.drawPath(path, drawPaint)
    }

    private fun midPointBtw(p1: PointF, p2: PointF): PointF {
        return PointF(p1.x + (p2.x - p1.x) / 2.0f, p1.y + (p2.y - p1.y) / 2.0f)
    }
}