package com.sony.dpt.drawing.eraser;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.drawing.AbstractDrawingDelegate;
import com.sony.dpt.drawing.geom.Point2D;
import com.sony.dpt.drawing.rendering.DrawingThread;
import com.sony.dpt.drawing.strokes.StrikeDelegate;
import com.sony.dpt.override.UpdateMode;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public abstract class AbstractEraserDelegate extends AbstractDrawingDelegate implements EraserDelegate {

    protected static final int CIRCLE_STROKE_WIDTH = 3;
    protected int eraserRadius;
    protected final RectF finalEraseInvalidationRectangle;
    protected final RectF invalidationRectangle;
    protected final RectF temp;
    // This will be use to repaint the circle white
    protected final RectF previousInvalidationRectangle;
    protected Paint eraserPaint;
    protected Paint circlePaint;
    protected final DrawingThread drawingThread;
    // This will be use to repaint the circle white
    protected boolean isErasing = false;
    private final PointF lastCircle;
    protected Paint circleErasePaint;
    protected boolean hasErasedBefore = false;

    protected AbstractEraserDelegate(
            int eraserRadius,
            View view,
            Bitmap cachedLayer,
            Canvas drawCanvas,
            final DrawingThread drawingThread) {
        super(view, cachedLayer, drawCanvas);

        this.eraserRadius = eraserRadius;
        this.finalEraseInvalidationRectangle = new RectF();
        this.invalidationRectangle = new RectF();
        this.previousInvalidationRectangle = new RectF();
        this.temp = new RectF();
        this.drawingThread = drawingThread;
        this.lastCircle = new PointF();
        init();
    }

    protected void init() {
        eraserPaint = new Paint();
        eraserPaint.setAntiAlias(false);
        eraserPaint.setColor(Color.WHITE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(false);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(CIRCLE_STROKE_WIDTH);

        circleErasePaint = new Paint();
        circleErasePaint.setAntiAlias(false);
        circleErasePaint.setColor(Color.WHITE);
        circleErasePaint.setStyle(Paint.Style.STROKE);
        circleErasePaint.setStrokeWidth(CIRCLE_STROKE_WIDTH);
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

    protected void expandInvalidation(RectF rect) {
        invalidationRectangle.union(rect);
        finalEraseInvalidationRectangle.union(rect);
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
                setRectToCurrentPoint(invalidationRectangle);
                break;
            case ACTION_CANCEL:
            case ACTION_UP:
                isErasing = false;
                handleMotion(event);
                finalEraseInvalidationRectangle.inset(-eraserRadius - 2, -eraserRadius - 2);
                drawingThread.enqueueArea(finalEraseInvalidationRectangle, UpdateMode.UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_SP2);
                finalEraseInvalidationRectangle.setEmpty();
                previousInvalidationRectangle.setEmpty();
                invalidationRectangle.setEmpty();
                hasErasedBefore = false;
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

        previousInvalidationRectangle.set(invalidationRectangle);
        invalidationRectangle.set(temp);

        drawCanvas.drawCircle(lastX, lastY, eraserRadius, eraserPaint);
        if (isErasing && hasErasedBefore && Point2D.distance(lastCircle, lastPosition()) > 1) {
            eraseLastCircle();
            drawErasingCircle();

        } else if (!hasErasedBefore && isErasing) {
            drawErasingCircle();
        } else if (hasErasedBefore && !isErasing) {
            eraseLastCircle();
        }

    }

    private void eraseLastCircle() {
        drawCanvas.drawCircle(lastCircle.x, lastCircle.y, eraserRadius + 1, circleErasePaint);
    }

    private void drawErasingCircle() {
        drawCanvas.drawCircle(lastX, lastY, eraserRadius + 1, circlePaint);
        hasErasedBefore = true;
        lastCircle.set(lastX, lastY);
    }

    @Override
    public boolean nativeDhw() {
        return false;
    }

    @Override
    public void setStrikeDelegate(StrikeDelegate strikeDelegate) {
    }
}
