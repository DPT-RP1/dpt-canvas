package com.sony.dpt.drawing;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;


public class DrawingManager implements DrawingDelegate {

    private final View view;
    private DrawingDelegate strikeDelegate;
    private DrawingDelegate currentDelegate;

    private int strokeWidth;

    public DrawingManager(final View view, final int strokeWidth) {
        this.view = view;
        this.strokeWidth = strokeWidth;
        init();
    }

    public void init() {
        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    strikeDelegate = new StrikeDelegate(strokeWidth, view);

                    currentDelegate = strikeDelegate;
                }
            });
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getToolType(0) != MotionEvent.TOOL_TYPE_STYLUS) return false;

        if(event.isButtonPressed(MotionEvent.BUTTON_SECONDARY)) {
            // HIGHLIGHTING;
            currentDelegate = strikeDelegate;
        } else if (event.isButtonPressed(MotionEvent.BUTTON_TERTIARY)) {

        } else {
            // STRIKING;
            currentDelegate = strikeDelegate;
        }

        return currentDelegate.onTouchEvent(event);
    }

    @Override
    public void invalidate(RectF dirty) {
        currentDelegate.invalidate(dirty);
    }

}
