package com.sony.dpt.override;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.View;

public interface IViewOverride extends SonyOverride<View> {
    void invalidate(View view, Rect rect, int updateMode);

    void invalidate(View view, RectF rect, int updateMode);

    void setDefaultUpdateMode(View view, int updateMode);

    void invalidate(View view, int updateMode);

    Canvas lockCanvas(SurfaceHolder surfaceHolder, int updateMode);

    Canvas lockCanvas(SurfaceHolder surfaceHolder, Rect boundingBox, int updateMode);
}
