package com.dayaner.sheetsdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * -------------------------------------
 * 作者：dayaner
 * -------------------------------------
 * 时间：4/26/21 12:13 PM
 * -------------------------------------
 * 描述：底部弹出式的 view 容器
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class BottomSheetLayout extends FrameLayout {

    /**
     * 折叠状态，此时只露出最小显示高度
     */
    public static int BOTTOM_SHEET_STATE_COLLAPSED = 1;

    /**
     * 正在滚动的状态
     */
    public static int BOTTOM_SHEET_STATE_SCROLLING = 2;

    /**
     * 展开状态，此时露出全部内容
     */
    public static int BOTTOM_SHEET_STATE_EXTENDED = 3;

    /**
     * 内容视图的状态
     */
    int state = 0;

    public int getState() {
        int scrollY = getScrollY();
        if(scrollY == minScrollY){
            return BOTTOM_SHEET_STATE_COLLAPSED;
        } else if(scrollY == maxScrollY){
            return BOTTOM_SHEET_STATE_EXTENDED;
        } else {
            return BOTTOM_SHEET_STATE_SCROLLING;
        }
    }

    /**
     * 当前滚动的进度，[BOTTOM_SHEET_STATE_COLLAPSED] 时是 0，[BOTTOM_SHEET_STATE_EXTENDED] 时是 1
     */
    @FloatRange(from = 0.0, to = 1.0)
    float process = 0F;

    public float getProcess() {
        if (maxScrollY > minScrollY) {
            float scrollY = getScrollY();
            return (scrollY - minScrollY) / (maxScrollY - minScrollY);
        } else {
            return 0;
        }
    }

    /**
     * 上一次发生滚动时的滚动方向，用于在松手时判断需要滚动到的位置
     */
    int lastDir = 0;


    /**
     * 当 [process] 发生变化时的回调
     */
    private ProcessChangedListener onProcessChangedListener;

    public interface ProcessChangedListener{
        void onProcessChangedListener(BottomSheetLayout bottomSheetLayout);
    }

    public void setProcessChangedListener(ProcessChangedListener onProcessChangedListener){
        this.onProcessChangedListener = onProcessChangedListener;
    }

    /**
     * 内容视图
     */
    View contentView = null;

    /**
     * 内容视图最小的显示高度
     */
    private int minShowingHeight = 0;

    /**
     * 添加内容视图时的初始状态
     */
    private int initState = BOTTOM_SHEET_STATE_COLLAPSED;

    /**
     * y 轴最小的滚动值，此时 [contentView] 在底部露出 [minShowingHeight]
     */
    private int minScrollY = 0;

    /**
     * y 轴最大的滚动值，此时 [contentView] 全部露出
     */
    private int maxScrollY = 0;

    /**
     * 上次触摸事件的 x 值，用于判断是否拦截事件
     */
    private float lastX = 0F;

    /**
     * 上次触摸事件的 y 值，用于处理自身的滑动事件
     */
    private float lastY = 0F;

    /**
     * 用来处理平滑滚动
     */
    private Scroller scroller = new Scroller(getContext());

    /**
     * 用于计算自身时的 y 轴速度，处理自身的 fling
     */
    private VelocityTracker velocityTracker = VelocityTracker.obtain();

    /**
     * fling 是一连串连续的滚动操作，这里需要暂存 fling 的目标 view
     */
    private View flingTarget = null;

    /**
     * 暂存 scroller 上次计算的 y 轴滚动位置，用以计算当前需要滚动的距离
     */
    private int lastComputeY = 0;

    public BottomSheetLayout(@NonNull Context context) {
        super(context);
    }

    public BottomSheetLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomSheetLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //todo initState: Int = BOTTOM_SHEET_STATE_COLLAPSED
    public void setContentView(View contentView, int minShowingHeight, int initState) {
        removeAllViews();
        this.contentView = contentView;
        this.minShowingHeight = Math.max(minShowingHeight, 0);
        this.initState = initState;
        addView(contentView);
    }

    public void removeContentView() {
        removeAllViews();
        state = 0;
        contentView = null;
        minShowingHeight = 0;
        initState = 0;
        minScrollY = 0;
        maxScrollY = 0;
    }

    //todo smoothly: Boolean = true
    public void setProcess(@FloatRange(from = 0.0, to = 1.0)float process,boolean smoothly) {
        int y = (int) ((maxScrollY - minScrollY) * process + minScrollY);
        if (smoothly) {
            smoothScrollToY(y);
        } else {
            scrollTo(0, y);
        }
    }

    /**
     * 布局正常布局，布局完成后根据 [minShowingHeight] 和 [contentView] 的位置计算滚动范围，并滚动到初始状态的位置
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        minScrollY = 0;
        maxScrollY = 0;
        if(contentView != null){
            int height = contentView.getHeight();
            if (minShowingHeight > height) {
                minShowingHeight = height;
            }
            minScrollY = contentView.getTop() + minShowingHeight - height;
            maxScrollY = contentView.getBottom() - height;
            if (initState == BOTTOM_SHEET_STATE_EXTENDED) {
                setProcess(1F, false);
            } else {
                setProcess(0F, false);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            // down 时，记录触点位置，复位上次的滚动方向，停掉动画
            case MotionEvent.ACTION_DOWN :
                lastX = ev.getX();
                lastY = ev.getY();
                lastDir = 0;
                if (isFling()) {
                    scroller.abortAnimation();
                }
                break;
            // up 或 cancel 时判断是否要平滑滚动到稳定位置
            case MotionEvent.ACTION_UP :
            case MotionEvent.ACTION_CANCEL :
                // 发生了移动，且处于滚动中的状态，且未被拦截，则自己处理
                if (lastDir != 0 && getState() == BOTTOM_SHEET_STATE_SCROLLING) {
                    smoothScrollToY(lastDir > 0 ? maxScrollY : minScrollY);
                    // 这里返回 true 防止分发给子 view 导致其抖动
                    return true;
                }
            break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 正在滚动中肯定要自己拦截处理
        if (getState() == BOTTOM_SHEET_STATE_SCROLLING) {
            return true;
        }
        // move 时，在内容 view 区域，且 y 轴偏移更大，就拦截
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return ViewUtils.isUnder(contentView, ev.getRawX(), ev.getRawY())
                    && Math.abs(lastX - ev.getX()) < Math.abs(lastY - ev.getY());
        } else {
            lastX = ev.getX();
            lastY = ev.getY();
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // down 时，触点在内容视图上时才继续处理
            case MotionEvent.ACTION_DOWN:
                velocityTracker.clear();
                velocityTracker.addMovement(event);
                return ViewUtils.isUnder(contentView, event.getRawX(), event.getRawY());
            // move 时分发滚动量
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                int dy = (int) (lastY - event.getY());
                lastY = event.getY();
                return dispatchScrollY(dy, ViewUtils.findScrollableTarget(contentView, event.getRawX(), event.getRawY(), dy));
            // up 时要处理子 view 的 fling
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);
                int yv = (int) -velocityTracker.getYVelocity();
                handleFling(yv, ViewUtils.findScrollableTarget(contentView, event.getRawX(), event.getRawY(), yv));
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    /**
     * fling 只用于目标 view 的滚动，不用于自身滚动
     */
    private void handleFling(int yv, View target) {
        if(target == null) {
            return;
        }
        flingTarget = target;
        scroller.fling(0, lastComputeY, 0, yv, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        invalidate();
    }

    /**
     * 利用 [scroller] 平滑滚动到目标位置，只用于自身的滚动
     */
    private void smoothScrollToY(int y) {
        int scrollY = getScrollY();
        if (scrollY == y) {
            return;
        }
        lastComputeY = scrollY;
        flingTarget = null;
        scroller.startScroll(0, scrollY, 0, y - scrollY);
        invalidate();
    }

    /**
     * 是否是 fling 只取决于有没有要 fling 的目标 view
     */
    private boolean isFling() {
        return flingTarget != null;
    }

    /**
     * 计算 [scroller] 当前的滚动量并分发，不再处理就关掉动画
     * 动画结束时及时复位 fling 的目标 view
     */
    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int currentY = scroller.getCurrY();
            int dScrollY = currentY - lastComputeY;
            lastComputeY = currentY;
            if (!dispatchScrollY(dScrollY, flingTarget)) {
                scroller.abortAnimation();
            }
            invalidate();
        } else {
            flingTarget = null;
        }
    }

    /**
     * 分发 y 轴滚动事件
     * 展开状态：优先处理 [target]，然后如果不是 fling （fling 不用于自身的滚动）才处理自己
     * 非展开状态：只处理自己
     *
     * @param dScrollY y 轴的滚动量
     * @param target 可以处理改滚动量的目标 view
     * @return 是否可以处理
     */
    private boolean dispatchScrollY(int dScrollY, View target) {
        // 0 默认可以处理
        if (dScrollY == 0) {
            return true;
        }
        if (getState() == BOTTOM_SHEET_STATE_EXTENDED) {
            if (target != null && target.canScrollVertically(dScrollY)) {
                target.scrollBy(0, dScrollY);
                return true;
            } else if (!isFling() && canScrollVertically(dScrollY)) {
                scrollBy(0, dScrollY);
                return true;
            } else {
                return false;
            }
        } else if (canScrollVertically(dScrollY)) {
            scrollBy(0, dScrollY);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 滚动范围是[[minScrollY], [maxScrollY]]，根据方向判断垂直方向是否可以滚动
     */
    @Override
    public boolean canScrollVertically(int direction) {
        int scrollY = getScrollY();
        if (direction > 0) {
            return scrollY < maxScrollY;
        } else {
            return scrollY > minScrollY;
        }
    }

    /**
     * 滚动前做范围限制
     */
    @Override
    public void scrollTo(int x, int y) {
        int scrollY;
        if (y < minScrollY) {
            scrollY = minScrollY;
        } else {
            scrollY = Math.min(y, maxScrollY);
        }
        super.scrollTo(x, scrollY);
    }

    /**
     * 当发生滚动时，更新滚动方向和当前内容视图的状态
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        lastDir = t - oldt;
        if (onProcessChangedListener != null) {
            onProcessChangedListener.onProcessChangedListener(this);
        }
    }
}