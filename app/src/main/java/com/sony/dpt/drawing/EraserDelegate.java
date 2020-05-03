package com.sony.dpt.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.override.ViewOverride;

import static com.sony.dpt.drawing.DrawingMode.ERASING;
import static com.sony.dpt.drawing.DrawingMode.STRIKING;
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

/**
 * This is in charge of erasing on a view
 */
public class EraserDelegate extends AbstractDrawingDelegate {

    private Paint eraserPaint;
    private static final int ERASER_RADIUS = 20;

    public EraserDelegate(final View view, Bitmap cachedLayer, Canvas drawCanvas) {
        super(view, cachedLayer, drawCanvas);
        init();
    }

    private void init() {
        eraserPaint = new Paint();
        eraserPaint.setAntiAlias(false);
        eraserPaint.setAlpha(0);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    private void handleMotion(final MotionEvent event) {
        lastX = event.getX();
        lastY = event.getY();
        invalidationRectangle.set(
                (int)lastX - ERASER_RADIUS,
                (int) lastY - ERASER_RADIUS,
                (int) lastX + ERASER_RADIUS,
                (int) lastY + ERASER_RADIUS
        );
        view.invalidate(invalidationRectangle);
    }

    public void onDraw(Canvas canvas) {
        drawCanvas.drawCircle(lastX, lastY, ERASER_RADIUS, eraserPaint);

        canvas.drawBitmap(cachedLayer, 0.0F, 0.0F, null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                epdUtil.setDhwState(false);
                handleMotion(event);
                break;
        }
        return true;
    }

    @Override
    public void invalidate(Rect dirty) {
        ViewOverride.invalidate(view, dirty, UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);
    }
}
