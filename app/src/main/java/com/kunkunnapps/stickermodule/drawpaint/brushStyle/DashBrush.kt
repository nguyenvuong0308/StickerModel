package com.mmosoft.photocollage.view.drawlib.brushStyle

import android.graphics.*
import android.view.MotionEvent
import com.mmosoft.photocollage.view.drawlib.Drawing

class DashBrush(var mEraser: Boolean) : Drawing {

    private var mAlpha: Int = 0
    private var brushType: Int = 0
    private var mStrokeWidth: Int = 0
    private var mStrokeWidth1: Int = 0
    private var mX: Int = 0
    private var mY: Int = 0
    private var radius: Int = 0
    private var mColor: Int = 0

    private var mPaint: Paint = Paint()
    private var mPath: Path = Path()

    private lateinit var mCap: Paint.Cap
    private lateinit var mJoin: Paint.Join

    init {
        this.mPaint.style = Paint.Style.STROKE
    }


    override fun getAlpha(): Int {
        return mAlpha
    }

    override fun getBrushCap(): Paint.Cap {
        return mCap
    }

    override fun getBrushType(): Int {
        return brushType
    }

    override fun getColor(): Int {
        return mColor
    }

    override fun getRadius(): Float {
        return radius.toFloat()
    }

    override fun getStrokeJoin(): Paint.Join {
        return mJoin
    }

    override fun getStrokeWidth(): Int {
        return mStrokeWidth
    }

    override fun onDrawBrush(canvas: Canvas) {
        if(!mEraser){
            mPaint.pathEffect = DashPathEffect(floatArrayOf((mStrokeWidth1 * 3).toFloat(), mStrokeWidth1.toFloat()), 0.0f)
        }
        canvas.drawPath(mPath, mPaint)
    }

    override fun onTouch(motionEvent: MotionEvent) {
        val x = motionEvent.x.toInt()
        val y = motionEvent.y.toInt()
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x.toFloat(), y.toFloat())
                touchUp()
                touchMove(x.toFloat(), y.toFloat())
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                touchMove(x.toFloat(), y.toFloat())
            }
            MotionEvent.ACTION_MOVE -> touchMove(x.toFloat(), y.toFloat())
        }
    }

    override fun setAlpha(alpha: Int) {
        this.mAlpha = alpha
//        mPaint.alpha = mAlpha
    }

    override fun setBrushCap(cap: Paint.Cap) {
        this.mCap = cap
        mPaint.strokeCap = mCap
    }

    override fun setBrushType(brushType: Int) {
        this.brushType = brushType
    }

    override fun setColor(color: Int) {
        this.mColor = color
        mPaint.color = mColor
    }

    override fun setRadius(radius: Float) {
        this.radius = radius.toInt()
    }

    override fun setStrokeJoin(join: Paint.Join) {
        this.mJoin = join
    }

    override fun setStrokeWidth(strokeWidth: Float) {
        this.mStrokeWidth = strokeWidth.toInt()
        this.mStrokeWidth1 = strokeWidth.toInt() + 3
        this.mPaint.strokeWidth = this.mStrokeWidth.toFloat()
        if (mEraser) {
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

    }

    private fun touchStart(x: Float, y: Float) {
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x.toInt()
        mY = y.toInt()
    }

    private fun touchUp() {
        mPath.lineTo(mX.toFloat(), mY.toFloat())
    }

    private fun touchMove(x: Float, y: Float) {
        mPath.quadTo(mX.toFloat(), mY.toFloat(), (mX.toFloat() + x) / 2.0f, (mY.toFloat() + y) / 2.0f)
        mX = x.toInt()
        mY = y.toInt()
    }
}