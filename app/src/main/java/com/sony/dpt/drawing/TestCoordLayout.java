package com.sony.dpt.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class TestCoordLayout extends FrameLayout {
    int count = 0;

    public TestCoordLayout(Context context) {
        super(context);
    }

    public TestCoordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestCoordLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        if (count < 20) {
            canvas.drawColor(-1);
            super.draw(canvas);
            count++;
        }
        System.out.println("Draw coord");
    }
}
