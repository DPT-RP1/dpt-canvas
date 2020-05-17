package com.sony.dpt.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.security.MessageDigest;

import static com.sony.dpt.image.ImageHelper.DEFAULT_HEIGHT;
import static com.sony.dpt.image.ImageHelper.DEFAULT_WIDTH;
import static com.sony.dpt.image.ImageHelper.applyMatrix;
import static com.sony.dpt.image.ImageHelper.getNonNullConfig;

public class CenterWidthTransformation implements Transformation<Bitmap> {

    private static final String ID = "com.sony.dpt.center_width";

    @NonNull
    @Override
    public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {

        BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
        Bitmap toTransform = resource.get();

        Bitmap.Config config = getNonNullConfig(toTransform);

        float width = toTransform.getWidth();

        final float dx = (DEFAULT_WIDTH - width) / 2;

        Bitmap toReuse = bitmapPool.get((int) DEFAULT_WIDTH, (int) DEFAULT_HEIGHT, config);

        Matrix matrix = new Matrix();
        matrix.setTranslate(dx, 0);

        applyMatrix(toTransform, toReuse, matrix);
        return BitmapResource.obtain(toReuse, bitmapPool);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CenterWidthTransformation;
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
