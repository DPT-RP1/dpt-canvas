package com.sony.dpt.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.sony.dpt.override.IViewOverride;
import com.sony.dpt.override.ViewOverride;
import com.sony.infras.dp_libraries.systemutil.SystemUtil;

public abstract class AbstractDrawingDelegate implements DrawingDelegate {

    protected float lastX;
    protected float lastY;

    protected Bitmap cachedLayer;
    protected Canvas drawCanvas;

    protected final View view;
    protected SystemUtil.EpdUtil epdUtil;

    protected static IViewOverride viewOverride;

    protected AbstractDrawingDelegate(final View view, Bitmap cachedLayer, Canvas drawCanvas) {
        this.cachedLayer = cachedLayer;
        this.drawCanvas = drawCanvas;
        this.view = view;

        epdUtil = SystemUtil.getEpdUtilInstance();

        if (viewOverride == null) viewOverride = ViewOverride.getInstance();
    }
}
