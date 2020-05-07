package com.sony.dpt.drawing.strokes;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SimpleStroke implements Stroke {

    private final List<PointF> points;
    private Rect boundingBox;
    private Path path;
    private PointF lastPoint;

    public SimpleStroke(PointF init) {
        points = new LinkedList<PointF>();
        boundingBox = new Rect((int) init.x, (int) init.y, (int) init.x, (int) init.y);
        path = new Path();
        path.moveTo(init.x, init.y);
        lastPoint = init;
    }

    @Override
    public void addPoint(PointF p) {
        boundingBox.union((int) p.x, (int) p.y);
        points.add(p);
        path.lineTo(p.x, p.y);
        lastPoint = p;
    }

    @Override
    public void addPoint(float x, float y) {
        addPoint(new PointF(x, y));
    }

    @Override
    public Collection<PointF> getPoints() {
        return points;
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
    public PointF getLastPoint() {
        return lastPoint;
    }
}
