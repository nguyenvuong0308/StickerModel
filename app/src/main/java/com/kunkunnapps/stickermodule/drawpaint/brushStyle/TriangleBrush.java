package com.kunkunnapps.stickermodule.drawpaint.brushStyle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathDashPathEffect.Style;
import android.view.MotionEvent;

import com.mmosoft.photocollage.view.drawlib.Drawing;

public class TriangleBrush implements Drawing {
    private int brushType;
    private boolean isFromPreview;
    private Cap mCap;
    private int mColor;
    private Join mJoin;
    private int mOpacity;
    private Paint mPaint;
    private Path mPath;
    private int mStrokeWidth;
    private int mStrokeWidth1;
    private Blur mStyle;
    private int mX;
    private int mY;
    private float radius;

    public TriangleBrush() {
        this.mPath = new Path();
        this.mPaint = new Paint();
    }

    public void onDrawBrush(Canvas canvas) {
        this.mPaint.setPathEffect(new PathDashPathEffect(getTriangle((float) this.mStrokeWidth1), (float) this.mStrokeWidth1, 0.0f, Style.ROTATE));
        canvas.drawPath(this.mPath, this.mPaint);
    }

    private Path getTriangle(float size) {
        Path path = new Path();
        float half = size / 2.0f;
        path.moveTo(-half, -half);
        path.lineTo(half, -half);
        path.lineTo(0.0f, half);
        path.close();
        return path;
    }

    public void onTouch(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        switch (e.getAction()) {
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
        this.mStrokeWidth1 = ((int) width) + 3;
        this.mPaint.setStrokeWidth((float) this.mStrokeWidth1);
    }

    public void setColor(int color) {
        this.mColor = color;
        this.mPaint.setColor(this.mColor);
    }

    public void setBrushCap(Cap cap) {
        this.mCap = cap;
        this.mPaint.setStrokeCap(this.mCap);
    }

    public void setBlurStyle(Blur style) {
        setmStyle(style);
    }

    public void setRadius(float radius) {
        this.radius = (float) ((int) radius);
    }

    public void setStrokeJoin(Join join) {
        setmJoin(join);
    }

    public void setAlpha(int alpha) {
        this.mOpacity = alpha;
        this.mPaint.setAlpha(this.mOpacity);
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

    public Paint.Style getBlurStyle() {
        return null;
    }

    public float getRadius() {
        return this.radius;
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
