package com.kunkunnapps.stickermodule.sticker.textsticker

import android.graphics.*
import android.text.TextPaint
import android.util.Log
import com.kunkunnapps.stickermodule.view.RectPointF

object StickerUtils {
    private const val TAG = "StickerUtils"

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

    fun getTextLengthLongest(text: String): Pair<Int, Int> {
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

    fun getTextHeight(paint: TextPaint): Int {
        val rect = Rect()
        val text = """qwertyuiop[]asdfghjkl;'\zxcvbnm,./<>?:"|P{}1234567890-=+_)(*&^%'$'#@!QWERTYUIOPASDFGHJKLZXCVBNM"""
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height()
    }

    fun getTotalTextHeight(text: String, paint: TextPaint): Int {
        var totalHeight = 0
        val rect = Rect()
        val standardText = """qwertyuiop[]asdfghjkl;'\zxcvbnm,./<>?:"|P{}1234567890-=+_)(*&^%'$'#@!QWERTYUIOPASDFGHJKLZXCVBNM"""
        paint.getTextBounds(standardText, 0, standardText.length, rect)

        val texts = text.split("\n")
        repeat(texts.size) {
            totalHeight += rect.height()
        }
        return totalHeight
    }
}