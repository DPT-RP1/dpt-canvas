package com.sony.dpt.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.sony.dpt.drawing.strokes.SimpleStroke;
import com.sony.dpt.drawing.strokes.SimpleStrokeContainer;
import com.sony.dpt.drawing.strokes.Stroke;
import com.sony.dpt.drawing.strokes.StrokesContainer;

import static android.graphics.Color.BLACK;
import static android.graphics.Paint.Style.STROKE;
import static com.sony.dpt.override.UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE;

/**
 * This is in charge of striking the pen on a view
 */
public class StrikeDelegate extends AbstractDrawingDelegate {

    private Paint paint;

    private float lastX;
    private float lastY;

    private int strokeWidth;

    private Stroke currentStroke;
    private StrokesContainer strokesContainer;

    public StrikeDelegate(final int strokeWidth, final View view, final Bitmap cachedLayer, final Canvas drawCanvas) {
        super(view, cachedLayer, drawCanvas);
        this.strokeWidth = strokeWidth;
        this.strokesContainer = new SimpleStrokeContainer();
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
            currentStroke.addPoint(event.getHistoricalX(i), event.getHistoricalY(i));
        }

        currentStroke.addPoint(new PointF(event.getX(), event.getY()));

        drawCanvas.drawPath(currentStroke.getPath(), paint);

        // Fast ceil
        int currentStrokeWidth = (int) paint.getStrokeWidth() + 1;

        // We inset by the stroke width so that the invalidation also encompass the full width of the line
        Rect boundingBox = new Rect(currentStroke.getBoundingBox());
        boundingBox.inset(-currentStrokeWidth, -currentStrokeWidth);
        invalidate(boundingBox);

        strokesContainer.addDrawingStroke(currentStroke);
        currentStroke = new SimpleStroke(currentStroke.getLastPoint());

    }

    public void onDraw(Canvas canvas) {

        canvas.drawColor(-1);
        canvas.drawBitmap(cachedLayer, 0.0F, 0.0F, null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        // Initialize the stroke. It can happen during a button switch between erasing and drawing
        if (currentStroke == null) {
            currentStroke = new SimpleStroke(new PointF(event.getX(), event.getY()));
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                epdUtil.setDhwState(true);

                break;
            case MotionEvent.ACTION_MOVE:
                handleMotion(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleMotion(event);
                currentStroke = null;
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

    public Paint getStrokePaint() {
        return paint;
    }
}
