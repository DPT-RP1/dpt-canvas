package com.sony.dpt.drawing.eraser;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

/**
 * This is in charge of erasing on a view
 * <p>
 * The best compromise is to erase quickly while the pen moves, summing the invalidation rectangles,
 * then pop a GC16 (Greyscale Clearing) partial at the end on an invalidation rectangle containing
 * everything cleared during the fast phase.
 */
public class BitmapEraserDelegate extends AbstractEraserDelegate implements EraserDelegate {

    private static final int CIRCLE_STROKE_WIDTH = 3;

    private Paint eraserPaint;

    private Paint circlePaint;
    private int eraserRadius;

    private Rect temp;
    // This will be use to repaint the circle white
    private Rect previousInvalidationRectangle;
    private boolean isErasing = false;

    public BitmapEraserDelegate(int eraserWidth, final View view, final Bitmap cachedLayer, final Canvas drawCanvas) {
        super(view, cachedLayer, drawCanvas);
        this.eraserRadius = eraserWidth;
        this.finalEraseInvalidationRectangle = new Rect();
        this.invalidationRectangle = new Rect();
        this.previousInvalidationRectangle = new Rect();
        this.temp = new Rect();
        init();
    }

    private void init() {
        eraserRadius = 20;
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

    private void handleMotion(final MotionEvent event) {
        for (int i = 0; i < event.getHistorySize(); i++) {
            addToInvalidate(event.getHistoricalX(i), event.getHistoricalY(i));
        }
        addToInvalidate(event.getX(), event.getY());

        // We redraw the part that we drew black last time.
        temp.set(invalidationRectangle);
        temp.union(previousInvalidationRectangle);

        temp.inset(
                -eraserRadius - CIRCLE_STROKE_WIDTH,
                -eraserRadius - CIRCLE_STROKE_WIDTH
        );
        previousInvalidationRectangle.set(invalidationRectangle);

        drawCanvas.drawCircle(lastX, lastY, eraserRadius, eraserPaint);

        invalidate(temp);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isErasing) canvas.drawCircle(lastX, lastY, eraserRadius + 1, circlePaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        epdUtil.setDhwState(false);
        isErasing = true;
        super.onTouchEvent(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setRectToCurrentPoint(finalEraseInvalidationRectangle);
                setRectToCurrentPoint(invalidationRectangle);
                setRectToCurrentPoint(previousInvalidationRectangle);
                handleMotion(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMotion(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleMotion(event);
                isErasing = false;
                invalidatePartialGC16(finalEraseInvalidationRectangle);
                finalEraseInvalidationRectangle.setEmpty();
                previousInvalidationRectangle.setEmpty();
                invalidationRectangle.setEmpty();
                break;
        }

        return true;
    }

    @Override
    public Paint getPaint() {
        return eraserPaint;
    }


}
