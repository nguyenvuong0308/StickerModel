package com.kunkunnapps.stickermodule.view

import android.graphics.Matrix.*
import android.util.Log
import kotlin.math.cos
import kotlin.math.sin

object MatrixUtils {
    private const val TAG = "MatrixUtils"

    fun calculateScaleX(scaleX: Float, angle: Float): Float {
        val newScaleX: Float = cos(angle) * scaleX
        Log.d(TAG, "calculateScaleX: $newScaleX")
        return newScaleX
    }

    fun calculateScaleY(scaleY: Float, angle: Float): Float {
        val newScaleY: Float = cos(angle) * scaleY
        Log.d(TAG, "calculateScaleY: $newScaleY")
        return newScaleY
    }

    fun calculateSkewX(scaleX: Float, angle: Float): Float {
        val newSkewX: Float = sin(angle) * scaleX
        Log.d(TAG, "calculateSkewX: $newSkewX")
        return newSkewX
    }

    fun calculateSkewY(scaleY: Float, angle: Float): Float {
        val newSkewY: Float = -sin(angle) * scaleY
        Log.d(TAG, "calculateSkewY: $newSkewY")
        return newSkewY
    }

    fun calculateTransX(centerX: Float, centerY: Float, angle: Float, scaleX: Float): Float {
        val x = centerX * (1 - cos(angle)) + centerY * sin(angle)
        Log.d(TAG, "calculateTransX: $x")
        return x
    }

    fun calculateTransY(centerX: Float, centerY: Float, angle: Float, scaleX: Float): Float {
        val y = centerY * (1 - cos(angle)) - centerX * sin(angle)
        Log.d(TAG, "calculateTransY: $y")
        return y
    }

    fun createValuesMatrix(
        angle: Float,
        scaleX: Float,
        scaleY: Float,
        transX: Float,
        transY: Float
    ): FloatArray {
        val values = FloatArray(9)
        values[MSCALE_X] =
            calculateScaleX(
                scaleX,
                angle
            )
        values[MSCALE_Y] =
            calculateScaleY(
                scaleY,
                angle
            )
        values[MSKEW_X] =
            calculateSkewX(
                scaleX,
                angle
            )
        values[MSKEW_Y] =
            calculateSkewY(
                scaleY,
                angle
            )
        values[MTRANS_X] = transX
        values[MTRANS_Y] = transY
        values[MPERSP_0] = 0f
        values[MPERSP_1] = 0f
        values[MPERSP_2] = 1f
        return values
    }
}