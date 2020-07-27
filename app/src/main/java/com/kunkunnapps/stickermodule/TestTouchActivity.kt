package com.kunkunnapps.stickermodule

import android.app.Activity
import android.content.Intent
import android.graphics.Path
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import com.kunkunnapps.stickermodule.view.MainActivity
import org.json.JSONObject

class TestTouchActivity : Activity() {
    private  val TAG = "TestTouchActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch)
        val spaceAdditional = 15f
        val rectF1 = RectF(0f, 0f, 720f,  241f)
        val r2Left = 116.toFloat()
        val r2Top = 68 + spaceAdditional
        val r2Right = r2Left + 484
        val r2Bottom = r2Top - spaceAdditional * 2 + 104 +5
        val rectF2 = RectF(r2Left, r2Top - spaceAdditional, r2Right,  r2Bottom)

        val left = rectF2.left
        val top = rectF2.top
        val right = rectF1.width() - rectF2.width() - rectF2.left
        val bottom = rectF1.height() - rectF2.height() - rectF2.top

        var percentLeft = left / rectF1.width()
        var percentTop = top / rectF1.height()
        var percentBottom = bottom / rectF1.height()
        var percentRight = right / rectF1.width()

        Log.d(TAG, "onCreate: percentLeft $percentLeft percentTop $percentTop percentBottom $percentBottom percentRight $percentRight")

        val resId =  R.drawable.label2
        val name: String = resources.getResourceEntryName(resId)


        val svgName: String = resources.getResourceEntryName(resId)
        val json = JSONObject()
        json.put("name", name)
        json.put("space_percent_left", percentLeft)
        json.put("space_percent_top", percentTop)
        json.put("space_percent_right", percentRight)
        json.put("space_percent_bottom", percentBottom)
        json.put("color_origin1", "#80abda")
        json.put("color_origin2", "")
        json.put("color_origin_gradient_1", "#ff1c3d")
        json.put("color_origin_gradient_2", "#d41128")
        json.put("color_origin_gradient_3", "")
        json.put("color_origin_gradient_4", "")
        json.put("color_origin_gradient_5", "")
        json.put("enable_color_gradient", true)

        Log.d(TAG, "json: \n $json")

      /*  percentLeft = 0.14814815f
        percentTop = 0.14619882f
        percentRight = 0.47368422f
        percentBottom =0.21442495f*/
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_SPACE_LEFT, percentLeft)
            putExtra(MainActivity.EXTRA_ID_DRAWABLE, resId)
            putExtra(MainActivity.EXTRA_SPACE_TOP, percentTop)
            putExtra(MainActivity.EXTRA_SPACE_RIGHT, percentRight)
            putExtra(MainActivity.EXTRA_SPACE_BOTTOM, percentBottom)
            /*putExtra(MainActivity.EXTRA_SVG_FROM_ASSET, svgName)*/
        }
        startActivity(intent)
        finish()
    }
}