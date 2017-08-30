package com.bean.xhttp.common;

/**
 * function sdk 错误码
 */

public final class ErrorCode {

    public static final String CODE_CONNECT = "connect";//服务器错误
    public static final String CODE_CONNECT_UNKNOWN_HOST = "10000";//不识别的主机(1)

    /**
     * 以下错误只会显示在logcat中
     */
    // 网络相关
    public static final String CODE_TIME_OUT = "time_out";//基层框架使用
    public static final String CODE_REQUEST_URL = "10001";//url不正确（1）
    public static final String CODE_CANCLE = "10002";//用户取消操作（1）
}
