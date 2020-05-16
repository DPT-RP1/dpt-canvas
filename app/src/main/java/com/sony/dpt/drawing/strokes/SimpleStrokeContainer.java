package com.sony.dpt.drawing.strokes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SimpleStrokeContainer implements StrokesContainer {

    private final Set<Stroke> strokes;
    private Stroke currentlyDrawing;

    public SimpleStrokeContainer() {
        this.strokes = new HashSet<Stroke>();
    }

    @Override
    public void setDrawingStroke(Stroke stroke) {
        currentlyDrawing = stroke;
    }

    @Override
    public void persistDrawing() {
        if (currentlyDrawing != null) this.strokes.add(this.currentlyDrawing);
    }

    @Override
    public void addStrokes(Collection<Stroke> strokes) {
        this.strokes.addAll(strokes);
    }


    @Override
    public Collection<Stroke> getAll() {
        return strokes;
    }
}
