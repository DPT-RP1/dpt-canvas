package com.sony.dpt.drawing.strokes;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.Collection;

public interface Stroke {

    void addPoint(final PointF p);

    void addPoint(final float x, final float y);

    Collection<PointF> getPoints();

    /**
     * Returns a bounding box containing all the points of this stroke.
     * The box is pen width independent, don't forget therefore to inset it.
     *
     * @return
     */
    Rect getBoundingBox();

    /**
     * Returns the complete path of that stroke
     *
     * @return
     */
    Path getPath();

    /**
     * End points of the stroke
     *
     * @return
     */
    PointF getLastPoint();

}
