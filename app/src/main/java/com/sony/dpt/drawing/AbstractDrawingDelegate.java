package com.sony.dpt.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.override.IViewOverride;
import com.sony.dpt.override.ViewOverride;
import com.sony.infras.dp_libraries.systemutil.SystemUtil;

import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_SP2;
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

public abstract class AbstractDrawingDelegate implements DrawingDelegate {

    protected float lastX;
    protected float lastY;

    protected Bitmap cachedLayer;
    protected Canvas drawCanvas;

    protected final View view;
    protected SystemUtil.EpdUtil epdUtil;

    protected PointF lastPosition;

    protected static IViewOverride viewOverride;

    protected AbstractDrawingDelegate(final View view, Bitmap cachedLayer, Canvas drawCanvas) {
        this.cachedLayer = cachedLayer;
        this.drawCanvas = drawCanvas;
        this.view = view;
        this.lastPosition = new PointF();

        epdUtil = SystemUtil.getEpdUtilInstance();

        if (viewOverride == null) viewOverride = ViewOverride.getInstance();
    }

    public PointF lastPosition() {
        lastPosition.set(lastX, lastY);
        return lastPosition;
    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(cachedLayer, 0.0F, 0.0F, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        lastX = event.getX();
        lastY = event.getY();
        lastPosition.set(lastX, lastY);
        return false;
    }

    @Override
    public void invalidate(Rect dirty) {
        viewOverride.invalidate(view, dirty, UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);
    }

    public void invalidatePartialGC16(Rect dirty) {
        viewOverride.invalidate(view, dirty, UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_SP2);
    }

    public void invalidate(Rect dirty, int mode) {
        viewOverride.invalidate(view, dirty, mode);
    }
}
