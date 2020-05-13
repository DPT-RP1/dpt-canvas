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

public class DrawableView extends SurfaceView implements SurfaceHolder.Callback {

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
        System.out.println("Surface changed");
        Canvas canvas = ViewOverride.lockCanvas(holder, UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);
        canvas.drawColor(Color.WHITE);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println("Surface destroyed");
    }
}
