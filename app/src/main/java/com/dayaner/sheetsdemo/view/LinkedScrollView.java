package com.dayaner.sheetsdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * -------------------------------------
 * 作者：dayaner
 * -------------------------------------
 * 时间：4/26/21 12:13 PM
 * -------------------------------------
 * 描述：联动滚动布局
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class LinkedScrollView extends FrameLayout {

    /**
     * 顶部View容器
     */
    public final FrameLayout topContainer = new FrameLayout(getContext());

    /**
     * 顶部视图中的可滚动 view，当自身无法再向下滚动时，会把剩余的滚动分发给它
     */
    private View topScrollableView = null;

    /**
     * 底部视图容器
     */
    public final FrameLayout bottomContainer = new FrameLayout(getContext());

    /**
     * 底部视图中的可滚动view,当自身无法再向下滚动时，会把剩余的滚动分发给他
     */
    private View bottomScrollableView = null;

    /**
     * y 轴的最大滚动范围 = 顶部视图高度 + 底部视图高度 - 自身的高度
     */
    private int maxScrollY = 0;

    /**
     * 上次触摸事件的 X 值，用于判断是否拦截事件
     */
    private float lastX = 0F;

    /**
     * 上次触摸事件的 Y 值，用于判断是否拦截事件
     */
    private float lastY = 0F;

    /**
     * 主要用于计算 fling 后的滚动距离
     */
    private final Scroller scroller = new Scroller(getContext());

    /**
     * 用于计算自身时的 y 轴速度，处理自身的 fling
     */
    private final VelocityTracker velocityTracker = VelocityTracker.obtain();

    /**
     * fling 是一连串连续的滚动操作，这里需要暂存 fling 相关的 view
     */
    private View flingChild;
    private View flingTarget;

    /**
     * 暂存 fling 时上次的 y 轴位置，用以计算当前需要滚动的距离
     */
    private int lastFlingY = 0;

    public LinkedScrollView(@NonNull Context context) {
        this(context, null);
    }

    public LinkedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinkedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化view
     */
    private void init() {
        addView(topContainer, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(bottomContainer, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 添加顶部视图
     *
     * @param v
     * @param scrollableChild
     */
    public void setTopView(View v, @NonNull View scrollableChild) {
        if (v == null) {
            return;
        }
        topContainer.removeAllViews();
        topContainer.addView(v);
        topScrollableView = scrollableChild;
        requestLayout();
    }

    /**
     * 移除顶部View
     */
    public void removeTopView() {
        topContainer.removeAllViews();
        topScrollableView = null;
    }

    /**
     * 添加底部视图
     *
     * @param v
     * @param scrollableChild
     */
    public void setBottomView(View v, View scrollableChild) {
        if (v == null) {
            return;
        }
        bottomContainer.removeAllViews();
        bottomContainer.addView(v);
        bottomScrollableView = (View) scrollableChild;
        requestLayout();
    }

    /**
     * 移除底部视图
     */
    public void removeBottomView() {
        bottomContainer.removeAllViews();
        bottomScrollableView = null;
    }

    /**
     * 布局时，topContainer 在顶部，bottomContainer 紧挨着 topContainer 底部
     * 布局完还要计算下最大的滚动距离
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        topContainer.layout(0, 0, topContainer.getMeasuredWidth(), topContainer.getMeasuredHeight());
        bottomContainer.layout(0, topContainer.getMeasuredHeight(), bottomContainer.getMeasuredWidth(),
                topContainer.getMeasuredHeight() + bottomContainer.getMeasuredHeight());
        maxScrollY = topContainer.getMeasuredHeight() + bottomContainer.getMeasuredHeight() - getHeight();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 手指按下就中止 fling 等滑动行为
            scroller.forceFinished(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                return false;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(lastX - ev.getX()) < Math.abs(lastY - ev.getY())) {
                    return true;
                } else {
                    lastX = ev.getX();
                    lastY = ev.getY();
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录 y 轴初始位置
                lastY = event.getY();
                velocityTracker.clear();
                velocityTracker.addMovement(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                // 移动时分发滚动量
                int dScrollY = (int) (lastY - event.getY());
                View child = ViewUtils.findChildUnder(this, event.getRawX(), event.getRawY());
                dispatchScrollY(dScrollY, child, ViewUtils.findScrollableTarget(child, event.getRawX(), event.getRawY(), dScrollY));
                lastY = event.getY();
                velocityTracker.addMovement(event);
                return true;
            case MotionEvent.ACTION_UP:
                // 手指抬起时计算 y 轴速度，然后自身处理 fling
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);
                int yv = (int) -velocityTracker.getYVelocity();
                View view = ViewUtils.findChildUnder(this, event.getRawX(), event.getRawY());
                handleFling(yv, view, ViewUtils.findScrollableTarget(view, event.getRawX(), event.getRawY(), yv));
                return true;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 处理 fling，通过 scroller 计算 fling，暂存 fling 的初值和需要 fling 的 view
     */
    private void handleFling(int yv, View child, View target) {
        lastFlingY = 0;
        scroller.fling(0, lastFlingY, 0, yv, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        flingChild = child;
        flingTarget = target;
        invalidate();
    }

    /**
     * 计算 fling 的滚动量，并将其分发到真正需要处理的 view
     */
    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int currentFlingY = scroller.getCurrY();
            int dScrollY = currentFlingY - lastFlingY;
            dispatchScrollY(dScrollY, flingChild, flingTarget);
            lastFlingY = currentFlingY;
            invalidate();
        } else {
            flingChild = null;
        }
    }

    private void dispatchScrollY(int dScrollY, View child, View target) {
        if (dScrollY == 0) {
            return;
        }
        // 滚动所处的位置没有在子 view，或者子 view 没有完全显示出来
        // 或者子 view 中没有要处理滚动的 target，或者 target 不在能够滚动
        if (child == null || !isChildTotallyShowing(child)
                || target == null || !target.canScrollVertically(dScrollY)) {
            // 优先自己处理，处理不了再根据滚动方向交给顶部或底部的 view 处理
            if (canScrollVertically(dScrollY)) {
                scrollBy(0, dScrollY);
                return;
            }
            if (dScrollY > 0) {
                if(bottomScrollableView != null) {
                    bottomScrollableView.scrollBy(0, dScrollY);
                }
            } else {
                if(topScrollableView != null) {
                    topScrollableView.scrollBy(0, dScrollY);
                }
            }
        } else {
            target.scrollBy(0, dScrollY);
        }
    }

    private boolean isChildTotallyShowing(View v) {
        float relativeY = v.getY() - getScrollY();
        return relativeY >= 0 && relativeY + v.getHeight() <= getHeight();
    }

    /**
     * 滚动范围是[0, [maxScrollY]]，根据方向判断垂直方向是否可以滚动
     */
    @Override
    public boolean canScrollVertically(int direction) {
        if (direction > 0) {
            return getScrollY() < maxScrollY;
        } else {
            return getScrollY() > 0;
        }
    }

    /**
     * 滚动前做范围限制
     */
    @Override
    public void scrollTo(int x, int y) {
        int tempY = y < 0 ? 0 : Math.min(y, maxScrollY);
        super.scrollTo(x, tempY);
    }

}