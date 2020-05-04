package com.sony.dpt.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.override.ViewOverride;

// Slow erase
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_SP2;
// Fast erase
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

/**
 * This is in charge of erasing on a view
 *
 * The best compromise is to erase quickly while the pen moves, summing the invalidation rectangles,
 * then pop a GC16 (Greyscale Clearing) partial at the end on an invalidation rectangle containing
 * everything cleared during the fast phase.
 */
public class EraserDelegate extends AbstractDrawingDelegate {

    private Paint eraserPaint;
    private int eraserRadius;
    private Rect finalEraseIinvalidationRectangle;

    public EraserDelegate(int eraserWidth, final View view, Bitmap cachedLayer, Canvas drawCanvas) {
        super(view, cachedLayer, drawCanvas);
        this.eraserRadius = eraserWidth;
        this.finalEraseIinvalidationRectangle = new Rect();
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
    }

    private void setRectToCurrentPoint(Rect rect) {
        rect.set(
                (int)lastX - eraserRadius,
                (int) lastY - eraserRadius,
                (int) lastX + eraserRadius,
                (int) lastY + eraserRadius
        );
    }

    private void handleMotion(final MotionEvent event) {
        lastX = event.getX();
        lastY = event.getY();
        setRectToCurrentPoint(invalidationRectangle);
        finalEraseIinvalidationRectangle.union(invalidationRectangle);

        invalidate(invalidationRectangle);
    }

    public void onDraw(Canvas canvas) {
        drawCanvas.drawCircle(lastX, lastY, eraserRadius, eraserPaint);

        canvas.drawBitmap(cachedLayer, 0.0F, 0.0F, null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        epdUtil.setDhwState(false);
        handleMotion(event);

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                setRectToCurrentPoint(finalEraseIinvalidationRectangle);
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                invalidatePartialGC16(finalEraseIinvalidationRectangle);
                finalEraseIinvalidationRectangle.setEmpty();
                break;
        }

        return true;
    }

    @Override
    public void invalidate(Rect dirty) {
        ViewOverride.invalidate(view, dirty, UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);
    }

    public void invalidatePartialGC16(Rect dirty) {
        ViewOverride.invalidate(view, dirty, UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_SP2);
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
