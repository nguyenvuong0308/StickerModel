package com.kunkunnapps.stickermodule.sticker.textsticker

import android.content.Context
import android.graphics.*
import androidx.annotation.DrawableRes
import java.lang.ref.WeakReference

class StickerButtonController(
    @DrawableRes
    var resIdDrawable: Int,
    var region: Region,
    var position: TextSticker.PositionButton,
    var size: Int,
    var context: WeakReference<Context>,
    var isDraw: Boolean = true
) {
    var bitmap: Bitmap

    init {
        bitmap = BitmapFactory.decodeResource(context.get()?.resources, resIdDrawable)
        val bitmapTmp = Bitmap.createScaledBitmap(bitmap, size, size, false)
        bitmap.recycle()
        bitmap = bitmapTmp
    }

    private var matrix: Matrix = Matrix()
    private var path: Path = Path()

    fun drawButton(rectFPointParent: StickerUtils.RectPointF, rotate: Float, canvas: Canvas) {
        if (isDraw) {
            matrix.reset()
            matrix.postRotate(rotate, (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat())
            var x = 0f
            var y = 0f

            when (position) {
                TextSticker.PositionButton.TOP_LEFT -> {
                    x = rectFPointParent.pointTopLeft.x
                    y = rectFPointParent.pointTopLeft.y
                }
                TextSticker.PositionButton.TOP_RIGHT -> {
                    x = rectFPointParent.pointTopRight.x
                    y = rectFPointParent.pointTopRight.y
                }

                TextSticker.PositionButton.BOTTOM_RIGHT -> {
                    x = rectFPointParent.pointBottomRight.x
                    y = rectFPointParent.pointBottomRight.y
                }

                TextSticker.PositionButton.BOTTOM_LEFT -> {
                    x = rectFPointParent.pointBottomLeft.x
                    y = rectFPointParent.pointBottomLeft.y
                }

                TextSticker.PositionButton.CENTER_RIGHT -> {
                    x = (rectFPointParent.pointTopRight.x + rectFPointParent.pointBottomRight.x) / 2
                    y = (rectFPointParent.pointTopRight.y + rectFPointParent.pointBottomRight.y) / 2
                }

                TextSticker.PositionButton.CENTER_BOTTOM -> {
                    x =
                        (rectFPointParent.pointBottomLeft.x + rectFPointParent.pointBottomRight.x) / 2
                    y =
                        (rectFPointParent.pointBottomLeft.y + rectFPointParent.pointBottomRight.y) / 2
                }
            }

            matrix.postTranslate(
                x - (bitmap.width / 2).toFloat(),
                y - (bitmap.height / 2).toFloat()
            )
            path.reset()
            path.apply {
                addRect(0f, 0f, size.toFloat(), size.toFloat(), Path.Direction.CCW)
                offset(x - (bitmap.width / 2).toFloat(), y - (bitmap.height / 2).toFloat())
                StickerUtils.fillBoundRegion(region, this)
            }
            canvas.drawBitmap(bitmap, matrix, null)
        } else {
            path.reset()
            StickerUtils.fillBoundRegion(region, path)
        }

    }
}