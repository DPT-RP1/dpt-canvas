package com.sony.dpt.drawing;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.override.ViewOverride;

import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

/**
 * This is in charge of striking the pen on a view
 */
public class StrikeDelegate extends AbstractDrawingDelegate {

    private float lastX;
    private float lastY;

    private int strokeWidth;

    public StrikeDelegate(final int strokeWidth, final View view) {
        super(view);
        this.strokeWidth = strokeWidth;
        init();
    }

    private void init() {

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
    }

    private void handleMotion(final MotionEvent event) {
        int historySize = event.getHistorySize();
        for (int i = 0; i < historySize; i++) {
            updatePath(event.getHistoricalX(i), event.getHistoricalY(i));
        }
        updatePath(event.getX(), event.getY());

        // We inset by the stroke width so that the invalidation also encompass the full width of the line
        view.invalidate(invalidationRectangle);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                epdUtil.setDhwState(true);
                lastX = event.getX();
                lastY = event.getY();
                resetInvalidation();
                break;
            case MotionEvent.ACTION_MOVE:
                handleMotion(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleMotion(event);
                resetInvalidation();
                break;
        }
        return true;
    }

    @Override
    public void invalidate(Rect dirty) {
        ViewOverride.invalidate(view, dirty, UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);
    }


}
