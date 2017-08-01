package com.dragon.library_http.http.core;


import com.dragon.library_http.http.callback.OnTaskCallback;

import java.util.Map;

/**
 * function
 */

interface ITaskManager<T extends ITaskManager> {


    /**
     * 请求方式 initGet
     *
     * @param url 请求url
     * @return 任务管理
     */
    T initGet(String url);

    /**
     * 请求方式 initPost
     *
     * @param url 请求url
     * @return 任务管理
     */
    T initPost(String url);

    /**
     * 该任务的一个人标志，身份
     *
     * @param tag 任务标志
     * @return 任务管理
     */
    T setTag(String tag);

    /**
     * 开启线程池
     * @return 任务管理
     */
    T startThreadPool();

    /**
     * 关闭线程池
     * @return 任务管理
     */
    T closeThreadPool();

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
     * 设置字符集
     *
     * @param charset 字符集
     * @return 异步任务
     */
    T setCharset(String charset);

    /**
     * 设置优先级
     *
     * @param priority 优先级
     * @return 任务管理器
     */
    T setPriority(int priority);

    /**
     * 发起异步任务
     *
     * @param taskCallback 请求回调
     * @return 该类的实例
     */
    T execute(OnTaskCallback taskCallback);

    /**
     * 取消指定任务
     *
     * @param tag 线程标志
     */
    void cancel(String tag);

    /**
     * 取消所有任务
     */
    void cancelAll();

}
