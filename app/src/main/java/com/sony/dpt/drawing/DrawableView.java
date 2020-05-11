package com.sony.dpt.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.sony.dpt.override.ViewOverride;
import com.sony.dpt.utils.WakelockUtils;

public class DrawableView extends SurfaceView {

    private final DrawingManager drawingManager;

    private static final int BASE_STROKE_SIZE = 6;
    private static final boolean HANDLE_PRESSURE_CHANGE = false;
    private static final boolean emulatoreMode = !ViewOverride.getInstance().isLoaded();

    public DrawableView(Context context) {
        super(context);
        drawingManager = new DrawingManager(this, BASE_STROKE_SIZE, HANDLE_PRESSURE_CHANGE, wakelockUtils(context));
    }

    public DrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawingManager = new DrawingManager(this, BASE_STROKE_SIZE, HANDLE_PRESSURE_CHANGE, wakelockUtils(context));
    }

    public DrawableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        drawingManager = new DrawingManager(this, BASE_STROKE_SIZE, HANDLE_PRESSURE_CHANGE, wakelockUtils(context));
    }

    private WakelockUtils wakelockUtils(Context context) {
        return new WakelockUtils(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //drawingManager.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas var1) {

        super.dispatchDraw(var1);
    }

    @Override
    public void draw(Canvas canvas) {
        /*if (this.mWindowType != 1000 && (this.mPrivateFlags & 128) == 128) {
            var1.drawColor(0, Mode.CLEAR);
        }*/
        //  canvas.drawColor(-1);
        System.out.println(canvas.getClipBounds());

        super.draw(canvas);
    }

    @Override
    public void invalidate(Rect dirty) {
        if (emulatoreMode) {
            super.invalidate();
        } else {
            drawingManager.invalidate(new RectF(dirty));
        }
    }

    public DrawingManager drawingManager() {
        return drawingManager;
    }
}
