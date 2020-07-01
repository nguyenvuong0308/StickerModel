package com.kunkunnapps.stickermodule

import android.graphics.Bitmap

class BitmapHolder {

    private var hashMap: LinkedHashMap<String, Bitmap> = linkedMapOf()
    companion object {
        private val instance = BitmapHolder()
        fun getInstance() = instance
    }

    fun putBitmap(key: String, bitmap: Bitmap) {
        hashMap[key] = bitmap
    }

    fun getBitmap(key: String): Bitmap? {
        val bitmap = hashMap[key]
        return if (bitmap?.isRecycled == false) {
            bitmap
        } else {
            hashMap.remove(key)
            null
        }
    }


    fun removeBitmap(key: String) {
        hashMap[key]?.recycle()
        hashMap.remove(key)
    }

    fun removeAllBitmap() {
        hashMap.forEach{ entry ->
            entry.value.recycle()
        }
        hashMap.clear()
        System.gc()
    }

}