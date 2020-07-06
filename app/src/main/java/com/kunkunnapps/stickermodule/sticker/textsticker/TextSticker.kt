package com.kunkunnapps.stickermodule.sticker.textsticker

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix
import com.kunkunnapps.stickermodule.R
import com.kunkunnapps.stickermodule.sticker.textsticker.StickerUtils.fillBoundRegion
import com.kunkunnapps.stickermodule.sticker.textsticker.StickerUtils.getRectPointFBitmap
import com.kunkunnapps.stickermodule.sticker.textsticker.StickerUtils.getTextHeight
import com.kunkunnapps.stickermodule.sticker.textsticker.StickerUtils.getTotalTextHeight
import com.kunkunnapps.stickermodule.view.DisplayUtils
import com.kunkunnapps.stickermodule.view.Utils
import com.kunkunnapps.stickermodule.view.getRealScaleX
import com.kunkunnapps.stickermodule.view.getRealScaleY
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.roundToInt

class TextSticker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val TAG = "TextSticker"

    //region Bitmap background
    private val mMatrixBg = Matrix()
    private lateinit var mBitmapBackground: Bitmap
    //endregion

    //region Text Info
    private var mTextPaint = TextPaint()
    private val mContentRegion = Region()
    private var mInEdit = false
    //endregion

    //region Paint draw path bound
    private val mDotBoundPaint = Paint()
    private var pathBound = Path()
    private var mDotWidth = 1f
    private var mDensity = 1f
    //endregion

    //region Button rotate
    private val mRegionRotate = Region()
    private val mRotateBitmap = BitmapFactory.decodeResource(
        resources,
        R.drawable.ic_rotate
    )
    //endregion

    //region Button zoom
    private val mZoomBitmap = BitmapFactory.decodeResource(
        resources,
        R.drawable.ic_zoom
    )
    private val mRegionZoom = Region()
    //endregion

    //region Button delete
    private val mRegionDelete = Region()
    private val mDeleteBitmap = BitmapFactory.decodeResource(
        resources,
        R.drawable.ic_delete
    )
    var deleteCallback: (() -> Unit)? = null
    //endregion

    //region Button scale horizontal
    private val mRegionScaleHorizontal = Region()
    private val mScaleHorizontalBitmap = BitmapFactory.decodeResource(
        resources,
        R.drawable.ic_zoom
    )
    //endregion

    private var isNeedCreatedBg: Boolean = true
    private var mButtonRadius =
        DisplayUtils.dpToPx(context, 8f)
    private var DEFAULT_WIDTH = 250

    private lateinit var stickerTextInfo: StickerTextInfo
    private var spaceMultiText = 0f
    private val mMatrixValues = FloatArray(9)
    private val rectTmp = Rect()
    private var isEnableHorizontalScale = true
    private var defaultTextSize = 30f
    constructor(context: Context, sticker: StickerTextInfo) : this(context) {
        mDensity = 3f
        mDensity *= mDensity
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
        setStickerInfo(sticker)
    }

    fun setEditEnable(isEnable: Boolean) {
        mInEdit = isEnable
    }

    fun setText(text: String) {
        this.stickerTextInfo.text = text
        if (isNeedCreatedBg) {
            mBitmapBackground = createBitmapBackgroundByText(text, mTextPaint)
        }
        invalidate()
    }

    fun setStickerInfo(stickerInfo: StickerTextInfo) {
        this.stickerTextInfo = stickerInfo
        if (stickerTextInfo.bitmap != null) {
            mBitmapBackground = stickerTextInfo.bitmap!!
            isNeedCreatedBg = false
            if (mBitmapBackground.width > DEFAULT_WIDTH) {
                val scale = DEFAULT_WIDTH.toFloat() / mBitmapBackground.width
                mMatrixBg.postScale(scale, scale)
            }

        } else {
            isNeedCreatedBg = true
            mBitmapBackground = createBitmapBackgroundByText(stickerInfo.text, mTextPaint)
        }
        invalidate()
    }

    private fun createBitmapBackgroundByText(text: String, textPaint: TextPaint): Bitmap {
        var heightReal = 0
        autoSizeTextPaint(
            text = text,
            width = DEFAULT_WIDTH,
            height = Int.MAX_VALUE,
            textPaint = textPaint,
            spaceMultiText = spaceMultiText
        ) { realHeight, realWidth ->
            heightReal = realHeight
        }
        val bitmap =
            Bitmap.createBitmap(
                DEFAULT_WIDTH,
                heightReal,
                Bitmap.Config.ARGB_8888
            )
        return bitmap
    }

    private fun autoResizeBitmapByText(bitmap: Bitmap, text: String, maxWidth: Int, textPaint: TextPaint) {
        val isOK = false
        mMatrixBg.reset()
        isNeedCreatedBg = false
        if (bitmap.width > DEFAULT_WIDTH) {
            val scale = DEFAULT_WIDTH.toFloat() / bitmap.width
            mMatrixBg.postScale(scale, scale)
        }
        textPaint.textSize = defaultTextSize
        while (!isOK) {
            autoSizeTextPaint(text, width = DisplayUtils.getWidthDisplay(context), height = (bitmap.height * mMatrixBg.getRealScaleY()).toInt(), spaceMultiText = spaceMultiText, textPaint = textPaint) { realHeight, realWidth ->
                mMatrixBg.reset()
                val scaleX = realWidth / bitmap.width
                val scaleY = realHeight/ bitmap.height
                if (scaleY > scaleX) {

                }
            }
        }

    }

    private fun autoSizeTextPaint(
        text: String,
        width: Int,
        height: Int,
        spaceMultiText: Float,
        textPaint: TextPaint,
        onDone: (realHeight: Int, realWidth: Int) -> Unit = { _, _ ->}
    ) {
        val longestLineIndex = StickerUtils.getTextLengthLongest(text)
        val rect = Rect()
        var isOk = false
        var textSize = textPaint.textSize
        textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, rect)
        //region calculate width max
        if (rect.width() < width) {
            while (!isOk) {
                if (rect.width() < width) {
                    textSize += 0.5f
                    textPaint.textSize = textSize
                    textPaint.getTextBounds(
                        text,
                        longestLineIndex.first,
                        longestLineIndex.second,
                        rect
                    )
                } else {
                    isOk = true
                }
            }
        }
        //endregion

        //region calculate width fit
        isOk = false
        if (rect.width() > width) {
            while (!isOk) {
                if (rect.width() > width) {
                    textSize -= 0.5f
                    textPaint.textSize = textSize
                    textPaint.getTextBounds(
                        text,
                        longestLineIndex.first,
                        longestLineIndex.second,
                        rect
                    )
                } else {
                    isOk = true
                }
            }
        }
        //endregion

        //region Calculate height fit
        isOk = false
        val texts = text.split("\n")
        var totalHeight = 0f

        while (!isOk) {
            totalHeight = 0f
            var index = 0
            texts.forEach { subText ->
                val textHeight = getTextHeight(textPaint)
                totalHeight += textHeight
                if (index > 0) {
                    totalHeight += spaceMultiText
                }
                index++
            }
            isOk = totalHeight <= height
            if (!isOk) {
                textSize -= 0.1f
                textPaint.textSize = textSize
            }
        }
        textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, rect)
        val totalWidth = rect.width().toFloat()
        //endregion
        onDone.invoke(totalHeight.toInt(), totalWidth.toInt())
    }

    private fun isDraw() = stickerTextInfo.text.isNotBlank()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isDraw()) {
            val rectPointFBoundBitmap =
                getRectPointFBitmap(bitmap = mBitmapBackground, matrix = mMatrixBg)
            /*draw background*/
            canvas.drawBitmap(mBitmapBackground, mMatrixBg, null)

            canvas.save()
            //region Draw text center background
            mMatrixBg.getValues(mMatrixValues)
            val bgRealWith =
                Utils.getDistanceTwoPoint(
                    rectPointFBoundBitmap.pointTopLeft,
                    rectPointFBoundBitmap.pointTopRight
                )
            val bgRealHeight =
                Utils.getDistanceTwoPoint(
                    rectPointFBoundBitmap.pointTopRight,
                    rectPointFBoundBitmap.pointBottomRight
                )
            val spaceStart = getSpaceStart()
            val spaceTop = getSpaceTop()
            val spaceEnd = getSpaceEnd()
            val spaceBottom = getSpaceBottom()
            mTextPaint.textAlign = Paint.Align.LEFT
            canvas.withMatrix(matrix = mMatrixBg) {

                val scaleX = mMatrixBg.getRealScaleX()
                val scaleY = mMatrixBg.getRealScaleY()
                this.scale(1f / scaleX, 1f / scaleY)

                var textTotalHeight = 0f
                autoSizeTextPaint(
                    stickerTextInfo.text,
                    width = (bgRealWith - getSpaceStart() - getSpaceEnd()).toInt(),
                    textPaint = mTextPaint,
                    height = (bgRealHeight - getSpaceTop() - getSpaceBottom()).toInt(),
                    onDone = { _textHeight, _textWidth ->
                        textTotalHeight = _textHeight.toFloat()
                    },
                    spaceMultiText = spaceMultiText
                )
                val texts = stickerTextInfo.text.split("\n")
                var spaceYFirst = spaceTop

                if (textTotalHeight < (bgRealHeight - spaceBottom - spaceTop)) {
                    spaceYFirst += (bgRealHeight - spaceBottom - spaceTop - textTotalHeight) / 2
                }

                var offsetY = spaceYFirst
                texts.forEachIndexed { index, text ->
                    mTextPaint.getTextBounds(text, 0, text.length, rectTmp)
                    val spaceX: Float
                    when (stickerTextInfo.textAlign) {
                        TextAlign.CENTER -> {
                            spaceX = (bgRealWith - rectTmp.width()) / 2
                        }

                        TextAlign.RIGHT -> {
                            spaceX = (bgRealWith - rectTmp.width()) - spaceEnd
                        }


                        TextAlign.LEFT -> {
                            spaceX = spaceStart
                        }
                    }

                    val textHeight = getTextHeight(mTextPaint)


                    offsetY += (textHeight + spaceMultiText)

                    Log.d(
                        TAG,
                        "onDraw: spaceYFirst $spaceYFirst mButtonRadius $mButtonRadius realHeight $bgRealWith totalHeight $bgRealHeight offsetY$offsetY height ${rectTmp.height()}"
                    )

                    this.drawText(
                        text,
                        spaceX,
                        offsetY,
                        mTextPaint
                    )
                }
            }
            //endregion

            canvas.restore()

            canvas.save()
            if (mInEdit) {
                pathBound.reset()
            }

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
            canvas.drawPath(pathBound, mDotBoundPaint)
            fillBoundRegion(mContentRegion, pathBound)
            canvas.restore()

            fillBoundRegion(mContentRegion, pathBound)
            /*draw button delete*/
            drawToolButton(
                canvas = canvas,
                offsetX = rectPointFBoundBitmap.pointTopLeft.x,
                offsetY = rectPointFBoundBitmap.pointTopLeft.y,
                radius = mButtonRadius,
                bitmap = mDeleteBitmap,
                region = mRegionDelete
            )

            /*draw button rotate*/
            drawToolButton(
                canvas = canvas,
                offsetX = rectPointFBoundBitmap.pointTopRight.x,
                offsetY = rectPointFBoundBitmap.pointTopRight.y,
                radius = mButtonRadius,
                bitmap = mRotateBitmap,
                region = mRegionRotate
            )

            /*draw button zoom*/
            drawToolButton(
                canvas = canvas,
                offsetX = rectPointFBoundBitmap.pointBottomRight.x,
                offsetY = rectPointFBoundBitmap.pointBottomRight.y,
                radius = mButtonRadius,
                bitmap = mZoomBitmap,
                region = mRegionZoom
            )

            if (isEnableHorizontalScale) {
                /*draw button zoom horizontal*/
                drawToolButton(
                    canvas = canvas,
                    offsetX = (rectPointFBoundBitmap.pointBottomRight.x + rectPointFBoundBitmap.pointTopRight.x) / 2,
                    offsetY = (rectPointFBoundBitmap.pointBottomRight.y + rectPointFBoundBitmap.pointTopRight.y) / 2,
                    radius = mButtonRadius,
                    bitmap = mScaleHorizontalBitmap,
                    region = mRegionScaleHorizontal
                )
            }
        }
    }

    //region calculate text space with background
    fun getSpaceStart(): Float {
        val bitmap = stickerTextInfo.bitmap
        return if (bitmap == null) {
            0f
        } else {
            stickerTextInfo.spacePercentLeft * bitmap.width * mMatrixBg.getRealScaleX()
        }
    }

    fun getSpaceEnd(): Float {
        val bitmap = stickerTextInfo.bitmap
        return if (bitmap == null) {
            0f
        } else {
            stickerTextInfo.spacePercentRight * bitmap.width * mMatrixBg.getRealScaleX()
        }
    }

    fun getSpaceTop(): Float {
        val bitmap = stickerTextInfo.bitmap
        return if (bitmap == null) {
            0f
        } else {
            stickerTextInfo.spacePercentTop * bitmap.height * mMatrixBg.getRealScaleY()
        }
    }
    //endregion

    fun getSpaceBottom(): Float {
        val bitmap = stickerTextInfo.bitmap
        return if (bitmap == null) {
            0f
        } else {
            stickerTextInfo.spacePercentBottom * bitmap.height * mMatrixBg.getRealScaleY()
        }
    }

    private fun drawToolButton(
        canvas: Canvas?,
        offsetX: Float,
        offsetY: Float,
        radius: Float,
        region: Region,
        bitmap: Bitmap
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
        canvas?.drawBitmap(bitmap, null, rectF, null)
    }

    //region Touch Event
    enum class TOUCH_MODE {
        ROTATE,
        ZOOM,
        MOVE,
        SCALE_HORIZONTAL,
        NONE
    }

    private var mTouchPointX = 0f
    private var mTouchPointY = 0f
    private var touchMode =
        TOUCH_MODE.NONE
    var mDiagonalLength = 0f
    var mMidPointX = 0f
    var mMidPointY = 0f
    var mLastDegrees = 0f
    //endregion

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mInEdit) return false
        val rectFBoundImagePoint = getRectPointFBitmap(mBitmapBackground, mMatrixBg)
        mMidPointX =
            (rectFBoundImagePoint.pointTopLeft.x + rectFBoundImagePoint.pointBottomRight.x) / 2
        mMidPointY =
            (rectFBoundImagePoint.pointTopLeft.y + rectFBoundImagePoint.pointBottomRight.y) / 2

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                mTouchPointX = event.rawX
                mTouchPointY = event.rawY
                when {
                    mRegionRotate.contains(event.x.toInt(), event.y.toInt()) -> {
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

                    mRegionZoom.contains(event.x.toInt(), event.y.toInt()) -> {
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

                    mRegionScaleHorizontal.contains(event.x.toInt(), event.y.toInt()) -> {
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

    private fun rotate(rawX: Float, rawY: Float) {
        val toDegrees =
            Math.toDegrees(atan2((rawY - mMidPointY), (rawX - mMidPointX)).toDouble()).toFloat()
        val degrees = toDegrees - mLastDegrees
        mMatrixBg.postRotate(degrees, mMidPointX, mMidPointY)

        mLastDegrees = toDegrees
        invalidate()
    }

    private fun zoom(rawX: Float, rawY: Float) {
        val toDiagonalLength = hypot((rawX) - mMidPointX, (rawY) - mMidPointY)
        val scale = toDiagonalLength / mDiagonalLength
        mMatrixBg.postScale(scale, scale, mMidPointX, mMidPointY)
        mDiagonalLength = toDiagonalLength
        invalidate()
    }

    private fun moveBitmap(rawX: Float, rawY: Float) {
        mMatrixBg.postTranslate(rawX - mTouchPointX, rawY - mTouchPointY)
        mTouchPointX = rawX
        mTouchPointY = rawY
        invalidate()
    }

    private fun scaleHorizontal(rawX: Float, rawY: Float) {
        val toDiagonalLength = hypot((rawX) - mMidPointX, (rawY) - mMidPointY)
        val scale = toDiagonalLength / mDiagonalLength
        mMatrixBg.preScale(scale, 1f, mMidPointX, mMidPointY)
        mDiagonalLength = toDiagonalLength
        invalidate()
    }

}