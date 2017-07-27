package com.dragon.library_http.core.task;


import com.dragon.library_http.callback.OnTaskCallback;

import java.util.Map;

/**
 * author xander on  2017/7/27.
 * function
 */

public interface IHttpTask {

    /** 设置url
     * @param url
     */
    void setUrl(String url);
    /**
     * 设置字符集
     *
     * @param charset 字符集
     * @return 异步任务
     */
    void setCharset(String charset);

    /**
     * 添加请求头
     *
     * @param heads 请求头
     * @return 异步任务
     */
    void setHeads(Map<String, String> heads);

    /**
     * 设置请求参数
     * @param params
     */
    void addParams(String params);
    /**
     * 设置回调接口
     *
     * @param taskCallback 回调接口
     * @return 异步任务
     */
    void setOnTaskCallback(OnTaskCallback taskCallback);

    /**
     * 取消任务
     */
    void cancle();
}
