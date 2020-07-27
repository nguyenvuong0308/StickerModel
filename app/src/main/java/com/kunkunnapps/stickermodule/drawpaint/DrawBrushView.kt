/*
package com.photoframe.cutout.common

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.photoframe.cutout.R
import com.photoframe.cutout.frame.model.BaseBrushData
import com.photoframe.cutout.frame.model.BrushImageData
import com.photoframe.cutout.frame.model.EraserBrushData
import com.photoframe.cutout.util.BitmapUtils
import com.photoframe.cutout.util.Logger
import kotlin.math.pow
import kotlin.math.sqrt

class DrawBrushView : View {

    companion object {
        const val MODE_DRAW = 1
        const val MODE_ERASER = 2
    }

    constructor(context: Context?) : super(context, null) {
        initViews(context, null)
    }

    constructor(context: Context?, attributes: AttributeSet?) : super(context, attributes, 0) {
        initViews(context, attributes)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initViews(context, attrs)
    }

    private val listBrushImage = ArrayList<BaseBrushData>()

    private var eraserSize: Float = 0f

    private var enableDraw = false
    private var scale = 1f
    private var mode = MODE_DRAW

    private val paintColor = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val eraserPaint = Paint().apply {
        alpha = 0
        color = Color.TRANSPARENT
        style = Paint.Style.STROKE
        maskFilter = null
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }


    private var bitmap: Bitmap? = null

    private var isVector = true

    var currentRes: Int = R.drawable.ic_draw_01

    var currentColor: String? = null

    val startPoint = PointF()

    private fun initViews(context: Context?, attrs: AttributeSet?) {
        val typeArray =
            context?.theme?.obtainStyledAttributes(attrs, R.styleable.DrawBrushView, 0, 0)
        enableDraw = typeArray?.getBoolean(R.styleable.DrawBrushView_enableDraw, false) ?: false
        eraserSize = resources.getDimension(R.dimen.brush_eraser_size_default)
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    fun setEnableDraw(isEnable: Boolean) {
        enableDraw = isEnable
    }

    fun setColor(color: String) {
        currentColor = color
    }

    fun setDrawMode(mode: Int) {
        this.mode = mode
    }

    fun getDrawMode(): Int = mode

    override fun onDraw(canvas: Canvas?) {
        drawView(canvas)
    }

    private fun drawView(canvas: Canvas?) {
        listBrushImage.forEach { brushImageData ->
            when (brushImageData) {
                is EraserBrushData -> {
                    val path = getEraserPath(brushImageData.listPoint)
                    eraserPaint.strokeWidth = brushImageData.size
                    canvas?.drawPath(path, eraserPaint)
                }
                is BrushImageData -> {
                    bitmap?.recycle()
                    bitmap = BitmapUtils.getBrushBitmap(
                        context,
                        brushImageData.res,
                        resources.getDimensionPixelOffset(R.dimen.brush_item_size),
                        brushImageData.isVector
                    )
                    brushImageData.listPoint.forEach { pointF ->
                        val matrix = Matrix()
                        matrix.postTranslate(pointF.x, pointF.y)
                        matrix.postScale(
                            brushImageData.scale,
                            brushImageData.scale,
                            pointF.x,
                            pointF.y
                        )
                        if (brushImageData.color == null) {
                            paintColor.colorFilter = null
                        } else {
                            paintColor.colorFilter = PorterDuffColorFilter(
                                Color.parseColor(brushImageData.color),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        }
                        canvas?.drawBitmap(bitmap!!, matrix, paintColor)
                    }
                }
            }

        }
    }

    private fun getEraserPath(listPoint: ArrayList<PointF>): Path {
        val path = Path()
        for (i in 0 until listPoint.size) {
            if (i == 0) {
                path.moveTo(listPoint[i].x, listPoint[i].y)
            } else {
                path.lineTo(listPoint[i].x, listPoint[i].y)
            }
        }
        return path
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!enableDraw || event == null)
            return false
        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                startPoint.set(event.x, event.y)
                val brushImageData = if (mode == MODE_DRAW) {
                    BrushImageData(
                        currentRes,
                        scale, isVector,currentColor
                    )
                } else {
                    EraserBrushData(eraserSize)
                }
                brushImageData.addPoint(PointF(event.x, event.y))
                listBrushImage.add(brushImageData)
                invalidate()
                attemptClaimDrag()
            }

            MotionEvent.ACTION_MOVE -> {
                if (mode == MODE_DRAW) {
                    drawBrush(event)
                } else {
                    drawEraser(event)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

            }
        }
        return true
    }

    private fun drawEraser(event: MotionEvent) {
        startPoint.set(event.x, event.y)
        if (listBrushImage.isEmpty())
            return
        val brushData = listBrushImage.last()
        brushData.addPoint(PointF(event.x, event.y))
        invalidate()
    }

    private fun drawBrush(event: MotionEvent) {
        if (bitmap == null)
            return
        val distance = sqrt((event.x - startPoint.x).pow(2) + (event.y - startPoint.y).pow(2))
        if (distance >= bitmap!!.width * scale && distance >= bitmap!!.height * scale) {
            startPoint.set(event.x, event.y)
            if (listBrushImage.isEmpty())
                return
            val brushData = listBrushImage.last()
            brushData.addPoint(PointF(event.x, event.y))
            invalidate()
        }
    }

    private fun attemptClaimDrag() {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
    }

    fun setScale(scale: Float) {
        this.scale = scale
    }

    fun setEraserSize(size: Float) {
        eraserSize = size
    }

    fun setBrushStyle(res: Int , isVector: Boolean) {
        currentRes = res
        this.isVector = isVector
    }
}*/
