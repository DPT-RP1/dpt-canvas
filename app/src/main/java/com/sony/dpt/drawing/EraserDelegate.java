package com.sony.dpt.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_SP2;
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

// Slow erase
// Fast erase

/**
 * This is in charge of erasing on a view
 * <p>
 * The best compromise is to erase quickly while the pen moves, summing the invalidation rectangles,
 * then pop a GC16 (Greyscale Clearing) partial at the end on an invalidation rectangle containing
 * everything cleared during the fast phase.
 */
public class EraserDelegate extends AbstractDrawingDelegate {

    private static final int CIRCLE_STROKE_WIDTH = 3;

    private Paint eraserPaint;

    private Paint circlePaint;
    private int eraserRadius;
    private Rect finalEraseInvalidationRectangle;
    private Rect invalidationRectangle;

    private Rect temp;
    // This will be use to repaint the circle white
    private Rect previousInvalidationRectangle;
    private boolean isErasing = false;

    public EraserDelegate(int eraserWidth, final View view, Bitmap cachedLayer, Canvas drawCanvas) {
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

    private void setRectToCurrentPoint(Rect rect) {
        rect.set(
                (int) lastX,
                (int) lastY,
                (int) lastX,
                (int) lastY
        );
        rect.inset(-eraserRadius - CIRCLE_STROKE_WIDTH, -eraserRadius - CIRCLE_STROKE_WIDTH);
    }

    private void addToInvalidate(float x, float y) {
        invalidationRectangle.union((int) x, (int) y);
        finalEraseInvalidationRectangle.union((int) x, (int) y);
    }

    private void handleMotion(final MotionEvent event) {
        for (int i = 0; i < event.getHistorySize(); i++) {
            addToInvalidate(event.getHistoricalX(i), event.getHistoricalY(i));
        }
        addToInvalidate(event.getX(), event.getY());

        lastX = event.getX();
        lastY = event.getY();

        // We redraw the part that we drew black last time.
        temp.set(invalidationRectangle);
        temp.union(previousInvalidationRectangle);

        temp.inset(
                -eraserRadius - CIRCLE_STROKE_WIDTH,
                -eraserRadius - CIRCLE_STROKE_WIDTH
        );
        invalidate(temp);

        previousInvalidationRectangle.set(invalidationRectangle);
    }

    public void onDraw(Canvas canvas) {
        drawCanvas.drawCircle(lastX, lastY, eraserRadius, eraserPaint);
        canvas.drawBitmap(cachedLayer, 0.0F, 0.0F, null);
        if (isErasing) canvas.drawCircle(lastX, lastY, eraserRadius + 1, circlePaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        epdUtil.setDhwState(false);
        isErasing = true;
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
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
    public void invalidate(Rect dirty) {
        viewOverride.invalidate(view, dirty, UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);
    }

    public void invalidatePartialGC16(Rect dirty) {
        viewOverride.invalidate(view, dirty, UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_SP2);
    }

    @Override
    public boolean pressureSensitive() {
        return false;
    }

    @Override
    public void setPenWidth(int penWidth) {
        this.eraserRadius = penWidth;
    }

    @Override
    public int penWidth() {
        return eraserRadius;
    }
}
