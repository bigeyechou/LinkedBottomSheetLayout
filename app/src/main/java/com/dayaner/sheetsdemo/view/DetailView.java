package com.dayaner.sheetsdemo.view;

import android.content.Context;
import android.os.Build;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.dayaner.sheetsdemo.R;

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
    private LinkedScrollView linkedScrollView;
    private BottomSheetLayout bottomSheetLayout;

    /**
     * 顶部列表
     */
    private RecyclerView topRecyclerView;

    /**
     * 底部视图
     */
    private FrameLayout bottomLayout;

    private View bottomScrollViewProvider;


    public void setBottomScrollViewProvider(View bottomScrollViewProvider) {
        this.bottomScrollViewProvider = bottomScrollViewProvider;
        linkedScrollView.setBottomView(bottomLayout, bottomScrollViewProvider);
    }

    private int toolbarHeight;
    private int minBottomShowingHeight = toolbarHeight;

    private boolean isBottomViewFloating = false;

    public void setBottomViewFloating(boolean bottomViewFloating) {
        this.isBottomViewFloating = bottomViewFloating;
    }

    private float topScrolledY = 0F;

    public DetailView(@NonNull Context context) {
        this(context, null);
    }

    public DetailView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        toolbarHeight = (int) getContext().getResources().getDimension(R.dimen.toolbar_height);
        linkedScrollView = new LinkedScrollView(getContext());
        addView(linkedScrollView);

        bottomSheetLayout = new BottomSheetLayout(getContext());
        LayoutParams bottomSheetParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        bottomSheetParams.topMargin = toolbarHeight;
        addView(bottomLayout, bottomSheetParams);

        toolbarView = new ToolbarView(getContext());
        addView(toolbarView, new LayoutParams(LayoutParams.MATCH_PARENT, toolbarHeight));

        linkedScrollView.topContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        LayoutParams linkedParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        linkedParams.bottomMargin = toolbarHeight;
        linkedScrollView.bottomContainer.setLayoutParams(linkedParams);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            linkedScrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    updateBottomView();
                    updateToolbar();
                }
            });
        }

        bottomSheetLayout.setProcessChangedListener(new BottomSheetLayout.ProcessChangedListener() {
            @Override
            public void onProcessChangedListener(BottomSheetLayout bottomSheetLayout) {
                updateToolbar();
            }
        });

        topRecyclerView = new RecyclerView(getContext());
        topRecyclerView.setPaddingRelative(0, toolbarHeight, 0, 0);
        topRecyclerView.setClipToPadding(false);
        topRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                topScrolledY += dy;
                if (topScrolledY < 0) {
                    topScrolledY = 0F;
                }
                updateToolbar();
            }
        });
        linkedScrollView.setTopView(topRecyclerView, topRecyclerView);
        bottomLayout = new FrameLayout(getContext());
        linkedScrollView.setBottomView(bottomLayout, bottomScrollViewProvider);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        post(new Runnable() {
            @Override
            public void run() {
                updateBottomView();
            }
        });
    }

    private void updateBottomView() {
        float bottomY = linkedScrollView.bottomContainer.getY() - linkedScrollView.getScrollY();
        boolean shouldBottomFloating = bottomY > (getHeight() - minBottomShowingHeight);
        if (shouldBottomFloating && !isBottomViewFloating) {
            isBottomViewFloating = true;
            linkedScrollView.removeBottomView();
            bottomSheetLayout.setContentView(bottomLayout, minBottomShowingHeight, BottomSheetLayout.BOTTOM_SHEET_STATE_COLLAPSED);
        } else if (!shouldBottomFloating && isBottomViewFloating) {
            isBottomViewFloating = false;
            bottomSheetLayout.removeContentView();
            linkedScrollView.setBottomView(bottomLayout, bottomScrollViewProvider);
        }
    }

    private void updateToolbar() {
        if (bottomSheetLayout.getState() == BottomSheetLayout.BOTTOM_SHEET_STATE_EXTENDED) {
            toolbarView.setProcess(1F);
        } else {
            toolbarView.setProcess(Math.max(topScrolledY, linkedScrollView.getScaleY() / toolbarHeight));
        }
    }
}
