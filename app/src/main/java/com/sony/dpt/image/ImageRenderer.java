package com.sony.dpt.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

import com.sony.dpt.override.IViewOverride;
import com.sony.dpt.override.UpdateMode;

public class ImageRenderer extends Thread {

    private final SurfaceHolder surfaceHolder;
    private final IViewOverride viewOverride;
    // We merge all flips into one: we won't queue up pages to flip
    private volatile Bitmap nextPage;
    private int nextUpdateMode;

    public ImageRenderer(final SurfaceHolder surfaceHolder, final IViewOverride viewOverride) {
        this.surfaceHolder = surfaceHolder;
        this.viewOverride = viewOverride;
    }


    public void flipPage(final Bitmap bitmap, int updateMode) {
        synchronized (this) {
            this.nextPage = bitmap;
            this.nextUpdateMode = updateMode;
            this.notify();
        }
    }

    @Override
    public void run() {
        boolean interrupted = false;

        while (!interrupted || nextPage != null) {
            synchronized (this) {
                if (nextPage != null) {
                    Canvas canvas = viewOverride.lockCanvas(surfaceHolder, nextUpdateMode);
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(nextPage, 0, 0, null);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

                nextPage = null;
                nextUpdateMode = UpdateMode.UI_DEFAULT_MODE;

                try {
                    this.wait();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        }
    }

}
