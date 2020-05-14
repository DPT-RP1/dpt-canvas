package com.sony.dpt.drawing;

import android.graphics.Rect;
import android.os.PowerManager;
import android.view.View;

import com.sony.infras.dp_libraries.systemutil.SystemUtil;

public abstract class AbstractDrawingDelegate implements DrawingDelegate {

    protected Rect invalidationRectangle;

    protected final View view;
    protected SystemUtil.EpdUtil epdUtil;

    protected PowerManager.WakeLock wakeLock;

    protected AbstractDrawingDelegate(final View view) {
        this.view = view;
        this.invalidationRectangle = new Rect(0, 0, 0, 0);

        epdUtil = SystemUtil.getEpdUtilInstance();

    }
}
