package com.jingjiu.http.util.http.core.manager;


import android.text.TextUtils;
import android.util.Log;

import com.jingjiu.http.util.http.callback.OnTaskCallback;
import com.jingjiu.http.util.http.core.IHttpSettings;
import com.jingjiu.http.util.http.core.IThreadPoolSettings;
import com.jingjiu.http.util.http.core.pool.ThreadPool;
import com.jingjiu.http.util.http.core.task.HttpTask;
import com.jingjiu.http.util.logger.JJLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import static com.jingjiu.http.common.ErrorCode.CODE_CANCLE;


/**
 * author xander on  2017/5/31.
 * function  处理具体的业务逻辑 ，获取字符串
 */
@SuppressWarnings("unchecked")
public class TaskManager implements IHttpSettings<TaskManager>, IThreadPoolSettings<TaskManager> {

    private final String TAG = "xander";

    /**
     * 异步任务
     */
    private HttpTask mHttpTask;

    /**
     * 线程池核心线程数
     */
    private static final int CORE_POOL_SIZE = 10;

    /**
     * 线程池最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE = 100;

    /**
     * 存活时间
     */
    private static final int KEEP_ALIVE = 15;

    /**
     * 线程池
     */
    private static ThreadPool sThreadPool;

    /**
     * 开启线程池
     */
    private boolean mStartThreadPool;
    /**
     * 定义一个静态私有变量(不初始化，不使用final关键字，使用volatile保证了多线程访问时instance变量的可见性，
     * 避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用)
     */
    private static volatile TaskManager mInstance;
    /**
     * 异步任务管理器
     */
    private static Map<String, HttpTask> mTaskMap;

    // 定义一个私有构造方法
    private TaskManager() {

    }

    private static class SingletonHolder {
        private static final TaskManager TASK_MANAGER = new TaskManager();
        ;
    }

    //定义一个共有的静态方法，返回该类型实例
    public static TaskManager getmInstance() {
        mInstance = SingletonHolder.TASK_MANAGER;
        return mInstance;
    }

    @Override
    public TaskManager setTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            if (mTaskMap == null) {
                mTaskMap = new HashMap<>();
            }
            mTaskMap.put(tag, mHttpTask);//用于管理任务
        }
        return mInstance;
    }


    @Override
    public TaskManager initTask() {
        mHttpTask = new HttpTask();
        return mInstance;
    }

    @Override
    public TaskManager startThreadPool() {
        mStartThreadPool = true;
        if (sThreadPool == null) {
            sThreadPool = new ThreadPool(0, Integer.MAX_VALUE,
                    KEEP_ALIVE, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
        }
        return mInstance;
    }

    @Override
    public TaskManager closeThreadPool() {
        sThreadPool.shutdownNow();
        return mInstance;
    }

    /**
     * @param url 请求url
     * @return 该类的实例
     */
    @Override
    public TaskManager get(String url) {
        mHttpTask.get(url);
        return mInstance;
    }

    /**
     * @param url 请求url
     * @return 该类的实例
     */
    @Override
    public TaskManager post(String url) {
        mHttpTask.post(url);
        return mInstance;
    }

    /**
     * @param params 请求参数
     * @return 该类的实例
     */
    @Override
    public TaskManager setParams(Map<String, String> params) {
        mHttpTask.setParams(params);
        return mInstance;
    }

    /**
     * @param key   字段
     * @param value 值
     * @return 该类的实例
     */
    @Override
    public TaskManager setParams(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return mInstance;
        }
        mHttpTask.setParams(key, value);
        return mInstance;
    }

    /**
     * @param key   字段
     * @param value 值
     * @return 该类的实例
     */
    @Override
    public TaskManager setHeads(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return mInstance;
        }
        Map<String, String> heads = new HashMap<>();
        heads.put(key, value);
        mHttpTask.setHeads(heads);
        return mInstance;
    }

    /**
     * @param heads 请求头
     * @return 该类的实例
     */
    @Override
    public TaskManager setHeads(Map<String, String> heads) {
        mHttpTask.setHeads(heads);
        return mInstance;
    }

    /**
     * @param timeout 超时时间
     * @return 该类的实例
     */
    @Override
    public TaskManager setTimeout(int timeout) {
        mHttpTask.setTimeout(timeout);
        return mInstance;
    }

    /**
     * @param charset 字符集
     * @return 该类的实例
     */
    @Override
    public TaskManager setCharset(String charset) {
        mHttpTask.setCharset(charset);
        return mInstance;
    }

/*    @Override
    public TaskManager openProxy(final boolean open) {
        mHttpTask.openProxy(open);
        return mInstance;
    }*/

    @Override
    public TaskManager setOnTaskCallback(final OnTaskCallback taskCallback) {
        mHttpTask.setOnTaskCallback(taskCallback);
        if (mStartThreadPool && sThreadPool != null) {
            mStartThreadPool = false;
            if (sThreadPool.isShutdown() || sThreadPool.isTerminated()) {
                JJLogger.logInfo(TAG, "TaskManager.execute : 线程池已关闭 错误码：" + CODE_CANCLE);
                return mInstance;
            }
            try {
                sThreadPool.execute(mHttpTask);
            } catch (RejectedExecutionException e) {
                JJLogger.logInfo(TAG, "TaskManager.execute :" + sThreadPool.getActiveCount());
            }
        } else {
            new Thread(mHttpTask).start();
        }
        return mInstance;
    }


    /**
     * @return 该类的实例
     */
    @Override
    public TaskManager execute() {
        if (mStartThreadPool && sThreadPool != null) {
            mStartThreadPool = false;
            if (sThreadPool.isShutdown() || sThreadPool.isTerminated()) {
                JJLogger.logInfo(TAG, "TaskManager.execute : 线程池已关闭 错误码：" + CODE_CANCLE);
                return mInstance;
            }
            try {
                sThreadPool.execute(mHttpTask);
            } catch (RejectedExecutionException e) {
                JJLogger.logInfo(TAG, "TaskManager.execute :" + sThreadPool.getActiveCount());
            }
        } else {
            new Thread(mHttpTask).start();
        }
        return mInstance;
    }

    /**
     * @param tag 线程标志
     */
    @Override
    public void cancel(String tag) {
        if (mTaskMap == null) {
            return;
        }
        int taskSize = mTaskMap.size();
        if (taskSize > 0) {
            for (final Map.Entry<String, HttpTask> entry : mTaskMap.entrySet()) {
                if (entry.getKey().equals(tag)) {
                    Log.i(TAG, "TaskManager.cancel :" + entry.getKey());
                    entry.getValue().cancle();
                }
            }
        }
    }

    @Override
    public void cancelAll() {
        if (mTaskMap == null) {
            return;
        }
        int taskSize = mTaskMap.size();
        if (taskSize > 0) {
            for (final Map.Entry<String, HttpTask> entry : mTaskMap.entrySet()) {
                entry.getValue().cancle();
            }
        }
        mTaskMap.clear();
    }

}
