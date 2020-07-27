package com.kunkunnapps.stickermodule.drawpaint.brushStyle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.MotionEvent;

import com.mmosoft.photocollage.view.drawlib.Drawing;

public class NormalBrush implements Drawing {
    private int mAlpha;
    private Style mBlurStyle;
    private int mBrushType;
    private Cap mCap;
    private int mColor;
    private Join mJoin;
    private Paint mPaintLine;
    private Path mPath;
    private int mRadius;
    private int mStrokeWidth;
    private int mStrokeWidth1;
    private float mX;
    private float mY;

    public NormalBrush() {
        this.mPath = new Path();
        this.mPaintLine = new Paint();
        this.mPaintLine.setAntiAlias(true);
        this.mPaintLine.setDither(true);
        this.mPaintLine.setStyle(Style.STROKE);
        this.mPaintLine.setMaskFilter(null);
    }

    public void onDrawBrush(Canvas canvas) {
        canvas.drawPath(this.mPath, this.mPaintLine);
    }

    public void onTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
            case MotionEvent.ACTION_UP:
                touch_up(x, y);
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
            default:
        }
    }

    private void touch_up(float x, float y) {
        this.mPath.lineTo(this.mX, this.mY);
    }

    private void touch_move(float x, float y) {
        this.mPath.quadTo(this.mX, this.mY, (this.mX + x) / 2.0f, (this.mY + y) / 2.0f);
        this.mX = x;
        this.mY = y;
    }

    private void touch_start(float x, float y) {
        this.mPath.reset();
        this.mPath.moveTo(x, y);
        this.mX = x;
        this.mY = y;
    }

    public void setStrokeWidth(float width) {
        this.mStrokeWidth = (int) width;
        this.mStrokeWidth1 = ((int) width) + 3;
        this.mPaintLine.setStrokeWidth((float) this.mStrokeWidth1);
    }

    public void setColor(int color) {
        this.mColor = color;
        this.mPaintLine.setColor(color);
    }

    public void setBrushCap(Cap cap) {
        this.mPaintLine.setStrokeCap(cap);
    }

    public void setAlpha(int alpha) {
        this.mPaintLine.setAlpha(alpha);
    }

    public void setBlurStyle(Blur style) {
    }

    public void setRadius(float radius) {
    }

    public void setStrokeJoin(Join join) {
        this.mPaintLine.setStrokeJoin(join);
    }

    public int getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public int getColor() {
        return this.mColor;
    }

    public Cap getBrushCap() {
        return this.mCap;
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public Style getBlurStyle() {
        return this.mBlurStyle;
    }

    public float getRadius() {
        return (float) this.mRadius;
    }

    public Join getStrokeJoin() {
        return this.mJoin;
    }

    public int getBrushType() {
        return this.mBrushType;
    }

    public void setBrushType(int brushType) {
        this.mBrushType = brushType;
    }
}
