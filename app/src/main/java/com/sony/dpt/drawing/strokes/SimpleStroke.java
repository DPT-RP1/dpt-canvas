package com.sony.dpt.drawing.strokes;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

import com.sony.dpt.drawing.geom.Circle;

import java.util.Objects;

public class SimpleStroke implements Stroke {

    private static int currentId = 0;

    private final RectF boundingBox;
    private final Path path;
    private final Path circlePath;
    private final int id;

    private final static Region clip = new Region();
    private final static Region pathRegion = new Region();
    private final static Region circleRegion = new Region();


    public SimpleStroke(float x, float y) {
        boundingBox = new RectF(x, y, x, y);
        path = new Path();
        path.moveTo(x, y);
        circlePath = new Path();
        this.id = currentId++;
    }

    @Override
    public RectF getBoundingBox() {
        return boundingBox;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public void updatePath(Path path) {
        this.path.addPath(path);
    }

    @Override
    public boolean collides(final Circle circle) {
        // We clip by the circle entire bounding box, which is the maximal area we ever get a chance
        // to find a collision in
        clip.set(circle.getBoundingBox());

        pathRegion.setPath(path, clip);

        circlePath.rewind();
        circlePath.addCircle(circle.getCenter().x, circle.getCenter().y, circle.getRadius(), Path.Direction.CW);

        circleRegion.setPath(circlePath, clip);
        return !pathRegion.quickReject(circleRegion) && pathRegion.op(circleRegion, Region.Op.INTERSECT);
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
