package com.binny.sdk.constant;

import android.os.Handler;
import android.os.Looper;

/**
 * author xander on  2018/1/5.
 * function sdk的公共常量
 * 开屏所用的常量； 和 广告推送所用常量
 */

public class Constant {
    /**
     * 主线程的 loop
     */
    public static final Handler HANDLER = new Handler(Looper.getMainLooper());
}
