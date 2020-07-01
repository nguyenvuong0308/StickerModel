package com.kunkunnapps.stickermodule

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Matrix
import android.graphics.Matrix.MSCALE_X
import android.graphics.Matrix.MSKEW_X
import android.util.DisplayMetrics
import android.util.TypedValue
import java.lang.Math.*
import kotlin.math.atan2
import kotlin.math.roundToInt


object DisplayUtils {
    fun getWidthDisplay(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getHeightDisplay(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    private fun showNavigationBar(resources: Resources): Boolean {
        val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return id > 0 && resources.getBoolean(id)
    }

    fun getNavigationBarHeight(context: Activity): Int {
        val metrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        context.windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight)
            realHeight - usableHeight
        else
            0
    }

    fun dpToPx(context: Context, dip: Float): Float {
        val displayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), displayMetrics)
    }

    fun pxToDp(context: Context, px: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px.toFloat(), displayMetrics)
            .toInt()
    }


    fun spToPx(context: Context, sp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), displayMetrics)
            .toInt()
    }




}

fun Matrix.getRotationAngle(): Float {
    val value =  FloatArray(9)
    getValues(value)
    val angle = -(atan2(value[MSKEW_X], value[MSCALE_X]) * (180 / PI)).roundToInt().toFloat()
    return angle
}