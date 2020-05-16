package com.sony.dpt.drawing;

import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

public interface DrawingDelegate {

    boolean onTouchEvent(MotionEvent event);

    boolean pressureSensitive();

    void setPenWidth(int penWidth);

    int penWidth();

    int maxPenWidth();

    PointF lastPosition();

    Paint getPaint();

    boolean nativeDhw();
}
