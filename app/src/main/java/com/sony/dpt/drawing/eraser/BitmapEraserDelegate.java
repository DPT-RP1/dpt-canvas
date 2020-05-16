package com.sony.dpt.drawing.eraser;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.drawing.rendering.DrawingThread;
import com.sony.dpt.override.UpdateMode;

/**
 * This is in charge of erasing on a view
 * <p>
 * The best compromise is to erase quickly while the pen moves, summing the invalidation rectangles,
 * then pop a GC16 (Greyscale Clearing) partial at the end on an invalidation rectangle containing
 * everything cleared during the fast phase.
 */
public class BitmapEraserDelegate extends AbstractEraserDelegate implements EraserDelegate {

    public BitmapEraserDelegate(int eraserWidth, final View view, final Bitmap cachedLayer, final Canvas drawCanvas, final DrawingThread drawingThread) {
        super(eraserWidth, view, cachedLayer, drawCanvas, drawingThread);
    }


    protected void handleMotion(final MotionEvent event) {
        super.handleMotion(event);
        drawingThread.enqueueArea(temp, UpdateMode.UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_SP2);
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }

    @Override
    public Paint getPaint() {
        return eraserPaint;
    }

}
