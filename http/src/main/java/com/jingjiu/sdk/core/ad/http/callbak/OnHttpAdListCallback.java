package com.jingjiu.sdk.core.ad.http.callbak;

import com.jingjiu.sdk.core.ad.bean.AdListBean;

/**
 * function 请求广告列表的回调
 */

public interface OnHttpAdListCallback {

    /**
     * 请求列表成功的回调
     *
     * @param newAdListBean 广告列表实体
     */
    void onHttpAdListSuccess(AdListBean newAdListBean);

    /**
     * 请求列表失败的回调
     *
     * @param e         异常信息
     * @param errorCode 错误码
     */
    void onHttpAdListFailure(Exception e, String errorCode);
}
