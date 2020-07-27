package com.kunkunnapps.stickermodule.drawpaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.kunkunnapps.stickermodule.DrawingAction
import com.raed.drawingview.ActionStack

class BrushDraw @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mDrawBitmap: Bitmap? = null
        set(value) {
            field = value
            brushPattern.drawBitmap = value
            clearPattern.drawBitmap = value
        }
    private var drawCanvas: Canvas? = null
        set(value) {
            field = value
            brushPattern.drawCanvas = value
            clearPattern.drawCanvas = value
        }
    private var isDrawing = false
    private var brushPattern: BrushPattern = BrushMultiBitmapPattern(context)
    private var clearPattern: BrushPattern = BrushEraserPattern()
    private var mActionStack: ActionStack ?= null

    var eraserMode = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDrawBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(mDrawBitmap!!)
    }

    fun setEraserSize(eraserSize: Float) {
        clearPattern.drawSize = eraserSize
    }

    fun setUndoAndRedoEnable(enabled: Boolean) {
        mActionStack = if (enabled) ActionStack() else null
    }

    private fun storeAction(rect: Rect, drawBitmap: Bitmap) {
        mActionStack?.let {
            val bitmap = Bitmap.createBitmap(
                drawBitmap,
                rect.left,
                rect.top,
                rect.right - rect.left,
                rect.bottom - rect.top
            )
            val action = DrawingAction(bitmap, rect)
            mActionStack?.addAction(action)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (eraserMode) {
                    clearPattern.touchDown(x, y)
                } else {
                    isDrawing = true
                    brushPattern.touchDown(x, y)
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (eraserMode) {
                    clearPattern.touchMove(x, y)
                } else {
                    if (isDrawing) {
                        brushPattern.touchMove(x, y)
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                if (eraserMode) {
                    clearPattern.touchUp(x, y)
                } else {
                    isDrawing = false
                    brushPattern.touchUp(x, y)
                }
                mActionStack?.addAction()
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mDrawBitmap!!, 0f, 0f, null)
    }
}