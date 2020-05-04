package com.sony.dpt.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.sony.dpt.override.ViewOverride;

public class DrawableView extends ConstraintLayout {

    private final DrawingManager drawingManager;

    private static final int BASE_STROKE_SIZE = 6;
    private static final boolean HANDLE_PRESSURE_CHANGE = false;
    private static final boolean emulatoreMode = !ViewOverride.getInstance().isLoaded();

    public DrawableView(Context context) {
        super(context);
        drawingManager = new DrawingManager(this, BASE_STROKE_SIZE, HANDLE_PRESSURE_CHANGE);
    }

    public DrawableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        drawingManager = new DrawingManager(this, BASE_STROKE_SIZE, HANDLE_PRESSURE_CHANGE);
    }

    public DrawableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        drawingManager = new DrawingManager(this, BASE_STROKE_SIZE, HANDLE_PRESSURE_CHANGE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawingManager.onDraw(canvas);
    }

    @Override
    public void invalidate(Rect dirty) {
        if (emulatoreMode) {
            super.invalidate();
        } else {
            drawingManager.invalidate(dirty);
        }
    }

    public DrawingManager drawingManager() {
        return drawingManager;
    }
}
