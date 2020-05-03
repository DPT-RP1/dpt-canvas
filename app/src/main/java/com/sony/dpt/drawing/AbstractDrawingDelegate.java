package com.sony.dpt.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import com.sony.infras.dp_libraries.systemutil.SystemUtil;

public abstract class AbstractDrawingDelegate implements DrawingDelegate {

    protected float lastX;
    protected float lastY;

    protected Rect invalidationRectangle;

    protected Bitmap cachedLayer;
    protected Canvas drawCanvas;

    protected final View view;
    protected SystemUtil.EpdUtil epdUtil;

    protected AbstractDrawingDelegate(final View view, Bitmap cachedLayer, Canvas drawCanvas) {
        this.cachedLayer = cachedLayer;
        this.drawCanvas = drawCanvas;
        this.view = view;
        this.invalidationRectangle = new Rect(0, 0, 0, 0);

        epdUtil = SystemUtil.getEpdUtilInstance();

    }
}
