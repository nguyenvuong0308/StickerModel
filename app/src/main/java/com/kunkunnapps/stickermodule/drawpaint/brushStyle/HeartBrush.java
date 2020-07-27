package com.kunkunnapps.stickermodule.drawpaint.brushStyle;

import android.graphics.Bitmap;
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


public class HeartBrush implements Drawing {
    private static Path mPath;
    ArrayList<Options> arrayOptions;
    private Bitmap bitmap;
    private int brushType;
    private boolean isDrawing;
    private boolean isFill;
    private PointF lastPoint;
    private Paint mBitmapPaint;
    private int mHeight;
    private Paint mPaint;
    private int mWidth;
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

    public HeartBrush(int mWidth, int mHeight) {
        this.pathsList = new ArrayList();
        this.arrayOptions = new ArrayList();
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        mPath = new Path();
        this.mBitmapPaint = new Paint();
        this.mPaint = new Paint();
        this.mBitmapPaint.setColor(SupportMenu.CATEGORY_MASK);
        this.mPaint.setStyle(Style.FILL_AND_STROKE);
        this.mPaint.setStrokeWidth(1.0f);
        this.mPaint.setStrokeCap(Cap.ROUND);
        this.points = new ArrayList();
    }

    public void drawHeartEffect(Canvas canvas) {
    }

    private void drawHeart(Canvas ctx, Options options) {
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
            for (int i = 8; i > 0; i--) {
                ctx.drawPath(drawHeart(options), this.mPaint);
            }
            ctx.restore();
        }
    }

    private Path drawHeart(Options options) {
        int width = options.starWidht;
        int height = options.starHeight;
        Path path = new Path();
        Rect r = new Rect(0, 0, width, height);
        float wScale = ((((float) width) * 1.0f) / 130.0f) * 1.0f;
        float hScale = ((((float) height) * 1.0f) / 120) * 1.0f;
        path.moveTo(((float) r.left) + (65.0f * wScale), ((float) r.top) + (20.0f * hScale));
        path.cubicTo(((float) r.left) + (65.0f * wScale), ((float) r.top) + (17.0f * hScale), ((float) r.left) + (60 * wScale), ((float) r.top) + (5.0f * hScale), ((float) r.left) + (45.0f * wScale), ((float) r.top) + (5.0f * hScale));
        path.cubicTo(((float) r.left) + (0.0f * wScale), ((float) r.top) + (5.0f * hScale), ((float) r.left) + (0.0f * wScale), ((float) r.top) + (42.5f * hScale), ((float) r.left) + (0.0f * wScale), ((float) r.top) + (42.5f * hScale));
        path.cubicTo(((float) r.left) + (0.0f * wScale), ((float) r.top) + (80.0f * hScale), ((float) r.left) + (20.0f * wScale), ((float) r.top) + (102.0f * hScale), ((float) r.left) + (65.0f * wScale), ((float) r.top) + (120 * hScale));
        path.cubicTo(((float) r.left) + (110.0f * wScale), ((float) r.top) + (102.0f * hScale), ((float) r.left) + (130.0f * wScale), ((float) r.top) + (80.0f * hScale), ((float) r.left) + (130.0f * wScale), ((float) r.top) + (42.5f * hScale));
        path.cubicTo(((float) r.left) + (130.0f * wScale), ((float) r.top) + (42.5f * hScale), ((float) r.left) + (130.0f * wScale), ((float) r.top) + (5.0f * hScale), ((float) r.left) + (90.0f * wScale), ((float) r.top) + (5.0f * hScale));
        path.cubicTo(((float) r.left) + (75.0f * wScale), ((float) r.top) + (5.0f * hScale), ((float) r.left) + (65.0f * wScale), ((float) r.top) + (17.0f * hScale), ((float) r.left) + (65.0f * wScale), ((float) r.top) + (20.0f * hScale));
        path.close();
        return path;
    }

    private void addRandomPoint(MotionEvent e) {
        Options options = new Options();
        options.points.x = e.getX();
        options.points.y = e.getY();
        options.angle = (int) getRandomInt(0, 180);
        options.linewidth = (int) getRandomInt(1, 50);
        options.opacity = (int) getRandomInt(250, MotionEventCompat.ACTION_MASK);
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
        addRandomPoint(e);
    }

    public void touch_move(MotionEvent e) {
        addRandomPoint(e);
    }

    public void touch_up() {
    }

    public void onDrawBrush(Canvas canvas) {
        for (int i = 0; i < this.arrayOptions.size(); i++) {
            drawHeart(canvas, (Options) this.arrayOptions.get(i));
        }
    }

    public void onTouch(MotionEvent event) {
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
    }

    public void setColor(int color) {
        this.mPaint.setColor(color);
    }

    public void setBrushCap(Cap cap) {
    }

    public void setAlpha(int alpha) {
    }

    public void setBlurStyle(Blur style) {
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

    public Bitmap getPreviewCanvasBitmap() {
        return null;
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

    public boolean isDrawing() {
        return this.isDrawing;
    }

    public void setDrawing(boolean isDrawing) {
        this.isDrawing = isDrawing;
    }

    public boolean isFill() {
        return this.isFill;
    }

    public void setFill(boolean isFill) {
        this.isFill = isFill;
    }
}
