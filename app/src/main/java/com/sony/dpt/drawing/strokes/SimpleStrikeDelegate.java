package com.sony.dpt.drawing.strokes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.drawing.geom.Point2D;
import com.sony.dpt.utils.WakelockUtils;

import static android.graphics.Color.BLACK;
import static android.graphics.Paint.Style.STROKE;
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

/**
 * This is in charge of striking the pen on a view
 */
public class SimpleStrikeDelegate extends AbstractStrikeDelegate implements StrikeDelegate {

    private static final int TOLERANCE_NOISE_PX = 10;

    private Paint paint;

    private int strokeWidth;

    private Stroke currentStroke;
    private StrokesContainer strokesContainer;

    private final RectF boundingBox;

    private final Path drawingPath;
    private final WakelockUtils wakelockUtils;
    private PointF prevPosition;
    private Antialiazer antializer;

    public SimpleStrikeDelegate(final int strokeWidth,
                                final View view,
                                final Bitmap cachedLayer,
                                final Canvas drawCanvas,
                                final StrokesContainer strokesContainer,
                                final WakelockUtils wakelockUtils) {
        super(view, cachedLayer, drawCanvas);
        this.strokeWidth = strokeWidth;
        this.strokesContainer = strokesContainer;
        this.boundingBox = new RectF();
        this.drawingPath = new Path();
        this.prevPosition = new PointF();
        this.wakelockUtils = wakelockUtils;
        this.antializer = new Antialiazer(drawCanvas, strokeWidth);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(BLACK);
        paint.setAntiAlias(false);
        paint.setDither(false);
        paint.setStyle(STROKE);
        paint.setStrokeWidth(strokeWidth);
    }

    private void handlePoint(float x, float y) {
        currentStroke.addPoint(x, y);
        drawingPath.lineTo(x, y);
        boundingBox.union(x, y);
        antializer.addPoint(x, y);
    }

    private void handleMotion(final MotionEvent event) {
        int historySize = event.getHistorySize();
        for (int i = 0; i < historySize; i++) {
            handlePoint(
                    event.getHistoricalX(i),
                    event.getHistoricalY(i)
            );
        }

        handlePoint(lastX, lastY);

        //drawCanvas.drawPath(drawingPath, paint);
        antializer.draw();
        drawingPath.rewind();
        drawingPath.moveTo(lastX, lastY);

        // We inset by the stroke width so that the invalidation also encompass the full width of the line
        int currentStrokeWidth = (int) paint.getStrokeWidth();
        boundingBox.inset(-currentStrokeWidth, -currentStrokeWidth);
        //invalidate(boundingBox);
    }

    public void onDraw(Canvas canvas) {
        //canvas.drawColor(-1);
        //super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        prevPosition.set(lastX, lastY);

        super.onTouchEvent(event);
        int action = event.getActionMasked();

        resetBoundingBox();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                wakelockUtils.acquire();
                // We decide if we want to keep drawing on the same stroke as before
                if (!tolerate(prevPosition)) {
                    //handleMotion(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                handleMotion(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleMotion(event);
                strokesContainer.persistDrawing();
                wakelockUtils.release();
                break;
        }
        return true;
    }

    private void resetBoundingBox() {
        boundingBox.set(lastX, lastY, lastX, lastY);
        boundingBox.union(prevPosition.x, prevPosition.y);
    }

    private boolean tolerate(PointF prevPosition) {
        if (Point2D.distance(prevPosition.x, prevPosition.y, lastX, lastY) > TOLERANCE_NOISE_PX) {
            // This will expand the invalidation to the previous strike, this should be async instead
            if (currentStroke != null) {
                boundingBox.union(currentStroke.getBoundingBox());
                boundingBox.inset(-maxPenWidth(), -maxPenWidth());
            }

            currentStroke = new SimpleStroke(lastX, lastY);
            strokesContainer.setDrawingStroke(currentStroke);
            drawingPath.rewind();
            drawingPath.moveTo(lastX, lastY);
            boundingBox.set(lastX, lastY, lastX, lastY);

            RectF lastStrokeBoundingBox = antializer.resetTotal();
            if (!lastStrokeBoundingBox.isEmpty()) {
                //invalidate(lastStrokeBoundingBox, UPDATE_MODE_NOWAIT_GC16_PARTIAL_SP1_IGNORE);
                return true;
            }
        }
        return false;
    }

    @Override
    public void invalidate(RectF dirty) {
        viewOverride.invalidate(view, dirty, UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);
    }

    @Override
    public boolean pressureSensitive() {
        return false;
    }

    @Override
    public void setPenWidth(int penWidth) {
        this.strokeWidth = penWidth;
        paint.setStrokeWidth(penWidth);
        // TODO: test: epdUtil.changeDhwStrokeWidth(penWidth, penWidth);
    }

    @Override
    public int penWidth() {
        return strokeWidth;
    }

    @Override
    public Paint getPaint() {
        return paint;
    }

    @Override
    public StrokesContainer getStrokesContainer() {
        return strokesContainer;
    }
}
