package com.dragon.library_http.core;

/**
 * author xander on  2017/5/31.
 * function  网络请求所需的通用配置信息
 */

public class ConfigHttp {
    public final static int METHOD_GET = 0x10;
    public final static int METHOD__POST = 0x11;

    public static int mHttpTimeout = 2000;

    public static int httpType = METHOD_GET;
}
