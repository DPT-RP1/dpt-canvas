package com.sony.dpt.drawing.strokes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.sony.dpt.drawing.AbstractDrawingDelegate;

public abstract class AbstractStrikeDelegate extends AbstractDrawingDelegate {

    protected AbstractStrikeDelegate(View view, Bitmap cachedLayer, Canvas drawCanvas) {
        super(view, cachedLayer, drawCanvas);
    }

    @Override
    public boolean nativeDhw() {
        return true;
    }

    @Override
    public int maxPenWidth() {
        return penWidth();
    }
}
