package com.bean.http.core.http.core.pool;

/**
 * author xander on  2017/8/10.
 * function
 */

public interface IThreadPool {
    /**
     * @param r
     */
     void start(final Runnable r);

    /**
     *
     */
     void closeThreadPool();

    boolean isShutdownPool();
    boolean isTerminatedPool();
    int getCount();
}
