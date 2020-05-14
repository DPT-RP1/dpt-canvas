package com.sony.dpt.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.sony.dpt.drawing.eraser.EraserDelegate;
import com.sony.dpt.drawing.eraser.StrokeEraserDelegate;
import com.sony.dpt.drawing.strokes.PressureSensitiveStrikeDelegate;
import com.sony.dpt.drawing.strokes.SimpleStrikeDelegate;
import com.sony.dpt.drawing.strokes.SimpleStrokeContainer;
import com.sony.dpt.drawing.strokes.StrikeDelegate;
import com.sony.dpt.utils.WakelockUtils;
import com.sony.infras.dp_libraries.systemutil.SystemUtil;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class DrawingManager implements DrawingDelegate {

    private final View view;
    private Bitmap cachedLayer;
    private Canvas drawCanvas;
    private StrikeDelegate strikeDelegate;
    private StrikeDelegate pressureStrikeDelegate;
    private StrikeDelegate currentStrikeDelegate;

    private EraserDelegate eraserDelegate;
    private DrawingDelegate currentDelegate;

    private static int INTERESTING_TOOL_TYPE = MotionEvent.TOOL_TYPE_STYLUS;
    private final SystemUtil.EpdUtil epdUtil;

    private boolean currentDhwState = false;
    private final WakelockUtils wakelockUtils;

    public DrawingManager(final View view,
                          final int penWidth,
                          final boolean handlePressureChanges,
                          final WakelockUtils wakelockUtils) {
        this.view = view;
        this.wakelockUtils = wakelockUtils;
        // This allows for non-stylus compatibility (emulator for ex.)
        detectEmulator();
        init(penWidth, handlePressureChanges);
        setListeners();
        epdUtil = SystemUtil.getEpdUtilInstance();
    }

    /**
     * This deactivate Stylus enforcement on the emulator.
     */
    private void detectEmulator() {
        if (SystemUtil.getEpdUtilInstance() instanceof SystemUtil.EmulatedEpdUtil) {
            INTERESTING_TOOL_TYPE = MotionEvent.TOOL_TYPE_FINGER;
        }
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

                    SimpleStrikeDelegate simpleStrikeDelegate = new SimpleStrikeDelegate(
                            penWidth,
                            view,
                            cachedLayer,
                            drawCanvas,
                            new SimpleStrokeContainer(),
                            wakelockUtils);
                    strikeDelegate = simpleStrikeDelegate;

                    pressureStrikeDelegate = new PressureSensitiveStrikeDelegate(
                            simpleStrikeDelegate
                    );

                    //eraserDelegate = new BitmapEraserDelegate(20, view, cachedLayer, drawCanvas);

                    if (handlePressureChanges) {
                        currentStrikeDelegate = pressureStrikeDelegate;
                    } else {
                        currentStrikeDelegate = strikeDelegate;
                    }

                    eraserDelegate = new StrokeEraserDelegate(
                            20,
                            view,
                            cachedLayer,
                            drawCanvas,
                            currentStrikeDelegate);

                    currentDelegate = currentStrikeDelegate;
                }
            });
        }
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

    private void setDhwState(boolean state) {
        epdUtil.setDhwState(state);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getToolType(0) != INTERESTING_TOOL_TYPE) return false;

        if (event.isButtonPressed(MotionEvent.BUTTON_SECONDARY)) {
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
    public void invalidate(RectF dirty) {
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

        eraserDelegate.setStrikeDelegate(currentStrikeDelegate);
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

    @Override
    public int maxPenWidth() {
        return currentDelegate.maxPenWidth();
    }

    @Override
    public PointF lastPosition() {
        return currentDelegate.lastPosition();
    }

    @Override
    public Paint getPaint() {
        return currentDelegate.getPaint();
    }

    @Override
    public boolean nativeDhw() {
        return currentDhwState;
    }


}
