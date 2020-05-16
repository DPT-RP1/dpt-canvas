package com.sony.dpt.drawing.strokes;

import java.util.Collection;

public interface StrokesContainer {

    void setDrawingStroke(final Stroke stroke);

    void persistDrawing();

    void addStrokes(final Collection<Stroke> strokes);

    Collection<Stroke> getAll();
}
