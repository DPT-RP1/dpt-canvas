package com.sony.dpt.drawing.strokes;

import java.util.Collection;

public interface StrokesContainer {

    /**
     * Adds a stroke to the drawing context
     * The goal is to provide a clear segregation between a very complex
     * in-drawing stroke and a potentially simplified persisted stroke.
     *
     * @param stroke
     */
    void setDrawingStroke(final Stroke stroke);

    /**
     * This saves the last draw strokes in the permanent strokes storage
     */
    void persistDrawing();

    void addStrokes(final Stroke... strokes);

    void addStrokes(final Collection<Stroke> strokes);

    void clear();

    Collection<Stroke> getAll();
}
