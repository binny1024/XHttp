package com.jingjiu.sdk.core.ad;

/**
 * function
 */

public interface IManager {

    /**
     * 取消管理类中发起的任务
     */
    void cancelAll();
    /**
     * 取消管理类中特特定的任务
     */
    void cancel(String tag);

    /**
     * 释放资源
     */
    void release();
}
