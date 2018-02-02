package com.binny.sdk.core.splash.check.callback;

/**
 * function 回调结果 在主线程
 */

public interface OnDoNotLoadAdCallback {

    /**
     * 运行在主线程
     * 不展示广告
     *
     * @param errorCode 错误码，不展示广告的原因
     */
    void onDoNotLoadAd(String errorCode);
}
