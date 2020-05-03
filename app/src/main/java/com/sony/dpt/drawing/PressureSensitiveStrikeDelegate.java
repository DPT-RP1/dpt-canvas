package com.sony.dpt.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.sony.dpt.override.ViewOverride;

import static android.graphics.Color.BLACK;
import static android.graphics.Paint.Style.STROKE;
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

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

    private final float baseWidth;
    private float currentWidth;

    public PressureSensitiveStrikeDelegate(final StrikeDelegate strikeDelegate) {
        this.strikeDelegate = strikeDelegate;
        this.baseWidth = strikeDelegate.getStrokeWidth();
        this.currentWidth = this.baseWidth;
    }

    public void onDraw(Canvas canvas) {

        strikeDelegate.onDraw(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        float pressure = event.getPressure();
        // We renormalize the pressure: 0.5 is "normal"
        float multiplier = pressure / 0.5f;

        this.currentWidth = baseWidth * multiplier;
        if (this.currentWidth < 1) this.currentWidth = 1;
        strikeDelegate.getStrokePaint().setStrokeWidth(currentWidth);

        return strikeDelegate.onTouchEvent(event);
    }

    @Override
    public void invalidate(Rect dirty) {
        strikeDelegate.invalidate(dirty);
    }
}
