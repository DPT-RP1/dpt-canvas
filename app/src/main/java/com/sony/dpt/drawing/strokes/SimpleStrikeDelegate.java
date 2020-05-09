package com.sony.dpt.drawing.strokes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import static android.graphics.Color.BLACK;
import static android.graphics.Paint.Style.STROKE;
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

/**
 * This is in charge of striking the pen on a view
 */
public class SimpleStrikeDelegate extends AbstractStrikeDelegate implements StrikeDelegate {

    private Paint paint;

    private int strokeWidth;

    private Stroke currentStroke;
    private StrokesContainer strokesContainer;

    private final Rect boundingBox;

    private final Path drawingPath;
    private long lastUpMs;

    public SimpleStrikeDelegate(final int strokeWidth,
                                final View view,
                                final Bitmap cachedLayer,
                                final Canvas drawCanvas,
                                final StrokesContainer strokesContainer) {
        super(view, cachedLayer, drawCanvas);
        this.strokeWidth = strokeWidth;
        this.strokesContainer = strokesContainer;
        this.boundingBox = new Rect();
        this.drawingPath = new Path();
        lastUpMs = 0;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(BLACK);
        paint.setAntiAlias(false);
        paint.setDither(false);
        paint.setStyle(STROKE);
        paint.setStrokeWidth(strokeWidth);

        // TODO: so does that mean there can only be one ?
        epdUtil.addDhwArea(
                new Rect(
                        0,
                        0,
                        view.getWidth(),
                        view.getHeight()
                ),
                strokeWidth,
                view.getWidth() > view.getHeight() ? 0 : 1
        );
    }

    private void handleMotion(final MotionEvent event) {
        int historySize = event.getHistorySize();
        for (int i = 0; i < historySize; i++) {
            float histX = event.getHistoricalX(i);
            float histY = event.getHistoricalY(i);
            currentStroke.addPoint(histX, histY);
            drawingPath.lineTo(histX, histY);
            boundingBox.union((int) histX, (int) histY);
        }

        boundingBox.union((int) lastY, (int) lastY);
        currentStroke.addPoint(lastPosition);
        drawingPath.lineTo(lastX, lastY);

        drawCanvas.drawPath(drawingPath, paint);

        // Fast ceil
        int currentStrokeWidth = (int) paint.getStrokeWidth() + 1;

        drawingPath.rewind();
        drawingPath.moveTo(lastX, lastY);

        // We inset by the stroke width so that the invalidation also encompass the full width of the line

        boundingBox.inset(-currentStrokeWidth, -currentStrokeWidth);

        invalidate(boundingBox);
    }

    public void onDraw(Canvas canvas) {
        canvas.drawColor(-1);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getActionMasked();

        boundingBox.set((int) lastX, (int) lastY, (int) lastX, (int) lastY);

        // We decide if we want to keep drawing on the same stroke as before
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpMs > 100) {
            if (currentStroke != null) {
                boundingBox.union(currentStroke.getBoundingBox());
                boundingBox.inset(-maxPenWidth(), -maxPenWidth());
            }

            currentStroke = new SimpleStroke(lastX, lastY);
            strokesContainer.setDrawingStroke(currentStroke);
            drawingPath.rewind();
            drawingPath.moveTo(lastX, lastY);
        }
        lastUpMs = currentTime;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleMotion(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMotion(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleMotion(event);
                strokesContainer.persistDrawing();
                break;
        }
        return true;
    }

    @Override
    public void invalidate(Rect dirty) {
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
