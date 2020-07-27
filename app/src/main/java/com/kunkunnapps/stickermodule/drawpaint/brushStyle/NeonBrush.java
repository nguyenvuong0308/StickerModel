package com.kunkunnapps.stickermodule.drawpaint.brushStyle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.MotionEvent;

import com.mmosoft.photocollage.view.drawlib.Drawing;

public class NeonBrush implements Drawing {
    private int brushType;
    private boolean isFromPreview;
    private Cap mCap;
    private int mColor;
    private Join mJoin;
    private int mOpacity;
    private Paint mPaint;
    private Paint mPaintInner;
    private Paint mPaintOuter;
    private Paint mPaintSolid;
    private Path mPath;
    private int mStrokeWidth;
    private int mStrokeWidth1;
    private Blur mStyle;
    private int mX;
    private int mY;
    private int radius;

    public NeonBrush() {
        this.mPath = new Path();
        this.mPaint = new Paint();
        this.mPaint.setStyle(Style.STROKE);
    }

    @Override
    public void onDrawBrush(Canvas canvas) {
        this.mPaintInner = new Paint();
        this.mPaintInner.setStrokeWidth((float) this.mStrokeWidth);
        this.mPaintInner.setAlpha(this.mOpacity);
        this.mPaintInner.setColor(-1);
        this.mPaintInner.setAntiAlias(true);
        this.mPaintInner.setDither(true);
        this.mPaintInner.setStyle(Style.STROKE);
        this.mPaintInner.setStrokeJoin(Join.ROUND);
        this.mPaintOuter = new Paint();
        this.mPaintOuter.setStrokeWidth((float) this.mStrokeWidth);
        this.mPaintOuter.setAlpha(this.mOpacity);
        this.mPaintOuter.setColor(-1);
        this.mPaintOuter.setAntiAlias(true);
        this.mPaintOuter.setDither(true);
        this.mPaintOuter.setStyle(Style.STROKE);
        this.mPaintOuter.setStrokeJoin(Join.ROUND);
        this.mPaintSolid = new Paint();
        this.mPaintSolid.setStrokeWidth((float) this.mStrokeWidth);
        this.mPaintSolid.setAlpha(this.mOpacity);
        this.mPaintSolid.setColor(-1);
        this.mPaintSolid.setAntiAlias(true);
        this.mPaintSolid.setDither(true);
        this.mPaintSolid.setStyle(Style.STROKE);
        this.mPaintSolid.setStrokeJoin(Join.ROUND);
        this.mPaintInner.setMaskFilter(new BlurMaskFilter(15.0f, Blur.INNER));
        this.mPaintOuter.setMaskFilter(new BlurMaskFilter(15.0f, Blur.OUTER));
        this.mPaintSolid.setMaskFilter(new BlurMaskFilter(15.0f, Blur.SOLID));
        this.mPaint.setAntiAlias(true);
        this.mPaint.setShadowLayer(30.0f, 10.0f, 5.0f, -1);
        canvas.drawPath(this.mPath, this.mPaint);
    }

    @Override
    public void onTouch(MotionEvent event) {
        setFromPreview(false);
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
            case MotionEvent.ACTION_UP:
                touchUp(x, y);
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
            default:
        }
    }

    private void touchUp(float x, float y) {
        this.mPath.lineTo((float) this.mX, (float) this.mY);
    }

    private void touch_move(float x, float y) {
        this.mPath.quadTo((float) this.mX, (float) this.mY, (((float) this.mX) + x) / 2.0f, (((float) this.mY) + y) / 2.0f);
        this.mX = (int) x;
        this.mY = (int) y;
    }

    private void touch_start(float x, float y) {
        this.mPath.reset();
        this.mPath.moveTo(x, y);
        this.mX = (int) x;
        this.mY = (int) y;
    }

    public void setStrokeWidth(float width) {
        this.mStrokeWidth = (int) width;
        this.mStrokeWidth1 = (int) width;
        this.mPaint.setStrokeWidth((float) this.mStrokeWidth1);
    }

    public void setColor(int color) {
        this.mColor = color;
        this.mPaint.setColor(this.mColor);
    }

    public void setBrushCap(Cap cap) {
        setmCap(cap);
    }

    public void setBlurStyle(Blur style) {
        setmStyle(style);
    }

    public void setRadius(float radius) {
        this.radius = (int) radius;
    }

    public void setStrokeJoin(Join join) {
        setmJoin(join);
    }

    public void setAlpha(int alpha) {
        this.mOpacity = alpha;
        this.mPaint.setAlpha(alpha);
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
        return this.mOpacity;
    }

    public Style getBlurStyle() {
        return null;
    }

    public float getRadius() {
        return (float) this.radius;
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

    public int getmOpacity() {
        return this.mOpacity;
    }

    public void setmOpacity(int mOpacity) {
        this.mOpacity = mOpacity;
    }

    public Cap getmCap() {
        return this.mCap;
    }

    public void setmCap(Cap mCap) {
        this.mCap = mCap;
    }

    public Blur getmStyle() {
        return this.mStyle;
    }

    public void setmStyle(Blur mStyle) {
        this.mStyle = mStyle;
    }

    public Join getmJoin() {
        return this.mJoin;
    }

    public void setmJoin(Join mJoin) {
        this.mJoin = mJoin;
    }

    public boolean isFromPreview() {
        return this.isFromPreview;
    }

    public void setFromPreview(boolean isFromPreview) {
        this.isFromPreview = isFromPreview;
    }
}
