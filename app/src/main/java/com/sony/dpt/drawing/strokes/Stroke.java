package com.sony.dpt.drawing.strokes;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.sony.dpt.drawing.geom.Circle;

public interface Stroke {

    /**
     * Returns a bounding box containing all the points of this stroke.
     * The box is pen width independent, don't forget therefore to inset it.
     *
     * @return
     */
    RectF getBoundingBox();

    /**
     * Returns the complete path of that stroke
     *
     * @return
     */
    Path getPath();

    void updatePath(Path path);

    boolean collides(Circle circle);

    void addPoint(PointF pointF);

    void addPoint(float x, float y);
}
