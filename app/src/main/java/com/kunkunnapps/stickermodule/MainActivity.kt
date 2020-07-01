package com.kunkunnapps.stickermodule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.kunkunnapps.stickermodule.view.StickerTextInfo
import com.kunkunnapps.stickermodule.view.TextSticker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val option = BitmapFactory.Options()
        option.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.label, option)
        val info = StickerTextInfo(
            spacePercentLeft = 0.087f,
            spacePercentTop = 0.23f,
            spacePercentRight = 0.069f,
            spacePercentBottom = 0.555f,

            bitmap = bitmap
        )

        val info2 = StickerTextInfo(
            spacePercentTop = 0f,
            spacePercentBottom = 0f,
            spacePercentLeft = 0f,
            spacePercentRight = 0f,
            bitmap = null
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
            imageStickerView.setStickerInfo(info2)
        }
    }
}