package com.dragon.library_http.core;


import com.dragon.library_http.callback.OnTaskCallback;

import java.util.Map;

/**
 * function
 */

public interface ITaskBuilder<T extends ITaskBuilder> {

    /**
     * 该任务的一个人标志，身份
     *
     * @return 该类的实例
     * @param tag
     */
    T tag(String tag);

    /**
     * 请求方式 get
     *
     * @param url 请求url
     * @return 该类的实例
     */
    T get(String url);

    /**
     * 请求方式 post
     *
     * @param url 请求url
     * @return 异步任务
     */
    T post(String url);

    /**
     * 添加请求参数
     *
     * @param params 请求参数
     * @return 异步任务
     */
    T addParams(Map<String, String> params);

    /**
     * 添加请求参数
     *
     * @param key   字段
     * @param value 值
     * @return 异步任务
     */
    T addParams(String key, String value);

    /**
     * 添加请求头
     *
     * @param key   字段
     * @param value 值
     * @return 异步任务
     */
    T addHeads(String key, String value);

    /**
     * 添加请求头
     *
     * @param heads 请求头
     * @return 异步任务
     */
    T addHeads(Map<String, String> heads);

    /**
     * 设置超时时间
     *
     * @param timeout 超时时间
     * @return 异步任务
     */
    T setTimeout(int timeout);

    /**
     * 设置回调接口
     *
     * @param taskCallback 回调接口
     * @return 异步任务
     */
    T setOnTaskCallback(OnTaskCallback taskCallback);

    /**
     * 设置字符集
     *
     * @param charset 字符集
     * @return 异步任务
     */
    T setCharset(String charset);

    /**
     * 发起异步任务
     *
     * @return 该类的实例
     */
    T build();

    /**  取消指定任务
     * @param tag 线程标志
     */
    void cancel(String tag);

    /**
     *取消所有任务
     */
    void cancelAll();
}
