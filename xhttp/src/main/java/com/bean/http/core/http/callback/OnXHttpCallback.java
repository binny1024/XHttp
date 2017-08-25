package com.bean.http.core.http.callback;


import com.bean.http.core.http.response.Response;

/**
 * author xander on  2017/7/25.
 * function
 */

public interface OnXHttpCallback {
    /**
     * 请求成功
     *
     * @param response 请求响应
     */
    void onSuccess(Response response);

    /**
     * 请求失败
     *
     * @param ex        异常信息
     * @param errorCode 错误码
     */
    void onFailure(Exception ex, String errorCode);
}
