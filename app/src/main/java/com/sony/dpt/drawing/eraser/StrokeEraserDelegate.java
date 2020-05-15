package com.sony.dpt.drawing.eraser;

import android.graphics.Bitmap;
import android.graphics.Canvas;
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

import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

/**
 * The goal of this class is to extend the behaviour of the eraser to delete entire strokes
 */
public class StrokeEraserDelegate extends AbstractEraserDelegate implements EraserDelegate {

    private StrikeDelegate strikeDelegate;
    private final Circle eraser;
    private Paint strokeEraserPaint;

    public StrokeEraserDelegate(int eraserWidth,
                                final View view, final Bitmap cachedLayer, final Canvas drawCanvas,
                                final StrikeDelegate strikeDelegate) {
        super(eraserWidth, view, cachedLayer, drawCanvas);

        this.strikeDelegate = strikeDelegate;
        this.eraser = new Circle();
        this.eraserRadius = eraserWidth;
        initStrokeErasure();
    }

    protected void initStrokeErasure() {
        strokeEraserPaint = new Paint();
        strokeEraserPaint.setAntiAlias(true);
        strokeEraserPaint.setDither(true);
        strokeEraserPaint.setAlpha(0);
        strokeEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        strokeEraserPaint.setStyle(Paint.Style.STROKE);
        strokeEraserPaint.setStrokeWidth(strikeDelegate.penWidth() + 3);
    }

    protected void handleMotion(final MotionEvent event) {
        super.handleMotion(event);
        detectCollisions();

        invalidate(invalidationRectangle, UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
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
        final int penWidth = strikeDelegate.maxPenWidth();

        eraser.setCenter(eraserPosition);
        eraser.setRadius(eraserWidth);
        eraser.setStrikeWidth(penWidth);

        StrokesContainer strokesContainer = strikeDelegate.getStrokesContainer();
        Collection<Stroke> candidates = strokesContainer.getAll();

        strokeEraserPaint.setStrokeWidth(penWidth + 3);

        Iterator<Stroke> it = candidates.iterator();

        while (it.hasNext()) {
            Stroke candidate = it.next();
            if (candidate.collides(eraser)) {
                expandInvalidation(candidate.getBoundingBox(), penWidth + 3);
                drawCanvas.drawPath(candidate.getPath(), strokeEraserPaint);
                it.remove();
            }
        }
    }

    @Override
    public float getStrokeWidth() {
        return strokeEraserPaint.getStrokeWidth();
    }

    @Override
    public void setStrikeDelegate(StrikeDelegate strikeDelegate) {
        this.strikeDelegate = strikeDelegate;
    }
}
