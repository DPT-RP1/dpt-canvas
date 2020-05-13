package com.sony.dpt.drawing;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class DrawableView extends ConstraintLayout {

    private final DrawingManager drawingManager;

    public DrawableView(Context context) {
        super(context);
        drawingManager = new DrawingManager(this, 6);
    }

    public DrawableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        drawingManager = new DrawingManager(this, 6);
    }

    public DrawableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        drawingManager = new DrawingManager(this, 6);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return drawingManager.onTouchEvent(event);
    }

    @Override
    public void invalidate(Rect dirty) {
        drawingManager.invalidate(dirty);
    }
}
