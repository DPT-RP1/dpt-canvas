package com.sony.dpt.drawing.geom;

/**
 * Direct copy of JDK functions.
 */
public class Line2D {

    /**
     * Measures the square of the shortest distance from the reference point
     * to a point on the line segment. If the point is on the segment, the
     * result will be 0.
     *
     * @param x1 the first x coordinate of the segment
     * @param y1 the first y coordinate of the segment
     * @param x2 the second x coordinate of the segment
     * @param y2 the second y coordinate of the segment
     * @param px the x coordinate of the point
     * @param py the y coordinate of the point
     * @return the square of the distance from the point to the segment
     * @see #ptSegDist(double, double, double, double, double, double)
     */
    public static double ptSegDistSq(double x1, double y1, double x2, double y2,
                                     double px, double py) {
        double pd2 = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);

        double x, y;
        if (pd2 == 0) {
            // Points are coincident.
            x = x1;
            y = y2;
        } else {
            double u = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / pd2;

            if (u < 0) {
                // "Off the end"
                x = x1;
                y = y1;
            } else if (u > 1.0) {
                x = x2;
                y = y2;
            } else {
                x = x1 + u * (x2 - x1);
                y = y1 + u * (y2 - y1);
            }
        }

        return (x - px) * (x - px) + (y - py) * (y - py);
    }

    /**
     * Measures the shortest distance from the reference point to a point on
     * the line segment. If the point is on the segment, the result will be 0.
     *
     * @param x1 the first x coordinate of the segment
     * @param y1 the first y coordinate of the segment
     * @param x2 the second x coordinate of the segment
     * @param y2 the second y coordinate of the segment
     * @param px the x coordinate of the point
     * @param py the y coordinate of the point
     * @return the distance from the point to the segment
     * @see #ptSegDistSq(double, double, double, double, double, double)
     */
    public static double ptSegDist(double x1, double y1, double x2, double y2,
                                   double px, double py) {
        return Math.sqrt(ptSegDistSq(x1, y1, x2, y2, px, py));
    }

}
