package com.jingjiu.sdk.core.ad.callback;

/*
* 广告管理接口是否存在
* */
public interface OnAdManagerCallback {

    /**
     * 广告存在的回调函数
     */
    void onAdExist();

    /**
     * 广告不存在的回调函数
     *
     * @param resultCode
     */
    void onAdNotExist(String resultCode);

}
