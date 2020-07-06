package com.kunkunnapps.stickermodule

import android.os.Bundle
import android.transition.TransitionManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.kunkunnapps.stickermodule.keyboard.KeyboardHeightProvider
import kotlinx.android.synthetic.main.activity_text.*

class TextActivity : AppCompatActivity() {
    private var keyboardHeightProvider: KeyboardHeightProvider? = null
    private fun getKeyboardListener() = object : KeyboardHeightProvider.KeyboardListener {
        override fun onHeightChanged(height: Int) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(container)
            TransitionManager.beginDelayedTransition(container)
            constraintSet.setMargin(controller.id, ConstraintSet.BOTTOM, height)
            constraintSet.applyTo(container)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)
        keyboardHeightProvider = KeyboardHeightProvider(this)
        keyboardHeightProvider?.addKeyboardListener(getKeyboardListener())
    }

    override fun onResume() {
        super.onResume()
        keyboardHeightProvider?.onResume()
    }

    override fun onPause() {
        super.onPause()
        keyboardHeightProvider?.onPause()
    }
}