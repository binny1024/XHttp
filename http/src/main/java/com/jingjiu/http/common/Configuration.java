package com.jingjiu.http.common;

import android.os.Handler;
import android.os.Looper;

/**
 * function sdk 所需的配置参数
 */

public final class Configuration {

    /**
     * 主线程的 loop
     */
    public static final Handler HANDLER = new Handler(Looper.getMainLooper());
}
