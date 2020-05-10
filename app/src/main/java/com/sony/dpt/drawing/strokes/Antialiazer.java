package com.sony.dpt.drawing.strokes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;

import static android.graphics.Color.BLACK;

public class Antialiazer {

    private final Canvas canvas;
    private PointF start;
    private PointF end;
    private float penWidth;
    private Path path;
    private Paint paint;
    private RectF totalBoundingBox;
    private RectF boundingBox;
    private RectF temp;

    public Antialiazer(Canvas canvas, float penWidth) {
        this.canvas = canvas;
        this.penWidth = penWidth + 1;
        this.path = new Path();
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setAntiAlias(true); // That's what we're doing
        this.paint.setDither(false);
        this.paint.setColor(BLACK);
        this.totalBoundingBox = new RectF();
        this.boundingBox = new RectF();
        this.temp = new RectF();
    }

    public void addPoint(float x, float y) {
        if (start == null && end == null) {
            start = new PointF(x, y);
            end = new PointF(x, y);
            boundingBox.set(start.x, start.y, start.x, start.y);
        } else {
            end.set(x, y);
            boundingBox.union(end.x, end.y);
            boundingBox.inset(-6, -6);
        }
        totalBoundingBox.union(boundingBox);
    }

    public RectF resetTotal() {
        start = null;
        end = null;
        temp.set(totalBoundingBox);
        totalBoundingBox.setEmpty();
        return temp;
    }

    public void draw() {

        // We save the unclipped canvans
        canvas.save();
        // We make the canvas smaller to only draw where we care
        canvas.clipRect(boundingBox, Region.Op.REPLACE);

        // We have a dot, that easy to anti-aliaze: draw a big circle
        if (start.x == end.x && start.y == end.y) {
            path.addCircle(start.x, start.y, penWidth / 2.0f, Path.Direction.CW);
            canvas.drawPath(path, paint);
        } else {
            // The goal here is to project a point at penWidth / 2 on the perpendicular line to our path
            // Then we draw the path as a closed filled path rather than a line
            final float length = PointF.length(
                    start.x - end.x,
                    start.y - end.y
            );

            if (length > 0.0f) {
                final float halfWidth = penWidth / 2.0f;

                // Move on the perpendicular vector by half stroke width
                final float xOffset = (end.y - start.y) * halfWidth / length;
                final float yOffset = (end.x - start.x) * halfWidth / length;

                // We redraw our line as a filled rectangle
                path.moveTo(start.x + xOffset, start.y - yOffset);
                path.lineTo(end.x + xOffset, end.y - yOffset);
                path.lineTo(end.x - xOffset, end.y + yOffset);
                path.lineTo(start.x - xOffset, start.y + yOffset);
                path.lineTo(start.x + xOffset, start.y - yOffset);
                path.close();

                // Start and end point become nice circles
                path.addCircle(start.x, start.y, halfWidth, Path.Direction.CW);
                path.addCircle(end.x, end.y, halfWidth, Path.Direction.CW);

                // We draw on the clipped canvas
                canvas.drawPath(path, paint);
            }
        }
        path.rewind();
        start.set(end);
        // We restore the canvas to the full size
        canvas.restore();
    }

    public void setPenWidth(float penWidth) {
        this.penWidth = penWidth;
    }
}