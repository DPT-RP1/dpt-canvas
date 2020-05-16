package com.sony.dpt.drawing.strokes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Persists nothing, useful for pressure mode for example
 */
public class DummyStrokeContainer implements StrokesContainer {

    private final List<Stroke> emptyList;

    public DummyStrokeContainer() {
        emptyList = Collections.emptyList();
    }

    @Override
    public void setDrawingStroke(Stroke stroke) {

    }

    @Override
    public void persistDrawing() {

    }

    @Override
    public void addStrokes(Collection<Stroke> strokes) {

    }



    @Override
    public Collection<Stroke> getAll() {
        return emptyList;
    }
}
