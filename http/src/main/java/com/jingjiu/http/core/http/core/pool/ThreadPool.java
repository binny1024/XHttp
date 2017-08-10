package com.jingjiu.http.core.http.core.pool;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class ThreadPool extends ThreadPoolExecutor implements IThreadPool{

    @Override
    public void execute(final Runnable command) {
        super.execute(command);
    }


    public ThreadPool(int corePoolSize,int maximumPoolSize,long keepAliveTime) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public synchronized void start(final Runnable r) {
        Log.i(TAG, "开始执行新任务" + getActiveCount());
        execute(r);
    }

    @Override
    public void closeThreadPool() {
        this.shutdownNow();
    }

    @Override
    public boolean isShutdownPool() {
        return isShutdown();
    }

    @Override
    public boolean isTerminatedPool() {
        return isTerminated();
    }

    @Override
    public int getCount() {
        return getActiveCount();
    }
}
