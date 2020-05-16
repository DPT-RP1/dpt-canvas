package com.sony.dpt.drawing.geom;

import android.graphics.PointF;
import android.graphics.Rect;

public class Circle {
    private PointF center;
    private float radius;
    private final Rect temp;
    private float strikeWidth;

    public Circle() {
        temp = new Rect();
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

    public void setStrikeWidth(float strikeWidth) {
        this.strikeWidth = strikeWidth;
    }

    public Rect getBoundingBox() {
        temp.set(
                (int) (center.x - radius - strikeWidth),
                (int) (center.y - radius - strikeWidth),
                (int) (center.x + radius + strikeWidth),
                (int) (center.y + radius + strikeWidth)
        );
        return temp;
    }
}
