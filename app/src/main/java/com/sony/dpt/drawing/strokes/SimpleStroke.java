package com.sony.dpt.drawing.strokes;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;

import com.sony.dpt.drawing.geom.Circle;

import java.util.Objects;

public class SimpleStroke implements Stroke {

    private static int currentId = 0;

    private final Rect boundingBox;
    private final Path path;
    private final Path circlePath;
    private final int id;

    public SimpleStroke(float x, float y) {
        boundingBox = new Rect((int) x, (int) y, (int) x, (int) y);
        path = new Path();
        path.moveTo(x, y);
        circlePath = new Path();
        this.id = currentId++;
    }

    @Override
    public Rect getBoundingBox() {
        return boundingBox;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public boolean collides(final Circle circle) {
        // We clip by the circle entire bounding box, which is the maximal area we ever get a chance
        // to find a collision in
        Region clip = new Region(circle.getBoundingBox());

        Region region1 = new Region();
        region1.setPath(path, clip);

        Region region2 = new Region();

        circlePath.rewind();
        circlePath.addCircle(circle.getCenter().x, circle.getCenter().y, circle.getRadius(), Path.Direction.CW);
        region2.setPath(circlePath, clip);
        return !region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT);
    }

    @Override
    public void addPoint(PointF pointF) {
        addPoint(pointF.x, pointF.y);
    }

    @Override
    public void addPoint(float x, float y) {
        path.lineTo(x, y);
        boundingBox.union((int) x, (int) y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleStroke that = (SimpleStroke) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
