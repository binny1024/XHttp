package com.dragon.library_http.http.core.task;


import com.dragon.library_http.http.callback.OnTaskCallback;

import java.util.Map;

/**
 * author xander on  2017/7/27.
 * function  定义线程任务
 */

public interface IHttpTask {
    /** 设置优先级
     * @param priority  优先级
     */
    void setPriority(int priority);
    /** 设置url
     * @param url
     */
    void setUrl(String url);
    /** 设置 setTag
     * @param tag
     */
    void setTag(String tag);
    /**
     * 设置字符集
     *
     * @param charset 字符集
     */
    void setCharset(String charset);

    /**
     * 添加请求头
     *
     * @param heads 请求头
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
     */
    void setOnTaskCallback(OnTaskCallback taskCallback);

    /**
     * 取消任务
     */
    void cancle();
}
