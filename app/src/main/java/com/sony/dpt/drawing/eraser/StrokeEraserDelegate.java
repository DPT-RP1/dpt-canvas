package com.sony.dpt.drawing.eraser;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.drawing.geom.Circle;
import com.sony.dpt.drawing.strokes.StrikeDelegate;
import com.sony.dpt.drawing.strokes.Stroke;
import com.sony.dpt.drawing.strokes.StrokesContainer;

import java.util.Collection;
import java.util.Iterator;

/**
 * The goal of this class is to extend the behaviour of the eraser to delete entire strokes
 */
public class StrokeEraserDelegate extends AbstractEraserDelegate implements EraserDelegate {

    private final StrikeDelegate strikeDelegate;
    private final Circle eraser;
    private Paint strokeEraserPaint;
    private boolean isErasing = false;

    private Paint eraserPaint;

    private Paint circlePaint;


    public StrokeEraserDelegate(int eraserWidth,
                                final View view, final Bitmap cachedLayer, final Canvas drawCanvas,
                                final StrikeDelegate strikeDelegate) {
        super(view, cachedLayer, drawCanvas);

        this.strikeDelegate = strikeDelegate;
        this.eraser = new Circle();
        this.eraserRadius = eraserWidth;

        init();
    }

    private void init() {
        eraserRadius = 20;
        eraserPaint = new Paint();
        eraserPaint.setAntiAlias(false);
        eraserPaint.setAlpha(0);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        circlePaint = new Paint();
        circlePaint.setAntiAlias(false);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(CIRCLE_STROKE_WIDTH);

        strokeEraserPaint = new Paint();
        strokeEraserPaint.setAntiAlias(false);
        strokeEraserPaint.setAlpha(0);
        strokeEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        strokeEraserPaint.setStyle(Paint.Style.STROKE);
        strokeEraserPaint.setStrokeWidth(6f);
    }

    private void handleMotion(final MotionEvent event) {
        for (int i = 0; i < event.getHistorySize(); i++) {
            addToInvalidate(event.getHistoricalX(i), event.getHistoricalY(i));
        }
        addToInvalidate(event.getX(), event.getY());

        // We redraw the part that we drew black last time.
        temp.set(invalidationRectangle);
        temp.union(previousInvalidationRectangle);

        temp.inset(
                -eraserRadius - CIRCLE_STROKE_WIDTH,
                -eraserRadius - CIRCLE_STROKE_WIDTH
        );
        previousInvalidationRectangle.set(invalidationRectangle);
        invalidationRectangle.set(temp);

        drawCanvas.drawCircle(lastX, lastY, eraserRadius, eraserPaint);

        detectCollisions();

        invalidate(invalidationRectangle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isErasing) canvas.drawCircle(lastX, lastY, eraserRadius + 1, circlePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        epdUtil.setDhwState(false);
        isErasing = true;
        super.onTouchEvent(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setRectToCurrentPoint(finalEraseInvalidationRectangle);
                setRectToCurrentPoint(invalidationRectangle);
                setRectToCurrentPoint(previousInvalidationRectangle);
                handleMotion(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMotion(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleMotion(event);
                isErasing = false;
                invalidatePartialGC16(finalEraseInvalidationRectangle);
                finalEraseInvalidationRectangle.setEmpty();
                previousInvalidationRectangle.setEmpty();
                invalidationRectangle.setEmpty();
                break;
        }

        return true;
    }

    @Override
    public void setPenWidth(int penWidth) {
        strokeEraserPaint.setStrokeWidth(penWidth);
    }

    @Override
    public Paint getPaint() {
        return strokeEraserPaint;
    }

    /**
     * Finds a list of strokes currently colliding with the eraser shape, that we assume is
     * a circle for now.
     */
    private void detectCollisions() {
        final PointF eraserPosition = lastPosition();
        final float eraserWidth = penWidth();

        eraser.setCenter(eraserPosition);
        eraser.setRadius(eraserWidth);

        StrokesContainer strokesContainer = strikeDelegate.getStrokesContainer();
        Collection<Stroke> candidates = strokesContainer.getAll();

        int penWidth = strikeDelegate.penWidth();
        strokeEraserPaint.setStrokeWidth(penWidth);

        Iterator<Stroke> it = candidates.iterator();

        while (it.hasNext()) {
            Stroke candidate = it.next();
            if (candidate.collides(eraser)) {
                addToInvalidate(candidate.getBoundingBox(), penWidth);
                drawCanvas.drawPath(candidate.getPath(), strokeEraserPaint);

                it.remove();
            }
        }
    }

    @Override
    public float getStrokeWidth() {
        return strokeEraserPaint.getStrokeWidth();
    }
}
