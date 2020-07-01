package com.kunkunnapps.stickermodule.view

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.kunkunnapps.stickermodule.*
import kotlin.math.atan2
import kotlin.math.hypot

class TextSticker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val TAG = "TextSticker"

    /*paint draw background*/
    private var mPaintBg = Paint()
    private val mMatrixBg = Matrix()
    private var mTextMatrix = Matrix()

    /*background*/
    private lateinit var bitmapBackground: Bitmap

    /*paint draw text*/
    private var mTextPaint = TextPaint()
    private var bitmapText: Bitmap? = null
    private var mText: String = "Something"

    /*paint draw rect bound view*/
    private val mDotBoundPaint = Paint()

    private var isNeedCreatedBg: Boolean = true
    private var mValuesMatrix = FloatArray(9)

    private var mButtonRadius =
        DisplayUtils.dpToPx(context, 8f)
    private val DEFAULT_WIDTH = 250

    /*bound path*/
    private var pathBound = Path()
    private var mDotWidth = 1f
    private var mDensity = 1f

    /*button rotate*/
    private val mRotateButtonRegion = Region()
    private val mRotateBitmap = BitmapFactory.decodeResource(resources,
        R.drawable.ic_rotate
    )

    /*button zoom*/
    private val mZoomBitmap = BitmapFactory.decodeResource(resources,
        R.drawable.ic_zoom
    )
    private val mZoomButtonRegion = Region()

    /*button delete*/
    private val mDeleteButtonRegion = Region()
    private val mDeleteBitmap = BitmapFactory.decodeResource(resources,
        R.drawable.ic_delete
    )
    var deleteCallback: (() -> Unit)? = null

    private val mContentRegion = Region()
    private var mInEdit = false

    /*button scale horizontal*/
    private val mScaleHorizontalButtonRegion = Region()
    private val mScaleHorizontalBitmap = BitmapFactory.decodeResource(resources,
        R.drawable.ic_zoom
    )
    private lateinit var stickerTextInfo: StickerTextInfo
    private var spaceMultiText = 1f
    private var spaceAddText = 0f

    constructor(context: Context, stickerInfo: StickerTextInfo) : this(context, null) {
        mDensity = 3f
        mDensity *= mDensity
        mPaintBg.apply {
            isAntiAlias = true
            color = Color.BLACK
        }


        mTextPaint.apply {
            isAntiAlias = true
            color = Color.BLACK
        }

        mDotBoundPaint.apply {
            color = Color.BLUE
            isAntiAlias = true
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(5f * mDensity, 5f * mDensity), 0f)
            strokeWidth = mDotWidth
        }

        setStickerInfo(stickerInfo)

    }

    fun setEditEnable(isEnable: Boolean) {
        mInEdit = isEnable
    }

    fun setText(text: String) {
        this.mText = text
        if (isNeedCreatedBg) {
            bitmapBackground = createBitmapBackgroundByText(text, mTextPaint)
        }
        invalidate()
    }

    fun setStickerInfo(stickerInfo: StickerTextInfo) {
        this.stickerTextInfo = stickerInfo
        if (stickerTextInfo.bitmap != null) {
            /*bitmapBackground.recycle()*/
            bitmapBackground = stickerTextInfo.bitmap!!
            isNeedCreatedBg = false
            if (bitmapBackground.width > DEFAULT_WIDTH) {
                val scale = DEFAULT_WIDTH.toFloat() / bitmapBackground.width
                mMatrixBg.postScale(scale, scale)
            }

        } else {
            isNeedCreatedBg = true
            /* bitmapBackground.recycle()*/
            bitmapBackground = createBitmapBackgroundByText(mText, mTextPaint)
        }

        invalidate()
    }

    fun drawTextIntoBitmap(bitmap: Bitmap, text: String) {

    }

    private fun isDraw() = mText.isNotBlank()

    private fun createBitmapBackgroundByText(text: String, textPaint: TextPaint): Bitmap {
        val staticLayout = createStaticLayoutAutoResizeText(DEFAULT_WIDTH, null, text, textPaint)
        val bitmap =
            Bitmap.createBitmap(
                DEFAULT_WIDTH,
                staticLayout.height.takeIf { it > 0 } ?: 1,
                Bitmap.Config.ARGB_8888)
        return bitmap
    }

    private fun getTextLength(text: String): Pair<Int, Int> {
        val isBreakLine = text.contains("\n")
        var longestLine = ""
        if (isBreakLine) {
            val listLine = text.split("\n")
            listLine.forEachIndexed { index, line ->
                if (longestLine.length < line.length) {
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

    private fun autoSizeTextPaint(text: String, width: Int, textPaint: TextPaint) {
        val longestLineIndex = getTextLength(text)
        val rectF = Rect()
        var isOk = false
        var textSize = textPaint.textSize
        textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, rectF)
        if (rectF.width() < width) {
            while (!isOk) {
                if (rectF.width() < width) {
                    textSize += 0.5f
                    textPaint.textSize = textSize
                    textPaint.getTextBounds(
                        text,
                        longestLineIndex.first,
                        longestLineIndex.second,
                        rectF
                    )
                } else {
                    isOk = true
                }
            }
        }
        isOk = false
        if (rectF.width() > width) {
            while (!isOk) {
                if (rectF.width() > width) {
                    textSize -= 0.5f
                    textPaint.textSize = textSize
                    textPaint.getTextBounds(
                        text,
                        longestLineIndex.first,
                        longestLineIndex.second,
                        rectF
                    )
                } else {
                    isOk = true
                }
            }
        }

    }

    private fun createStaticLayoutAutoResizeText(
        width: Int,
        height: Int?,
        text: String,
        paint: TextPaint
    ): StaticLayout {

        autoSizeTextPaint(text, width, mTextPaint)

        var textLayout = StaticLayout(
            text, paint, width, Layout.Alignment.ALIGN_CENTER, spaceMultiText, spaceAddText, false
        )
        if (height == null) {
            return textLayout
        } else {
            var isOk = false
            var textSize = paint.textSize
            if (textLayout.height < height) {
                while (!isOk) {
                    if (textLayout.height < height) {
                        textSize += 0.5f
                        paint.textSize = textSize
                        textLayout = StaticLayout(
                            text, paint, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false
                        )
                    } else {
                        isOk = true
                    }
                }
            }
            isOk = false
            if (textLayout.height > height) {
                while (!isOk) {
                    if (textLayout.height > height) {
                        textSize -= 0.5f
                        paint.textSize = textSize
                        textLayout = StaticLayout(
                            text, paint, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false
                        )
                    } else {
                        isOk = true
                    }
                }
            }
        }
        return textLayout

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isDraw()) {
            bitmapBackground.let { bitmapBackground ->
                val rectPointFBoundBitmap =
                    getRectPointFBitmap(bitmap = bitmapBackground, matrix = mMatrixBg)
                /*draw background*/
                canvas.drawBitmap(bitmapBackground, mMatrixBg, null)

                if (mInEdit) {
                    pathBound.reset()
                    pathBound.apply {
                        moveTo(
                            rectPointFBoundBitmap.pointTopLeft.x,
                            rectPointFBoundBitmap.pointTopLeft.y
                        )
                        lineTo(
                            rectPointFBoundBitmap.pointTopRight.x,
                            rectPointFBoundBitmap.pointTopRight.y
                        )
                        lineTo(
                            rectPointFBoundBitmap.pointBottomRight.x,
                            rectPointFBoundBitmap.pointBottomRight.y
                        )
                        lineTo(
                            rectPointFBoundBitmap.pointBottomLeft.x,
                            rectPointFBoundBitmap.pointBottomLeft.y
                        )
                        close()
                    }
                    canvas.save()

                    canvas.drawPath(pathBound, mDotBoundPaint)

                    canvas.restore()
                    fillBoundRegion(mContentRegion, pathBound)
                    /*draw button delete*/
                    drawToolButton(
                        canvas = canvas,
                        offsetX = rectPointFBoundBitmap.pointTopLeft.x,
                        offsetY = rectPointFBoundBitmap.pointTopLeft.y,
                        radius = mButtonRadius,
                        bimap = mDeleteBitmap,
                        region = mDeleteButtonRegion
                    )

                    /*draw button rotate*/
                    drawToolButton(
                        canvas = canvas,
                        offsetX = rectPointFBoundBitmap.pointTopRight.x,
                        offsetY = rectPointFBoundBitmap.pointTopRight.y,
                        radius = mButtonRadius,
                        bimap = mRotateBitmap,
                        region = mRotateButtonRegion
                    )

                    /*draw button zoom*/
                    drawToolButton(
                        canvas = canvas,
                        offsetX = rectPointFBoundBitmap.pointBottomRight.x,
                        offsetY = rectPointFBoundBitmap.pointBottomRight.y,
                        radius = mButtonRadius,
                        bimap = mZoomBitmap,
                        region = mZoomButtonRegion
                    )

                    /*draw button zoom horizontal*/
                    drawToolButton(
                        canvas = canvas,
                        offsetX = (rectPointFBoundBitmap.pointBottomRight.x + rectPointFBoundBitmap.pointTopRight.x) / 2,
                        offsetY = (rectPointFBoundBitmap.pointBottomRight.y + rectPointFBoundBitmap.pointTopRight.y) / 2,
                        radius = mButtonRadius,
                        bimap = mScaleHorizontalBitmap,
                        region = mScaleHorizontalButtonRegion
                    )


                    Log.d(TAG, "onDraw: bitmapBackground.width ${bitmapBackground.width}")


                    val realWith =
                        Utils.getDistanceTwoPoint(
                            rectPointFBoundBitmap.pointTopLeft,
                            rectPointFBoundBitmap.pointTopRight
                        )
                    val realHeight =
                        Utils.getDistanceTwoPoint(
                            rectPointFBoundBitmap.pointTopRight,
                            rectPointFBoundBitmap.pointBottomRight
                        )
                    val valuesBase = FloatArray(9)

                    mMatrixBg.getValues(valuesBase)
                    bitmapText = textAsBitmap(
                        width = bitmapBackground.width,
                        height = bitmapBackground.height,
                        scaleX = valuesBase[Matrix.MSCALE_X],
                        scaleY = valuesBase[Matrix.MSCALE_Y],
                        text = mText,
                        paint = mTextPaint
                    )

                    mTextMatrix.reset()


                    val valuesText =
                        MatrixUtils.createValuesMatrix(
                            angle = Math.toRadians(mMatrixBg.getRotate().toDouble())
                                .toFloat(),
                            scaleX = 1f,
                            scaleY = 1f,
                            transX = valuesBase[Matrix.MTRANS_X],
                            transY = valuesBase[Matrix.MTRANS_Y]
                        )
                    mTextMatrix.setValues(valuesText)

                    Log.d(
                        TAG,
                        "onDraw: rotate ${mMatrixBg.getRotate2()} ${mMatrixBg.getRotate()}  ${mTextMatrix.getRotate()}$mLastDegrees"
                    )

                    Log.d(
                        TAG,
                        "onDraw: mTextMatrix ${mTextMatrix.toShortString()} $realWith $realHeight"
                    )
                    Log.d(TAG, "onDraw: mMatrixBg ${mMatrixBg.toShortString()}")
                    Log.d(
                        TAG,
                        "onDraw: 1getRealScaleX ${mTextMatrix.getRealScaleX()} getRealScaleY ${mTextMatrix.getRealScaleY()}"
                    )
                    bitmapText?.let {
                        canvas.drawBitmap(it, mMatrixBg, mTextPaint)
                    }
                    if (bitmapText?.isRecycled == false) {
                        bitmapText?.recycle()
                        bitmapText = null
                    }
                }
            }
        }

    }

    private fun textAsBitmap(
        bitmap: Bitmap,
        text: String,
        paint: TextPaint
    ): Bitmap? {
        val image = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val width = (image.width)
        val height = (image.height)
        val canvas = Canvas(image)

        val centerX = width / 2
        val centerY = height / 2

        val realWidth =
            width - width * stickerTextInfo.spacePercentLeft - width * stickerTextInfo.spacePercentRight
        val realHeight =
            height - height * stickerTextInfo.spacePercentBottom - height * stickerTextInfo.spacePercentTop

        val textLayout = createStaticLayoutAutoResizeText(
            width = realWidth.toInt(),
            height = realHeight.toInt(),
            paint = paint,
            text = text
        )

        val x = (centerX - textLayout.width / 2).takeIf { it > 0 } ?: 0
        val y = (centerY - textLayout.height / 2).takeIf { it > 0 } ?: 0
        canvas.translate(x.toFloat(), y.toFloat())
        textLayout.draw(canvas)
        return image
    }

    private fun textAsBitmap(
        width: Int,
        height: Int,
        text: String,
        paint: TextPaint,
        scaleX: Float,
        scaleY: Float
    ): Bitmap? {
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)

        val centerX = width / 2
        val centerY = height / 2

        val realWidth =
            width - width * stickerTextInfo.spacePercentLeft - width * stickerTextInfo.spacePercentRight
        val realHeight =
            height - height * stickerTextInfo.spacePercentBottom - height * stickerTextInfo.spacePercentTop

        val textLayout = createStaticLayoutAutoResizeText(
            width = realWidth.toInt(),
            height = realHeight.toInt(),
            paint = paint,
            text = text
        )

        val x = (centerX - textLayout.width / 2).takeIf { it > 0 } ?: 0
        val y = (centerY - textLayout.height / 2).takeIf { it > 0 } ?: 0
        canvas.translate(x.toFloat(), y.toFloat())
        textLayout.draw(canvas)
        return image
    }

    private fun drawToolButton(
        canvas: Canvas?,
        offsetX: Float,
        offsetY: Float,
        radius: Float,
        region: Region,
        bimap: Bitmap
    ) {
        Path().apply {
            addRect(0f, 0f, radius * 2, radius * 2, Path.Direction.CCW)
            offset(offsetX - radius, offsetY - radius)
            fillBoundRegion(region, this)
        }
        val rectF = RectF(
            offsetX - radius,
            offsetY - radius,
            offsetX + radius,
            offsetY + radius
        )
        canvas?.drawBitmap(bimap, null, rectF, null)
    }


    private var mTouchPointX = 0f
    private var mTouchPointY = 0f
    private var touchMode =
        TOUCH_MODE.NONE
    var mDiagonalLength = 0f
    var mMidPointX = 0f
    var mMidPointY = 0f
    var mLastDegrees = 0f

    enum class TOUCH_MODE {
        ROTATE,
        ZOOM,
        MOVE,
        SCALE_HORIZONTAL,
        NONE
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mInEdit) return false
        val rectFBoundImagePoint = getRectPointFBitmap(bitmapBackground, mMatrixBg)
        mMidPointX =
            (rectFBoundImagePoint.pointTopLeft.x + rectFBoundImagePoint.pointBottomRight.x) / 2
        mMidPointY =
            (rectFBoundImagePoint.pointTopLeft.y + rectFBoundImagePoint.pointBottomRight.y) / 2
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                mTouchPointX = event.rawX
                mTouchPointY = event.rawY
                when {
                    mRotateButtonRegion.contains(event.x.toInt(), event.y.toInt()) -> {
                        this.bringToFront()
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

                    mZoomButtonRegion.contains(event.x.toInt(), event.y.toInt()) -> {
                        touchMode =
                            TOUCH_MODE.ZOOM
                        this.bringToFront()
                        mDiagonalLength = hypot(event.x - mMidPointX, event.y - mMidPointY)
                        return true
                    }

                    mContentRegion.contains(event.x.toInt(), event.y.toInt()) -> {
                        this.bringToFront()
                        touchMode =
                            TOUCH_MODE.MOVE
                        return true
                    }

                    mScaleHorizontalButtonRegion.contains(event.x.toInt(), event.y.toInt()) -> {
                        this.bringToFront()
                        mDiagonalLength = hypot(event.x - mMidPointX, event.y - mMidPointY)
                        touchMode =
                            TOUCH_MODE.SCALE_HORIZONTAL
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
                        rotate(event.x, event.y)
                    }

                    TOUCH_MODE.ZOOM -> {
                        zoom(event.x, event.y)
                    }

                    TOUCH_MODE.MOVE -> {
                        moveBitmap(event.rawX, event.rawY)
                    }

                    TOUCH_MODE.NONE -> {

                    }

                    TOUCH_MODE.SCALE_HORIZONTAL -> {
                        scaleHorizontal(event.x, event.y)
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
        return false
    }

    private fun scaleHorizontal(rawX: Float, rawY: Float) {
        Log.d(
            TAG,
            "scaleHorizontal1: MSCALE_X ${mValuesMatrix[Matrix.MSCALE_X]} MSCALE_Y ${mValuesMatrix[Matrix.MSCALE_Y]}"
        )
        val toDiagonalLength = hypot((rawX) - mMidPointX, (rawY) - mMidPointY)
        val scale = toDiagonalLength / mDiagonalLength
        Log.d(
            TAG,
            "scaleHorizontal1: scale----------------------$scale-----------------}"
        )
        mMatrixBg.preScale(scale, 1f, mMidPointX, mMidPointY)
        mDiagonalLength = toDiagonalLength
        mMatrixBg.getValues(mValuesMatrix)
        invalidate()

        Log.d(
            TAG,
            "scaleHorizontal2: MSCALE_X ${mValuesMatrix[Matrix.MSCALE_X]} MSCALE_Y ${mValuesMatrix[Matrix.MSCALE_Y]}"
        )

    }

    /*move content*/
    private fun moveBitmap(rawX: Float, rawY: Float) {
        mMatrixBg.postTranslate(rawX - mTouchPointX, rawY - mTouchPointY)
        /*mTextMatrix.postTranslate(rawX - mTouchPointX, rawY - mTouchPointY)*/

        mTouchPointX = rawX
        mTouchPointY = rawY
        invalidate()
    }

    /*zoom content*/
    private fun zoom(rawX: Float, rawY: Float) {
        val toDiagonalLength = hypot((rawX) - mMidPointX, (rawY) - mMidPointY)
        val scale = toDiagonalLength / mDiagonalLength
        mMatrixBg.postScale(scale, scale, mMidPointX, mMidPointY)
        mDiagonalLength = toDiagonalLength
        mMatrixBg.getValues(mValuesMatrix)
        invalidate()
    }

    /*rotate content*/
    private fun rotate(rawX: Float, rawY: Float) {
        val toDegrees =
            Math.toDegrees(atan2((rawY - mMidPointY), (rawX - mMidPointX)).toDouble()).toFloat()
        val degrees = toDegrees - mLastDegrees
        mMatrixBg.postRotate(degrees, mMidPointX, mMidPointY)
        /*mTextMatrix.preRotate(degrees, mMidPointX, mMidPointY)*/

        mLastDegrees = toDegrees
        mMatrixBg.getValues(mValuesMatrix)
        invalidate()
    }


    /*Lấy 4 điểm bảo quanh bitmap bằng ma trận*/
    private fun getRectPointFBitmap(bitmap: Bitmap, matrix: Matrix): RectPointF {
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
        return RectPointF(
            pointTopLeft = PointF(topLeftX, topLeftY),
            pointTopRight = PointF(topRightX, topRightY),
            pointBottomLeft = PointF(bottomLeftX, bottomLeftY),
            pointBottomRight = PointF(bottomRightX, bottomRightY)
        )
    }


    /*Fill region by path*/
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

class RectPointF(
    var pointTopLeft: PointF,
    var pointTopRight: PointF,
    var pointBottomLeft: PointF,
    var pointBottomRight: PointF
)

class StickerTextInfo(
    var spacePercentTop: Float,
    var spacePercentBottom: Float,
    var spacePercentRight: Float,
    var spacePercentLeft: Float,
    var bitmap: Bitmap?
)