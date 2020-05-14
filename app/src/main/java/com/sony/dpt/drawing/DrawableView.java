package com.sony.dpt.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import com.sony.dpt.override.UpdateMode;
import com.sony.dpt.override.ViewOverride;
import com.sony.dpt.utils.WakelockUtils;
import com.sony.infras.dp_libraries.systemutil.SystemUtil;

public class DrawableView extends SurfaceView implements SurfaceHolder.Callback2 {

    private final DrawingManager drawingManager;

    private static final int BASE_STROKE_SIZE = 6;
    private static final boolean HANDLE_PRESSURE_CHANGE = false;
    private static final boolean emulatoreMode = !ViewOverride.getInstance().isLoaded();

    public DrawableView(Context context) {
        super(context);
        this.getHolder().addCallback(this);
        drawingManager = new DrawingManager(this, BASE_STROKE_SIZE, HANDLE_PRESSURE_CHANGE, wakelockUtils(context));
    }

    public DrawableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);
        drawingManager = new DrawingManager(this, BASE_STROKE_SIZE, HANDLE_PRESSURE_CHANGE, wakelockUtils(context));
    }

    public DrawableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.getHolder().addCallback(this);
        drawingManager = new DrawingManager(this, BASE_STROKE_SIZE, HANDLE_PRESSURE_CHANGE, wakelockUtils(context));
    }

    private WakelockUtils wakelockUtils(Context context) {
        return new WakelockUtils(context);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return drawingManager.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        SystemUtil.getEpdUtilInstance().addDhwArea(
                new Rect(
                        0,
                        0,
                        width,
                        height
                ),
                6,
                width < height ? 0 : 1
        );
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        Canvas canvas = ViewOverride.lockCanvas(holder, UpdateMode.EINK_WAVEFORM_MODE_DU);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceRedrawNeededAsync(SurfaceHolder holder, Runnable drawingFinished) {

    }
}
