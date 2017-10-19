package com.dragon.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * author xander on  2017/4/28.
 * function
 */
/*
* function:
* Encapsulate findViewById to avoid forced type conversions
* */
public final class UtilWidget {
    @SuppressWarnings("unchecked")
    public static <V extends View> V getView(Activity activity , int itemViewId){
        return (V) activity.findViewById(itemViewId);
    }
    @SuppressWarnings("unchecked")
    public static <V extends View> V getView(View convertView, int itemViewId){
        return (V) convertView.findViewById(itemViewId);
    }
    /**
     * 点击动画效果
     *
     *  view 的透明度变化
     */
    public static void setViewAlphaAnimation(View view) {
        AlphaAnimation alphaAni = new AlphaAnimation(0.05f, 1.0f);
        alphaAni.setDuration(100);                // 设置动画效果时间
        view.startAnimation(alphaAni);        // 添加光效动画到VIew
    }

    public static void showErrorInfo(Activity activity, final String message, final String title) {
        new AlertDialog.Builder(activity)
                .setTitle(title).setMessage(message)
                .setPositiveButton(" 确定 ", null).show();
    }
}
