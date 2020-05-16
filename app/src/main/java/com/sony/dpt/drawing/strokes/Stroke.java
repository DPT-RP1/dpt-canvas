package com.sony.dpt.drawing.strokes;

import android.graphics.Path;
import android.graphics.RectF;

import com.sony.dpt.drawing.geom.Circle;

public interface Stroke {

    RectF getBoundingBox();

    Path getPath();

    void updatePath(Path path);

    boolean collides(Circle circle);

    void addPoint(float x, float y);
}
