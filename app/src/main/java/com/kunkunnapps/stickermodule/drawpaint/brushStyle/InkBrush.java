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
import android.graphics.PointF;
import android.view.MotionEvent;

import com.mmosoft.photocollage.view.drawlib.Drawing;

public class InkBrush implements Drawing {
    private int brushType;
    private PointF lastPoint;
    private int mAplha;
    private int mColor;
    private Paint mPaint;
    private Path mPath;
    private int mStrokeWidth;
    private int mStrokeWidth1;

    public InkBrush() {
        this.mPath = new Path();
        this.mPaint = new Paint();
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeCap(Cap.ROUND);
    }

    public void touch_start(float x, float y) {
        this.lastPoint = new PointF(x, y);
        this.mPath.moveTo(x, y);
    }

    public void touch_move(float x, float y) {
        this.mPath.moveTo((float) (((double) this.lastPoint.x) - getRandomInt(0, this.mStrokeWidth1 + 1)), (float) (((double) this.lastPoint.y) - getRandomInt(0, this.mStrokeWidth1 + 1)));
        this.mPath.lineTo((float) (((double) x) - getRandomInt(0, this.mStrokeWidth1 + 1)), (float) (((double) y) - getRandomInt(0, this.mStrokeWidth1 + 1)));
        this.mPath.moveTo(this.lastPoint.x, this.lastPoint.y);
        this.mPath.lineTo(x, y);
        this.mPath.moveTo((float) (((double) this.lastPoint.x) + getRandomInt(0, this.mStrokeWidth1 + 1)), (float) (((double) this.lastPoint.y) + getRandomInt(0, this.mStrokeWidth1 + 1)));
        this.mPath.lineTo((float) (((double) x) + getRandomInt(0, this.mStrokeWidth1 + 1)), (float) (((double) y) + getRandomInt(0, this.mStrokeWidth1 + 1)));
        this.lastPoint = new PointF(x, y);
    }

    public void touch_up(float x, float y) {
        this.lastPoint = new PointF(x, y);
        this.mPath.lineTo(this.lastPoint.x, this.lastPoint.y);
    }

    public Paint getUpdatedPaint() {
        return this.mPaint;
    }

    public double getRandomInt(int min, int max) {
        return Math.floor(Math.random() * ((double) ((max - min) + 1))) + ((double) min);
    }

    public void onDrawBrush(Canvas canvas) {
        canvas.drawPath(this.mPath, this.mPaint);
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

    public void setStrokeWidth(float width) {
        this.mStrokeWidth = (int) width;
        if (width > 10.0f) {
            this.mStrokeWidth1 = 10;
        } else {
            this.mStrokeWidth1 = (int) width;
        }
        this.mPaint.setStrokeWidth((float) this.mStrokeWidth1);
    }

    public void setColor(int color) {
        this.mColor = color;
        this.mPaint.setColor(color);
    }

    public void setBrushCap(Cap cap) {
        this.mPaint.setStrokeCap(cap);
    }

    public void setAlpha(int alpha) {
        this.mAplha = alpha;
        this.mPaint.setAlpha(this.mAplha);
    }

    public void setBlurStyle(Blur style) {
        this.mPaint.setStyle(Style.STROKE);
    }

    public void setRadius(float radius) {
    }

    public void setStrokeJoin(Join join) {
    }

    public int getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public int getColor() {
        return this.mColor;
    }

    public Cap getBrushCap() {
        return null;
    }

    public int getAlpha() {
        return this.mAplha;
    }

    public Style getBlurStyle() {
        return null;
    }

    public float getRadius() {
        return 0.0f;
    }

    public Join getStrokeJoin() {
        return null;
    }

    public int getBrushType() {
        return this.brushType;
    }

    public void setBrushType(int brushType) {
        this.brushType = brushType;
    }
}
