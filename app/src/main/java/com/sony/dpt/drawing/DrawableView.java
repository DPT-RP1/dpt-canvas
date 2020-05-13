package com.sony.dpt.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    public void invalidate(Rect dirty) {
        drawingManager.invalidate(dirty);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("Surface created");
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
                0
        );


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println("Surface destroyed");
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        System.out.println("Surface redraw");
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1);
        p.setAntiAlias(false);
        p.setDither(false);

        Canvas canvas = ViewOverride.lockCanvas(holder, UpdateMode.EINK_WAVEFORM_MODE_DU);
        canvas.drawColor(Color.WHITE);
        canvas.drawRect(1000, 1000, 1100, 1100, p);
        holder.unlockCanvasAndPost(canvas);


    }

    @Override
    public void surfaceRedrawNeededAsync(SurfaceHolder holder, Runnable drawingFinished) {

    }
}
