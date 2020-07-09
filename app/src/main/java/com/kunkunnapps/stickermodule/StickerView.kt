package com.kunkunnapps.stickermodule

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Align
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.kunkunnapps.stickermodule.sticker.textsticker.StickerUtils
import com.kunkunnapps.stickermodule.view.Utils
import kotlin.math.atan2
import kotlin.math.hypot


class StickerView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    val TAG = "StickerView"
    private val mMatrix = Matrix()
    private var mBitmap: Bitmap? = null
    private var textBitmap: Bitmap? = null
    private val mPaint = Paint()
    private val mTextPaint = TextPaint()
    private val mArrayOfFloat = FloatArray(9)
    private var isChangeText = false
    var text: String = "Test"
        set(value) {
            field = value
            isChangeText = true
            invalidate()
        }

    private val mDotBoundPaint = Paint()
    private var mDotHeight = 1f

    private var mDensity = 1f
    private var mButtonRadius = 12f

    private val mButtonPaint = Paint()

    private val mRotateButtonRegion = Region()
    private val mDeleteButtonRegion = Region()
    private val mImageRegion = Region()

    private var mInEdit = false
    private var scale = 1f
    private lateinit var stickerInfo : StickerBgTextInfo

    private val mScaleBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_zoom)
    private val mDeleteBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_delete)

    constructor(context: Context, stickerInfo: StickerBgTextInfo): this(context, null) {
        this.stickerInfo = stickerInfo
        mBitmap = stickerInfo.bitmap
    }

    var deleteCallback: (() -> Unit)? = null

    init {
        FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        mDensity = 3f
        mDotHeight *= mDensity
        mButtonRadius *= mDensity
        mPaint.apply {
            isAntiAlias = true
            color = Color.BLACK

        }

        mTextPaint.apply {
            isAntiAlias = true
            color = Color.BLACK
        }


        mDotBoundPaint.apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(5f * mDensity, 5f * mDensity), 0f)
            strokeWidth = mDotHeight
        }

        mButtonPaint.apply {
            color = Color.GREEN
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }


    override fun onDraw(canvas: Canvas) {
        mBitmap?.let {

            val rectPointF = getRectPointFBitmap(bitmap = mBitmap!!, matrix = mMatrix)

            canvas.save()
            canvas.drawBitmap(mBitmap!!, mMatrix, null)
            if (mInEdit) {
                val path = Path().apply {
                    moveTo(rectPointF.pointTopLeft.x, rectPointF.pointTopLeft.y)
                    lineTo(rectPointF.pointTopRight.x, rectPointF.pointTopRight.y)
                    lineTo(rectPointF.pointBottomRight.x, rectPointF.pointBottomRight.y)
                    lineTo(rectPointF.pointBottomLeft.x, rectPointF.pointBottomLeft.y)
                    close()
                }


                getBoundRegion(mImageRegion, path)
                canvas.drawPath(path, mDotBoundPaint)
                canvas.restore()

                drawRotateButton(canvas, rectPointF.pointTopRight.x, rectPointF.pointTopRight.y)
                drawDeleteButton(canvas, rectPointF.pointTopLeft.x, rectPointF.pointTopLeft.y)
                drawDeleteButton(canvas, rectPointF.pointTopRight.x, rectPointF.pointTopRight.y)
            }

            if (text.isNotBlank()) {

                textBitmap = textAsBitmap(mBitmap!!.width, mBitmap!!.height, text, 44f, Color.BLACK)
                textBitmap?.let {
                    canvas.drawBitmap(it, mMatrix, mTextPaint)
                }
                if (textBitmap?.isRecycled == false) {
                    textBitmap?.recycle()
                    textBitmap = null
                }
            }
        }
    }

    fun getPointFCenter(point1: PointF, point2: PointF): PointF {
        return PointF((point1.x + point2.x) / 2, (point1.y + point2.y) / 2)
    }


    private fun calculatorDistanceTwoPoint(point1: PointF, point2: PointF): Float {
        return hypot((point1.x - point2.x).toDouble(), (point1.y - point2.y).toDouble()).toFloat()
    }


    fun getRectPointFBitmap(bitmap: Bitmap, matrix: Matrix): StickerUtils.RectPointF {
        val arrayOfFloat = FloatArray(9)
        matrix.getValues(arrayOfFloat)

        val topLeftX = arrayOfFloat[Matrix.MTRANS_X]
        val topLeftY = arrayOfFloat[Matrix.MTRANS_Y]

        val topRightX = arrayOfFloat[Matrix.MSCALE_X] * bitmap.width + topLeftX
        val topRightY = topLeftY + arrayOfFloat[Matrix.MSKEW_Y] * bitmap.width

        val bottomLeftX = topLeftX + arrayOfFloat[Matrix.MSKEW_X] * bitmap.height
        val bottomLeftY = arrayOfFloat[Matrix.MSCALE_Y] * bitmap.height + topLeftY

        val bottomRightX =
            arrayOfFloat[Matrix.MSCALE_X] * bitmap.width + topLeftX + arrayOfFloat[Matrix.MSKEW_X] * bitmap.height

        val bottomRightY =
            arrayOfFloat[Matrix.MSCALE_Y] * bitmap.height + topLeftY + arrayOfFloat[Matrix.MSKEW_Y] * bitmap.width
        return StickerUtils.RectPointF(
            pointTopLeft = PointF(topLeftX, topLeftY),
            pointTopRight = PointF(topRightX, topRightY),
            pointBottomLeft = PointF(bottomLeftX, bottomLeftY),
            pointBottomRight = PointF(bottomRightX, bottomRightY)
        )
    }


    private fun textAsBitmap(
        width: Int,
        height: Int,
        text: String,
        textSize: Float,
        textColor: Int
    ): Bitmap? {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        var image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        image = Utils.convertToMutable(image)
        val canvas = Canvas(image)
        val centerX = width / 2
        val centerY = height / 2
        paint.textAlign = Align.LEFT
        val realWidth = width - width * stickerInfo.spacePercentLeft - width * stickerInfo.spacePercentRight
        val realHeight = height - height * stickerInfo.spacePercentBottom - height * stickerInfo.spacePercentTop

        val textLayout = getStaticLayout(width = realWidth.toInt(), height = realHeight.toInt(), paint = paint, text = text)
        val rectF = Rect()
        val textLength = getTextLength(text)
        paint.getTextBounds(text, textLength.first, textLength.second,rectF )
        Log.d(TAG, "textAsBitmap: rectWidth ${rectF.width()} rectHeight: ${rectF.height()}")
        val x = (centerX - textLayout.width / 2).takeIf { it > 0 } ?: 0
        val y = (centerY - textLayout.height / 2).takeIf { it > 0 } ?: 0
        canvas.translate(x.toFloat(), y.toFloat())
        textLayout.draw(canvas)
        return image
    }

    private fun getTextLength(text: String): Pair<Int, Int> {
        val isBreakLine = text.contains("\n")
        var longestLine = ""
        if (isBreakLine) {
            val listLine = text.split("\n")
            listLine.forEachIndexed { index, line ->
                if(longestLine.length < line.length) {
                    longestLine = line
                }
            }
        } else {
            longestLine = text
        }
        val start = text.indexOf(longestLine)
        val end = start + longestLine.length
        Log.d(TAG, "getTextLength: $longestLine")
        return Pair(start, end)

    }

    private fun getStaticLayout(width: Int, height: Int, paint: TextPaint, text: String): StaticLayout {
        var textLayout = StaticLayout(
            text, paint, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false
        )
        var isOk = false
        var textSize = paint.textSize
        while (!isOk) {
            if(textLayout.height > height) {
                textSize -= 0.5f
                paint.textSize = textSize
                textLayout = StaticLayout(
                    text, paint, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false
                )
            } else {
                isOk = true
            }
        }
        return textLayout
    }

    private fun drawBoundPath(canvas: Canvas, path: Path) {

    }

    private fun drawRotateButton(canvas: Canvas?, offsetX: Float, offsetY: Float) {
        val path = Path().apply {
            addRect(0f, 0f, mButtonRadius * 2, mButtonRadius * 2, Path.Direction.CCW)
            offset(offsetX - mButtonRadius, offsetY - mButtonRadius)
            getBoundRegion(mRotateButtonRegion, this)
        }
        val recf = RectF(
            offsetX - mButtonRadius,
            offsetY - mButtonRadius,
            offsetX + mButtonRadius,
            offsetY + mButtonRadius
        )
        canvas?.drawBitmap(mScaleBitmap, null, recf, null)
        // canvas?.drawPath(path, mButtonPaint)
    }

    private fun drawDeleteButton(canvas: Canvas?, offsetX: Float, offsetY: Float) {
        val path = Path().apply {
            addRect(0f, 0f, mButtonRadius * 2, mButtonRadius * 2, Path.Direction.CCW)
            offset(offsetX - mButtonRadius, offsetY - mButtonRadius)
            getBoundRegion(mDeleteButtonRegion, this)
        }
        val recf = RectF(
            offsetX - mButtonRadius,
            offsetY - mButtonRadius,
            offsetX + mButtonRadius,
            offsetY + mButtonRadius
        )
        canvas?.drawBitmap(mDeleteBitmap, null, recf, null)
        // canvas?.drawPath(path, mButtonPaint)
    }

    fun setBitmap(bitmap: Bitmap?) {
        bitmap?.let {
            mMatrix.postTranslate(100f, 100f)
            mBitmap = it
            requestLayout()
        }

    }


    fun setBitmap(bitmap: Bitmap?, inEdit: Boolean) {
        bitmap?.let {
            mMatrix.postTranslate(100f, 100f)
            mBitmap = it
            requestLayout()
            setInEdit(inEdit)
        }

    }

    fun setBitmap(bitmap: Bitmap?, inEdit: Boolean, parentWidth: Int, parentHeight: Int) {
        bitmap?.let {
            var limitX = parentWidth - bitmap.width
            if (limitX <= 0) limitX = 1
            var limitY = parentWidth - bitmap.height
            if (limitY <= 0) limitY = 1
            mMatrix.postTranslate((0..limitX).random().toFloat(), (0..limitY).random().toFloat())
            mBitmap = it
            requestLayout()
            setInEdit(inEdit)
        }

    }

    private fun getBoundRegion(region: Region, path: Path) {
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


    private var mTouchPointX = 0f
    private var mTouchPointY = 0f
    private var mIsRotateMode = false
    private var mIsMotion = false
    var mDiagonalLength = 0f
    var mMidPointX = 0f
    var mMidPointY = 0f
    var mLastDegrees = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!mInEdit) return false
        if (event?.action == MotionEvent.ACTION_DOWN) {
            mTouchPointX = event.rawX
            mTouchPointY = event.rawY
            if (mRotateButtonRegion.contains(event.x.toInt(), event.y.toInt())) {
                mIsRotateMode = true

                val arrayOfFloat = FloatArray(9)
                mMatrix.getValues(arrayOfFloat)

                val topLeftX = getMatrixTranslateX()
                val topLeftY = getMatrixTranslateY()
                val bottomRightX = event.x
                val bottomRightY = event.y
                mMidPointX = (topLeftX + bottomRightX) / 2
                mMidPointY = (topLeftY + bottomRightY) / 2
                mDiagonalLength = hypot(bottomRightX - mMidPointX, bottomRightY - mMidPointY)
                mLastDegrees = Math.toDegrees(
                    atan2(
                        (bottomRightY - mMidPointY),
                        (bottomRightX - mMidPointX)
                    ).toDouble()
                ).toFloat()
                return true
            } else if (mDeleteButtonRegion.contains(event.x.toInt(), event.y.toInt())) {
                deleteCallback?.invoke()
            } else if (mImageRegion.contains(event.x.toInt(), event.y.toInt())) {
                mIsMotion = true
                return true
            } else return false

        } else if (event?.action == MotionEvent.ACTION_MOVE) {
            if (mIsRotateMode) {
                rotate(event.x, event.y)
            } else if (mIsMotion) {
                motionView(event.rawX, event.rawY)
            }
            return true

        } else if (event?.action == MotionEvent.ACTION_UP) {
            mIsRotateMode = false
            mIsMotion = false
            return true
        }

        return false
    }

    private fun motionView(rawX: Float, rawY: Float) {
        mMatrix.postTranslate(rawX - mTouchPointX, rawY - mTouchPointY)
        mTouchPointX = rawX
        mTouchPointY = rawY
        invalidate()
    }

    private fun rotate(rawX: Float, rawY: Float) {
        val toDiagonalLength = hypot((rawX) - mMidPointX, (rawY) - mMidPointY)
        scale = toDiagonalLength / mDiagonalLength
        mMatrix.postScale(scale, scale, mMidPointX, mMidPointY)
        mDiagonalLength = toDiagonalLength

        val toDegrees =
            Math.toDegrees(atan2((rawY - mMidPointY), (rawX - mMidPointX)).toDouble()).toFloat()

        val degrees = toDegrees - mLastDegrees
        mMatrix.postRotate(degrees, mMidPointX, mMidPointY)
        mLastDegrees = toDegrees
        mMatrix.getValues(mArrayOfFloat)

        invalidate()
    }

    fun setInEdit(inEdit: Boolean) {
        mInEdit = inEdit
        invalidate()
    }

    private fun getMatrixTranslateX(): Float {
        mMatrix.getValues(mArrayOfFloat)
        return mArrayOfFloat[Matrix.MTRANS_X]
    }

    private fun getMatrixTranslateY(): Float {
        mMatrix.getValues(mArrayOfFloat)
        return mArrayOfFloat[Matrix.MTRANS_Y]
    }

    private fun getMatrixScaleX(): Float {
        mMatrix.getValues(mArrayOfFloat)
        return mArrayOfFloat[Matrix.MSCALE_X]
    }

    private fun getMatrixScaleY(): Float {
        mMatrix.getValues(mArrayOfFloat)
        return mArrayOfFloat[Matrix.MSCALE_Y]
    }

    private fun getMatrixSkewX(): Float {
        mMatrix.getValues(mArrayOfFloat)
        return mArrayOfFloat[Matrix.MSKEW_X]
    }

    private fun getMatrixSkewY(): Float {
        mMatrix.getValues(mArrayOfFloat)
        return mArrayOfFloat[Matrix.MSKEW_Y]
    }

    fun getBitmap(): Bitmap? = mBitmap


    class StickerBgTextInfo(
        var spacePercentTop: Float,
        var spacePercentBottom: Float,
        var spacePercentRight: Float,
        var spacePercentLeft: Float,
        var bitmap: Bitmap
    )
}