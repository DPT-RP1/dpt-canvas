package com.sony.dpt.drawing.strokes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.drawing.geom.Point2D;
import com.sony.dpt.drawing.rendering.DrawingThread;

import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_IGNORE;
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

/**
 * This is in charge of striking the pen on a view
 */
public class SimpleStrikeDelegate extends AbstractStrikeDelegate implements StrikeDelegate {

    private static final int TOLERANCE_NOISE_PX = 10;

    private int strokeWidth;

    private Stroke currentStroke;
    private final StrokesContainer strokesContainer;

    private final RectF boundingBox;

    private final PointF prevPosition;
    private final Antialiazer antializer;
    private final DrawingThread drawingThread;

    public SimpleStrikeDelegate(final int strokeWidth,
                                final View view,
                                final Bitmap cachedLayer,
                                final Canvas drawCanvas,
                                final DrawingThread drawingThread,
                                final StrokesContainer strokesContainer) {
        super(view, cachedLayer, drawCanvas);
        this.strokeWidth = strokeWidth;
        this.strokesContainer = strokesContainer;
        this.boundingBox = new RectF();
        this.prevPosition = new PointF();
        this.drawingThread = drawingThread;
        this.antializer = new Antialiazer(drawCanvas, cachedLayer, strokeWidth);
    }

    private void handlePoint(float x, float y) {
        currentStroke.addPoint(x, y);
        boundingBox.union(x, y);
        antializer.addPoint(x, y);
    }

    private void handleMotion(final MotionEvent event) {
        handleMotion(event, false);
    }

    private void handleMotion(final MotionEvent event, boolean silent) {
        int historySize = event.getHistorySize();
        for (int i = 0; i < historySize; i++) {
            handlePoint(
                    event.getHistoricalX(i),
                    event.getHistoricalY(i)
            );
        }

        handlePoint(lastX, lastY);

        // We inset by the stroke width so that the invalidation also encompass the full width of the line
        boundingBox.inset(-strokeWidth, -strokeWidth);

        if (!silent) {
            drawingThread.enqueueArea(boundingBox, UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        prevPosition.set(lastX, lastY);

        super.onTouchEvent(event);
        int action = event.getActionMasked();

        resetBoundingBox();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // We decide if we want to keep drawing on the same stroke as before
                tolerate(prevPosition);
                handleMotion(event);

                break;
            case MotionEvent.ACTION_MOVE:
                handleMotion(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleMotion(event, true);
                RectF lastStrokeBoundingBox = antializer.resetTotal(currentStroke);
                drawingThread.enqueueArea(lastStrokeBoundingBox, UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_IGNORE);
                strokesContainer.persistDrawing();
                break;
        }
        return true;
    }

    private void resetBoundingBox() {
        boundingBox.set(lastX, lastY, lastX, lastY);
        boundingBox.union(prevPosition.x, prevPosition.y);
    }

    private void tolerate(PointF prevPosition) {
        if (Point2D.distance(prevPosition.x, prevPosition.y, lastX, lastY) > TOLERANCE_NOISE_PX) {
            currentStroke = new SimpleStroke(lastX, lastY);
            strokesContainer.setDrawingStroke(currentStroke);
            boundingBox.set(lastX, lastY, lastX, lastY);
        }
    }

    @Override
    public boolean pressureSensitive() {
        return false;
    }

    @Override
    public void setPenWidth(int penWidth) {
        this.strokeWidth = penWidth;
        antializer.setPenWidth(penWidth);
        // TODO: test: epdUtil.changeDhwStrokeWidth(penWidth, penWidth);
    }

    @Override
    public int penWidth() {
        return strokeWidth;
    }

    @Override
    public Paint getPaint() {
        return antializer.getPaint();
    }

    @Override
    public StrokesContainer getStrokesContainer() {
        return strokesContainer;
    }
}
