package com.kunkunnapps.stickermodule

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.kunkunnapps.stickermodule.view.getRotationAngle
import kotlin.math.atan2
import kotlin.math.hypot

class ImageStickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private  val TAG = "ImageStickerView"
    val mMatrixScale = Matrix()

    init {
        rotation = 45f
        mMatrixScale.set(matrix)
    }
    val NONE = 0
    val MOVE = 1
    val ZOOM = 2
    val ROTATE = 3

    var touchMode = NONE
    var touchX = 0f
    var touchY = 0f
    var touchRawX = 0f
    var touchRawY = 0f
    var mPivotX = 0f
    var mPivotY = 0f
    val mZoomBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_zoom), 100, 100, false)
    val mRotateBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_rotate), 100, 100, false)
    val regionZoom = Region()
    val regionRotate = Region()
    var mDiagonalLength: Double = 0.0
    var mLastDegrees: Double = 0.0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "onTouchEvent: event.x ${event.x}  event.y ${event.y}  event.rawX ${event.rawX}  event.rawY ${event.rawY}")
                touchMode = MOVE
                touchX = event.x
                touchY = event.y
                touchRawX = event.rawX
                touchRawY = event.rawY
                mPivotX = translationX + (width)/2
                mPivotY = translationY + (height)/2


                if (regionZoom.contains(event.x.toInt(), event.y.toInt())) {
                    mDiagonalLength = hypot(touchRawX - mPivotX, touchRawY - mPivotY).toDouble()
                    Log.d(TAG, "Click inside region Zoom")
                    Log.d(TAG, "Zoom1 mPivotX $mPivotX mPivotY $mPivotY mDiagonalLength $mDiagonalLength width $width height $height translationX $translationX translationY $translationY")
                    touchMode = ZOOM
                } else if (regionRotate.contains(event.x.toInt(), event.y.toInt())) {
                    Log.d(TAG, "Click inside region Rotate")
                    touchMode = ROTATE
                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val mValues = FloatArray(9)
                if (touchMode == MOVE) {
                    val spaceX = event.x - touchX
                    val spaceY = event.y - touchY
                    Log.d(TAG, "onTouchEvent: spaceX $spaceX  spaceY $spaceY event.x ${event.x}  event.y ${event.y}  event.rawX ${event.rawX}  event.rawY ${event.rawY}")

                    val deltaVector = floatArrayOf(spaceX, spaceY)
                    matrix.mapVectors(deltaVector)
                    setHasTransientState(true)
                    translationX += deltaVector[0]
                    translationY += deltaVector[1]
                } else if (touchMode == ZOOM) {
                    val newDiagonalLength = hypot( event.rawX - mPivotX, event.rawY - mPivotY).toDouble()
                    val scale = newDiagonalLength/ mDiagonalLength
                    mMatrixScale.postScale(scale.toFloat(), scale.toFloat())

                    mMatrixScale.getValues(mValues)
                    Log.d(TAG, "onTouchEvent: mMatrixScale.toShortString() ${mMatrixScale.toShortString()}")
                    Log.d(TAG, "onTouchEvent: matrix.toShortString() ${ matrix.toShortString()}")

                    scaleX= mValues[Matrix.MSCALE_X]
                    scaleY= mValues[Matrix.MSCALE_Y]
                    mDiagonalLength = newDiagonalLength
                } else if(touchMode == ROTATE) {
                    val toDegrees =
                        Math.toDegrees(atan2((event.rawY - mPivotY), (event.rawX - mPivotX)).toDouble()).toFloat()

                    val degrees = toDegrees - mLastDegrees
                    mMatrixScale.postRotate(degrees.toFloat(), mPivotX, mPivotY)
                    mLastDegrees = toDegrees.toDouble()

                    rotation = mMatrixScale.getRotationAngle()
                    Log.d(TAG, "onTouchEvent: mMatrixScale.getRotationAngle() ${ mMatrixScale.getRotationAngle()}")

                }
            }



            MotionEvent.ACTION_UP -> {
                touchMode = NONE
            }
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val matrixZoom = Matrix()
        matrixZoom.setTranslate((width - mZoomBitmap.width).toFloat(),
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
        canvas.drawBitmap(mZoomBitmap, matrixZoom, null)

        val matrixRotate = Matrix()
        matrixRotate.setTranslate((width - mRotateBitmap.width).toFloat(),
           0f
        )

        val pathRotate = Path().apply {
            moveTo((width - mRotateBitmap.width).toFloat(), 0f)
            lineTo(width.toFloat(), 0f)
            lineTo(width.toFloat(), height.toFloat())
            lineTo((width - mRotateBitmap.width).toFloat(), height.toFloat())
            close()
        }
        fillBoundRegion(path = pathRotate, region = regionRotate)

        canvas.drawBitmap(mRotateBitmap, matrixRotate, null)

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