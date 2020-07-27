package com.mmosoft.photocollage.view.drawlib

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent

interface Drawing {

    fun getAlpha(): Int

    fun getBrushCap(): Paint.Cap

    fun getBrushType(): Int

    fun getColor(): Int

    fun getRadius(): Float

    fun getStrokeJoin(): Paint.Join

    fun getStrokeWidth(): Int

    fun onDrawBrush(canvas: Canvas)

    fun onTouch(motionEvent: MotionEvent)

    fun setAlpha(alpha: Int)

    fun setBrushCap(cap: Paint.Cap)

    fun setBrushType(brushType: Int)

    fun setColor(color: Int)

    fun setRadius(radius: Float)

    fun setStrokeJoin(join: Paint.Join)

    fun setStrokeWidth(strokeWidth: Float)
}