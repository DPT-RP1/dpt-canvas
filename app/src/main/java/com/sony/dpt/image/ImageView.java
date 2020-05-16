package com.sony.dpt.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sony.dpt.override.IViewOverride;
import com.sony.dpt.override.UpdateMode;
import com.sony.dpt.override.ViewOverride;

import static com.sony.dpt.image.ImageHelper.scale;

public class ImageView extends SurfaceView implements SurfaceHolder.Callback2 {

    private ImageRenderer imageRenderer;
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

        Glide.with(this)
                .asBitmap()
                .load("http://1.bp.blogspot.com/-3AOcdMsJZrw/UWZnYcvol6I/AAAAAAAAAEo/Lo1BJ50FPMM/s1600/naruto-04.jpg")
                .transform(scale())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageRenderer.flipPage(resource, UpdateMode.UPDATE_MODE_CONVERT_A2_PARTIAL);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
