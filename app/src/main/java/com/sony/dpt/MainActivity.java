package com.sony.dpt;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.sony.dpt.override.UpdateMode;
import com.sony.dpt.override.ViewOverride;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((SurfaceView) findViewById(R.id.pen)).setZOrderOnTop(true);

        setHierarchyUpdateMode(getWindow().getDecorView());

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