package com.binny.sdkdemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * author xander on  2017/9/11.
 * function
 */

public class Util {
    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    /**
     * 获取屏幕的宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static <T extends View> T getView(Activity activity, int id) {

        return (T) activity.findViewById(id);

    }
    public static <T extends View> T getView(View activity, int id) {

        return (T) activity.findViewById(id);

    }
    public static void setViewAlphaAnimation(View view) {
        AlphaAnimation alphaAni = new AlphaAnimation(0.05f, 1.0f);
        alphaAni.setDuration(100);                // 设置动画效果时间
        view.startAnimation(alphaAni);        // 添加光效动画到VIew
    }
    /**
     * 返回当前屏幕是否为竖屏。
     * @param context
     * @return 当且仅当当前屏幕为竖屏时返回true,否则返回false。
     */
    public static boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

}
