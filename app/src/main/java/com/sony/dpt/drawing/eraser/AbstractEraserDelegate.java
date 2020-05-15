package com.sony.dpt.drawing.eraser;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.drawing.AbstractDrawingDelegate;
import com.sony.dpt.drawing.strokes.StrikeDelegate;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public abstract class AbstractEraserDelegate extends AbstractDrawingDelegate implements EraserDelegate {

    protected static final int CIRCLE_STROKE_WIDTH = 3;
    protected int eraserRadius;
    protected RectF finalEraseInvalidationRectangle;
    protected RectF invalidationRectangle;
    protected RectF temp;
    // This will be use to repaint the circle white
    protected RectF previousInvalidationRectangle;
    protected Paint eraserPaint;
    protected Paint circlePaint;
    // This will be use to repaint the circle white
    protected boolean isErasing = false;


    protected AbstractEraserDelegate(
            int eraserRadius,
            View view,
            Bitmap cachedLayer,
            Canvas drawCanvas) {
        super(view, cachedLayer, drawCanvas);

        this.eraserRadius = eraserRadius;
        this.finalEraseInvalidationRectangle = new RectF();
        this.invalidationRectangle = new RectF();
        this.previousInvalidationRectangle = new RectF();
        this.temp = new RectF();
        init();
    }

    protected void init() {
        eraserPaint = new Paint();
        eraserPaint.setAntiAlias(false);
        eraserPaint.setAlpha(0);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        circlePaint = new Paint();
        circlePaint.setAntiAlias(false);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(CIRCLE_STROKE_WIDTH);
    }

    protected void setRectToCurrentPoint(RectF rect) {
        rect.set(
                lastX,
                lastY,
                lastX,
                lastY
        );
        rect.inset(-eraserRadius - CIRCLE_STROKE_WIDTH, -eraserRadius - CIRCLE_STROKE_WIDTH);
    }

    protected void expandInvalidation(float x, float y) {
        invalidationRectangle.union((int) x, (int) y);
        finalEraseInvalidationRectangle.union((int) x, (int) y);
    }

    protected void expandInvalidation(RectF rect, float inset) {
        invalidationRectangle.union(rect);
        invalidationRectangle.inset(-inset, -inset);
        finalEraseInvalidationRectangle.union(rect);
        finalEraseInvalidationRectangle.inset(-inset, -inset);
    }

    @Override
    public void setPenWidth(int penWidth) {
        this.eraserRadius = penWidth;
    }

    @Override
    public int penWidth() {
        return eraserRadius;
    }

    @Override
    public int maxPenWidth() {
        return penWidth();
    }

    @Override
    public float getStrokeWidth() {
        return CIRCLE_STROKE_WIDTH;
    }

    @Override
    public boolean pressureSensitive() {
        return false;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isErasing) canvas.drawCircle(lastX, lastY, eraserRadius + 1, circlePaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        isErasing = true;
        switch (event.getActionMasked()) {
            case ACTION_DOWN:
                setRectToCurrentPoint(finalEraseInvalidationRectangle);
                setRectToCurrentPoint(invalidationRectangle);
                setRectToCurrentPoint(previousInvalidationRectangle);
                handleMotion(event);
                break;
            case ACTION_MOVE:
                handleMotion(event);
                invalidationRectangle.setEmpty();
                break;
            case ACTION_CANCEL:
            case ACTION_UP:
                isErasing = false;
                handleMotion(event);
                invalidatePartialGC16(finalEraseInvalidationRectangle);
                finalEraseInvalidationRectangle.setEmpty();
                previousInvalidationRectangle.setEmpty();
                invalidationRectangle.setEmpty();
                break;
        }
        return true;
    }

    protected void handleMotion(final MotionEvent event) {
        for (int i = 0; i < event.getHistorySize(); i++) {
            expandInvalidation(event.getHistoricalX(i), event.getHistoricalY(i));
        }
        expandInvalidation(event.getX(), event.getY());

        // We redraw the part that we drew black last time.
        temp.set(invalidationRectangle);
        temp.union(previousInvalidationRectangle);
        temp.inset(-eraserRadius - eraserPaint.getStrokeWidth(), -eraserRadius - eraserPaint.getStrokeWidth());
        previousInvalidationRectangle.set(invalidationRectangle);
        invalidationRectangle.set(temp);

        drawCanvas.drawCircle(lastX, lastY, eraserRadius, eraserPaint);
    }

    @Override
    public boolean nativeDhw() {
        return false;
    }

    @Override
    public void setStrikeDelegate(StrikeDelegate strikeDelegate) {
    }
}
