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
    private DrawingDelegate pressureStrikeDelegate;
    private DrawingDelegate currentStrikeDelegate;

    private DrawingDelegate eraserDelegate;
    private DrawingDelegate currentDelegate;

    public DrawingManager(final View view,
                          final int penWidth,
                          final boolean handlePressureChanges) {
        this.view = view;
        init(penWidth, handlePressureChanges);
        setListeners();
    }

    public void init(final int penWidth, final boolean handlePressureChanges) {
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

                    StrikeDelegate simpleStrikeDelegate = new StrikeDelegate(penWidth, view, cachedLayer, drawCanvas);
                    strikeDelegate = simpleStrikeDelegate;

                    pressureStrikeDelegate =  new PressureSensitiveStrikeDelegate(simpleStrikeDelegate);

                    eraserDelegate = new EraserDelegate(20, view, cachedLayer, drawCanvas);

                    if (handlePressureChanges) {
                        currentStrikeDelegate = pressureStrikeDelegate;
                    } else {
                        currentStrikeDelegate = strikeDelegate;
                    }
                    currentDelegate = currentStrikeDelegate;
                }
            });
        }

        view.setWillNotDraw(false);
    }


    private void setListeners() {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View that, MotionEvent event) {
                return onTouchEvent(event);
            }
        });
    }



    public void onDraw(Canvas canvas) {
        currentDelegate.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getToolType(0) != MotionEvent.TOOL_TYPE_STYLUS) return false;

        if(event.isButtonPressed(MotionEvent.BUTTON_SECONDARY)) {
            // HIGHLIGHTING;
            currentDelegate = currentStrikeDelegate;
        } else if (event.isButtonPressed(MotionEvent.BUTTON_TERTIARY)) {
            // ERASING;
            currentDelegate = eraserDelegate;
        } else {
            // STRIKING;
            currentDelegate = currentStrikeDelegate;
        }

        return currentDelegate.onTouchEvent(event);
    }

    @Override
    public void invalidate(Rect dirty) {
        currentDelegate.invalidate(dirty);
    }

    private int savedStrikePenWidth;

    /**
     * Sets the pressure sensitive mode to true for all strike, eraser and highlight delegates.
     * Keeps the previous fixed width to restore later.
     * @param enable True to activate pressure sensitivity
     */
    public void pressureSensitive(boolean enable) {
        if (enable) {
            savedStrikePenWidth = strikeDelegate.penWidth();
            currentStrikeDelegate = pressureStrikeDelegate;
        } else {
            if (savedStrikePenWidth > 0) {
                strikeDelegate.setPenWidth(savedStrikePenWidth);
            }
            savedStrikePenWidth = 0;
            currentStrikeDelegate = strikeDelegate;
        }
    }

    public boolean pressureSensitive() {
        return currentDelegate.pressureSensitive();
    }

    @Override
    public void setPenWidth(int penWidth) {
        currentDelegate.setPenWidth(penWidth);
    }

    @Override
    public int penWidth() {
        return currentDelegate.penWidth();
    }



}
