package com.sony.dpt.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import com.sony.dpt.override.UpdateMode;
import com.sony.dpt.override.ViewOverride;
import com.sony.infras.dp_libraries.systemutil.SystemUtil;

public class DrawableView extends SurfaceView implements SurfaceHolder.Callback2 {

    private final DrawingManager drawingManager;

    public DrawableView(Context context) {
        super(context);
        drawingManager = new DrawingManager(this, 6);
        this.getHolder().addCallback(this);
    }

    public DrawableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        drawingManager = new DrawingManager(this, 6);
        this.getHolder().addCallback(this);
    }

    public DrawableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        drawingManager = new DrawingManager(this, 6);
        this.getHolder().addCallback(this);
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
