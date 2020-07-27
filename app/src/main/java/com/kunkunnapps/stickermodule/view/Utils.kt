package com.kunkunnapps.stickermodule.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.os.Environment
import android.util.Log
import java.io.*
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sqrt


object Utils {
    fun convertToMutable(imgIn: Bitmap): Bitmap? {
        var imgIn = imgIn
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            val file =
                File(Environment.getDataDirectory().path + File.separator.toString() + "temp.tmp")

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            val randomAccessFile = RandomAccessFile(file, "rw")

            // get the width and height of the source bitmap.
            val width = imgIn.width
            val height = imgIn.height
            val type: Bitmap.Config = imgIn.config

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            val channel: FileChannel = randomAccessFile.getChannel()
            val map: MappedByteBuffer =
                channel.map(FileChannel.MapMode.READ_WRITE, 0, (imgIn.rowBytes * height).toLong())
            imgIn.copyPixelsToBuffer(map)
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle()
            System.gc() // try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type)
            map.position(0)
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map)
            //close the temporary file and channel , then delete that also
            channel.close()
            randomAccessFile.close()

            // delete the temp file
            file.delete()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imgIn
    }

    fun getDistanceTwoPoint(point1: PointF, point2: PointF): Float {
        val x1 = point1.x
        val y1= point1.y
        val x2= point2.x
        val y2= point2.y
        val x = hypot(x1 - x2, y1 - y2)
        Log.d("getDistanceTwoPoint", "getDistanceTwoPoint: point1 $point1  point2 $point2 x$x")
        return x
    }

    fun loadAssetFile(context: Context, fileName: String): String? {
        try {
            val bufferedReader =
                BufferedReader(InputStreamReader(context.getAssets().open(fileName)))
            val out = StringBuilder()
            var eachline: String? = bufferedReader.readLine()
            while (eachline != null) {
                out.append(eachline)
                eachline = bufferedReader.readLine()
            }
            return out.toString()
        } catch (e: IOException) {
            Log.e("Load Asset File", e.toString())
        }
        return null
    }

}

fun Matrix.getRotate(): Float {
    val v = FloatArray(9)
    getValues(v)
// translation is simple
// translation is simple
    val tx = v[Matrix.MTRANS_X]
    val ty = v[Matrix.MTRANS_Y]

// calculate real scale

// calculate real scale
    val scalex = v[Matrix.MSCALE_X]
    val skewy = v[Matrix.MSKEW_Y]
    val rScale =
        sqrt(scalex * scalex + skewy * skewy.toDouble()).toFloat()

// calculate the degree of rotation

// calculate the degree of rotation
    val rAngle = (atan2(
        v[Matrix.MSKEW_X].toDouble(),
        v[Matrix.MSCALE_X].toDouble()
    ) * (180 / Math.PI)).roundToInt().toFloat()

    Log.d("getRotate", "getRotate: $rAngle")
    return rAngle
}

fun Matrix.getRotate2(): Float {
    val v = FloatArray(9)
    getValues(v)
// translation is simple
// translation is simple
    val tx = v[Matrix.MTRANS_X]
    val ty = v[Matrix.MTRANS_Y]

// calculate real scale

// calculate real scale
    val scalex = v[Matrix.MSCALE_X]
    val skewy = v[Matrix.MSKEW_Y]
    val rScale =
        sqrt(scalex * scalex + skewy * skewy.toDouble()).toFloat()

// calculate the degree of rotation

// calculate the degree of rotation
    val rAngle = (atan2(
        v[Matrix.MSKEW_Y].toDouble(),
        v[Matrix.MSCALE_Y].toDouble()
    ) * (180 / Math.PI)).roundToInt().toFloat()

    Log.d("getRotate", "getRotate: $rAngle")
    return -rAngle
}



fun Matrix.getRealScaleX() : Float {
    // calculate real scale
    val v = FloatArray(9)
    getValues(v)
    val scalex = v[Matrix.MSCALE_X]
    val skewy = v[Matrix.MSKEW_Y]
    val rScale =
        sqrt(scalex * scalex + skewy * skewy.toDouble()).toFloat()
    Log.d("getRealScale", "getRealScale: $rScale")
    return rScale
}

fun Matrix.getTransX() : Float {
    // calculate real scale
    val v = FloatArray(9)
    getValues(v)
    return v[Matrix.MTRANS_X]
}

fun Matrix.getTransY() : Float {
    // calculate real scale
    val v = FloatArray(9)
    getValues(v)
    return v[Matrix.MTRANS_Y]
}


fun Matrix.getRealScaleY() : Float {
    // calculate real scale
    val v = FloatArray(9)
    getValues(v)
    val scaleY = v[Matrix.MSCALE_Y]
    val skewX = v[Matrix.MSKEW_X]
    val rScale =
        sqrt(scaleY * scaleY + skewX * skewX.toDouble()).toFloat()
    Log.d("getRealScale", "getRealScale: $rScale")
    return rScale
}

fun Matrix.new() : FloatArray {
    val valuesBase = FloatArray(9)
    getValues(valuesBase)

    val newValues = FloatArray(9)
    newValues[Matrix.MTRANS_Y] = valuesBase[Matrix.MTRANS_Y]
    newValues[Matrix.MTRANS_X] = valuesBase[Matrix.MTRANS_X]
    newValues[Matrix.MSCALE_X] = 1f
    newValues[Matrix.MSCALE_Y] = 1f

    return newValues
}

fun <T>ArrayList<T>.previous(_index: Int): T {
    val index = _index -1
    return if (index < 0) {
        last()
    } else {
        get(index)
    }
}
