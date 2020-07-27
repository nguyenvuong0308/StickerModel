package com.kunkunnapps.stickermodule.drawpaint

import android.graphics.Color
import android.graphics.Path
import android.graphics.PointF
import java.util.*

class BrushMultiLinePattern : BrushPattern() {
    private val points: MutableList<PointF> = ArrayList()

    override fun touchDown(touchX: Float, touchY: Float) {
        points.add(PointF(touchX, touchY))
        drawCanvas?.save()
    }

    override fun touchMove(touchX: Float, touchY: Float) {
        val space = 5f
        drawCanvas!!.drawColor(Color.TRANSPARENT)
        points.add(PointF(touchX, touchY))
        stroke(offsetPoints(-20f))
        stroke(offsetPoints(-space))
        stroke(points)
        stroke(offsetPoints(space))
        stroke(offsetPoints(20f))
        if (points.size > 30) {
            val pointNewStart = points.get(points.size - 1)
            points.clear()
            points.add(pointNewStart)
        }
    }

    override fun touchUp(touchX: Float, touchY: Float) {
        points.clear()
        drawCanvas!!.restore()
    }

    private fun offsetPoints(`val`: Float): List<PointF> {
        val offsetPoints: MutableList<PointF> = ArrayList()
        for (i in points.indices) {
            val point = points[i]
            offsetPoints.add(PointF(point.x + `val`, point.y + `val`))
        }
        return offsetPoints
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