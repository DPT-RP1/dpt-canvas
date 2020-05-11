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

    private static ViewOverride instance;


    private boolean loaded;

    private ViewOverride() {
        try {
            invalidateRect = View.class.getMethod("invalidate", Rect.class, int.class);
            invalidate = View.class.getMethod("invalidate", int.class);
            setDefaultUpdateMode = View.class.getMethod("setDefaultUpdateMode", int.class);
            lockCanvas = SurfaceHolder.class.getMethod("lockCanvas", int.class);

            invalidateRect.setAccessible(true); // Small acceleration
            invalidate.setAccessible(true);
            setDefaultUpdateMode.setAccessible(true);
            lockCanvas.setAccessible(true);
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

    public void invalidate(View view, RectF rect, int updateMode) {
        Rect temp = new Rect();
        rect.roundOut(temp);
        invalidate(view, temp, updateMode);
    }

    @Override
    public void setDefaultUpdateMode(View view, int updateMode) {
        try {
            setDefaultUpdateMode.invoke(view, updateMode);
        } catch (Exception ignored) {

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

    @Override
    public Canvas lockCanvas(SurfaceHolder surfaceHolder, int updateMode) {
        try {
            return (Canvas) lockCanvas.invoke(surfaceHolder, updateMode);
        } catch (Exception ignored) {
            return surfaceHolder.lockCanvas();
        }
    }
}
