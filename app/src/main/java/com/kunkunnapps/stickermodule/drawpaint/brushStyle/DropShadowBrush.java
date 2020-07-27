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

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.mmosoft.photocollage.view.drawlib.Drawing;


public class DropShadowBrush implements Drawing {
    private int brushType;
    private int globalAlpha1;
    private int globalAlpha2;
    private int globalAlpha3;
    private int globalAlpha4;
    private int globalAlpha5;
    private boolean isDrawing;
    private PointF lastPoint;
    private int mAlpha;
    private Blur mBlur;
    private Cap mCap;
    private int mColor;
    private Join mJoin;
    private int mOpacity;
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;
    private Paint mPaint4;
    private Paint mPaint5;
    private Path mPath1;
    private Path mPath2;
    private Path mPath3;
    private Path mPath4;
    private Path mPath5;
    private int mRadius;
    private float mStrokeWidth;
    private float mStrokeWidth1;

    public DropShadowBrush() {
        this.mPath1 = new Path();
        this.mPath2 = new Path();
        this.mPath3 = new Path();
        this.mPath4 = new Path();
        this.mPath5 = new Path();
        this.mColor = ViewCompat.MEASURED_STATE_MASK;
        this.mStrokeWidth = 10.0f;
        this.mStrokeWidth1 = 10.0f;
        this.mPaint1 = new Paint();
        this.mPaint1.setStyle(Style.STROKE);
        this.mPaint1.setStrokeCap(Cap.ROUND);
        this.mPaint2 = new Paint();
        this.mPaint2.setStyle(Style.STROKE);
        this.mPaint2.setStrokeCap(Cap.ROUND);
        this.mPaint3 = new Paint();
        this.mPaint3.setStyle(Style.STROKE);
        this.mPaint3.setStrokeCap(Cap.ROUND);
        this.mPaint4 = new Paint();
        this.mPaint4.setStyle(Style.STROKE);
        this.mPaint4.setStrokeCap(Cap.ROUND);
        this.mPaint5 = new Paint();
        this.mPaint5.setStyle(Style.STROKE);
        this.mPaint5.setStrokeCap(Cap.ROUND);
    }

    public void touch_start(float x, float y) {
        this.isDrawing = true;
        this.lastPoint = new PointF(x, y);
        this.mPath1.moveTo(x, y);
        this.mPath2.moveTo(x, y);
        this.mPath3.moveTo(x, y);
        this.mPath4.moveTo(x, y);
        this.mPath5.moveTo(x, y);
    }

    public void touch_move(float x, float y) {
        if (this.isDrawing) {
            this.globalAlpha1 = MotionEventCompat.ACTION_MASK;
            this.mPath1.moveTo(this.lastPoint.x - 8.0f, this.lastPoint.y - 8.0f);
            this.mPath1.lineTo(x - 8.0f, y - 8.0f);
            this.globalAlpha2 = 230;
            this.mPath2.moveTo(this.lastPoint.x - 2.0f, this.lastPoint.y - 2.0f);
            this.mPath2.lineTo(x - 2.0f, y - 2.0f);
            this.globalAlpha3 = 140;
            this.mPath3.moveTo(this.lastPoint.x, this.lastPoint.y);
            this.mPath3.lineTo(x, y);
            this.globalAlpha4 =130;
            this.mPath4.moveTo(this.lastPoint.x + 8.0f, this.lastPoint.y + 8.0f);
            this.mPath4.lineTo(x + 8.0f, y + 8.0f);
            this.globalAlpha5 = 120;
            this.mPath5.moveTo(this.lastPoint.x + 2.0f, this.lastPoint.y + 2.0f);
            this.mPath5.lineTo(x + 2.0f, y + 2.0f);
            this.lastPoint = new PointF(x, y);
        }
    }

    public Path getFinalPath() {
        return null;
    }

    public Paint getPaint() {
        return null;
    }

    public double getRandomInt(int min, int max) {
        return Math.floor(Math.random() * ((double) ((max - min) + 1))) + ((double) min);
    }

    public void onDrawBrush(Canvas canvas) {
        this.mPaint1.setAlpha(this.globalAlpha1);
        this.mPaint2.setAlpha(this.globalAlpha2);
        this.mPaint3.setAlpha(this.globalAlpha3);
        this.mPaint4.setAlpha(this.globalAlpha4);
        this.mPaint5.setAlpha(this.globalAlpha5);
        canvas.drawPath(this.mPath1, this.mPaint1);
        canvas.drawPath(this.mPath2, this.mPaint2);
        canvas.drawPath(this.mPath3, this.mPaint3);
        canvas.drawPath(this.mPath4, this.mPaint4);
        canvas.drawPath(this.mPath5, this.mPaint5);
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
        this.mStrokeWidth1 = width;
        this.mPaint1.setStrokeWidth(this.mStrokeWidth1);
        this.mPaint2.setStrokeWidth(this.mStrokeWidth1);
        this.mPaint3.setStrokeWidth(this.mStrokeWidth1);
        this.mPaint4.setStrokeWidth(this.mStrokeWidth1);
        this.mPaint5.setStrokeWidth(this.mStrokeWidth1);
    }

    public void setColor(int color) {
        this.mColor = color;
        this.mPaint1.setColor(color);
        this.mPaint2.setColor(color);
        this.mPaint3.setColor(color);
        this.mPaint4.setColor(color);
        this.mPaint5.setColor(color);
    }

    public void setBrushCap(Cap cap) {
    }

    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
    }

    public void setBlurStyle(Blur style) {
    }

    public void setRadius(float radius) {
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
        return 0.0f;
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
}
