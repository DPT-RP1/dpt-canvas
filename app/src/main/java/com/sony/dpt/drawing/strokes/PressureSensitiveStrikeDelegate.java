package com.sony.dpt.drawing.strokes;

import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * This can sense pressure change in the Sony Pen / DPT RP1 matrix
 * This is naive and not even implemented by Sony
 * <p>
 * TODO:
 * - Better transitioning into different pressure
 * - Make clear threshold
 * - Find example of working pressure math
 */
public class PressureSensitiveStrikeDelegate extends AbstractStrikeDelegate implements StrikeDelegate {

    private final SimpleStrikeDelegate strikeDelegate;

    private final int baseWidth;
    private int currentWidth;
    private int maxPenWidth;

    public PressureSensitiveStrikeDelegate(final SimpleStrikeDelegate strikeDelegate) {
        super(null, null, null);
        this.strikeDelegate = strikeDelegate;
        this.baseWidth = strikeDelegate.penWidth();
        this.currentWidth = this.baseWidth;
        this.maxPenWidth = this.baseWidth;
    }

    public boolean onTouchEvent(MotionEvent event) {
        float pressure = event.getPressure();
        // We renormalize the pressure:
        float multiplier = 1.0f;
        if (pressure < 0.4f) {
            multiplier = 0.5f;
        } else if (pressure > 0.6f) {
            multiplier = 2f;
        }

        this.currentWidth = (int) (baseWidth * multiplier);
        if (this.currentWidth < 1) this.currentWidth = 1;
        setPenWidth(this.currentWidth);

        return strikeDelegate.onTouchEvent(event);
    }

    @Override
    public boolean pressureSensitive() {
        return true;
    }

    @Override
    public void setPenWidth(int penWidth) {
        strikeDelegate.setPenWidth(penWidth);
        if (maxPenWidth < penWidth) maxPenWidth = penWidth;
    }

    @Override
    public int penWidth() {
        return strikeDelegate.penWidth();
    }

    @Override
    public int maxPenWidth() {
        return maxPenWidth;
    }

    @Override
    public PointF lastPosition() {
        return strikeDelegate.lastPosition();
    }

    @Override
    public Paint getPaint() {
        return strikeDelegate.getPaint();
    }

    @Override
    public StrokesContainer getStrokesContainer() {
        return strikeDelegate.getStrokesContainer();
    }
}
