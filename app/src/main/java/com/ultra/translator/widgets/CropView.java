package com.ultra.translator.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by HP 6300 Pro on 12/19/2017.
 */

public class CropView extends View {
    public CropView(Context context) {
        this(context, null, 0);
    }

    public CropView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
