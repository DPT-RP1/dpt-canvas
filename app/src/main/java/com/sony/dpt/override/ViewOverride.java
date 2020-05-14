package com.sony.dpt.override;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.View;

import java.lang.reflect.Method;

public class ViewOverride implements IViewOverride {

    private static Method invalidateRect;
    private static Method setDefaultUpdateMode;
    private static Method lockCanvas;

    private static ViewOverride instance;

    private final Rect converter;

    private boolean loaded;

    private ViewOverride() {
        converter = new Rect();
        try {
            invalidateRect = View.class.getMethod("invalidate", Rect.class, int.class);
            invalidateRect.setAccessible(true); // Small acceleration

            setDefaultUpdateMode = View.class.getMethod("setDefaultUpdateMode", int.class);
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

    public static void setDefaultUpdateMode(View view, int updateMode) {
        try {
            setDefaultUpdateMode.invoke(view, updateMode);
        } catch (Exception e) {
            //
        }
    }

    public static Canvas lockCanvas(SurfaceHolder surfaceHolder, int updateMode) {
        try {
            if (lockCanvas == null) {
                lockCanvas = surfaceHolder.getClass().getMethod("lockCanvas", int.class);
            }

            return (Canvas) lockCanvas.invoke(surfaceHolder, updateMode);
        } catch (Exception e) {
            return surfaceHolder.lockCanvas();
        }
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
