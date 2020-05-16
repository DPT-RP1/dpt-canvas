package com.sony.dpt.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

import static com.bumptech.glide.load.Key.CHARSET;
import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.PAINT_FLAGS;
import static com.sony.dpt.image.Orientation.PORTRAIT;

public class ImageHelper {

    // DPT dimensions
    public static final float DEFAULT_WIDTH = 1650;
    public static final float DEFAULT_HEIGHT = 2200;
    private static final String ID = "com.sony.dpt.fit_height";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);
    private static final Paint DEFAULT_PAINT = new Paint(PAINT_FLAGS);

    public static Orientation orientation(float width, float height) {
        if (width < height) {
            return PORTRAIT;
        } else {
            return Orientation.LANDSCAPE;
        }
    }

    public static Transformation<Bitmap> scale() {
        return new Transformation<Bitmap>() {
            @NonNull
            @Override
            public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {

                BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
                Bitmap toTransform = resource.get();

                Bitmap.Config config = getNonNullConfig(toTransform);

                float width = toTransform.getWidth();
                float height = toTransform.getHeight();

                // We will fit height
                float ratio = width / height;
                final float targetWidth = DEFAULT_HEIGHT * ratio;

                Bitmap toReuse = bitmapPool.get((int) targetWidth, (int) DEFAULT_HEIGHT, config);

                TransformationUtils.setAlpha(toTransform, toReuse);

                Matrix matrix = new Matrix();
                matrix.setScale(targetWidth / width, DEFAULT_HEIGHT / height);
                applyMatrix(toTransform, toReuse, matrix);
                return BitmapResource.obtain(toReuse, bitmapPool);
            }

            @Override
            public boolean equals(Object o) {
                return o instanceof FitCenter;
            }

            @Override
            public int hashCode() {
                return ID.hashCode();
            }

            @Override
            public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
                messageDigest.update(ID_BYTES);
            }
        };
    }

    public static Bitmap.Config getNonNullConfig(@NonNull Bitmap bitmap) {
        return bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
    }

    public static void applyMatrix(@NonNull Bitmap inBitmap, @NonNull Bitmap targetBitmap, Matrix matrix) {

        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawBitmap(inBitmap, matrix, DEFAULT_PAINT);
        canvas.setBitmap(null);
    }

}
