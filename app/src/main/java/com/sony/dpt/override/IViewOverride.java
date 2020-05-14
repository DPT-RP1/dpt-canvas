package com.sony.dpt.override;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public interface IViewOverride extends SonyOverride<View> {
    void invalidate(View view, Rect rect, int updateMode);

    void invalidate(View view, RectF rect, int updateMode);
}
