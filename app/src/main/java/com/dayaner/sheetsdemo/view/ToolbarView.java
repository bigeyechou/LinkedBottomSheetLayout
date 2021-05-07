package com.dayaner.sheetsdemo.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * -------------------------------------
 * 作者：dayaner
 * -------------------------------------
 * 时间：4/26/21 12:15 PM
 * -------------------------------------
 * 描述：
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class ToolbarView extends ConstraintLayout {

    private float process = 1F;

    public ToolbarView(@NonNull Context context) {
        super(context);
    }

    public ToolbarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolbarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public float getProcess() {
        return process;
    }

    public void setProcess(float process) {
        if (process < 0F){
            this.process = 0F;
        }else if (process > 1F){
            this.process = 1F;
        }else {
            this.process = process;
        }
        updateProcess(process);
    }

    private void updateProcess(float process) {

    }
}
