package com.sony.dpt.drawing.rendering;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;

import com.sony.dpt.drawing.geom.Area;
import com.sony.dpt.override.IViewOverride;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DrawingThread extends Thread {

    private final SurfaceHolder surfaceHolder;
    private final Bitmap cachedLayer;
    private final IViewOverride viewOverride;
    private final ConcurrentLinkedQueue<Area> areas;

    public DrawingThread(final SurfaceHolder surfaceHolder, final Bitmap cachedLayer, final IViewOverride viewOverride) {
        this.surfaceHolder = surfaceHolder;
        this.cachedLayer = cachedLayer;
        this.viewOverride = viewOverride;
        this.areas = new ConcurrentLinkedQueue<Area>();
    }

    @Override
    public void run() {
        boolean interrupted = false;
        while (!interrupted || !areas.isEmpty()) {
            while (!areas.isEmpty()) {
                Area area = areas.poll();
                if (area != null) {
                    Canvas canvas = viewOverride.lockCanvas(surfaceHolder, area.boundingBox, area.updateMode);
                    canvas.drawBitmap(cachedLayer, 0, 0, null);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        }
    }

    public void enqueueArea(final Rect boundingBox, final int updateMode) {
        areas.add(new Area(boundingBox, updateMode));
        synchronized (this) {
            this.notify();
        }
    }

    public void enqueueArea(final RectF boundingBox, final int updateMode) {
        enqueueArea(
                new Rect(
                        (int) boundingBox.left,
                        (int) boundingBox.top,
                        (int) boundingBox.right,
                        (int) boundingBox.bottom
                ),
                updateMode
        );
    }
}