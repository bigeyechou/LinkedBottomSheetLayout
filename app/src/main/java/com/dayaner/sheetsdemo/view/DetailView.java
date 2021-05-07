package com.dayaner.sheetsdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * -------------------------------------
 * 作者：dayaner
 * -------------------------------------
 * 时间：4/26/21 12:13 PM
 * -------------------------------------
 * 描述：详情页
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class DetailView extends FrameLayout {
    private ToolbarView toolbarView;

    public DetailView(@NonNull Context context) {
        super(context);
    }

    public DetailView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
