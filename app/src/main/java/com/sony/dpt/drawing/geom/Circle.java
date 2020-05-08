package com.sony.dpt.drawing.geom;

import android.graphics.PointF;

public class Circle {
    private PointF center;
    private float radius;

    public Circle() {

    }

    public Circle(PointF center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public PointF getCenter() {
        return center;
    }

    public void setCenter(PointF center) {
        this.center = center;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
