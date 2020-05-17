package com.sony.dpt.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.security.MessageDigest;

import static com.sony.dpt.image.ImageHelper.DEFAULT_PAINT;
import static com.sony.dpt.image.ImageHelper.applyMatrix;
import static com.sony.dpt.image.ImageHelper.getNonNullConfig;

public class FrameTransformation implements Transformation<Bitmap> {

    private static final String ID = "com.sony.dpt.frame";

    @NonNull
    @Override
    public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {

        BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
        Bitmap toTransform = resource.get();

        Bitmap.Config config = getNonNullConfig(toTransform);

        float width = toTransform.getWidth();
        float height = toTransform.getHeight();

        float targetWidth = width + 1;

        Bitmap toReuse = bitmapPool.get((int) targetWidth, (int) height, config);

        Matrix matrix = new Matrix();
        matrix.setTranslate(1, 0); // We move 1px to the right
        applyMatrix(toTransform, toReuse, matrix);

        Canvas canvas = new Canvas(toReuse);
        canvas.drawLine(0, 0, 0, height, DEFAULT_PAINT);
        canvas.drawLine(width, 0, width, height, DEFAULT_PAINT);

        return BitmapResource.obtain(toReuse, bitmapPool);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FrameTransformation;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID.getBytes(CHARSET));
    }
}
