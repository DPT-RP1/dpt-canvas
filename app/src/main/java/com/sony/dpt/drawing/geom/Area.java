package com.sony.dpt.drawing.geom;

import android.graphics.Rect;

public class Area {
    public int updateMode;
    public Rect boundingBox;

    public Area(Rect boundingBox, int updateMode) {
        this.updateMode = updateMode;
        this.boundingBox = boundingBox;
    }
}
