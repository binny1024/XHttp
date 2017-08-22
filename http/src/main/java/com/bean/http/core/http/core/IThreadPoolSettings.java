package com.bean.http.core.http.core;

import com.bean.http.core.http.core.pool.IThreadPool;

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
    T initHttp();
    /**
     * 该任务的一个人标志，身份
     *
     * @param tag 任务标志
     * @return 该类的实例
     */
    T setTag(String tag);

    /**
     * 开启串行线程池
     */
    T startSerialThreadPool();
    /**
     * 开启并行线程池
     */
    T startConcurrenceThreadPool();

    /**
     * @param poolExecutor 自定义线程池
     * @return 管理器
     */
    T customThreadPool(IThreadPool poolExecutor);
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
