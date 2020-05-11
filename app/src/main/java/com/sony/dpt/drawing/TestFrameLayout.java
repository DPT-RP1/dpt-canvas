package com.sony.dpt.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class TestFrameLayout extends RelativeLayout {
    int count = 0;

    public TestFrameLayout(Context context) {
        super(context);
    }

    public TestFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TestFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void draw(Canvas canvas) {

        if (count < 20) {
            canvas.drawColor(-1);
            super.draw(canvas);
            count++;
        }
        System.out.println("Draw on Layout: " + canvas.getClipBounds());
    }

}
