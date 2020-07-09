package com.kunkunnapps.stickermodule.sticker.textsticker

import android.graphics.Rect
import android.text.TextPaint
import android.util.Log

class ResizeTextHelper {
    private val TAG = "ResizeTextHelper"
    var oldWidth = 0
    var oldHeight = 0
    var oldText = ""

    fun autoSizeTextPaint(
        text: String,
        width: Int,
        height: Int,
        spaceMultiText: Float,
        textPaint: TextPaint,
        onDone: (realHeight: Int, realWidth: Int) -> Unit = { _, _ -> }
    ) {
        if (oldWidth != width || oldHeight != height || oldText != text) {
            val longestLineIndex = StickerUtils.getTextLengthLongest(text)
            oldWidth = width
            oldHeight = height
            oldText = text
            if (text.isBlank()) return
            val rect = Rect()
            var isOk = false
            var textSize = textPaint.textSize
            textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, rect)
            //region calculate width max
            val step = 0.1f
            if (rect.width() < width) {
                while (!isOk) {
                    if (rect.width() < width) {
                        textSize += step
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
                    Log.d(
                        TAG,
                        "autoSizeTextPaint: textSize4 $textSize rect.width() ${rect.width()} width $width "
                    )
                }
                textSize -= step
                textPaint.textSize = textSize
            } else if (rect.width() > width) {
                while (!isOk) {
                    if (rect.width() > width) {
                        textSize -= step
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
                    Log.d(
                        TAG,
                        "autoSizeTextPaint: textSize3 $textSize rect.width() ${rect.width()} width $width "
                    )
                }
            }
            //endregion

            //region Calculate height fit
            isOk = false
            val texts = text.split("\n")
            var totalHeight = 0f

            while (!isOk) {
                totalHeight = StickerUtils.getTotalTextHeight(text, textPaint).toFloat()
                totalHeight += spaceMultiText * (texts.count() - 1)
                isOk = totalHeight <= height
                if (!isOk) {
                    textSize -= step
                    textPaint.textSize = textSize
                }
                Log.d(
                    TAG,
                    "autoSizeTextPaint: textSize1 $textSize totalHeight ${totalHeight} height $height "
                )
            }
            textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, rect)
            val totalWidth = rect.width().toFloat()
            //endregion
            onDone.invoke(totalHeight.toInt(), totalWidth.toInt())
        } else {
            val totalHeight = StickerUtils.getTotalTextHeight(text, textPaint).toFloat()
            val rect = Rect()
            val longestLineIndex = StickerUtils.getTextLengthLongest(text)
            textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, rect)
            val totalWidth = rect.width().toFloat()
            onDone.invoke(totalHeight.toInt(), totalWidth.toInt())
        }

    }
}