package com.kunkunnapps.stickermodule

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.caverock.androidsvg.SVG
import kotlinx.android.synthetic.main.activity_merge_drawable.*
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringReader
import java.nio.charset.StandardCharsets


class MergeDrawable : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merge_drawable)

        val vectorShape = VectorShape(this, R.drawable.ic_group_3)
        vectorShape.getLayersAt(1)?.color = Color.parseColor("#ffaffa")


        val sb = StringBuilder()
        val inS = assets.open("baba.svg")
        val br =
            BufferedReader(InputStreamReader(inS, StandardCharsets.UTF_8))
        var str: String?
        while (br.readLine().also { str = it } != null) {
            sb.append(str)
        }
        br.close()


        val svg = SVG.getFromString("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"700\" height=\"700\u202C\" viewBox=\"0 0 153.44 180.03\">\n" +
                "  <defs>\n" +
                "    <style>\n" +
                "      .cls-1 {\n" +
                "        stroke: #f991c1;\n" +
                "        stroke-width: 10px;\n" +
                "        fill-rule: evenodd;\n" +
                "        fill: url(#linear-gradient);\n" +
                "      }\n" +
                "    </style>\n" +
                "    <linearGradient id=\"linear-gradient\" x1=\"2611.72\" y1=\"6434\" x2=\"2611.72\" y2=\"6263.97\" gradientUnits=\"userSpaceOnUse\">\n" +
                "      <stop offset=\"0\" stop-color=\"#160427\"/>\n" +
                "      <stop offset=\"1\" stop-color=\"#fe23d7\"/>\n" +
                "    </linearGradient>\n" +
                "  </defs>\n" +
                "  <path class=\"cls-1\" d=\"M2611.72,6263.96a71.715,71.715,0,1,1-71.72,71.72A71.718,71.718,0,0,1,2611.72,6263.96Zm5.96,170.04s9.42-23.71-16.49-69.51,50.73,0.89,50.73.89S2679.31,6392.93,2617.68,6434Z\" transform=\"translate(-2535 -6258.97)\"/>\n" +
                "</svg>\n")
        val picture = svg.renderToPicture(100, 100)

        val bitmap = Bitmap.createBitmap(
            picture.width,
            picture.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        picture.draw(canvas)


    }

    fun  xmlStringToDrawable( xmlString: String): Drawable{
        val factory =
            XmlPullParserFactory.newInstance()
        factory.isValidating = true
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xmlString));
        return Drawable.createFromXml(resources,parser)
    }
}