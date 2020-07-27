package com.kunkunnapps.stickermodule.drawpaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
import com.kunkunnapps.stickermodule.R
import com.kunkunnapps.stickermodule.view.previous
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.sqrt

class BrushMultiBitmapPattern(context: Context): BrushPattern() {

    val startPoint = PointF()
    private var bitmapsPen : ArrayList<Bitmap> = arrayListOf()
    private val mPositions: ArrayList<PointF> = ArrayList<PointF>(100)
    private var mMatrixPen = Matrix()

    init {
        val bitmap1 = BitmapFactory.decodeResource(context.resources, R.drawable.ic_bitmap1)
        val bitmap2 = BitmapFactory.decodeResource(context.resources, R.drawable.ic_bitmap2)
        val bitmap3 = BitmapFactory.decodeResource(context.resources, R.drawable.ic_bitmap3)
        bitmapsPen.add(bitmap1)
        bitmapsPen.add(bitmap2)
        bitmapsPen.add(bitmap3)
    }
    private var random =Random()
    private var indexPen = 0
    override fun touchDown(touchX: Float, touchY: Float) {
        startPoint.x = touchX
        startPoint.y = touchY
        val currentPen = bitmapsPen[indexPen]
        mMatrixPen.reset()
        mMatrixPen.postTranslate(touchX - currentPen.width / 2, touchY - currentPen.height / 2)


        drawCanvas?.drawBitmap(currentPen, mMatrixPen, null)
        indexPen = if (indexPen == bitmapsPen.size - 1) 0 else indexPen + 1
    }

    override fun touchMove(touchX: Float, touchY: Float) {
        val distance = sqrt((touchX - startPoint.x).pow(2) + (touchY - startPoint.y).pow(2))
        val currentPen = bitmapsPen[indexPen]
        val previousPen = bitmapsPen.previous(indexPen)
        val penWidth = (currentPen.width + previousPen.width)/2
        val penHeight = (currentPen.height + previousPen.height)/2

        if (distance >= penWidth  && distance >= penHeight) {
            mMatrixPen.reset()
            mMatrixPen.postTranslate(touchX - currentPen.width / 2, touchY - currentPen.height / 2)
            mMatrixPen.postRotate(random.nextInt(90).toFloat(), touchX , touchY )
            drawCanvas?.drawBitmap(currentPen, mMatrixPen, null)
            startPoint.set(touchX, touchY)
            indexPen = if (indexPen == bitmapsPen.size - 1) 0 else indexPen + 1
        }
    }

    override fun touchUp(touchX: Float, touchY: Float) {
        mPositions.clear()
    }
}