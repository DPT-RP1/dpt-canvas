package com.sony.dpt.drawing.strokes;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

import com.sony.dpt.drawing.geom.Circle;

public class SimpleStroke implements Stroke {

    private final Rect boundingBox;
    private final Path path;
    private final RectF tempRectF;
    private final Path circlePath;

    public SimpleStroke(PointF init) {
        this(init.x, init.y);
    }

    public SimpleStroke(float x, float y) {
        boundingBox = new Rect((int) x, (int) y, (int) x, (int) y);
        path = new Path();
        path.moveTo(x, y);
        tempRectF = new RectF();
        circlePath = new Path();
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
        Region clip = new Region(
                boundingBox.left,
                boundingBox.top,
                boundingBox.right,
                boundingBox.bottom
        );

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

}
