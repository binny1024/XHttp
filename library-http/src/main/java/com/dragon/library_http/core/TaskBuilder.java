package com.dragon.library_http.core;


import android.text.TextUtils;

import com.dragon.library_http.JJLogger;
import com.dragon.library_http.callback.OnTaskCallback;
import com.dragon.library_http.core.manager.TaskManager;
import com.dragon.library_http.core.pool.JJThreadPool;
import com.dragon.library_http.core.task.HttpTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.dragon.library_http.core.ConfigHttp.METHOD_GET;
import static com.dragon.library_http.core.ConfigHttp.METHOD__POST;
import static com.dragon.library_http.core.ConfigHttp.httpType;
import static com.dragon.library_http.core.ConfigHttp.mHttpTimeout;


/**
 * author xander on  2017/5/31.
 * function  处理具体的业务逻辑 ，获取字符串
 */
@SuppressWarnings("unchecked")
public class TaskBuilder implements ITaskBuilder<TaskBuilder> {

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
     * 默认字符集
     */
    private String mCharset = "utf-8";


    /**
     * CPU数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 线程池核心线程数
     */
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;

    /**
     * 线程池最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    /**
     * 存活时间
     */
    private static final int KEEP_ALIVE = 1;

    /**
     * 线程池
     */
    private static JJThreadPool executor;

    /**
     * 定义一个静态私有变量(不初始化，不使用final关键字，使用volatile保证了多线程访问时instance变量的可见性，
     * 避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用)
     */
    private static volatile TaskBuilder instance;

    /**
     * 定义一个私有构造方法
     */
    public TaskBuilder() {
        mHttpTask = new HttpTask();
        if (executor == null) {
            executor = new JJThreadPool(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(100));
        }
        instance = this;
    }


    @Override
    public TaskBuilder tag(String tag) {
        mTag = tag;
        return instance;
    }

    /**
     * @param url 请求url
     * @return 该类的实例
     */
    @Override
    public TaskBuilder get(String url) {
        mHttpTask.setUrl(url);
        httpType = METHOD_GET;
        return instance;
    }

    /**
     * @param url 请求url
     * @return  该类的实例
     */
    @Override
    public TaskBuilder post(String url) {
        mHttpTask.setUrl(url);
        httpType = METHOD__POST;
        return instance;
    }

    /**
     * @param params 请求参数
     * @return 该类的实例
     */
    @Override
    public TaskBuilder addParams(Map<String, String> params) {
        mParams = params;
        return instance;
    }

    /**
     * @param key   字段
     * @param value 值
     * @return 该类的实例
     */
    @Override
    public TaskBuilder addParams(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return instance;
        }
        if (mParams == null) {
            mParams = new HashMap<>();
        }
        mParams.put(key, value);
        return instance;
    }

    /**
     * @param key   字段
     * @param value 值
     * @return 该类的实例
     */
    @Override
    public TaskBuilder addHeads(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return instance;
        }
        Map<String, String> heads = new HashMap<>();
        heads.put(key, value);
        mHttpTask.setHeads(heads);
        return instance;
    }

    /**
     * @param heads 请求头
     * @return 该类的实例
     */
    @Override
    public TaskBuilder addHeads(Map<String, String> heads) {
        mHttpTask.setHeads(heads);
        return instance;
    }

    /**
     * @param timeout 超时时间
     * @return 该类的实例
     */
    @Override
    public TaskBuilder setTimeout(int timeout) {
        mHttpTimeout = timeout;
        return instance;
    }


    /**
     * @param taskCallback 回调接口
     * @return 该类的实例
     */
    @Override
    public TaskBuilder setOnTaskCallback(OnTaskCallback taskCallback) {
        mHttpTask.setOnTaskCallback(taskCallback);
        return instance;
    }

    /**
     * @param charset 字符集
     * @return 该类的实例
     */
    @Override
    public TaskBuilder setCharset(String charset) {
        mHttpTask.setCharset(charset);
        return instance;
    }

    /**
     * @return 该类的实例
     */
    @Override
    public TaskBuilder build() {
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
            JJLogger.logInfo("param", "请求参数 ：" + sbuf.toString());
        }

        executor.execute(mHttpTask);
        if (!TextUtils.isEmpty(mTag)) {
             JJLogger.logInfo("xander","TaskBuilder.build : "+mTag);
            TaskManager.getIstance().addTask(mTag, mHttpTask);
        }
        return instance;
    }

    /**
     * @param tag 线程标志
     */
    @Override
    public void cancel(String tag) {
        TaskManager.getIstance().cancel(tag);
        executor.shutdownNow();
        JJLogger.logInfo("cancelTask", tag.toString());
    }

    @Override
    public void cancelAll() {
        TaskManager.getIstance().cancelAll();
        executor.shutdownNow();
    }






}
