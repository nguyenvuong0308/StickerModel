package com.kunkunnapps.stickermodule.sticker.textsticker

import android.graphics.Bitmap
import android.graphics.Color

data class StickerTextInfo(
    var text: String,

    var textAlign: TextAlign,
    
    //region TextColor
    var textColor: Int,
    var textColorAlpha: Int,
    //endregion

    //region TextShadow
    var textShadowColorOrigin: Int,
    var textShadowAlpha: Float,
    var textShadowWeight: Float,
    //endregion

    //region TextShader
    var bitmapTextShader: Bitmap?,
    var textShaderAlpha: Int,
    //endregion

    //region Background bitmap - Label
    var spacePercentTop: Float,
    var spacePercentBottom: Float,
    var spacePercentRight: Float,
    var spacePercentLeft: Float,
    var bitmap: Bitmap?
    //endregion
) {
    fun getShadowColorMergeAlpha(): Int {
        val hexColor = String.format("#%06X", 0xFFFFFF and textShadowColorOrigin)
        val colorAlpha =
            StickerUtils.addAlpha(hexColor, textShadowAlpha.toDouble() / 100)
        return Color.parseColor(colorAlpha)
    }
}

enum class TextAlign {
    CENTER,
    LEFT,
    RIGHT
}