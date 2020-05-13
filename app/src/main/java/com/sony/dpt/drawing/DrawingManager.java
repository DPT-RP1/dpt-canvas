package com.sony.dpt.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class DrawingManager implements DrawingDelegate {

    private final View view;
    private Bitmap cachedLayer;
    private Canvas drawCanvas;
    private DrawingDelegate strikeDelegate;
    private DrawingDelegate eraserDelegate;
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

                    // Cached Layer contains the entire drawing layer
                    cachedLayer = Bitmap.createBitmap(view.getWidth(), view.getHeight(), ARGB_8888);

                    // This canvas is used to draw the path to the bitmap, the bitmap
                    // then being rendered to the view
                    drawCanvas = new Canvas(cachedLayer);

                    strikeDelegate = new StrikeDelegate(strokeWidth, view, cachedLayer, drawCanvas);
                    eraserDelegate = new EraserDelegate(view, cachedLayer, drawCanvas);

                    currentDelegate = strikeDelegate;
                }
            });
        }

        //view.setWillNotDraw(false);
    }

    public void onDraw(Canvas canvas) {
        currentDelegate.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getToolType(0) != MotionEvent.TOOL_TYPE_STYLUS) return false;

        if(event.isButtonPressed(MotionEvent.BUTTON_SECONDARY)) {
            // HIGHLIGHTING;
            currentDelegate = strikeDelegate;
        } else if (event.isButtonPressed(MotionEvent.BUTTON_TERTIARY)) {
            // ERASING;
            currentDelegate = eraserDelegate;
        } else {
            // STRIKING;
            currentDelegate = strikeDelegate;
        }

        return currentDelegate.onTouchEvent(event);
    }

    @Override
    public void invalidate(Rect dirty) {
        currentDelegate.invalidate(dirty);
    }

}
