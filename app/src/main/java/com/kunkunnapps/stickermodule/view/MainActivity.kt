package com.kunkunnapps.stickermodule.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Path
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.kunkunnapps.stickermodule.R
import com.kunkunnapps.stickermodule.sticker.textsticker.StickerTextInfo
import com.kunkunnapps.stickermodule.sticker.textsticker.TextAlign
import com.kunkunnapps.stickermodule.sticker.textsticker.TextSticker
import com.kunkunnapps.stickermodule.xmlparse.VectorChildFinder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_text.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ID_DRAWABLE = "EXTRA_ID_DRAWABLE"
        const val EXTRA_SPACE_LEFT = "EXTRA_SPACE_LEFT"
        const val EXTRA_SPACE_TOP = "EXTRA_SPACE_TOP"
        const val EXTRA_SPACE_RIGHT = "EXTRA_SPACE_RIGHT"
        const val EXTRA_SPACE_BOTTOM = "EXTRA_SPACE_BOTTOM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pathConvert = VectorChildFinder(this, R.drawable.ic_shape_1)
        val vPath = pathConvert.findPathByName("path1")
        val path = Path()
        vPath.toPath(path)
        val option = BitmapFactory.Options()
        option.inPreferredConfig = Bitmap.Config.ARGB_8888


        val bitmapShader = BitmapFactory.decodeResource(
            resources,
            R.drawable.bg_shader_test,
            option
        )
        val info = StickerTextInfo(
            text = "Something",
            textColor = Color.BLUE,
            textColorAlpha = 100,
            textShadowColorOrigin = Color.CYAN,
            textShadowWeight = 0f,
            textShadowAlpha = 100f,
            bitmapTextShader = null,
            textShaderAlpha = 100,
            spacePercentLeft = 0.033333335f,
            spacePercentTop = 0.2725f,
            spacePercentRight = 0.15694444f,
            spacePercentBottom = 0.2675f,
            bitmap = null,
            textAlign = TextAlign.CENTER
        )

        frameMain?.post {
            val imageStickerView =
                TextSticker(this, info, frameMain.width, frameMain.height)
            imageStickerView.path = path
            imageStickerView.setEditEnable(false)
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            frameMain.addView(imageStickerView, lp)

            btnChangeText.setOnClickListener {
                imageStickerView.setText(edt.text.toString())
            }

            btnAddBitmap.setOnClickListener {
                imageStickerView.stickerTextInfo.apply {
                    spacePercentLeft = intent.getFloatExtra(EXTRA_SPACE_LEFT, 0f)
                    spacePercentTop = intent.getFloatExtra(EXTRA_SPACE_TOP, 0f)
                    spacePercentRight = intent.getFloatExtra(EXTRA_SPACE_RIGHT, 0f)
                    spacePercentBottom = intent.getFloatExtra(EXTRA_SPACE_BOTTOM, 0f)
                }

                val id = intent.getIntExtra(EXTRA_ID_DRAWABLE, R.drawable.label)

                val bitmap = BitmapFactory.decodeResource(
                    resources,
                    id,
                    option
                )
                imageStickerView.setBackgroundBitmap(bitmap)
            }

            btnRemoveBitmap.setOnClickListener {
                imageStickerView.stickerTextInfo.apply {
                    spacePercentLeft = 0f
                    spacePercentTop = 0f
                    spacePercentRight = 0f
                    spacePercentBottom = 0f
                }
                imageStickerView.setBackgroundBitmap(null)
            }

            imageStickerView.setTextColor(Color.BLUE)
            imageStickerView.setTextColorAlpha(255)

            btnBackup.setOnClickListener {
                edt.setText( "Something")
            }

            btnApplyBackup.setOnClickListener {
                edt.setText( "aaaaaaaaa")
            }

        }
    }
}