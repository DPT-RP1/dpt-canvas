package com.sony.dpt.override;

import android.graphics.Rect;
import android.view.View;

import com.sony.dpt.drawing.DrawableView;

import java.lang.reflect.Method;

public class ViewOverride implements SonyOverride<View> {

    private static Method invalidateRect;
    private static Method setDefaultUpdateMode;

    static {
        try {
            invalidateRect = DrawableView.class.getMethod("invalidate", Rect.class, int.class);
            invalidateRect.setAccessible(true); // Small acceleration

            setDefaultUpdateMode = View.class.getMethod("setDefaultUpdateMode", int.class);
            setDefaultUpdateMode.setAccessible(true);

        } catch (Exception ignored) {}
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
}
