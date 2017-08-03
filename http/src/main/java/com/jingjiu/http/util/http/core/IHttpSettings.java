package com.jingjiu.http.util.http.core;

import com.jingjiu.http.util.http.callback.OnTaskCallback;

import java.util.Map;

/**
 * author xander on  2017/8/1.
 * function 对 http 的设置
 */

public interface IHttpSettings <T>{


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
    T setParams(Map<String, String> params);

    /**
     * 添加请求参数
     *
     * @param key   字段
     * @param value 值
     * @return 异步任务
     */
    T setParams(String key, String value);

    /**
     * 添加请求头
     *
     * @param key   字段
     * @param value 值
     * @return 异步任务
     */
    T setHeads(String key, String value);

    /**
     * 添加请求头
     *
     * @param heads 请求头
     * @return 异步任务
     */
    T setHeads(Map<String, String> heads);

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

/*    *//** 代理开关
     * @param open 布尔值
     * @return 实例
     *//*
    T openProxy(boolean open);*/
    /**
     * 设置回调接口
     *
     * @param taskCallback 回调接口
     */
    T setOnTaskCallback(OnTaskCallback taskCallback);

}
