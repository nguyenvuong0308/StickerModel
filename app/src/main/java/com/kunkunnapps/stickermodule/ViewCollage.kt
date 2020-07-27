package com.kunkunnapps.stickermodule

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.kunkunnapps.stickermodule.view.DisplayUtils


class ViewCollage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    val bitmapMask  = BitmapFactory.decodeResource(resources, R.drawable.collage_3)
    val bitmapOriginal  = BitmapFactory.decodeResource(resources, R.drawable.image)
    private var mWidth = DisplayUtils.getWidthDisplay(context)
    private var mHeight = DisplayUtils.getHeightDisplay(context)
    private var mMatrix = Matrix()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val mask = Bitmap.createScaledBitmap(bitmapMask, mWidth, mHeight, false)

        val result =
            Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)


        val tempCanvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)


        tempCanvas.drawBitmap(bitmapOriginal, mMatrix, null)
        tempCanvas.drawBitmap(mask, 0f, 0f, paint)
        paint.xfermode = null

        //Draw result after performing masking
        canvas.drawBitmap(result, 0f, 0f, Paint())
    }

    var mTouchPointX = 0f
    var mTouchPointY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                mTouchPointX = event.rawX
                mTouchPointY = event.rawY
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                mMatrix.postTranslate(event.rawX - mTouchPointX, event.rawY - mTouchPointY)
                mTouchPointX = event.rawX
                mTouchPointY = event.rawY
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }


}