package com.kunkunnapps.stickermodule

import android.graphics.Matrix
import android.graphics.PointF

class DrawingAction(
    var point: PointF,
    var bitmapUndo: BitmapUndo,
    var strokeColor: Int ?= null,
    var strokeSize
) {

    class BitmapUndo(
        var resourceId: Int,
        var matrix: Matrix
    )
}