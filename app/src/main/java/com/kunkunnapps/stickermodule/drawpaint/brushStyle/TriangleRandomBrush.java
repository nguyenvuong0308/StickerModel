package com.kunkunnapps.stickermodule.drawpaint.brushStyle;

import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;

import androidx.core.internal.view.SupportMenu;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.mmosoft.photocollage.view.drawlib.Drawing;
import com.kunkunnapps.stickermodule.drawpaint.bean.DrawingBrushProperty;

import java.util.ArrayList;

public class TriangleRandomBrush implements Drawing {
    private static Path mPath;
    ArrayList<Options> arrayOptions;
    private int brushType;
    private boolean isDrawing;
    private boolean isFromPreview;
    private PointF lastPoint;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private float mX;
    private float mY;
    private ArrayList<DrawingBrushProperty> pathsList;
    private ArrayList<PointF> points;

    private class Options {
        private int angle;
        private int color;
        private int linewidth;
        private int opacity;
        private PointF points;
        private float scale;
        private int starHeight;
        private int starWidht;

        private Options() {
            this.points = new PointF();
        }
    }

    static {
        mPath = new Path();
    }

    public TriangleRandomBrush() {
        this.points = new ArrayList();
        this.pathsList = new ArrayList();
        this.arrayOptions = new ArrayList();
        this.mBitmapPaint = new Paint();
        this.mPaint = new Paint();
        this.mBitmapPaint.setColor(SupportMenu.CATEGORY_MASK);
    }

    private void drawStar(Canvas ctx, Options options) {
        if (options != null) {
            ctx.save();
            ctx.translate(options.points.x, options.points.y);
            this.mPaint.setColor(options.color);
            this.mPaint.setStyle(Style.FILL_AND_STROKE);
            this.mPaint.setStrokeWidth((float) options.linewidth);
            this.mPaint.setStrokeCap(Cap.ROUND);
            this.mPaint.setAlpha(options.opacity);
            ctx.scale(options.scale, options.scale);
            ctx.rotate((float) (options.angle * 0));
            for (int i = 5; i > 0; i--) {
                ctx.drawPath(drawTriangle(options), this.mPaint);
            }
            ctx.restore();
        }
    }

    private Path drawTriangle(Options options) {
        int width = options.starWidht;
        int height = options.starHeight;
        Path path = new Path();
        double bigB = (double) Math.min(width, height);
        Rect r = new Rect(0, 0, width, height);
        int topXPoint = r.left + (width / 2);
        int topYPoint = r.top;
        path.moveTo((float) topXPoint, (float) topYPoint);
        path.lineTo((float) ((width / 2) + topXPoint), (float) ((int) (((double) topYPoint) + bigB)));
        path.lineTo((float) (topXPoint - (width / 2)), (float) ((int) (((double) topYPoint) + bigB)));
        path.lineTo((float) topXPoint, (float) topYPoint);
        path.close();
        return path;
    }

    private Path drawStar(Options options) {
        int width = options.starWidht;
        int height = options.starHeight;
        Path path = new Path();
        int minDim = Math.min(width, height);
        Rect rect = new Rect(0, 0, width, height);
        double bigB = (double) minDim;
        double bigA = Math.tan(Math.toRadians(18.0d)) * bigB;
        double littleHypot = (((double) minDim) / Math.cos(Math.toRadians(18.0d))) / ((2.0d + Math.cos(Math.toRadians(72.0d))) + Math.cos(Math.toRadians(72.0d)));
        double littleA = Math.cos(Math.toRadians(72.0d)) * littleHypot;
        double littleB = Math.sin(Math.toRadians(72.0d)) * littleHypot;
        int topXPoint = rect.left + (width / 2);
        int topYPoint = rect.top;
        path.moveTo((float) topXPoint, (float) topYPoint);
        path.lineTo((float) ((int) (((double) topXPoint) + bigA)), (float) ((int) (((double) topYPoint) + bigB)));
        path.lineTo((float) ((int) ((((double) topXPoint) - littleA) - littleB)), (float) ((int) (((double) topYPoint) + littleB)));
        path.lineTo((float) ((int) ((((double) topXPoint) + littleA) + littleB)), (float) ((int) (((double) topYPoint) + littleB)));
        path.lineTo((float) ((int) (((double) topXPoint) - bigA)), (float) ((int) (((double) topYPoint) + bigB)));
        path.lineTo((float) topXPoint, (float) topYPoint);
        path.close();
        return path;
    }

