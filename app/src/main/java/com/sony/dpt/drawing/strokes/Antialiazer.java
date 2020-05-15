package com.sony.dpt.drawing.strokes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Region;

import java.util.concurrent.ConcurrentLinkedQueue;

import static android.graphics.Color.BLACK;

public class Antialiazer {

    private final Canvas canvas;
    private PointF start;
    private PointF end;
    private float penWidth;
    private final Path path;
    private final Paint paint;
    private final RectF totalBoundingBox;
    private final RectF boundingBox;
    private final RectF temp;

    private AntialiazingThread antialiazingThread;

    public Antialiazer(final Canvas canvas, final Bitmap cachedLayer, float penWidth) {
        this.canvas = canvas;
        this.penWidth = penWidth;
        this.path = new Path();
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setAntiAlias(false);
        this.paint.setDither(false);
        this.paint.setColor(BLACK);
        this.totalBoundingBox = new RectF();
        this.boundingBox = new RectF();
        this.temp = new RectF();

        antialiazingThread = new AntialiazingThread(canvas, cachedLayer);
    }

    public void addPoint(float x, float y) {
        if (start == null && end == null) {
            start = new PointF(x, y);
            end = new PointF(x, y);
            boundingBox.set(start.x, start.y, start.x, start.y);
            if (!antialiazingThread.isAlive()) {
                antialiazingThread.start();
            }
        } else {
            end.set(x, y);
            boundingBox.union(end.x, end.y);
        }
        totalBoundingBox.union(boundingBox);
        draw();
    }

    public RectF resetTotal() {
        start = null;
        end = null;

        antialiazingThread.apply();
        antialiazingThread.resetStroke();

        temp.set(totalBoundingBox);
        if (!totalBoundingBox.isEmpty()) {
            temp.inset(-penWidth, -penWidth);
            totalBoundingBox.setEmpty();
        }
        return temp;
    }

    private void draw() {

        // We save the unclipped canvans
        canvas.save();
        // We make the canvas smaller to only draw where we care
        temp.set(boundingBox);
        temp.inset(-penWidth, -penWidth);
        canvas.clipRect(temp, Region.Op.REPLACE);

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
                final float halfWidth = (float) Math.ceil(penWidth / 2.0f);

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
                antialiazingThread.enqueueForAntialiazing(path);
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

    public Paint getPaint() {
        return paint;
    }

    public static class AntialiazingThread extends Thread {

        private final Paint antializedPaint;
        private final Canvas antializer;
        private final Bitmap antializedBitmap;
        private final Bitmap cachedLayer;
        private final RectF clipBounds;
        private final Canvas drawingCanvas;
        private final ConcurrentLinkedQueue<Path> paths;

        public AntialiazingThread(final Canvas drawingCanvas, final Bitmap cachedLayer) {
            this.cachedLayer = cachedLayer;
            this.clipBounds = new RectF();
            antializedPaint = new Paint(BLACK);
            antializedPaint.setDither(true);
            antializedPaint.setAntiAlias(true);

            antializedBitmap = Bitmap.createBitmap(cachedLayer.getWidth(), cachedLayer.getHeight(), Bitmap.Config.ARGB_8888);
            antializer = new Canvas(antializedBitmap);

            this.drawingCanvas = drawingCanvas;
            this.paths = new ConcurrentLinkedQueue<Path>();
        }

        public void resetStroke() {
            clipBounds.setEmpty();
            antializer.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            paths.clear();
        }

        public void apply() {
            drawingCanvas.drawBitmap(antializedBitmap, 0, 0, null);
            resetStroke();
        }

        public void enqueueForAntialiazing(final Path path) {
            paths.add(new Path(path));
            synchronized (this) {
                this.notify();
            }
        }

        @Override
        public void run() {
            boolean interrupted = false;
            while (!interrupted || !paths.isEmpty()) {

                while (!paths.isEmpty()) {
                    Path next = paths.poll();
                    antializer.drawPath(next, antializedPaint);
                }

                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        }

        public boolean isReady() {
            return paths.isEmpty();
        }
    }
}