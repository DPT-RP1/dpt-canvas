package com.sony.dpt.drawing.geom;

import android.graphics.PointF;

public class Point2D {

    /**
     * Return the square of the distance between two points.
     *
     * @param x1 the x coordinate of point 1
     * @param y1 the y coordinate of point 1
     * @param x2 the x coordinate of point 2
     * @param y2 the y coordinate of point 2
     * @return (x2 - x1)^2 + (y2 - y1)^2
     */
    public static double distanceSq(double x1, double y1, double x2, double y2) {
        x2 -= x1;
        y2 -= y1;
        return x2 * x2 + y2 * y2;
    }

    /**
     * Return the distance between two points.
     *
     * @param x1 the x coordinate of point 1
     * @param y1 the y coordinate of point 1
     * @param x2 the x coordinate of point 2
     * @param y2 the y coordinate of point 2
     * @return the distance from (x1,y1) to (x2,y2)
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(distanceSq(x1, y1, x2, y2));
    }

    public static double distance(PointF p1, PointF p2) {
        return distance(p1.x, p1.y, p2.x, p2.y);
    }

}
