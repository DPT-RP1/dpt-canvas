package com.sony.dpt.drawing;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

public interface DrawingDelegate {

    void onDraw(Canvas canvas);

    boolean onTouchEvent(MotionEvent event);

    void invalidate(Rect dirty);
}
