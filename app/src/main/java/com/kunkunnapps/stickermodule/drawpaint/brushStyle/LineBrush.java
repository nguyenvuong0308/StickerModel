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

public class LineBrush implements Drawing {
    public static final int DIMEN_LONG = 490;
    private int brushType;
    private boolean isDrawing;
    private boolean isFromPreview;
    private PointF lastPoint;
    private Blur mBlur;
    private Cap mCap;
    private int mColor;
    private Join mJoin;
    private int mOpacity;
    private Paint mPaint1;
    private Path mPath;
    private int mRadius;
    private float mStrokeWidth;
    private float mStrokeWidth1;

    public LineBrush() {
        this.mPath = new Path();
        this.mPaint1 = new Paint();
        this.mPaint1.setStyle(Style.STROKE);
        this.mPaint1.setStrokeCap(Cap.BUTT);
    }

    public void touch_start(float x, float y) {
        this.isDrawing = true;
        this.lastPoint = new PointF(x, y);
    }

    public void touch_move(float x, float y) {
        if (this.isDrawing) {
            this.mPath.moveTo(this.lastPoint.x, this.lastPoint.y);
            this.mPath.lineTo(x, y);
            this.mPath.moveTo(this.lastPoint.x - (this.mStrokeWidth / 2.0f), this.lastPoint.y - (this.mStrokeWidth / 2.0f));
            this.mPath.lineTo(x - (this.mStrokeWidth / 2.0f), y - (this.mStrokeWidth / 2.0f));
            this.lastPoint = new PointF(x, y);
        }
    }

    public void touch_up(float x, float y) {
        this.isDrawing = false;
    }

    public void onDrawBrush(Canvas canvas) {
        canvas.drawPath(this.mPath, this.mPaint1);
    }

    public void onTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
            default:
        }
    }

    public void setStrokeWidth(float width) {
        this.mStrokeWidth = width;
        this.mStrokeWidth1 = (float) (((int) width) + 3);
        this.mPaint1.setStrokeWidth(this.mStrokeWidth1);
    }

    public void setColor(int color) {
        this.mColor = color;
        this.mPaint1.setColor(color);
    }

    public void setBrushCap(Cap cap) {
    }

    public void setAlpha(int alpha) {
        this.mOpacity = alpha;
        this.mPaint1.setAlpha(alpha);
    }

    public void setBlurStyle(Blur style) {
    }

    public void setRadius(float radius) {
        this.mRadius = (int) radius;
    }

    public void setStrokeJoin(Join join) {
    }

    public int getStrokeWidth() {
        return (int) this.mStrokeWidth;
    }

    public int getColor() {
        return this.mColor;
    }

    public Cap getBrushCap() {
        return this.mCap;
    }

    public int getAlpha() {
        return this.mOpacity;
    }

    public Join getStrokeJoin() {
        return this.mJoin;
    }

    public Style getBlurStyle() {
        return null;
    }

    public float getRadius() {
        return (float) this.mRadius;
    }

    public int getBrushType() {
        return this.brushType;
    }

    public void setBrushType(int brushType) {
        this.brushType = brushType;
    }

    public int getmRadius() {
        return this.mRadius;
    }

    public void setmRadius(int mRadius) {
        this.mRadius = mRadius;
    }

    public Blur getmBlur() {
        return this.mBlur;
    }

    public void setmBlur(Blur mBlur) {
        this.mBlur = mBlur;
    }

    public boolean isFromPreview() {
        return this.isFromPreview;
    }

    public void setFromPreview(boolean isFromPreview) {
        this.isFromPreview = isFromPreview;
    }
}
