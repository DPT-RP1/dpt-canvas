package com.sony.dpt.drawing.strokes;

import android.graphics.Path;
import android.graphics.Rect;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SimpleStrokeContainer implements StrokesContainer {

    private Path path;
    private List<Stroke> strokes;
    private float penWidth;

    private List<Stroke> currentlyDrawing;

    public SimpleStrokeContainer() {
        this.strokes = new LinkedList<Stroke>();
        this.currentlyDrawing = new LinkedList<Stroke>();
    }

    @Override
    public void addDrawingStroke(Stroke stroke) {
        currentlyDrawing.add(stroke);
    }

    @Override
    public void persistDrawing() {
        this.strokes.addAll(this.currentlyDrawing);
        this.currentlyDrawing.clear();
    }

    @Override
    public void addStrokes(Stroke... strokes) {
        addStrokes(Arrays.asList(strokes));
    }

    @Override
    public void addStrokes(Collection<Stroke> strokes) {
        this.strokes.addAll(strokes);
    }

    @Override
    public void clear() {
        this.currentlyDrawing.clear();
        this.strokes.clear();
    }

    @Override
    public Collection<Stroke> findIntersect(Rect boundingBox) {
        return null;
    }

    @Override
    public Collection<Stroke> getAll() {
        return strokes;
    }
}
