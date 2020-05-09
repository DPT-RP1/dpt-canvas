package com.sony.dpt.override;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import java.lang.reflect.Method;

public class ViewOverride implements IViewOverride {

    private static Method invalidateRect;
    private static ViewOverride instance;

    private boolean loaded;

    private ViewOverride() {
        try {
            invalidateRect = View.class.getMethod("invalidate", Rect.class, int.class);
            invalidateRect.setAccessible(true); // Small acceleration
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
    public boolean isLoaded() {
        return loaded;
    }
}
