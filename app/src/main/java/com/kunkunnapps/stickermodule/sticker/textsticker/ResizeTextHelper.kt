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
        textSizeMin: Float = 1f,
        onDone: (realHeight: Int, realWidth: Int) -> Unit = { _, _ -> }
    ) {
        Log.d(
            TAG,
            "autoSizeTextPaint: text $text width $width height $height spaceMultiText $spaceMultiText "
        )
        if (oldWidth != width || oldHeight != height || oldText != text) {
            val longestLineIndex = StickerUtils.getTextLengthLongest(text, textPaint)
            oldWidth = width
            oldHeight = height
            oldText = text
            if (text.isBlank()) return
            var isOk = false
            //region Tính text size gần đúng
            val bounds = Rect()
            var textSize = textPaint.textSize
            textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, bounds)
            textSize = (textSize * Math.min(
                width.toFloat() / bounds.width(),
                (height.toFloat() / bounds.height())
            ))
            textSize += 1f
            textPaint.textSize = textSize
            //endregion
            val step = 0.1f

            textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, bounds)

            while (!isOk) {
                if (bounds.width() > width) {
                    textSize -= step
                    if (textSize < textSizeMin) {
                        textSize = textSizeMin
                        isOk = true
                    }
                    textPaint.textSize = textSize
                    textPaint.getTextBounds(
                        text,
                        longestLineIndex.first,
                        longestLineIndex.second,
                        bounds
                    )
                } else {
                    isOk = true
                }
                Log.d(
                    TAG,
                    "autoSizeTextPaint: textSize3 $textSize rect.width() ${bounds.width()} width $width "
                )
            }

            //region Calculate height fit

            val texts = text.split("\n")
            var totalHeight = 0f
            isOk = false
            while (!isOk) {
                totalHeight = StickerUtils.getTotalTextHeight(text, textPaint).toFloat()
                totalHeight += spaceMultiText * (texts.count() - 1)
                isOk = totalHeight <= height
                if (!isOk) {
                    textSize -= step
                    if (textSize < textSizeMin) {
                        textSize = textSizeMin
                        textPaint.textSize = textSize
                        break
                    } else {
                        textPaint.textSize = textSize
                    }
                }

            }
            Log.d(TAG, "autoSizeTextPaint: textSize3 $textSize")
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

    fun autoSizeTextPaint2(
        text: String,
        width: Int,
        height: Int,
        spaceMultiText: Float,
        textPaint: TextPaint,
        textSizeMin: Float = 1f,
        onDone: (realHeight: Int, realWidth: Int) -> Unit = { _, _ -> }
    ) {
        Log.d(
            TAG,
            "autoSizeTextPaint: text $text width $width height $height spaceMultiText $spaceMultiText "
        )
        if (oldWidth != width || oldHeight != height || oldText != text) {
            val longestLineIndex = StickerUtils.getTextLengthLongest(text, textPaint)
            oldWidth = width
            oldHeight = height
            oldText = text
            if (text.isBlank()) return
            var isOk = false
            //region Tính text size gần đúng
            val bounds = Rect()
            var textSize = textPaint.textSize
            textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, bounds)
            textSize = (textSize * Math.min(
                width.toFloat() / bounds.width(),
                (height.toFloat() / bounds.height())
            ))
            textSize += 1f
            textPaint.textSize = textSize
            //endregion
            val step = 0.1f

            textPaint.getTextBounds(text, longestLineIndex.first, longestLineIndex.second, bounds)

            while (!isOk) {
                if (bounds.width() > width) {
                    textSize -= step
                    if (textSize < textSizeMin) {
                        textSize = textSizeMin
                        isOk = true
                    }
                    textPaint.textSize = textSize
                    textPaint.getTextBounds(
                        text,
                        longestLineIndex.first,
                        longestLineIndex.second,
                        bounds
                    )
                } else {
                    isOk = true
                }
                Log.d(
                    TAG,
                    "autoSizeTextPaint: textSize3 $textSize rect.width() ${bounds.width()} width $width "
                )
            }

            //region Calculate height fit

            val texts = text.split("\n")
            var totalHeight = 0f
            isOk = false
            while (!isOk) {
                totalHeight = StickerUtils.getTotalTextHeight(text, textPaint).toFloat()
                totalHeight += spaceMultiText * (texts.count() - 1)
                isOk = totalHeight <= height
                if (!isOk) {
                    textSize -= step
                    if (textSize < textSizeMin) {
                        textSize = textSizeMin
                        textPaint.textSize = textSize
                        break
                    } else {
                        textPaint.textSize = textSize
                    }
                }

            }
            Log.d(TAG, "autoSizeTextPaint: textSize3 $textSize")
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