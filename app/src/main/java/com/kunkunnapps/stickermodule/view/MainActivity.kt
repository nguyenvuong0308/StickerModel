package com.kunkunnapps.stickermodule.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.kunkunnapps.stickermodule.R
import com.kunkunnapps.stickermodule.sticker.textsticker.StickerTextInfo
import com.kunkunnapps.stickermodule.sticker.textsticker.TextAlign
import com.kunkunnapps.stickermodule.sticker.textsticker.TextSticker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val option = BitmapFactory.Options()
        option.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.label,
            option
        )
        val info = StickerTextInfo(
            text = "Something",
            textColor = Color.BLUE,
            textColorAlpha = 100,
            textShadowColor = Color.CYAN,
            textShadowWeight = 0,
            textShadowAlpha = 100,
            textShader = null,
            textShaderAlpha = 100,
            spacePercentLeft = 0.087f,
            spacePercentTop = 0.23f,
            spacePercentRight = 0.069f,
            spacePercentBottom = 0.455f,
            bitmap = bitmap,
            textAlign = TextAlign.RIGHT
        )

        val imageStickerView =
            TextSticker(this, info)
        imageStickerView.setEditEnable(true)
        val lp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        frameMain.addView(imageStickerView, lp)

        btnChangeText.setOnClickListener {
            imageStickerView.setText(edt.text.toString())
        }

        btnAddBitmap.setOnClickListener {
            imageStickerView.setStickerInfo(info)
        }

        btnRemoveBitmap.setOnClickListener {
        }
    }
}