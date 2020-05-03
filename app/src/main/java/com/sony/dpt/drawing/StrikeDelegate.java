package com.sony.dpt.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.override.ViewOverride;

import static android.graphics.Color.BLACK;
import static android.graphics.Paint.Style.STROKE;
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

/**
 * This is in charge of striking the pen on a view
 */
public class StrikeDelegate extends AbstractDrawingDelegate {

    private Path currentPath;
    private Paint paint;

    private float lastX;
    private float lastY;

    private int strokeWidth;

    public StrikeDelegate(final int strokeWidth, final View view, final Bitmap cachedLayer, final Canvas drawCanvas) {
        super(view, cachedLayer, drawCanvas);
        this.strokeWidth = strokeWidth;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(BLACK);
        paint.setAntiAlias(false);
        paint.setDither(false);
        paint.setStyle(STROKE);
        paint.setStrokeWidth((float) strokeWidth);

        currentPath = new Path();

        epdUtil.addDhwArea(
                new Rect(
                        0,
                        0,
                        view.getWidth(),
                        view.getHeight()
                ),
                strokeWidth,
                0
        );
    }

    private void resetPath() {
        currentPath.reset();
        currentPath.moveTo(lastX, lastY);
    }

    private void resetInvalidation() {
        invalidationRectangle.set(
                (int) lastX,
                (int) lastY,
                (int) lastX,
                (int) lastY
        );
    }

    private void updatePath(final float x, final float y) {
        lastX = x;
        lastY = y;
        invalidationRectangle.union((int) lastX, (int) lastY);
        currentPath.lineTo(lastX, lastY);
    }

    private void handleMotion(final MotionEvent event) {
        int historySize = event.getHistorySize();
        for (int i = 0; i < historySize; i++) {
            updatePath(event.getHistoricalX(i), event.getHistoricalY(i));
        }
        updatePath(event.getX(), event.getY());

        drawCanvas.drawPath(currentPath, paint);
        resetPath();

        // Fast ceil
        int currentStrokeWidth = (int) paint.getStrokeWidth() + 1;

        // We inset by the stroke width so that the invalidation also encompass the full width of the line
        invalidationRectangle.inset(-currentStrokeWidth, -currentStrokeWidth);
        view.invalidate(invalidationRectangle);
        resetInvalidation();
    }

    public void onDraw(Canvas canvas) {

        canvas.drawColor(-1);
        canvas.drawBitmap(cachedLayer, 0.0F, 0.0F, null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                epdUtil.setDhwState(true);
                lastX = event.getX();
                lastY = event.getY();
                resetPath();
                resetInvalidation();
                break;
            case MotionEvent.ACTION_MOVE:
                handleMotion(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleMotion(event);
                resetInvalidation();
                epdUtil.setDhwState(false);
                break;
        }
        return true;
    }

    @Override
    public void invalidate(Rect dirty) {
        ViewOverride.invalidate(view, dirty, UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);
    }

    // BETA - This is not fully understood
    public void changeStrokeWidth(final int newWidth) {
        this.strokeWidth = newWidth;
        epdUtil.changeDhwStrokeWidth((int) newWidth, (int) newWidth);
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public Paint getStrokePaint() {
        return paint;
    }
}
