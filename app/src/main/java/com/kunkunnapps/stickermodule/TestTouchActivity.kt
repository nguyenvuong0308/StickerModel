package com.kunkunnapps.stickermodule

import android.app.Activity
import android.content.Intent
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
        val rectF1 = RectF(0f, 0f, 720f,  239f)
        val r2Left = 52f
        val r2Top = 35 + spaceAdditional
        val r2Right = r2Left + 619f
        val r2Bottom = r2Top - spaceAdditional * 2 + 120f
        val rectF2 = RectF(r2Left, r2Top - spaceAdditional, r2Right,  r2Bottom)

        val left = rectF2.left
        val top = rectF2.top
        val right = rectF1.width() - rectF2.width() - rectF2.left
        val bottom = rectF1.height() - rectF2.height() - rectF2.top

        val percentLeft = left / rectF1.width()
        val percentTop = top / rectF1.height()
        val percentBottom = bottom / rectF1.height()
        val percentRight = right / rectF1.width()

        Log.d(TAG, "onCreate: percentLeft $percentLeft percentTop $percentTop percentBottom $percentBottom percentRight $percentRight")

        val resId =  R.drawable.label1
        val name: String = resources.getResourceEntryName(resId)
        val json = JSONObject()
        json.put("name", name)
        json.put("space_percent_left", percentLeft)
        json.put("space_percent_top", percentTop)
        json.put("space_percent_right", percentRight)
        json.put("space_percent_bottom", percentBottom)

        Log.d(TAG, "json: \n $json")

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_ID_DRAWABLE, resId)
            putExtra(MainActivity.EXTRA_SPACE_LEFT, percentLeft)
            putExtra(MainActivity.EXTRA_SPACE_TOP, percentTop)
            putExtra(MainActivity.EXTRA_SPACE_RIGHT, percentRight)
            putExtra(MainActivity.EXTRA_SPACE_BOTTOM, percentBottom)
        }
        startActivity(intent)
        finish()
    }
}