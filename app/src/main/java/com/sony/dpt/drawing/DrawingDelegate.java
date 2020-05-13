package com.sony.dpt.drawing;

import android.graphics.Rect;
import android.view.MotionEvent;

public interface DrawingDelegate {

    boolean onTouchEvent(MotionEvent event);

    void invalidate(Rect dirty);
}
