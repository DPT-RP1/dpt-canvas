package com.sony.dpt.override;

import android.graphics.Rect;
import android.view.View;

public interface IViewOverride extends SonyOverride<View> {
    void invalidate(View view, Rect rect, int updateMode);
}
