package com.binny.sdk.core.splash.http.callbak;

/**
 * function 请求广告数据的回调
 */

public interface OnHttpAdShowIdCallback {

    /**
     * 请求要展示的哪一张广告成功的回调
     *
     * @param showValidAdId 要展示的广告id
     */
    void onHttpAdIdSuccess(String showValidAdId);

    /**
     * 请求要展示的哪一张广告失败的回调
     *
     * @param e         异常信息
     * @param errorCode 错误码
     */
    void onHttpAdIdFailure(Exception e, String errorCode);

}
