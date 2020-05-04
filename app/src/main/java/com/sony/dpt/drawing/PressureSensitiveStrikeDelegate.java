package com.sony.dpt.drawing;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;


/**
 * This can sense pressure change in the Sony Pen / DPT RP1 matrix
 * This is naive and not even implemented by Sony
 *
 * TODO:
 *   - Better transitioning into different pressure
 *   - Make clear threshold
 *   - Find example of working pressure math
 */
public class PressureSensitiveStrikeDelegate implements DrawingDelegate {

    private final StrikeDelegate strikeDelegate;

    private final int baseWidth;
    private int currentWidth;

    public PressureSensitiveStrikeDelegate(final StrikeDelegate strikeDelegate) {
        this.strikeDelegate = strikeDelegate;
        this.baseWidth = strikeDelegate.penWidth();
        this.currentWidth = this.baseWidth;
    }

    public void onDraw(Canvas canvas) {

        strikeDelegate.onDraw(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        float pressure = event.getPressure();
        // We renormalize the pressure: 0.5 is "normal"
        float multiplier = pressure / 0.5f;

        this.currentWidth = (int) (baseWidth * multiplier);
        if (this.currentWidth < 1) this.currentWidth = 1;
        strikeDelegate.setPenWidth(currentWidth);

        return strikeDelegate.onTouchEvent(event);
    }

    @Override
    public void invalidate(Rect dirty) {
        strikeDelegate.invalidate(dirty);
    }

    @Override
    public boolean pressureSensitive() {
        return true;
    }

    @Override
    public void setPenWidth(int penWidth) {
        strikeDelegate.setPenWidth(penWidth);
    }

    @Override
    public int penWidth() {
        return strikeDelegate.penWidth();
    }
}
