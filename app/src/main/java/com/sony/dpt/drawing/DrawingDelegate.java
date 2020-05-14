package com.sony.dpt.drawing;

import android.graphics.RectF;
import android.view.MotionEvent;

public interface DrawingDelegate {

    boolean onTouchEvent(MotionEvent event);

    void invalidate(RectF dirty);
}
