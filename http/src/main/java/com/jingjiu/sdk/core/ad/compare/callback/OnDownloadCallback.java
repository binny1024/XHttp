package com.jingjiu.sdk.core.ad.compare.callback;

/**
 * function
 */

public interface OnDownloadCallback {
    /**
     * 下载成功的回调
     *
     * @param adId 广告id
     */
    void onSuccess(String adId);

    /**
     * 下载失败的回调
     *
     * @param adId
     * @param errorCode 错误码
     */
    void onFailure(String adId, String errorCode);
}
