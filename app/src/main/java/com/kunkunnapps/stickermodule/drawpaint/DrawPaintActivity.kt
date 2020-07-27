package com.kunkunnapps.stickermodule.drawpaint

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kunkunnapps.stickermodule.R
import kotlinx.android.synthetic.main.activity_draw_paint.*

class DrawPaintActivity: AppCompatActivity(R.layout.activity_draw_paint) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        button.setOnClickListener {
            brushView.eraserMode = !brushView.eraserMode
        }
        var eraserSize = 10f
        button2.setOnClickListener {
            brushView.setEraserSize(eraserSize)
            eraserSize +=5f
        }
    }

}