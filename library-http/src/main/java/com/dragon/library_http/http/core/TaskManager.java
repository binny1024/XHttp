package com.dragon.library_http.http.core;


import android.text.TextUtils;
import android.util.Log;

import com.dragon.library_http.JJLogger;
import com.dragon.library_http.http.callback.OnTaskCallback;
import com.dragon.library_http.http.core.pool.ThreadPool;
import com.dragon.library_http.http.core.task.HttpTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import static com.dragon.library_http.ErrorCode.CODE_CANCLE;
import static com.dragon.library_http.http.core.ConfigHttp.METHOD_GET;
import static com.dragon.library_http.http.core.ConfigHttp.METHOD__POST;
import static com.dragon.library_http.http.core.ConfigHttp.httpType;
import static com.dragon.library_http.http.core.ConfigHttp.mHttpTimeout;


/**
 * author xander on  2017/5/31.
 * function  处理具体的业务逻辑 ，获取字符串
 */
@SuppressWarnings("unchecked")
public class TaskManager implements ITaskManager<TaskManager> {

    private final String TAG = "xander";

    /**
     * 异步任务
     */
    private HttpTask mHttpTask;

    /**
     * 是任务管理器中的标志
     */
    private String mTag;

    /**
     * 接受请求参数
     */
    private Map<String, String> mParams;

    /**
     * 处理请求参数或请求体
     */
    private StringBuilder sbuf = new StringBuilder();


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
    private Map<String, HttpTask> mTaskMap;
    // 定义一个私有构造方法
    private TaskManager() {

    }

    //定义一个共有的静态方法，返回该类型实例
    public static TaskManager getmInstance() {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (mInstance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (TaskManager.class) {
                //未初始化，则初始instance变量
                if (mInstance == null) {
                    mInstance = new TaskManager();
                }
            }
        }
        return mInstance;
    }
    private void initHttpTask(String url, int method) {
        mHttpTask = new HttpTask();
        httpType = method;
        mHttpTask.setUrl(url);
    }

    @Override
    public TaskManager setTag(String tag) {
        mTag = tag;
        mHttpTask.setTag(tag);
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
    public TaskManager initGet(String url) {
        initHttpTask(url, METHOD_GET);
        return mInstance;
    }

    /**
     * @param url 请求url
     * @return 该类的实例
     */
    @Override
    public TaskManager initPost(String url) {
        initHttpTask(url, METHOD__POST);
        return mInstance;
    }

    /**
     * @param params 请求参数
     * @return 该类的实例
     */
    @Override
    public TaskManager addParams(Map<String, String> params) {
        mParams = params;
        return mInstance;
    }

    /**
     * @param key   字段
     * @param value 值
     * @return 该类的实例
     */
    @Override
    public TaskManager addParams(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return mInstance;
        }
        if (mParams == null) {
            mParams = new HashMap<>();
        }
        mParams.put(key, value);
        return mInstance;
    }

    /**
     * @param key   字段
     * @param value 值
     * @return 该类的实例
     */
    @Override
    public TaskManager addHeads(String key, String value) {
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
    public TaskManager addHeads(Map<String, String> heads) {
        mHttpTask.setHeads(heads);
        return mInstance;
    }

    /**
     * @param timeout 超时时间
     * @return 该类的实例
     */
    @Override
    public TaskManager setTimeout(int timeout) {
        mHttpTimeout = timeout;
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

    @Override
    public TaskManager setPriority(int priority) {
        mHttpTask.setPriority(priority);
        return mInstance;
    }

    /**
     * @param taskCallback 请求回调
     * @return 该类的实例
     */
    @Override
    public TaskManager execute(OnTaskCallback taskCallback) {
        mHttpTask.setOnTaskCallback(taskCallback);
        /*
        * 处理请求参数
        * */
        if (mParams != null) {
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                sbuf.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            mParams = null;
            if (sbuf.length() > 0) {
                sbuf.deleteCharAt(sbuf.length() - 1);
            }
            mHttpTask.addParams(sbuf.toString());
        }
        if (!TextUtils.isEmpty(mTag)) {
            if (mTaskMap == null) {
                mTaskMap = new HashMap<>();
            }
            mTaskMap.put(mTag, mHttpTask);
        }
        if (mStartThreadPool&&sThreadPool != null) {
            mStartThreadPool = false;
            if (sThreadPool.isShutdown() || sThreadPool.isTerminated()) {
                JJLogger.logInfo(TAG, "TaskManager.execute : 线程池已关闭 错误码：" + CODE_CANCLE);
                return mInstance;
            }
            sThreadPool.setTag(mTag);
            try {
                sThreadPool.execute(mHttpTask);
            }catch (RejectedExecutionException e){
                JJLogger.logInfo(TAG,"TaskManager.execute :" + sThreadPool.getActiveCount());
            }
        }else {
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
             JJLogger.logInfo(TAG,"TaskManage任务正在进行");
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
        mTaskMap.clear();
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
