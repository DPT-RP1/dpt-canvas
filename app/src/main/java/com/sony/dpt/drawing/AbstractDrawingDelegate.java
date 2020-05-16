package com.sony.dpt.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import com.sony.infras.dp_libraries.systemutil.SystemUtil;

public abstract class AbstractDrawingDelegate implements DrawingDelegate {

    protected float lastX;
    protected float lastY;

    protected final Bitmap cachedLayer;
    protected final Canvas drawCanvas;

    protected final View view;
    protected final SystemUtil.EpdUtil epdUtil;

    protected final PointF lastPosition;

    protected AbstractDrawingDelegate(final View view, final Bitmap cachedLayer, final Canvas drawCanvas) {
        this.cachedLayer = cachedLayer;
        this.drawCanvas = drawCanvas;
        this.view = view;
        this.lastPosition = new PointF();

        epdUtil = SystemUtil.getEpdUtilInstance();
    }

    public PointF lastPosition() {
        lastPosition.set(lastX, lastY);
        return lastPosition;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        lastX = event.getX();
        lastY = event.getY();
        lastPosition.set(lastX, lastY);
        return false;
    }
}
