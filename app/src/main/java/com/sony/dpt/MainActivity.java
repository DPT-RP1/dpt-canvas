package com.sony.dpt;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sony.dpt.drawing.DrawableView;
import com.sony.dpt.media.CbzImagePack;
import com.sony.dpt.media.ImagePack;
import com.sony.dpt.media.ImagePackImageView;
import com.sony.dpt.override.UpdateMode;
import com.sony.dpt.override.ViewOverride;

import java.io.IOException;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawableView drawableView = findViewById(R.id.pen);
        drawableView.setZOrderOnTop(true);

        final ImagePackImageView imagePackImageView = findViewById(R.id.background);
        try {
            ImagePack cbz = CbzImagePack.open("/data/manga/01.cbz");
            imagePackImageView.setImagePack(cbz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setHierarchyUpdateMode(getWindow().getDecorView());

        GestureDetector gestureDetector = new DptGestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 != null && e2 != null) return onFling(e1, e2);
                return true;
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    if (distanceX > 0) {
                        try {
                            imagePackImageView.flipPrevious();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            imagePackImageView.flipNext();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
                return false;
            }
        });


        drawableView.setGestureDetector(gestureDetector);

    }

    public void setHierarchyUpdateMode(View parent) {
        ViewOverride.getInstance().setDefaultUpdateMode(parent, UpdateMode.UPDATE_MODE_NOWAIT_NOCONVERT_DU_SP1_IGNORE);

        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setHierarchyUpdateMode(viewGroup.getChildAt(i));
            }
        }
    }


}