package com.kunkunnapps.stickermodule.sticker.textsticker

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Region
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.hypot

class StickerTouchEvent {
    private val TAG = "StickerTouchEvent"

    //region Touch Event
    private var mTouchPointX = 0f
    private var mTouchPointY = 0f
    private var touchMode =
        TOUCH_MODE.NONE
    var mDiagonalLength = 0f
    var mMidPointX = 0f
    var mMidPointY = 0f
    var mLastDegrees = 0f
    //endregion

    //region Button rotate
    val mRegionRotate = Region()
    //endregion

    //region Button zoom
    val mRegionZoom = Region()
    //endregion

    //region Button delete
    val mRegionDelete = Region()
    var deleteCallback: (() -> Unit)? = null
    //endregion

    //region Button scale vertical
    val mRegionScaleVertical = Region()
    //endregion

    //region Button scale horizontal
    val mRegionScaleHorizontal = Region()
    //endregion

    fun onTouch(editable: Boolean, view: View, event: MotionEvent, bitmap: Bitmap, matrix: Matrix, contentRegion: Region, onContentTouch: () -> Unit): Boolean {
        if(editable) {
            val rectFBoundImagePoint = StickerUtils.getRectPointFBitmap(bitmap, matrix)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mMidPointX =
                        (rectFBoundImagePoint.pointTopLeft.x + rectFBoundImagePoint.pointBottomRight.x) / 2
                    mMidPointY =
                        (rectFBoundImagePoint.pointTopLeft.y + rectFBoundImagePoint.pointBottomRight.y) / 2
                    mTouchPointX = event.rawX
                    mTouchPointY = event.rawY
                    view.bringToFront()

                    when {

                        mRegionRotate.contains(event.x.toInt(), event.y.toInt()) -> {
                            touchMode =
                                TOUCH_MODE.ROTATE

                            mDiagonalLength = hypot(event.x - mMidPointX, event.y - mMidPointY)

                            mLastDegrees = Math.toDegrees(
                                atan2(
                                    (event.y - mMidPointY),
                                    (event.x - mMidPointX)
                                ).toDouble()
                            ).toFloat()
                            return true
                        }

                        mRegionZoom.contains(event.x.toInt(), event.y.toInt()) -> {
                            touchMode =
                                TOUCH_MODE.ZOOM
                            mDiagonalLength = hypot(event.x - mMidPointX, event.y - mMidPointY)
                            return true
                        }

                        mRegionScaleHorizontal.contains(event.x.toInt(), event.y.toInt()) -> {
                            mMidPointX =
                                (rectFBoundImagePoint.pointTopLeft.x + rectFBoundImagePoint.pointBottomLeft.x) / 2
                            mMidPointY =
                                (rectFBoundImagePoint.pointTopLeft.y + rectFBoundImagePoint.pointBottomLeft.y) / 2

                            touchMode =
                                TOUCH_MODE.SCALE_HORIZONTAL
                            mDiagonalLength = hypot(event.x - mMidPointX, event.y - mMidPointY)
                            return true
                        }

                        mRegionScaleVertical.contains(event.x.toInt(), event.y.toInt()) -> {
                            mMidPointX =
                                (rectFBoundImagePoint.pointTopLeft.x + rectFBoundImagePoint.pointTopRight.x) / 2
                            mMidPointY =
                                (rectFBoundImagePoint.pointTopLeft.y + rectFBoundImagePoint.pointTopRight.y) / 2
                            touchMode =
                                TOUCH_MODE.SCALE_VERTICAL
                            mDiagonalLength = hypot(event.x - mMidPointX, event.y - mMidPointY)
                            return true
                        }

                        mRegionDelete.contains(event.x.toInt(), event.y.toInt()) -> {
                            touchMode =
                                TOUCH_MODE.NONE
                            deleteCallback?.invoke()
                            return true
                        }

                        contentRegion.contains(event.x.toInt(), event.y.toInt()) -> {
                            touchMode =
                                TOUCH_MODE.MOVE
                            return true
                        }

                        else -> {
                            touchMode =
                                TOUCH_MODE.NONE
                        }
                    }
                    return false

                }

                MotionEvent.ACTION_MOVE -> {
                    when (touchMode) {
                        TOUCH_MODE.ROTATE -> {
                            rotate(event.x, event.y, matrix, view)
                        }

                        TOUCH_MODE.ZOOM -> {
                            zoom(event.x, event.y, matrix, view)
                        }

                        TOUCH_MODE.MOVE -> {
                            moveBitmap(event.rawX, event.rawY, matrix, view)
                        }

                        TOUCH_MODE.NONE -> {

                        }

                        TOUCH_MODE.SCALE_HORIZONTAL -> {
                            scaleHorizontal(event.x, event.y, matrix, view)
                        }

                        TOUCH_MODE.SCALE_VERTICAL -> {
                            scaleVertical(event.x, event.y, matrix, view)
                        }
                    }
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    touchMode =
                        TOUCH_MODE.NONE
                    return true
                }
            }
        } else {
            if (event.action == MotionEvent.ACTION_DOWN && contentRegion.contains(event.x.toInt(), event.y.toInt())) {
                onContentTouch.invoke()
                return true
            }
        }


        return false
    }

    private fun scaleVertical(x: Float, y: Float, matrix: Matrix, view: View) {
        val toDiagonalLength = hypot((x) - mMidPointX, (y) - mMidPointY)
        val scale = toDiagonalLength / mDiagonalLength
        matrix.preScale(1f, scale)
        mDiagonalLength = toDiagonalLength
        view.invalidate()
    }

    private fun rotate(rawX: Float, rawY: Float, matrix: Matrix, view: View) {
        val toDegrees =
            Math.toDegrees(atan2((rawY - mMidPointY), (rawX - mMidPointX)).toDouble()).toFloat()
        val degrees = toDegrees - mLastDegrees
        matrix.postRotate(degrees, mMidPointX, mMidPointY)

        mLastDegrees = toDegrees
        view.invalidate()
    }

    private fun zoom(rawX: Float, rawY: Float, matrix: Matrix, view: View) {
        val toDiagonalLength = hypot((rawX) - mMidPointX, (rawY) - mMidPointY)
        val scale = toDiagonalLength / mDiagonalLength
        matrix.postScale(scale, scale, mMidPointX, mMidPointY)
        mDiagonalLength = toDiagonalLength
        view.invalidate()
    }

    private fun moveBitmap(rawX: Float, rawY: Float, matrix: Matrix, view: View) {
        matrix.postTranslate(rawX - mTouchPointX, rawY - mTouchPointY)
        mTouchPointX = rawX
        mTouchPointY = rawY
        view.invalidate()
    }

    private fun scaleHorizontal(rawX: Float, rawY: Float, matrix: Matrix, view: View) {
        val toDiagonalLength = hypot((rawX) - mMidPointX, (rawY) - mMidPointY)
        val scale = toDiagonalLength / mDiagonalLength
        matrix.preScale(scale, 1f)
        Log.d(
            TAG,
            "scaleHorizontal: mMidPointX $mMidPointX mMidPointY $mMidPointY rawX $rawX rawY$rawY toDiagonalLength $toDiagonalLength scale $scale mDiagonalLength $mDiagonalLength"
        )
        mDiagonalLength = toDiagonalLength
        view.invalidate()
    }
}

enum class TOUCH_MODE {
    ROTATE,
    ZOOM,
    MOVE,
    SCALE_HORIZONTAL,
    SCALE_VERTICAL,
    NONE
}