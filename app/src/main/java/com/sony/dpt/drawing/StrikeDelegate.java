package com.sony.dpt.drawing;

import android.graphics.Rect;
import android.graphics.RectF;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.override.ViewOverride;

import static android.content.Context.POWER_SERVICE;
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

/**
 * This is in charge of striking the pen on a view
 */
public class StrikeDelegate extends AbstractDrawingDelegate {

    private float lastX;
    private float lastY;

    private int strokeWidth;

    private Rect converter;

    public StrikeDelegate(final int strokeWidth, final View view) {
        super(view);
        this.strokeWidth = strokeWidth;
        this.converter = new Rect();
    }

    private void resetInvalidation() {
        invalidationRectangle.set(lastX, lastY, lastX, lastY);
    }

    private void updatePath(final float x, final float y) {
        lastX = x;
        lastY = y;
        invalidationRectangle.union(lastX, lastY);
    }

    private void handleMotion(final MotionEvent event) {
        int historySize = event.getHistorySize();
        for (int i = 0; i < historySize; i++) {
            updatePath(event.getHistoricalX(i), event.getHistoricalY(i));
        }
        updatePath(event.getX(), event.getY());

        // We inset by the stroke width so that the invalidation also encompass the full width of the line
        invalidationRectangle.inset(-strokeWidth, -strokeWidth);
        invalidate(invalidationRectangle);
    }


    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                PowerManager powerManager = (PowerManager) view.getContext().getSystemService(POWER_SERVICE);
                wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                        "MyApp::MyWakelockTag");
                wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);

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
                wakeLock.release();
                break;
        }
        return true;
    }

    @Override
    public void invalidate(RectF dirty) {
        converter.set((int) dirty.left, (int) dirty.top, (int) dirty.right, (int) dirty.bottom);
        ViewOverride.invalidate(view, converter, UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);
    }


}
