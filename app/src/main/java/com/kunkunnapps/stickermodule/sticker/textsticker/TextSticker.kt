package com.kunkunnapps.stickermodule.sticker.textsticker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix
import com.kunkunnapps.stickermodule.R
import com.kunkunnapps.stickermodule.sticker.textsticker.StickerUtils.calculateRotation
import com.kunkunnapps.stickermodule.sticker.textsticker.StickerUtils.fillBoundRegion
import com.kunkunnapps.stickermodule.sticker.textsticker.StickerUtils.getRectPointFBitmap
import com.kunkunnapps.stickermodule.sticker.textsticker.StickerUtils.getTextHeightLineTallest
import com.kunkunnapps.stickermodule.sticker.textsticker.StickerUtils.getTotalTextHeight
import com.kunkunnapps.stickermodule.view.DisplayUtils
import com.kunkunnapps.stickermodule.view.Utils
import com.kunkunnapps.stickermodule.view.getRealScaleX
import com.kunkunnapps.stickermodule.view.getRealScaleY
import java.lang.ref.WeakReference
import kotlin.math.max


class TextSticker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val TAG = "TextSticker"
    var path: Path? = null

    //region Bitmap background
    private val mMatrixBg = Matrix()
    private var mBitmapBackground: Bitmap? = null
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
    private var buttonRotate: StickerButtonController? = null
    //endregion

    //region Button zoom
    private var buttonZoom: StickerButtonController? = null
    //endregion

    //region Button delete
    private var buttonDelete: StickerButtonController? = null
    var onRemoveSticker: (() -> Unit)? = null
    //endregion

    //region Button scale vertical
    private var buttonScaleVertical: StickerButtonController? = null
    //endregion

    //region Button scale horizontal
    private var buttonScaleHorizontal: StickerButtonController? = null
    //endregion

    private var minTextSize = 10f

    private var mWidth = 0
    private var mHeight = 0
    private var defaultSpace = 0f
    private var defaultTextSize = 100f

    private var isNeedCreatedBg: Boolean = true
    private var mButtonRadius =
        DisplayUtils.dpToPx(context, 10f)
    private var DEFAULT_WIDTH = DisplayUtils.getWidthDisplay(context) / 2

    lateinit var stickerTextInfo: StickerTextInfo
    private var spaceMultiText = 0f
    private val mMatrixValues = FloatArray(9)
    private val rectTmp = Rect()
    private var isEnableHorizontalScale = true
    private var isEnableVerticalScale = true
    private var resizeTextHelper = ResizeTextHelper()
    private val rectMax = Rect()

    private var textShader: BitmapShader? = null
    private var buttonsController: ArrayList<StickerButtonController> = ArrayList()
    private val stickerTouchEvent: StickerTouchEvent = StickerTouchEvent().apply {
        this.deleteCallback = onRemoveSticker
    }

    constructor(
        context: Context,
        sticker: StickerTextInfo,
        width: Int,
        height: Int
    ) : this(context) {
        mWidth = width
        mHeight = height
        mDensity = 3f
        mDensity *= mDensity
        mTextPaint.apply {
            isAntiAlias = true
            color = Color.BLACK
            textSize = defaultTextSize
        }

        initButtonController()
        mDotBoundPaint.apply {
            color = Color.BLUE
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = mDotWidth
        }

        setStickerInfo(sticker)
    }

    private fun initButtonController() {
        val sizeButton = DisplayUtils.dpToPx(context, 20f).toInt()
        buttonZoom = StickerButtonController(
            size = sizeButton,
            position = PositionButton.BOTTOM_RIGHT,
            region = stickerTouchEvent.mRegionZoom,
            context = WeakReference(context),
            resIdDrawable = R.drawable.ic_zoom
        )

        buttonRotate = StickerButtonController(
            size = sizeButton,
            position = PositionButton.TOP_RIGHT,
            region = stickerTouchEvent.mRegionRotate,
            context = WeakReference(context),
            resIdDrawable = R.drawable.ic_rotate
        )

        buttonDelete = StickerButtonController(
            size = sizeButton,
            position = PositionButton.TOP_LEFT,
            region = stickerTouchEvent.mRegionDelete,
            context = WeakReference(context),
            resIdDrawable = R.drawable.ic_delete
        )

        buttonScaleHorizontal = StickerButtonController(
            size = sizeButton,
            position = PositionButton.CENTER_RIGHT,
            region = stickerTouchEvent.mRegionScaleHorizontal,
            context = WeakReference(context),
            resIdDrawable = R.drawable.ic_zoom,
            isDraw = isEnableHorizontalScale
        )

        buttonScaleVertical = StickerButtonController(
            size = sizeButton,
            position = PositionButton.CENTER_RIGHT,
            region = stickerTouchEvent.mRegionScaleHorizontal,
            context = WeakReference(context),
            resIdDrawable = R.drawable.ic_zoom,
            isDraw = isEnableVerticalScale
        )
        buttonsController.add(buttonZoom!!)
        buttonsController.add(buttonRotate!!)
        buttonsController.add(buttonDelete!!)
        buttonsController.add(buttonScaleHorizontal!!)
        buttonsController.add(buttonScaleVertical!!)
    }

    fun setEditEnable(isEnable: Boolean) {
        mInEdit = isEnable
    }

    fun setText(text: String) {
        reversTranslateMatrix {
            this.stickerTextInfo.text = text
            if (isNeedCreatedBg) {
                mBitmapBackground = createBitmapBackgroundByText(text, mTextPaint)
            }
        }
        invalidate()
    }

    private fun setStickerInfo(stickerInfo: StickerTextInfo) {
        this.stickerTextInfo = stickerInfo
        stickerInfo.bitmapTextShader?.let {
            if (!it.isRecycled) {
                textShader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                mTextPaint.shader = textShader
            }
        }

        if (stickerTextInfo.bitmap != null) {
            mBitmapBackground = stickerTextInfo.bitmap!!
            isNeedCreatedBg = false
            mMatrixBg.reset()
            if (mBitmapBackground!!.width > DEFAULT_WIDTH) {
                val scale = DEFAULT_WIDTH.toFloat() / mBitmapBackground!!.width
                mMatrixBg.postScale(scale, scale)
            }

        } else {
            isNeedCreatedBg = true
            mBitmapBackground = createBitmapBackgroundByText(stickerInfo.text, mTextPaint)
        }
        invalidate()
    }

    fun setBackgroundBitmap(bitmapBg: Bitmap?) {
        reversTranslateMatrix {
            stickerTextInfo.bitmap = bitmapBg
            if (bitmapBg != null) {
                val midPoint = getMidPoint(mBitmapBackground)
                mBitmapBackground = bitmapBg
                isNeedCreatedBg = false
                val scaleX = mMatrixBg.getRealScaleX()
                val scaleY = mMatrixBg.getRealScaleY()
                mMatrixBg.postScale(1 / scaleX, 1 / scaleY, midPoint.x, midPoint.y)
                val scale = DEFAULT_WIDTH.toFloat() / mBitmapBackground!!.width
                mMatrixBg.postScale(scale, scale, midPoint.x, midPoint.y)
            } else {
                isNeedCreatedBg = true
                mBitmapBackground = createBitmapBackgroundByText(stickerTextInfo.text, mTextPaint)
            }
        }
        invalidate()
    }

    private fun reversTranslateMatrix(doWork: () -> Unit) {
        val values = FloatArray(9)
        mMatrixBg.getValues(values)
        val oldPointX = values[Matrix.MTRANS_X]
        val oldPointY = values[Matrix.MTRANS_Y]
        doWork.invoke()
        mMatrixBg.getValues(values)
        val currentPointX = values[Matrix.MTRANS_X]
        val currentPointY = values[Matrix.MTRANS_Y]
        mMatrixBg.postTranslate(oldPointX - currentPointX, oldPointY - currentPointY)
    }

    private fun createBitmapBackgroundByText(text: String, textPaint: TextPaint): Bitmap {
        var heightReal = 0
        var widthReal = 0

        getWidthHeightBitmapText(text, textPaint) { width, height ->
            heightReal = max(height, 1)
            widthReal = max(width, 1)
        }
        val bitmap =
            Bitmap.createBitmap(
                widthReal,
                heightReal,
                Bitmap.Config.ARGB_8888
            )
        val scaleX = mMatrixBg.getRealScaleX()
        val scaleY = mMatrixBg.getRealScaleY()
        val midPoint = getMidPoint(mBitmapBackground)
        mMatrixBg.preScale(1 / scaleX, 1 / scaleY, midPoint.x, midPoint.y)
        val screenWidth = DEFAULT_WIDTH
        val scale = screenWidth.toFloat() / widthReal
        mMatrixBg.preScale(scale, scale, midPoint.x, midPoint.y)

        return bitmap
    }

    fun moveToCenter() {
        mBitmapBackground?.let { bitmap ->
            val rectPointFBoundBitmap = getRectPointFBitmap(bitmap, mMatrixBg)
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
            val pointCenterX = (mWidth - bgRealWith) / 2
            val pointCenterY = (mHeight - bgRealHeight) / 2
            val values = FloatArray(9)
            mMatrixBg.getValues(values)
            val currentPointX = values[Matrix.MTRANS_X]
            val currentPointY = values[Matrix.MTRANS_Y]
            mMatrixBg.postTranslate(pointCenterX - currentPointX, pointCenterY - currentPointY)

            invalidate()
        }
    }

    private fun getWidthHeightBitmapText(
        text: String,
        textPaint: TextPaint,
        onDone: (width: Int, height: Int) -> Unit = { _, _ -> }
    ) {
        val longestLineIndex = StickerUtils.getTextLengthLongest(text, textPaint)
        val rect = Rect()
        textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, rect)
        val width = rect.width()
        val height = getTotalTextHeight(text, textPaint)
        onDone.invoke(width, height)
    }

    private fun isDraw() = !(stickerTextInfo.text.isBlank() && stickerTextInfo.bitmap == null)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isDraw() && mBitmapBackground != null) {
            val rectPointFBoundBitmap =
                getRectPointFBitmap(bitmap = mBitmapBackground!!, matrix = mMatrixBg)
            /*draw background*/
            canvas.drawBitmap(mBitmapBackground!!, mMatrixBg, null)

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
                resizeTextHelper.autoSizeTextPaint(
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
                val textHeight = getTextHeightLineTallest(stickerTextInfo.text, mTextPaint, rectMax)
                val isOneLine = texts.count() == 1
                texts.forEachIndexed { index, text ->
                    mTextPaint.getTextBounds(text, 0, text.length, rectTmp)
                    val spaceX: Float = when (stickerTextInfo.textAlign) {
                        TextAlign.CENTER -> {
                            val subWidth = bgRealWith - spaceStart - spaceEnd
                            spaceStart + (subWidth - rectTmp.width()) / 2
                        }

                        TextAlign.RIGHT -> {
                            (bgRealWith - rectTmp.width()) - spaceEnd
                        }


                        TextAlign.LEFT -> {
                            spaceStart
                        }
                    }


                    if (isOneLine) {
                        offsetY += spaceMultiText + textHeight - rectMax.bottom
                    } else {
                        offsetY += (textHeight + spaceMultiText)
                    }

                    Log.d(
                        TAG,
                        "onDraw: spaceYFirst $spaceYFirst mButtonRadius $mButtonRadius textHeight $textHeight totalHeight $textTotalHeight  boundHeight ${rectTmp.height()} offsetY $offsetY"
                    )

                    this.drawText(
                        text,
                        spaceX,
                        offsetY,
                        mTextPaint
                    )

                }
                Log.d(
                    TAG,
                    "onDraw: spaceStart $spaceStart spaceTop $spaceTop spaceEnd $spaceEnd spaceBottom $spaceBottom spaceYFirst $spaceYFirst "
                )

                Log.d(
                    TAG,
                    "onDraw: textTotalHeight $textTotalHeight textHeight $textHeight bgRealHeight $bgRealHeight "
                )

            }
            //endregion


            canvas.restore()

            canvas.save()
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
            if (mInEdit) {
                canvas.drawPath(pathBound, mDotBoundPaint.apply {
                    color = Color.BLUE
                })
                buttonsController.forEach {
                    it.drawButton(rectPointFBoundBitmap, getRotate(), canvas)
                }
            } else {
                canvas.drawPath(pathBound, mDotBoundPaint.apply {
                    color = Color.TRANSPARENT
                })
            }
            fillBoundRegion(mContentRegion, pathBound)
            canvas.restore()
        }
    }

    private fun getRotate(): Float {
        val rectPointFBoundBitmap =
            getRectPointFBitmap(bitmap = mBitmapBackground!!, matrix = mMatrixBg)

        val rotate = calculateRotation(
            rectPointFBoundBitmap.pointBottomRight.x,
            rectPointFBoundBitmap.pointBottomRight.y,
            rectPointFBoundBitmap.pointBottomLeft.x,
            rectPointFBoundBitmap.pointBottomLeft.y
        )
        Log.d(TAG, "getRotate: $rotate")
        return rotate
    }

    //region calculate text space with background
    private fun getSpaceStart(): Float {
        val bitmap = stickerTextInfo.bitmap
        return if (bitmap == null) {
            defaultSpace * mMatrixBg.getRealScaleX()
        } else {
            stickerTextInfo.spacePercentLeft * bitmap.width * mMatrixBg.getRealScaleX()
        }
    }

    private fun getSpaceEnd(): Float {
        val bitmap = stickerTextInfo.bitmap
        return if (bitmap == null) {
            defaultSpace * mMatrixBg.getRealScaleX()
        } else {
            stickerTextInfo.spacePercentRight * bitmap.width * mMatrixBg.getRealScaleX()
        }
    }

    private fun getSpaceTop(): Float {
        val bitmap = stickerTextInfo.bitmap
        return if (bitmap == null) {
            defaultSpace * mMatrixBg.getRealScaleY()
        } else {
            stickerTextInfo.spacePercentTop * bitmap.height * mMatrixBg.getRealScaleY()
        }
    }
    //endregion

    private fun getSpaceBottom(): Float {
        val bitmap = stickerTextInfo.bitmap
        return if (bitmap == null) {
            defaultSpace * mMatrixBg.getRealScaleY()
        } else {
            stickerTextInfo.spacePercentBottom * bitmap.height * mMatrixBg.getRealScaleY()
        }
    }


    enum class PositionButton {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        CENTER_RIGHT,
        CENTER_BOTTOM
    }

    private fun getMidPoint(bitmapBg: Bitmap?): PointF {
        return if (bitmapBg == null) {
            PointF(0f, 0f)
        } else {
            val rectFBoundImagePoint = getRectPointFBitmap(bitmapBg, mMatrixBg)
            val midPointX =
                (rectFBoundImagePoint.pointTopLeft.x + rectFBoundImagePoint.pointBottomRight.x) / 2
            val midPointY =
                (rectFBoundImagePoint.pointTopLeft.y + rectFBoundImagePoint.pointBottomRight.y) / 2
            PointF(midPointX, midPointY)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mBitmapBackground == null) return false

        return stickerTouchEvent.onTouch(
            editable = mInEdit,
            view = this,
            event = event,
            bitmap = mBitmapBackground!!,
            matrix = mMatrixBg,
            contentRegion = mContentRegion,
            onContentTouch = {
                mInEdit = true
                invalidate()
            }
        )
    }


    fun setTextColor(textColor: Int) {
        stickerTextInfo.textColor = textColor
        mTextPaint.color = textColor
        invalidate()
    }

    fun setTextColorAlpha(textColorAlpha: Int) {
        stickerTextInfo.textColorAlpha = textColorAlpha
        mTextPaint.alpha = textColorAlpha
        invalidate()
    }

    fun setTextShadowColor(shadowColor: Int) {
        stickerTextInfo.textShadowColorOrigin = shadowColor

        mTextPaint.setShadowLayer(
            stickerTextInfo.textShadowWeight,
            10f,
            10f,
            stickerTextInfo.getShadowColorMergeAlpha()
        )
        invalidate()
    }

    fun setTextShadowWeight(shadowWeight: Float) {
        stickerTextInfo.textShadowWeight = shadowWeight
        mTextPaint.setShadowLayer(
            stickerTextInfo.textShadowWeight,
            10f,
            10f,
            stickerTextInfo.getShadowColorMergeAlpha()
        )
        invalidate()
    }

    fun setTextShadowAlpha(shadowAlpha: Float) {
        stickerTextInfo.textShadowAlpha = shadowAlpha

        mTextPaint.setShadowLayer(
            stickerTextInfo.textShadowWeight,
            5f,
            10f,
            stickerTextInfo.getShadowColorMergeAlpha()
        )
        invalidate()
    }

    fun setShader(bitmapShader: Bitmap?) {
        stickerTextInfo.bitmapTextShader = bitmapShader
        bitmapShader?.takeIf { !it.isRecycled }?.let {
            textShader = BitmapShader(it, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR)
            mTextPaint.shader = textShader
            invalidate()
        } ?: run {
            textShader = null
            mTextPaint.shader = null
            invalidate()
        }
    }

    fun setShaderAlpha(shaderAlpha: Int) {
        if (stickerTextInfo.bitmapTextShader != null) {
            mTextPaint.alpha = shaderAlpha
            invalidate()
        }
    }

    fun setFont(typeface: Typeface) {
        mTextPaint.typeface = typeface
        invalidate()
    }

    /*fun applyBackup() {
        backupInfo?.let { backupInfo ->
            stickerTextInfo = backupInfo.stickerInfo
            if (mBitmapBackground?.isRecycled == false) {
                mBitmapBackground?.recycle()
            }
            mBitmapBackground = backupInfo.bitmapBg
            mMatrixBg.set(backupInfo.matrix)
            invalidate()
        }
        backupInfo = null
    }

    fun backupInfo() {
        if (backupInfo?.bitmapBg?.isRecycled == false) {
            backupInfo?.bitmapBg?.recycle()
        }

        if (backupInfo?.stickerInfo?.bitmapTextShader?.isRecycled == false) {
            backupInfo?.stickerInfo?.bitmapTextShader?.recycle()
        }

        val bitmapBg = mBitmapBackground!!.copy(Bitmap.Config.ARGB_8888, true)
        backupInfo = BackupInfo(
            bitmapBg = bitmapBg,
            matrix = Matrix(mMatrixBg),
            stickerInfo = stickerTextInfo.copy(
                bitmap = if (stickerTextInfo.bitmap != null) bitmapBg else null,
                bitmapTextShader = if (stickerTextInfo.bitmapTextShader != null) stickerTextInfo.bitmapTextShader!!.copy(
                    Bitmap.Config.ARGB_8888,
                    true
                ) else null
            )
        )
    }

    private var backupInfo: BackupInfo? = null

    class BackupInfo(
        var stickerInfo: StickerTextInfo,
        var matrix: Matrix,
        var bitmapBg: Bitmap
    )*/
}