package com.binny.sdk.core.vlayer.callback;

/**
 * author xander on  2017/9/9.
 * function  广告点击的监听接口
 */

public interface OnJJADLayerListener {
    /** 广告点击的监听
     * @param url 跳转的连接
     */
    void onAdClickedListener(final String url);

    /** 配置错误信息
     * @param msg 错误信息
     */
    void onAdConfigError(final String msg);
}
