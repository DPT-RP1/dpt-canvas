package com.sony.dpt;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class DptGestureDetector extends GestureDetector {
    private final OnGestureListener onGestureListener;

    public DptGestureDetector(Context context, OnGestureListener listener) {
        super(context, listener);
        this.onGestureListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (ev.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                super.onTouchEvent(ev);
                return true;
            }
            return super.onTouchEvent(ev);
        }
        return false;
    }
}
