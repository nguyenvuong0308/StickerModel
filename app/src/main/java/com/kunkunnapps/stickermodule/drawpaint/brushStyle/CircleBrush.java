package com.kunkunnapps.stickermodule.drawpaint.brushStyle;

import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.view.MotionEvent;

import androidx.core.view.MotionEventCompat;


import com.mmosoft.photocollage.view.drawlib.Drawing;
import com.kunkunnapps.stickermodule.drawpaint.bean.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CircleBrush implements Drawing {
    public static boolean isFromPreview;
    private int alphaValue;
    private int brushType;
    private int mAlpha;
    private Style mBlurStyle;
    private Cap mCap;
    private int mColor;
    private Join mJoin;
    private List<Coordinates> mListCircle;
    private Paint mPaint;
    private int mRadius;
    private int mRadius1;
    private int mStrokeWidth;
    private Random random;

    public CircleBrush() {
        this.mListCircle = new ArrayList();
        this.random = new Random();
        this.mPaint = new Paint();
    }

    public void touch_start(float x, float y) {
        this.mListCircle.add(new Coordinates((int) x, (int) y, (int) getRandomInt(5, this.mRadius * 2), this.random.nextInt(MotionEventCompat.ACTION_MASK)));
    }

    public void touch_move(float x, float y) {
        this.mListCircle.add(new Coordinates((int) x, (int) y, (int) getRandomInt(5, this.mRadius * 2), this.random.nextInt(MotionEventCompat.ACTION_MASK)));
    }

    public double getRandomInt(int min, int max) {
        return Math.floor(Math.random() * ((double) ((max - min) + 1))) + ((double) min);
    }

    public void onDrawBrush(Canvas canvas) {
        for (Coordinates d : this.mListCircle) {
            this.mPaint.setAlpha(d.getOpacity());
            canvas.drawCircle((float) d.getMx(), (float) d.getMy(), (float) d.getRadius(), this.mPaint);
        }
    }

    public void onTouch(MotionEvent event) {
        isFromPreview = false;
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

    public void setColor(int color) {
        this.mPaint.setColor(color);
        this.mColor = color;
    }

    public void setBrushCap(Cap cap) {
        this.mPaint.setStrokeCap(cap);
        this.mCap = cap;
    }

    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
        this.mAlpha = alpha;
    }

    public void setBlurStyle(Blur style) {
    }

    public void setRadius(float radius) {
        if (this.mRadius > 5) {
            this.mRadius = 5;
        } else {
            this.mRadius = (int) radius;
        }
        this.mPaint.setStrokeWidth((float) this.mRadius);
    }

    public void setStrokeJoin(Join join) {
        this.mPaint.setStrokeJoin(join);
        this.mJoin = join;
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
        return this.brushType;
    }

    public void setBrushType(int brushType) {
        this.brushType = brushType;
    }

    public void setStrokeWidth(float width) {
        this.mStrokeWidth = (int) width;
    }
}
