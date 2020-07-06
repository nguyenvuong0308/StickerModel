package com.kunkunnapps.stickermodule

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVG

class CustomTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var mMatrix = Matrix()

    init {

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val svg = SVG.getFromString("<svg\n" +
                "    xmlns=\"http://www.w3.org/2000/svg\"\n" +
                "    viewBox=\"0 0 1024 1024\"\n" +
                "    id=\"vector\">\n" +
                "    <path\n" +
                "        id=\"path\"\n" +
                "        d=\"M 0.025 518.828 C -2.718 235.431 217.611 3.073 492.141 0.033 C 766.632 -3.127 991.35 224.151 994.093 507.589 C 996.837 791.025 776.547 1023.343 502.095 1026.503 C 227.566 1029.623 2.769 802.345 0.025 518.828 Z\"\n" +
                "        fill=\"#d8d8d8\"\n" +
                "        stroke=\"#979797\"\n" +
                "        stroke-width=\"0.1\"/>\n" +
                "</svg>\n")
        val picture = svg.renderToPicture(1024, 1024)
        val bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888)
        val canv = Canvas(bitmap)
        picture.draw(canv)
        matrix.setScale(1f, 0.5f)
        canvas.drawBitmap(bitmap, matrix, null)
    }
}