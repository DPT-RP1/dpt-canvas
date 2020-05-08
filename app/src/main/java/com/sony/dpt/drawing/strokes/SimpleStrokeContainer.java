package com.sony.dpt.drawing.strokes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SimpleStrokeContainer implements StrokesContainer {

    private List<Stroke> strokes;
    private List<Stroke> currentlyDrawing;
    private long lastPersistTimeMs;

    public SimpleStrokeContainer() {
        this.strokes = new ArrayList<Stroke>();
        this.currentlyDrawing = new ArrayList<Stroke>();
        this.lastPersistTimeMs = 0;
    }

    @Override
    public void addDrawingStroke(Stroke stroke) {
        currentlyDrawing.add(stroke);
    }

    @Override
    public Stroke persistDrawing() {
        Stroke result;
        long persistTime = System.currentTimeMillis();
        if (persistTime - lastPersistTimeMs < 100) {
            // We're still drawing a stroke, let's continue on the same one
            result = this.currentlyDrawing.get(this.currentlyDrawing.size() - 1);
        } else {
            this.strokes.addAll(this.currentlyDrawing);
            this.currentlyDrawing.clear();
            result = null;
        }
        this.lastPersistTimeMs = persistTime;
        return result;
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
    public Collection<Stroke> getAll() {
        return strokes;
    }
}
