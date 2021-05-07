package com.dayaner.sheetsdemo.view;

import android.view.View;
import android.view.ViewGroup;

/**
 * -------------------------------------
 * 作者：dayaner
 * -------------------------------------
 * 时间：4/9/21 8:05 PM
 * -------------------------------------
 * 描述：
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class ViewUtils {
    /**
     * 寻找 ViewGroup 在某个点下的子 View
     *
     * @param group
     * @param rawX
     * @param rawY
     * @return
     */
    public static View findChildUnder(ViewGroup group, final float rawX, final float rawY) {
        return findFirst(false, group, new ViewUnderListener() {
            @Override
            public boolean viewUnder(View view) {
                return isUnder(view, rawX, rawY);
            }
        });
    }


    /**
     * 寻找 ViewGroup 中某个符合条件的子 View，支持递归遍历其子 View
     *
     * @param recursively
     * @param group
     * @return
     */
    public static View findFirst(boolean recursively, ViewGroup group, ViewUnderListener underListener) {
        int childCount = group.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = group.getChildAt(i);
            if (underListener.viewUnder(v)) {
                return v;
            }
            if (recursively) {
                if (v instanceof ViewGroup) {
                    return findFirst(recursively, (ViewGroup) v, underListener);
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    /**
     * 判断view是否在某个点下
     *
     * @param view
     * @param rawX
     * @param rawY
     * @return
     */
    public static boolean isUnder(View view, float rawX, float rawY) {
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
//        return rawX.toInt() in xy[ 0]..(xy[0] + width) && rawY.toInt() in xy[ 1]..(xy[1] + height)
        return (xy[0] < rawX && rawX <= (xy[0] + view.getWidth())) && (xy[1] < rawY && rawY <= (xy[1] + view.getHeight()));
    }


    /**
     * 寻找在某个点下，可以处理滚动量的子 View
     * @param view
     * @param rawX
     * @param rawY
     * @param dScrollY
     * @return
     */
    public static View findScrollableTarget(View view, float rawX, float rawY, int dScrollY) {
        if (view == null){
            return null;
        }
        if (!isUnder(view, rawX, rawY)) {
            return null;
        }
        if (view.canScrollVertically(dScrollY)) {
            return view;
        }
        if (!(view instanceof ViewGroup)) {
            return null;
        } else {
            View t = null;
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                t = findScrollableTarget(group.getChildAt(i), rawX, rawY, dScrollY);
                if (t != null) {
                    break;
                }
            }
            return t;
        }
    }


    public interface ViewUnderListener {
        boolean viewUnder(View view);
    }
}
