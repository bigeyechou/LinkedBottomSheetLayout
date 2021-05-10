package com.dayaner.sheetsdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dayaner.sheetsdemo.R;

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
    private Context mContext;
    private View bgView;
    private TextView tvTitle;
    private TextView tvBack;
    private ConstraintLayout detailLayout;

    public ToolbarView(@NonNull Context context) {
        this(context,null);
    }

    public ToolbarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ToolbarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.view_toolbar,this);
        bgView = findViewById(R.id.bg_view);
        tvTitle = findViewById(R.id.tv_title);
        tvBack = findViewById(R.id.tv_back);
        detailLayout = findViewById(R.id.detail_layout);
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

    public ToolbarView setup(CharSequence title,int bgColor){
        tvTitle.setText(title);
        bgView.setBackgroundColor(bgColor);
        return this;
    }

    public void setListeners(OnClickListener clickBack){
        tvBack.setOnClickListener(clickBack);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (process < 1){
            detailLayout.setY(detailLayout.getHeight());
        }
    }

    private void updateProcess(float process) {
        bgView.setAlpha(process);
        float titleY;
        float titleAlpha;
        float detailY;
        float detailAlpha;
        if (this.process != 1F && process == 1F){
            titleY = -tvTitle.getHeight();
            titleAlpha = 0F;
            detailY = 0F;
            detailAlpha = 1F;
        }else if (this.process == 1F && process != 1F){
            titleY = 0F;
            titleAlpha = 1F;
            detailY = detailLayout.getHeight();
            detailAlpha = 0F;
        }else {
            return;
        }
        tvTitle.animate().y(titleY).alpha(titleAlpha).setDuration(100).start();
        detailLayout.animate().y(detailY).alpha(detailAlpha).setDuration(100).start();
    }
}
