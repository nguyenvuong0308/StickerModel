package com.kunkunnapps.stickermodule.textsticker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.kunkunnapps.stickermodule.R
import com.kunkunnapps.stickermodule.view.getRotationAngle
import kotlin.math.atan2
import kotlin.math.hypot

class Sticker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val TAG = "Sticker"

    var isEnableTouch = true
    var touchMode = TouchMode.NONE
    var mPivotX = 0f
    var mPivotY = 0f
    var pointTouch = PointF(0f, 0f)

    /*zoom controller*/
    val mZoomBitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(resources, R.drawable.ic_zoom),
        context.resources.getDimensionPixelSize(R.dimen.size_24dp),
        context.resources.getDimensionPixelSize(R.dimen.size_24dp),
        false
    )
    val regionZoom = Region()
    var mDiagonalLength: Double = 0.0
    /**/

    /*zoom controller*/
    val mRotateBitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(resources, R.drawable.ic_rotate),
        context.resources.getDimensionPixelSize(R.dimen.size_24dp),
        context.resources.getDimensionPixelSize(R.dimen.size_24dp),
        false
    )
    val regionRotate = Region()
    var mLastDegrees: Double = 0.0
    /**/


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnableTouch) {
            return super.onTouchEvent(event)
        } else {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchMode = TouchMode.MOVE
                    pointTouch.x = event.x
                    pointTouch.y = event.y

                    when {
                        regionZoom.contains(event.x.toInt(), event.y.toInt()) -> {
                            mDiagonalLength =
                                hypot(event.rawX - pivotX, event.rawY - pivotY).toDouble()
                            touchMode = TouchMode.ZOOM
                        }

                        regionRotate.contains(event.x.toInt(), event.y.toInt()) -> {
                            mLastDegrees = Math.toDegrees(
                                atan2(
                                    (event.x - pivotX),
                                    (event.y - pivotY)
                                ).toDouble()
                            )

                            touchMode = TouchMode.ROTATE
                        }


                    }

                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    when (touchMode) {
                        TouchMode.MOVE -> {
                            move(event)

                        }

                        TouchMode.NONE -> {

                        }

                        TouchMode.ZOOM -> {
                            val newDiagonalLength =
                                hypot(event.rawX - pivotX, event.rawY - pivotY).toDouble()
                            val scale = newDiagonalLength / mDiagonalLength
                            val mMatrix = Matrix(matrix)
                            mMatrix.postScale(scale.toFloat(), scale.toFloat(), pivotX, pivotY)
                            val values = FloatArray(9)
                            mMatrix.getValues(values)
                            scaleX = scale.toFloat()
                            scaleY = scale.toFloat()
                            Log.d(TAG, "onTouchEvent: pivotX $pivotX pivotY $pivotY scale $scale newDiagonalLength $newDiagonalLength mDiagonalLength $mDiagonalLength")
                        }

                        TouchMode.ROTATE -> {




                        }
                    }
                    return true
                }
            }
        }

        return false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        /*draw zoom*/
        val matrixZoom = Matrix()
        matrixZoom.setTranslate(
            (width - mZoomBitmap.width).toFloat(),
            (height - mZoomBitmap.height).toFloat()
        )

        val path = Path().apply {
            moveTo((width - mZoomBitmap.width).toFloat(), (height - mZoomBitmap.height).toFloat())
            lineTo(width.toFloat(), (height - mZoomBitmap.height).toFloat())
            lineTo(width.toFloat(), height.toFloat())
            lineTo((width - mZoomBitmap.width).toFloat(), height.toFloat())
            close()
        }

        fillBoundRegion(path = path, region = regionZoom)
        canvas?.drawBitmap(mZoomBitmap, matrixZoom, null)

        /*draw zoom*/
        val matrixRotate = Matrix()
        matrixRotate.setTranslate(
            (width - mZoomBitmap.width).toFloat(),
            0f
        )
        val pathRotate = Path().apply {
            moveTo((width - mRotateBitmap.width).toFloat(), 0f)
            lineTo(width.toFloat(), 0f)
            lineTo(width.toFloat(), mRotateBitmap.height.toFloat())
            lineTo((width - mRotateBitmap.width).toFloat(), mRotateBitmap.height.toFloat())
            close()
        }

        fillBoundRegion(path = pathRotate, region = regionRotate)
        canvas?.drawBitmap(mRotateBitmap, matrixRotate, null)


    }

    private fun move(event: MotionEvent) {
        val spaceX = event.x - pointTouch.x
        val spaceY = event.y - pointTouch.y
        Log.d(
            TAG,
            "onTouchEvent: spaceX $spaceX  spaceY $spaceY event.x ${event.x}  event.y ${event.y}  event.rawX ${event.rawX}  event.rawY ${event.rawY}"
        )

        val deltaVector = floatArrayOf(spaceX, spaceY)
        matrix.mapVectors(deltaVector)
        setHasTransientState(true)
        translationX += deltaVector[0]
        translationY += deltaVector[1]
    }

    private fun fillBoundRegion(region: Region, path: Path) {
        val boundRecF = RectF()
        path.computeBounds(boundRecF, true)
        region.setPath(
            path,
            Region(
                boundRecF.left.toInt(),
                boundRecF.top.toInt(),
                boundRecF.right.toInt(),
                boundRecF.bottom.toInt()
            )
        )
    }


}