package com.kunkunnapps.stickermodule

import android.content.Context
import android.graphics.*
import android.graphics.drawable.shapes.Shape
import android.util.Xml
import androidx.core.graphics.PathParser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.*

class VectorShape(context: Context, id: Int) :
    Shape() {
    private val viewportRect = RectF()
    private val layers: MutableList<Layer> = ArrayList()

    companion object {
        private const val TAG_VECTOR = "vector"
        private const val TAG_PATH = "path"
    }

    init {
        val parser = context.resources.getXml(id)
        val set = Xml.asAttributeSet(parser)
        try {
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    val tagName = parser.name
                    if (tagName == TAG_VECTOR) {
                        val attrs =
                            intArrayOf(android.R.attr.viewportWidth,android.R.attr.viewportHeight)
                        val ta = context.obtainStyledAttributes(set, attrs)
                        viewportRect[0f, 0f, ta.getFloat(0, 0f)] = ta.getFloat(1, 0f)
                        ta.recycle()
                    } else if (tagName == TAG_PATH) {
                        val attrs = intArrayOf(
                            android.R.attr.name,
                            android.R.attr.fillColor,
                            android.R.attr.pathData
                        )
                        val ta = context.obtainStyledAttributes(set, attrs)
                        layers.add(
                            Layer(
                                ta.getString(2),
                                ta.getColor(1, -0x21523f22),
                                ta.getString(0) ?: ""
                            )
                        )
                        ta.recycle()
                    }
                }
                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }



    fun getLayersAt(x: Int, y: Int, outLayers: Deque<Layer?>) {
        outLayers.clear()
        for (layer in layers) {
            if (layer.region.contains(x, y)) {
                outLayers.addLast(layer)
            }
        }
    }

    fun getLayersAt(index: Int): Layer? {
        return layers.getOrNull(index)
    }

    override fun onResize(width: Float, height: Float) {
        val matrix = Matrix()
        val shapeRegion =
            Region(0, 0, width.toInt(), height.toInt())
        matrix.setRectToRect(
            viewportRect,
            RectF(0f, 0f, width, height),
            Matrix.ScaleToFit.CENTER
        )
        for (layer in layers) {
            layer.transform(matrix, shapeRegion)
        }
    }

    override fun draw(
        canvas: Canvas,
        paint: Paint
    ) {
        for (layer in layers) {
            canvas.drawPath(layer.transformedPath, layer.paint)
        }
    }

    class Layer(data: String?, color: Int, name: String) {
        var color: Int
        var originalPath: Path
        var transformedPath = Path()
        var paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        var region = Region()
        var name: String
        var selected = false

        init {
            originalPath = PathParser.createPathFromPathData(data)
            this.color = color
            paint.color = color
            this.name = name
        }

        fun transform(
            matrix: Matrix?,
            clip: Region?
        ) {
            originalPath.transform(matrix, transformedPath)
            region.setPath(transformedPath, clip)
        }

        override fun toString(): String {
            return name
        }

        fun toggle() {
            selected = !selected
            paint.color = if (selected) Color.RED else color
        }


    }
}