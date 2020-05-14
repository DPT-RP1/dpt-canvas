package com.sony.dpt.drawing.eraser;

import com.sony.dpt.drawing.DrawingDelegate;
import com.sony.dpt.drawing.strokes.StrikeDelegate;

public interface EraserDelegate extends DrawingDelegate {
    float getStrokeWidth();

    void setStrikeDelegate(StrikeDelegate strikeDelegate);
}
