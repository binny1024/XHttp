package com.dragon.library_http.core.pool;


import com.dragon.library_http.JJLogger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * author xander on  2017/7/27.
 * function  
 */

public class JJThreadPool extends ThreadPoolExecutor {
    private static final String TAG = "JJThreadPool";
    public JJThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
         JJLogger.logInfo(TAG,"JJThreadPool.beforeExecute :");
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
         JJLogger.logInfo(TAG,"JJThreadPool.afterExecute :");
    }

    @Override
    public int getActiveCount() {
         JJLogger.logInfo(TAG,"JJThreadPool.getActiveCount :");
        return super.getActiveCount();
    }

    /** 立即终止线程池，并尝试打断正在执行的任务，并且清空任务缓存队列
     * @return 尚未执行的任务
     */
    @Override
    public List<Runnable> shutdownNow() {
         JJLogger.logInfo(TAG,"JJThreadPool.shutdownNow :");
        return super.shutdownNow();
    }
}
