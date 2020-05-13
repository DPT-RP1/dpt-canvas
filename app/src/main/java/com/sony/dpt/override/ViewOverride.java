package com.sony.dpt.override;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.View;

import com.sony.dpt.drawing.DrawableView;

import java.lang.reflect.Method;

public class ViewOverride implements SonyOverride<View> {

    private static Method invalidateRect;
    private static Method setDefaultUpdateMode;

    private static Method lockCanvas;

    static {
        try {
            invalidateRect = DrawableView.class.getMethod("invalidate", Rect.class, int.class);
            invalidateRect.setAccessible(true); // Small acceleration

            setDefaultUpdateMode = View.class.getMethod("setDefaultUpdateMode", int.class);
            setDefaultUpdateMode.setAccessible(true);

        } catch (Exception ignored) {
        }
    }

    private final View view;

    public ViewOverride(final View view) {
        this.view = view;
    }

    public void invalidate(Rect rect, int updateMode) {
        invalidate(view, rect, updateMode);
    }

    public static void invalidate(View view, Rect rect, int updateMode) {
        try {
            invalidateRect.invoke(view, rect, updateMode);
        } catch (Exception ignored) {
        }
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
            //
            return null;
        }
    }
}
