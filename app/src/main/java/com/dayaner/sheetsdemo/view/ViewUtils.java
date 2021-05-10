package com.dayaner.sheetsdemo.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * -------------------------------------
 * 作者：likang
 * -------------------------------------
 * 时间： 2021/4/8 6:06 PM
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
        return findFirst(false, group, rawX, rawY);
    }


    /**
     * 寻找 ViewGroup 中某个符合条件的子 View，支持递归遍历其子 View
     *
     * @param recursively
     * @param group
     * @return
     */
    public static View findFirst(boolean recursively, ViewGroup group, float rawX, float rawY) {
        int childCount = group.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = group.getChildAt(i);
            if (isUnder(v, rawX, rawY)) {
                return v;
            }
            if (recursively) {
                if (v instanceof ViewGroup) {
                    return findFirst(recursively, (ViewGroup) v, rawX, rawY);
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
        if (view == null) {
            return false;
        }
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        return (xy[0] < rawX && rawX <= (xy[0] + view.getWidth())) && (xy[1] < rawY && rawY <= (xy[1] + view.getHeight()));
    }


    /**
     * 寻找在某个点下，可以处理滚动量的子 View
     *
     * @param view
     * @param rawX
     * @param rawY
     * @param dScrollY
     * @return
     */
    public static View findScrollableTarget(View view, float rawX, float rawY, int dScrollY) {
        if (view == null) {
            return null;
        }
        if (!isUnder(view, rawX, rawY)) {
            return null;
        }
        if (view.canScrollVertically(dScrollY) || (dScrollY > 0 && view instanceof RecyclerView)) {
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
}