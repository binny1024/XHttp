package com.jingjiu.http.core.http.core;

/**
 * author xander on  2017/8/1.
 * function 对线程池的操作
 */

public interface IThreadPoolSettings<T extends IThreadPoolSettings> {
    /**
     * 初始化 任务
     *
     * @return 该接口的实例
     */
    T initTask();
    /**
     * 该任务的一个人标志，身份
     *
     * @param tag 任务标志
     * @return 该类的实例
     */
    T setTag(String tag);

    /**
     * 开启线程池
     */
    T startThreadPool();

    /**
     * 关闭线程池
     */
    T closeThreadPool();

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
