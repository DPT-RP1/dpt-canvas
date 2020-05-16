package com.sony.dpt.override;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.View;

import java.lang.reflect.Method;

public class ViewOverride implements IViewOverride {

    private static Method invalidateRect;
    private static Method invalidate;
    private static Method setDefaultUpdateMode;
    private static Method lockCanvas;
    private static Method lockCanvasRect;

    private static ViewOverride instance;

    private final Rect converter;

    private boolean loaded;

    private ViewOverride() {
        converter = new Rect();
        try {
            invalidateRect = View.class.getMethod("invalidate", Rect.class, int.class);
            invalidate = View.class.getMethod("invalidate", int.class);
            setDefaultUpdateMode = View.class.getMethod("setDefaultUpdateMode", int.class);

            invalidateRect.setAccessible(true); // Small acceleration
            invalidate.setAccessible(true);
            setDefaultUpdateMode.setAccessible(true);
            loaded = true;

        } catch (Exception ignored) {
            loaded = false;
        }
    }

    public static IViewOverride getInstance() {
        if (instance == null) instance = new ViewOverride();
        return instance;
    }

    public void invalidate(View view, Rect rect, int updateMode) {
        try {
            invalidateRect.invoke(view, rect, updateMode);
        } catch (Exception ignored) {
            view.invalidate(rect);
        }
    }


    @Override
    public void invalidate(View view, RectF rect, int updateMode) {
        synchronized (converter) {
            converter.set((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
        }
        invalidate(view, converter, updateMode);
    }

    @Override
    public void setDefaultUpdateMode(View view, int updateMode) {
        try {
            setDefaultUpdateMode.invoke(view, updateMode);
        } catch (Exception e) {
            //
        }
    }

    @Override
    public Canvas lockCanvas(SurfaceHolder surfaceHolder, int updateMode) {
        try {
            if (lockCanvas == null) {
                lockCanvas = surfaceHolder.getClass().getMethod("lockCanvas", int.class);
                lockCanvas.setAccessible(true);
            }

            return (Canvas) lockCanvas.invoke(surfaceHolder, updateMode);
        } catch (Exception e) {
            return surfaceHolder.lockCanvas();
        }
    }

    @Override
    public Canvas lockCanvas(SurfaceHolder surfaceHolder, Rect boundingBox, int updateMode) {
        try {
            if (lockCanvasRect == null) {
                lockCanvasRect = surfaceHolder.getClass().getMethod("lockCanvas", Rect.class, int.class);
                lockCanvasRect.setAccessible(true);
            }

            return (Canvas) lockCanvasRect.invoke(surfaceHolder, boundingBox, updateMode);
        } catch (Exception e) {
            return surfaceHolder.lockCanvas(boundingBox);
        }
    }

    @Override
    public void invalidate(View view, int updateMode) {
        try {
            invalidate.invoke(view, updateMode);
        } catch (Exception ignored) {
            view.invalidate();
        }
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
