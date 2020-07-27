package com.kunkunnapps.stickermodule.drawpaint

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*

class MultipleLines2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var bitmap: Bitmap? = null
    private var drawCanvas: Canvas? = null
    private var drawPaint: Paint = Paint()
    private var dotPaint: Paint = Paint()
    private var dotPath = Path()
    private var dotWidth = 10f
    var eraserMode = false
        set(value) {
            field = value
            if (value) {
                drawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            } else {
                drawPaint.xfermode = null
            }
        }
    private var drawPath: Path = Path()
    init {
        drawPaint.apply {
            isAntiAlias = true
            isDither = true
            color = -0x10000
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 3f
        }
        dotPaint.apply {
            isAntiAlias = true
            isDither = true
            color = Color.RED
            style = Paint.Style.FILL
        }
    

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(bitmap!!)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (eraserMode) {
                    drawPath.moveTo(x, y)
                } else {

                    dotPath.moveTo(x, y)
                    touch_start(x, y)
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (eraserMode) {
                    drawPath.lineTo(x, y)
                    drawCanvas?.drawPath(drawPath, drawPaint)
                } else {
                    touch_move(x, y)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                if (eraserMode) {
                    drawCanvas?.drawPath(drawPath, drawPaint)
                    drawPath.reset()
                } else {
                    touch_up()
                }
                invalidate()
            }
        }
        return true
    }

    private var isDrawing = false
    private val points: MutableList<PointF> = ArrayList()
    private fun touch_start(touchX: Float, touchY: Float) {
        isDrawing = true
        points.add(PointF(touchX, touchY))
        drawCanvas!!.save()
    }
    private var random = Random()

    private fun touch_move(touchX: Float, touchY: Float) {
        if (!isDrawing) return
        val space = 5f
        drawCanvas!!.drawColor(Color.TRANSPARENT)
        points.add(PointF(touchX, touchY))
        stroke(offsetPoints(-20f))
        stroke(offsetPoints(-space.toFloat()))
        stroke(points)
        stroke(offsetPoints(space.toFloat()))
        stroke(offsetPoints(20f))
        if (points.size > 30) {
            val pointNewStart = points.get(points.size - 1)
            points.clear()
            points.add(pointNewStart)
        }

    }

    private fun touch_up() {
        isDrawing = false
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

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
    }

    private fun midPointBtw(p1: PointF, p2: PointF): PointF {
        return PointF(p1.x + (p2.x - p1.x) / 2.0f, p1.y + (p2.y - p1.y) / 2.0f)
    }
}