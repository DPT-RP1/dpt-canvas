package com.sony.dpt.image;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sony.dpt.override.IViewOverride;
import com.sony.dpt.override.ViewOverride;

public class ImageView extends SurfaceView implements SurfaceHolder.Callback2 {

    protected ImageRenderer imageRenderer;
    private IViewOverride viewOverride;

    public ImageView(Context context) {
        super(context);
        this.getHolder().addCallback(this);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.getHolder().addCallback(this);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.getHolder().addCallback(this);
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        viewOverride = ViewOverride.getInstance();
        imageRenderer = new ImageRenderer(holder, viewOverride);
        imageRenderer.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
