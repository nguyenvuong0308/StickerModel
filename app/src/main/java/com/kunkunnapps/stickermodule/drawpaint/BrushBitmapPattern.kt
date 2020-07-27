package com.kunkunnapps.stickermodule.drawpaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kunkunnapps.stickermodule.R

class BrushBitmapPattern(context: Context): BrushPattern() {
    private var bitmapPen : Bitmap
    private val mPositions: ArrayList<Vector2> = ArrayList<Vector2>(100)
    private val mBitmapBrushDimensions: Vector2 = Vector2(0f, 0f)

    init {
        val bitmapOrigin = BitmapFactory.decodeResource(context.resources, R.drawable.ic_zoom)
        bitmapPen = Bitmap.createScaledBitmap(bitmapOrigin, 50, 50, false)
        bitmapOrigin.recycle()
        mBitmapBrushDimensions.x = (bitmapPen.width/2).toFloat()
        mBitmapBrushDimensions.y = (bitmapPen.height/2).toFloat()
    }

    override fun touchDown(touchX: Float, touchY: Float) {
        mPositions.add(
            Vector2(
                touchX - mBitmapBrushDimensions.x / 2,
                touchY - mBitmapBrushDimensions.y / 2
            )
        )
        drawCanvas?.drawBitmap(bitmapPen, touchX - mBitmapBrushDimensions.x / 2, touchY - mBitmapBrushDimensions.y / 2, null)
    }

    override fun touchMove(touchX: Float, touchY: Float) {
        mPositions.add(
            Vector2(
                touchX - mBitmapBrushDimensions.x / 2,
                touchY - mBitmapBrushDimensions.y / 2
            )
        )
        drawCanvas?.drawBitmap(bitmapPen, touchX - mBitmapBrushDimensions.x / 2, touchY - mBitmapBrushDimensions.y / 2, null)
    }

    override fun touchUp(touchX: Float, touchY: Float) {
        mPositions.clear()
    }

    private class Vector2(var x: Float, var y: Float)
}