package com.kunkunnapps.stickermodule.sticker.textsticker

import android.graphics.Rect
import android.text.TextPaint
import android.util.Log
import kotlin.math.roundToInt

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
            val longestLineIndex = StickerUtils.getTextLengthLongest(text, textPaint)
            oldWidth = width
            oldHeight = height
            oldText = text
            if (text.isBlank()) return
            //region calculate width fit
            val bounds = Rect()
            var textSize = textPaint.textSize
            textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, bounds)
            textSize = textSize * width / bounds.width()
            textPaint.textSize = textSize
            //endregion

            //region Calculate height fit
            val step = 0.5f
            var isOk = false
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
            }

            textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, bounds)
            val totalWidth = bounds.width().toFloat()
            //endregion
            onDone.invoke(totalHeight.toInt(), totalWidth.toInt())
        } else {
            val totalHeight = StickerUtils.getTotalTextHeight(text, textPaint).toFloat()
            val rect = Rect()
            val longestLineIndex = StickerUtils.getTextLengthLongest(text, textPaint)
            textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, rect)
            val totalWidth = rect.width().toFloat()
            onDone.invoke(totalHeight.toInt(), totalWidth.toInt())
        }

    }
}