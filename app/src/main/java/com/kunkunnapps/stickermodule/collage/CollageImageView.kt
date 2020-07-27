/*
package com.photoframe.cutout.collage.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.dinuscxj.gesture.MultiTouchGestureDetector
import com.photoframe.cutout.collage.model.Point
import com.photoframe.cutout.collage.model.Size
import com.photoframe.cutout.collage.model.TemplatePart
import com.photoframe.cutout.util.BitmapUtils

class CollageImageView : AppCompatImageView {
    private lateinit var mTemplatePart: TemplatePart
    private var mLayoutSize = 0f
    private var mSpace = 20f
    private var mCorner = 0f
    private var mClipPath = Path()
    private var hasMask = false
    private var hasStroke = false
    private var mStrokePath: Path? = null
    private val mStrokePaint = Paint()
    private val mRectF = RectF()
    private val mRegion = Region()
    private val mBoundPaint = Paint()
    val orangePaint = Paint()

    private val mFloatArray = FloatArray(9)


    var isSelect: Boolean = false
    var changePositionState = false
    private val mMultiTouchGestureDetector: MultiTouchGestureDetector

    private var mImageBitmap: Bitmap? = null

    private var mCloneBitmap: Bitmap? = null
    private var mResultBitmap: Bitmap? = null
    private val mXFerDstInMode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

    private lateinit var mBitmapShader: BitmapShader
    private var mCenterX = 0f
    private var mCenterY = 0f

    private var mRotate = 0f

    private var mScaleFactor = 1f
    private var mOffsetX = 0f
    private var mOffsetY = 0f

    private var mImageMatrix = Matrix()
    private var mMinScale = 0.5f

    private val mPaint = Paint()
    private var mTempCanvas = Canvas()

    private var mMask: Bitmap? = null
    private var mPathEffect: CornerPathEffect

    init {
        mMultiTouchGestureDetector =
            MultiTouchGestureDetector(context, MultiTouchGestureDetectorListener())
        mBoundPaint.color = Color.parseColor("#f26522")
        mBoundPaint.style = Paint.Style.FILL_AND_STROKE
        mBoundPaint.strokeWidth = 6f

        orangePaint.color = Color.parseColor("#f26522")
        orangePaint.style = Paint.Style.STROKE
        orangePaint.strokeWidth = 6f
        scaleType = ScaleType.CENTER_CROP
        mPathEffect = CornerPathEffect(mCorner)
    }

    fun init(bitmap: Bitmap?, templatePart: TemplatePart, parentSize: Int) {
        mTemplatePart = templatePart
        mImageBitmap = bitmap
        mLayoutSize = parentSize.toFloat()
        setImageBitmap(bitmap)
        initSize()
        drawClipPath()
        initMask()
        resetShader()
        invalidate()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private fun initSize() {
        x = mTemplatePart.position.convertToTargetSize(mLayoutSize.toInt()).x
        y = mTemplatePart.position.convertToTargetSize(mLayoutSize.toInt()).y
        val params = FrameLayout.LayoutParams(
            mTemplatePart.mInnerSize.convertToTargetSize(mLayoutSize).width.toInt(),
            mTemplatePart.mInnerSize.convertToTargetSize(mLayoutSize).height.toInt()
        )
        layoutParams = params
        requestLayout()
    }

    private fun drawClipPath() {
        mClipPath = getPath(mTemplatePart.mListPoint)
        val matrix = Matrix()
        val innerSizeW = mTemplatePart.mInnerSize.convertToTargetSize(mLayoutSize).width
        val innerSizeH = mTemplatePart.mInnerSize.convertToTargetSize(mLayoutSize).height
        val scaleX = (innerSizeW - mSpace) / innerSizeW
        val scaleY = (innerSizeH - mSpace) / innerSizeH
        matrix.setScale(
            scaleX,
            scaleY,
            mTemplatePart.mInnerSize.convertToTargetSize(mLayoutSize).width / 2f,
            mTemplatePart.mInnerSize.convertToTargetSize(mLayoutSize).height / 2f
        )
        if (mTemplatePart.strokeWidth > 1) {
            hasStroke = true
            mStrokePath = getPath(mTemplatePart.mListPoint)
            mStrokePath?.transform(matrix)
            mStrokePaint.color = Color.parseColor("#ffffff")
            mStrokePaint.style = Paint.Style.STROKE
            //mStrokePaint.strokeWidth = mTemplatePart.strokeWidth.toFloat()
            mStrokePaint.strokeWidth = 10f
        } else {
            hasStroke = false
            mStrokePath = Path()
        }
        mClipPath.transform(matrix)
        mClipPath.computeBounds(mRectF, true)
        mRegion.setPath(
            mClipPath, Region(
                mRectF.left.toInt(),
                mRectF.top.toInt(),
                mRectF.right.toInt(),
                mRectF.bottom.toInt()
            )
        )
    }

    private fun initMask() {
        if (mTemplatePart.hasMask) {
            hasMask = true
            mMask = BitmapUtils.getBitmapFromAsset(mTemplatePart.mMask.assetPath, context)
            mMask = BitmapUtils.resizeBitmap(
                mMask!!,
                mTemplatePart.mInnerSize.convertToTargetSize(mLayoutSize).width.toInt(),
                mTemplatePart.mInnerSize.convertToTargetSize(mLayoutSize).height.toInt()
            )

            mCloneBitmap = Bitmap.createBitmap(
                mMask!!.width,
                mMask!!.height,
                Bitmap.Config.ARGB_8888
            )

            mResultBitmap = mCloneBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
            mTempCanvas = Canvas(mResultBitmap!!)

            if (hasMask) {
                mResultBitmap = mCloneBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
                mTempCanvas.setBitmap(mResultBitmap)
                mTempCanvas.drawBitmap(mImageBitmap!!, imageMatrix, null)
                mPaint.xfermode = mXFerDstInMode
                mTempCanvas.drawBitmap(mMask!!, 0f, 0f, mPaint)
                mPaint.xfermode = null
                setImageBitmap(mResultBitmap)
            }
        } else {
            //mImageBitmap = mOriginalBitmap
            hasMask = false
        }
    }

    fun resetShader() {
        mBitmapShader = BitmapShader(mImageBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mCenterX = width / 2f
        mCenterY = width / 2f
        mScaleFactor = 1f
        mImageMatrix.reset()
        val size = Size(
            mTemplatePart.mInnerSize.convertToTargetSize(mLayoutSize).width,
            mTemplatePart.mInnerSize.convertToTargetSize(mLayoutSize).height
        )
        val scale =
            (size.width / mImageBitmap!!.width).coerceAtLeast(size.height / mImageBitmap!!.height)
        mImageMatrix.postScale(scale, scale)
        mScaleFactor = scale
        mOffsetX = (size.width - (mImageBitmap!!.width * scale)) / 2f
        mOffsetY = (size.height - (mImageBitmap!!.height * scale)) / 2f
        mImageMatrix.postTranslate(mOffsetX, mOffsetY)
        mBitmapShader.setLocalMatrix(mImageMatrix)
    }

    private fun getPath(points: ArrayList<Point>): Path {

        val path = Path()
        val minX = points.minBy { it.x }?.x ?: 0f
        val minY = points.minBy { it.y }?.y ?: 0f
        path.apply {
            reset()
            moveTo(
                points[0].convertToTargetSize(mLayoutSize.toInt(), minX, minY).x,
                points[0].convertToTargetSize(mLayoutSize.toInt(), minX, minY).y
            )
            for (index in 1 until points.size) {
                lineTo(
                    points[index].convertToTargetSize(mLayoutSize.toInt(), minX, minY).x,
                    points[index].convertToTargetSize(mLayoutSize.toInt(), minX, minY).y
                )
            }
            close()
        }
        return path
    }

    override fun onDraw(canvas: Canvas?) {

        if (mImageBitmap == null) return
        canvas?.rotate(mRotate, width / 2f, height / 2f)

        if (hasMask) {
//            canvas?.clipPath(mClipPath)
            mResultBitmap = mCloneBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
            mTempCanvas.setBitmap(mResultBitmap)
            mTempCanvas.drawBitmap(mImageBitmap!!, mImageMatrix, null)
            mPaint.xfermode = mXFerDstInMode
            mTempCanvas.drawBitmap(mMask!!, 0f, 0f, mPaint)
            mPaint.xfermode = null
            canvas?.drawBitmap(mResultBitmap!!, 0f, 0f, mPaint)

        } else {
            canvas?.drawPath(mClipPath, mPaint.apply {
                isAntiAlias = true
                pathEffect = mPathEffect
                shader = mBitmapShader
                xfermode = null
            })
        }

        if (hasStroke) {
            canvas?.drawPath(mStrokePath!!, mStrokePaint.apply {
                style = Paint.Style.STROKE
                pathEffect = mPathEffect
            })
        }
        if (isSelect) {
            canvas?.drawPath(mClipPath, mBoundPaint.apply {
                pathEffect = mPathEffect
                style = Paint.Style.STROKE
            })
        }

//        if (changePositionState) {
//            canvas?.drawPath(mClipPath, orangePaint.apply {
//                style = Paint.Style.STROKE
//                pathEffect = mPathEffect
//            })
//        }
    }

    fun setSelectImage(isSelected: Boolean) {
        if (isSelected == isSelect)
            return
        isSelect = isSelected
        invalidate()
    }

    fun touch(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        val point = Point(event!!.x, event.y)
        if (hasMask && mMask != null) {
            try {
                var shapePointX = (event.x / (width.toFloat() / mMask!!.width.toFloat())).toInt()
                var shapePointY = (event.y / (height.toFloat() / mMask!!.height.toFloat())).toInt()
                if (shapePointX < 0) {
                    shapePointX = 0
                }
                if (shapePointY < 0) {
                    shapePointY = 0
                }
                if (mMask?.getPixel(shapePointX, shapePointY) == 0) {
                    return false
                }
            } catch (e: Exception) {
                println("error:" + e.message)
            }
            mMultiTouchGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_DOWN) {
//                onClick.onClick(index)
            }

            return true
        } else {
            if (mRegion.contains(point.x.toInt(), point.y.toInt())) {
                mMultiTouchGestureDetector.onTouchEvent(event)
                if (event.action == MotionEvent.ACTION_DOWN) {
//                    onClick.onClick(index)
                }

                return true
            }

        }
        return false
    }

    inner class MultiTouchGestureDetectorListener :
        MultiTouchGestureDetector.SimpleOnMultiTouchGestureListener() {

        override fun onScale(detector: MultiTouchGestureDetector?) {
            if (detector == null) return

            val currentScale = getMatrixScaleX()
            val detectorScale = detector.scale

            val localPointF = PointF()
            midDiagonalPoint(localPointF)

            if (currentScale * detectorScale <= mMinScale) {
                val scale = mMinScale / currentScale
                mImageMatrix.postScale(scale, scale, localPointF.x, localPointF.y)
            } else {
                mImageMatrix.postScale(detectorScale, detectorScale, localPointF.x, localPointF.y)
            }
            mBitmapShader.setLocalMatrix(mImageMatrix)
            invalidate()
        }

        override fun onRotate(detector: MultiTouchGestureDetector?) {
//            mRotation += detector!!.rotation
//            mImageMatrix.postRotate(detector!!.rotation, mCenterX, mCenterY)
//            mBitmapShader.setLocalMatrix(mImageMatrix)
//            invalidate()
        }

        override fun onMove(detector: MultiTouchGestureDetector?) {

            if (detector == null) return

            val currentTransX = getMatrixTranslateX()
            val currentTransY = getMatrixTranslateY()
            val moveX = detector.moveX
            val moveY = detector.moveY
            var distanceX: Float
            var distanceY: Float

            if (currentTransX + moveX >= 0f) {
                distanceX = (0 - currentTransX)
            } else if (currentTransX + moveX <= width - (mImageBitmap!!.width * getMatrixScaleX())) {
                distanceX = (width - (mImageBitmap!!.width * getMatrixScaleX()) - currentTransX)
            } else {
                distanceX = moveX
            }

            if (currentTransY + moveY >= 0f) {
                distanceY = (0 - currentTransY) + 0.5f
            } else if (currentTransY + moveY <= height - (mImageBitmap!!.height * getMatrixScaleY())) {
                distanceY = (height - (mImageBitmap!!.height * getMatrixScaleY()) - currentTransY)
            } else {
                distanceY = moveY
            }

            mImageMatrix.postTranslate(moveX, moveY)
            mBitmapShader.setLocalMatrix(mImageMatrix)
            invalidate()
        }
    }

    fun getMatrixScaleX(): Float {
        mImageMatrix.getValues(mFloatArray)
        return mFloatArray[Matrix.MSCALE_X]
    }

    fun getMatrixScaleY(): Float {
        mImageMatrix.getValues(mFloatArray)
        return mFloatArray[Matrix.MSCALE_Y]
    }

    fun getMatrixTranslateX(): Float {
        mImageMatrix.getValues(mFloatArray)
        return mFloatArray[Matrix.MTRANS_X]
    }

    fun getMatrixTranslateY(): Float {
        mImageMatrix.getValues(mFloatArray)
        return mFloatArray[Matrix.MTRANS_Y]
    }

    fun changeStyle(templatePart: TemplatePart) {
        mTemplatePart = templatePart
        initSize()
        drawClipPath()
        initMask()
        resetShader()
        invalidate()
    }

    fun changeInnerSpace(space: Float) {
        mSpace = space
        drawClipPath()
        invalidate()
    }

    fun changeCorner(corner: Float) {
        mCorner = corner
        mPathEffect = CornerPathEffect(mCorner)
        invalidate()
    }

    private fun midDiagonalPoint(paramPointF: PointF) {
        val arrayOfFloat = FloatArray(9)
        mImageMatrix.getValues(arrayOfFloat)
        val f1 = 0f * arrayOfFloat[Matrix.MSCALE_X] + 0f * arrayOfFloat[Matrix.MSKEW_X] + arrayOfFloat[Matrix.MTRANS_X]
        val f2 = 0f * arrayOfFloat[Matrix.MSKEW_Y] + 0f * arrayOfFloat[Matrix.MSCALE_Y] + arrayOfFloat[Matrix.MTRANS_Y]
        val f3 =
            arrayOfFloat[Matrix.MSCALE_X] * mImageBitmap!!.width + arrayOfFloat[Matrix.MSKEW_X] * mImageBitmap!!.height + arrayOfFloat[Matrix.MTRANS_X]
        val f4 =
            arrayOfFloat[Matrix.MSKEW_Y] * mImageBitmap!!.width + arrayOfFloat[Matrix.MSCALE_Y] * mImageBitmap!!.height + arrayOfFloat[Matrix.MTRANS_Y]
        val f5 = f1 + f3
        val f6 = f2 + f4
        paramPointF.set(f5 / 2.0f, f6 / 2.0f)
    }

    fun changeRotate(rotate: Float) {
        mRotate = rotate
        drawClipPath()
        invalidate()
    }

    fun flipImage() {
        val localPointF = PointF()
        midDiagonalPoint(localPointF)
        mImageMatrix.postScale(-1f, 1f, localPointF.x, localPointF.y)
        mBitmapShader.setLocalMatrix(mImageMatrix)
        invalidate()
    }

    fun rotateImage() {
        val localPointF = PointF()
        midDiagonalPoint(localPointF)
        mImageMatrix.postRotate(90f, localPointF.x, localPointF.y)
        mBitmapShader.setLocalMatrix(mImageMatrix)
        invalidate()
    }
}*/
