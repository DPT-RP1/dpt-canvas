package com.sony.dpt.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sony.dpt.override.IViewOverride;
import com.sony.dpt.override.UpdateMode;
import com.sony.dpt.override.ViewOverride;
import com.sony.dpt.utils.WakelockUtils;
import com.sony.infras.dp_libraries.systemutil.SystemUtil;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DrawableView extends SurfaceView implements SurfaceHolder.Callback2 {

    private final DrawingManager drawingManager;

    private static final int BASE_STROKE_SIZE = 5;
    private static final boolean HANDLE_PRESSURE_CHANGE = false;
    private static final boolean emulatoreMode = !ViewOverride.getInstance().isLoaded();

    public DrawableView(Context context) {
        super(context);
        this.getHolder().addCallback(this);
        drawingManager = new DrawingManager(this, BASE_STROKE_SIZE, HANDLE_PRESSURE_CHANGE, wakelockUtils(context));
    }

    public DrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);
        drawingManager = new DrawingManager(this, BASE_STROKE_SIZE, HANDLE_PRESSURE_CHANGE, wakelockUtils(context));
    }

    public DrawableView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        SystemUtil.getEpdUtilInstance().removeAllDhwArea();
        SystemUtil.getEpdUtilInstance().addDhwArea(
                new Rect(
                        0,
                        0,
                        width,
                        height
                ),
                drawingManager.penWidth(),
                width < height ? 0 : 1
        );
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        SystemUtil.getEpdUtilInstance().removeAllDhwArea();
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        Canvas canvas = ViewOverride.getInstance().lockCanvas(holder, UpdateMode.EINK_WAVEFORM_MODE_DU);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceRedrawNeededAsync(SurfaceHolder holder, Runnable drawingFinished) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawingManager.onDraw(canvas);
    }

    private static class DrawingThread extends Thread {

        private final SurfaceHolder surfaceHolder;
        private final Bitmap cachedLayer;
        private final IViewOverride viewOverride;
        private final ConcurrentLinkedQueue<Rect> areas;

        public DrawingThread(final SurfaceHolder surfaceHolder, final Bitmap cachedLayer, final IViewOverride viewOverride) {
            this.surfaceHolder = surfaceHolder;
            this.cachedLayer = cachedLayer;
            this.viewOverride = viewOverride;
            this.areas = new ConcurrentLinkedQueue<Rect>();
        }

        @Override
        public void run() {
            while (true) {
                // Dirty trick from Sony: trigger a first frame in A2 partial, then lock the next frame in GC16
                Canvas canvas = viewOverride.lockCanvas(surfaceHolder, UpdateMode.UPDATE_MODE_CONVERT_A2_PARTIAL);
                canvas.drawBitmap(cachedLayer, 0, 0, null);
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        public void enqueueArea(final Rect area) {
            areas.add(area);
            synchronized (this) {
                this.notify();
            }
        }
    }
}
