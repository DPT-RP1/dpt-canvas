package com.sony.dpt.drawing.eraser;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import com.sony.dpt.drawing.AbstractDrawingDelegate;

import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_SP2;
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

public abstract class AbstractEraserDelegate extends AbstractDrawingDelegate implements EraserDelegate {

    protected static final int CIRCLE_STROKE_WIDTH = 3;
    protected int eraserRadius;
    protected Rect finalEraseInvalidationRectangle;
    protected Rect invalidationRectangle;
    protected Rect temp;
    // This will be use to repaint the circle white
    protected Rect previousInvalidationRectangle;

    protected AbstractEraserDelegate(View view, Bitmap cachedLayer, Canvas drawCanvas) {
        super(view, cachedLayer, drawCanvas);

        this.finalEraseInvalidationRectangle = new Rect();
        this.invalidationRectangle = new Rect();
        this.previousInvalidationRectangle = new Rect();
        this.temp = new Rect();
    }

    protected void setRectToCurrentPoint(Rect rect) {
        rect.set(
                (int) lastX,
                (int) lastY,
                (int) lastX,
                (int) lastY
        );
        rect.inset(-eraserRadius - CIRCLE_STROKE_WIDTH, -eraserRadius - CIRCLE_STROKE_WIDTH);
    }

    protected void addToInvalidate(float x, float y) {
        invalidationRectangle.union((int) x, (int) y);
        finalEraseInvalidationRectangle.union((int) x, (int) y);
    }

    protected void addToInvalidate(Rect rect, int inset) {
        invalidationRectangle.union(rect);
        invalidationRectangle.inset(-inset, -inset);
        finalEraseInvalidationRectangle.union(rect);
        finalEraseInvalidationRectangle.inset(-inset, -inset);
    }

    @Override
    public void invalidate(Rect dirty) {
        viewOverride.invalidate(view, dirty, UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);
    }

    public void invalidatePartialGC16(Rect dirty) {
        viewOverride.invalidate(view, dirty, UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_SP2);
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
    public float getStrokeWidth() {
        return CIRCLE_STROKE_WIDTH;
    }

    @Override
    public boolean pressureSensitive() {
        return false;
    }
}
