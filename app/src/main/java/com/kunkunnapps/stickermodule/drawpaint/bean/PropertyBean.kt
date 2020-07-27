package com.kunkunnapps.stickermodule.drawpaint.bean

class PropertyBean {

    private var alpha: Int = 0
    private var blurstyle: Int = 0
    private var color: Int = 0
    private var radius: Int = 0
    private var stokewidth: Int = 0

    fun getStokewidth(): Int {
        return this.stokewidth
    }

    fun setStokewidth(stokewidth: Int) {
        this.stokewidth = stokewidth
    }

    fun getColor(): Int {
        return this.color
    }

    fun setColor(color: Int) {
        this.color = color
    }

    fun getAlpha(): Int {
        return this.alpha
    }

    fun setAlpha(alpha: Int) {
        this.alpha = alpha
    }

    fun getBrushStyle(): Int {
        return this.blurstyle
    }

    fun setBrushStyle(brushstyle: Int) {
        this.blurstyle = brushstyle
    }

    fun getRadius(): Int {
        return this.radius
    }

    fun setRadius(radius: Int) {
        this.radius = radius
    }
}