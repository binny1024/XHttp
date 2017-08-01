package com.dragon.library_http.http.core;

import android.os.Handler;
import android.os.Looper;

/**
 * author xander on  2017/5/31.
 * function  网络请求所需的通用配置信息
 */

public class ConfigHttp {
    public final static int METHOD_GET = 0x10;
    public final static int METHOD__POST = 0x11;

    public static int mHttpTimeout = 1000;

    public static int httpType = METHOD_GET;

    /**
     * 主线程的 loop
     */
    public static final Handler HANDLER = new Handler(Looper.getMainLooper());
}