    private void addRandomPoint(float x, float y) {
        Options options = new Options();
        options.points.x = x;
        options.points.y = y;
        options.angle = (int) getRandomInt(0, 180);
        options.linewidth = (int) getRandomInt(10, 50);
        options.opacity = (int) getRandomInt(30, MotionEventCompat.ACTION_MASK);
        options.scale = (float) ((int) (getRandomInt(1, 20) / 10.0d));
        options.color = Color.rgb((int) getRandomInt(0, MotionEventCompat.ACTION_MASK), (int) getRandomInt(0, MotionEventCompat.ACTION_MASK), (int) getRandomInt(0, MotionEventCompat.ACTION_MASK));
        int randomInt = (int) getRandomInt(50, 100);
        options.starWidht = randomInt;
        options.starHeight = randomInt;
        this.arrayOptions.add(options);
    }

    public double getRandomInt(int min, int max) {
        return Math.floor(Math.random() * ((double) ((max - min) + 1))) + ((double) min);
    }

    public void touch_start(MotionEvent e, float x, float y) {
        PointF point = new PointF();
        point.x = x;
        point.y = y;
        this.points.add(point);
        mPath.moveTo(point.x, point.y);
        this.pathsList.add(new DrawingBrushProperty(mPath, 2, ViewCompat.MEASURED_STATE_MASK));
        addRandomPoint(x, y);
    }

    public void touch_move(MotionEvent e) {
        addRandomPoint(e.getX(), e.getY());
    }

    public void touch_move(float x, float y) {
        addRandomPoint(x, y);
    }

    public void touch_up() {
    }

    public void onDrawBrush(Canvas canvas) {
        for (int i = 0; i < this.arrayOptions.size(); i++) {
            drawStar(canvas, (Options) this.arrayOptions.get(i));
        }
    }

    public void onTouch(MotionEvent event) {
        setFromPreview(false);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(event, x, y);
            case MotionEvent.ACTION_UP:
                touch_up();
            case MotionEvent.ACTION_MOVE:
                touch_move(event);
            default:
        }
    }

    public void setStrokeWidth(float width) {
        this.mPaint.setStrokeWidth(width);
    }

    public void setColor(int color) {
        this.mPaint.setColor(color);
    }

    public void setBrushCap(Cap cap) {
        this.mPaint.setStrokeCap(cap);
    }

    public void setAlpha(int alpha) {
    }

    public void setBlurStyle(Blur style) {
        this.mPaint.setStyle(Style.FILL_AND_STROKE);
    }

    public void setRadius(float radius) {
    }

    public void setStrokeJoin(Join join) {
    }

    public int getStrokeWidth() {
        return 0;
    }

    public int getColor() {
        return 0;
    }

    public Cap getBrushCap() {
        return null;
    }

    public int getAlpha() {
        return 0;
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


    private void touch_start(int x, int y) {
        PointF point = new PointF();
        point.x = (float) x;
        point.y = (float) y;
        this.points.add(point);
        mPath.moveTo(point.x, point.y);
        this.pathsList.add(new DrawingBrushProperty(mPath, 2, ViewCompat.MEASURED_STATE_MASK));
        addRandomPoint((float) x, (float) y);
    }

    public int getBrushType() {
        return this.brushType;
    }

    public void setBrushType(int brushType) {
        this.brushType = brushType;
    }

    public PointF getLastPoint() {
        return this.lastPoint;
    }

    public void setLastPoint(PointF lastPoint) {
        this.lastPoint = lastPoint;
    }

    public boolean isDrawing() {
        return this.isDrawing;
    }

    public void setDrawing(boolean isDrawing) {
        this.isDrawing = isDrawing;
    }

    public float getmX() {
        return this.mX;
    }

    public void setmX(float mX) {
        this.mX = mX;
    }

    public float getmY() {
        return this.mY;
    }

    public void setmY(float mY) {
        this.mY = mY;
    }

    public boolean isFromPreview() {
        return this.isFromPreview;
    }

    public void setFromPreview(boolean isFromPreview) {
        this.isFromPreview = isFromPreview;
    }
}
