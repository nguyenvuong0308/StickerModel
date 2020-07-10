package com.kunkunnapps.stickermodule.sticker.textsticker

import android.graphics.*
import android.text.TextPaint
import android.util.Log
import kotlin.math.atan2
import kotlin.math.roundToLong

object StickerUtils {
    private const val TAG = "StickerUtils"

    class RectPointF(
        var pointTopLeft: PointF,
        var pointTopRight: PointF,
        var pointBottomLeft: PointF,
        var pointBottomRight: PointF
    )

    /*Lấy 4 điểm bảo quanh bitmap bằng ma trận*/
    fun getRectPointFBitmap(bitmap: Bitmap, matrix: Matrix): RectPointF {
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

    fun getTextLengthLongest(text: String, textPaint: TextPaint): Pair<Int, Int> {
        val isBreakLine = text.contains("\n")
        var longestLine = ""
        val rect = Rect()
        var maxWidth = 0
        if (isBreakLine) {
            val listLine = text.split("\n")
            listLine.forEachIndexed { index, line ->
                textPaint.getTextBounds(line,0, line.length, rect)
                if (rect.width() > maxWidth) {
                    maxWidth = rect.width()
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

    fun getWidthText(text: String, paint: TextPaint): Int {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.width()
    }

    /*Fill region by path*/
    fun fillBoundRegion(region: Region, path: Path) {
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

    /*fun getTextHeight(paint: TextPaint): Int {
        val rect = Rect()
        val text = """qwertyuiop[]asdfghjkl;'\zxcvbnm,./<>?:"|P{}1234567890-=+_)(*&^%'$'#@!QWERTYUIOPASDFGHJKLZXCVBNM"""
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height()
    }*/

    fun getTextHeightLineTallest(text: String, paint: TextPaint): Int {
        val rect = Rect()
        val texts = text.split("\n")
        var maxHeight = 0
        texts.forEach {
            paint.getTextBounds(it, 0, it.length, rect)
            val height = rect.height()
            if (height > maxHeight) {
                maxHeight = height
            }
        }
        return maxHeight
    }

    fun getTotalTextHeight(text: String, paint: TextPaint): Int {
        val totalHeight: Int
        val heightOneLine = getTextHeightLineTallest(text, paint)


        val texts = text.split("\n")
        totalHeight = heightOneLine * texts.count()
        return totalHeight
    }

    /**
     * @param originalColor color, without alpha
     * @param alpha         from 0.0 to 1.0
     * @return
     */
    fun addAlpha(originalColor: String, alpha: Double): String {
        var _originalColor = originalColor
        val alphaFixed = (alpha * 255).roundToLong()
        var alphaHex = java.lang.Long.toHexString(alphaFixed)
        if (alphaHex.length == 1) {
            alphaHex = "0$alphaHex"
        }
        _originalColor = _originalColor.replace("#", "#$alphaHex")
        return _originalColor
    }

    fun calculateRotation(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): Float {
        val x = x1 - x2.toDouble()
        val y = y1 - y2.toDouble()
        val radians = atan2(y, x)
        return Math.toDegrees(radians).toFloat()
    }
}


