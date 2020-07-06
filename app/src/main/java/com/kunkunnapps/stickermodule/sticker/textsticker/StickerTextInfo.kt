package com.kunkunnapps.stickermodule.sticker.textsticker

import android.graphics.Bitmap

class StickerTextInfo(
    var text: String,

    var textAlign: TextAlign,
    
    //region TextColor
    var textColor: Int,
    var textColorAlpha: Int,
    //endregion

    //region TextShadow
    var textShadowColor: Int,
    var textShadowAlpha: Int,
    var textShadowWeight: Int,
    //endregion

    //region TextShader
    var textShader: Bitmap?,
    var textShaderAlpha: Int,
    //endregion

    //region Background bitmap - Label
    var spacePercentTop: Float,
    var spacePercentBottom: Float,
    var spacePercentRight: Float,
    var spacePercentLeft: Float,
    var bitmap: Bitmap?
    //endregion
)

enum class TextAlign {
    CENTER,
    LEFT,
    RIGHT
}