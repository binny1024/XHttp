package com.dragon.library_http.core;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.dragon.library_http.JJLogger;
import com.dragon.library_http.callback.OnHttpTaskCallback;
import com.dragon.library_http.exception.AdException;
import com.dragon.library_http.response.Response;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.dragon.library_http.ErrorCode.CODE_CANCLE;
import static com.dragon.library_http.ErrorCode.CODE_CONNECT;
import static com.dragon.library_http.ErrorCode.CODE_CONNECT_UNKNOWN_HOST;
import static com.dragon.library_http.ErrorCode.CODE_REQUEST_URL;
import static com.dragon.library_http.ErrorCode.CODE_TIME_OUT;
import static com.dragon.library_http.Util.toByteArray;
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
    private XHttpTask mXHttpTask;

    /**
     * 是任务管理器中的标志
     */
    private String mTag;
    /**
     * 异步任务管理器
     */
    static Map<String, XHttpTask> mTaskManager;
    /**
     * 请求的URL
     */
    private String mUrl;



    /**
     * 异步任务结果的回调
     */
    private OnHttpTaskCallback mTaskCallback;

    /**
     * 接受请求参数
     */
    private Map<String, String> mParams;

    /**
     * 接受请求头
     */
    private Map<String, String> mHeads;

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
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100));

    /**
     * 定义一个静态私有变量(不初始化，不使用final关键字，使用volatile保证了多线程访问时instance变量的可见性，
     * 避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用)
     */
    private static volatile TaskBuilder instance;

    /**
     * 定义一个私有构造方法
     */
    public TaskBuilder() {
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
        mUrl = url;
        httpType = METHOD_GET;
        return instance;
    }

    /**
     * @param url 请求url
     * @return  该类的实例
     */
    @Override
    public TaskBuilder post(String url) {
        mUrl = url;
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
        if (mHeads == null) {
            mHeads = new HashMap<>();
        }
        mHeads.put(key, value);
        return instance;
    }

    /**
     * @param heads 请求头
     * @return 该类的实例
     */
    @Override
    public TaskBuilder addHeads(Map<String, String> heads) {
        mHeads = heads;
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
    public TaskBuilder setOnHttpTaskCallback(OnHttpTaskCallback taskCallback) {
        mTaskCallback = taskCallback;
        return instance;
    }

    /**
     * @param charset 字符集
     * @return 该类的实例
     */
    @Override
    public TaskBuilder setCharset(String charset) {
        mCharset = charset;
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
            Log.i("param", "请求参数 ：" + sbuf.toString());
        }
        mXHttpTask = new XHttpTask();
        mXHttpTask.executeOnExecutor(executor, mUrl);
        if (!TextUtils.isEmpty(mTag)) {
            Log.i("xander","TaskBuilder.build : "+mTag);
            TaskManager.getIstance().addTask(mTag,mXHttpTask);
        }
        return instance;
    }

    /**
     * @param tag 线程标志
     */
    @Override
    public void cancel(String tag) {
        TaskManager.getIstance().cancel(tag);
        JJLogger.logInfo("cancelTask", tag.toString());
    }

    @Override
    public void cancelAll() {
        TaskManager.getIstance().cancelAll();
    }

    public interface ITaskManager {
        /**
         * @param tag  线程标志
         * @param task 线程
         */
        void addTask(String tag, XHttpTask task);

        /**
         *取消任务
         * @param tag
         */
        void cancel(String tag);
        /**
         *取消所有任务
         */
        void cancelAll();
    }

    /**
     * 异步任务管理器
     */
    public static class TaskManager  {
        private static final String TAG = "TaskManager";
        // 定义一个私有构造方法
        private TaskManager() {
        }

        //定义一个静态私有变量(不初始化，不使用final关键字，使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用)
        private static volatile  TaskManager instance;

        //定义一个共有的静态方法，返回该类型实例
        public static TaskManager getIstance() {
            // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
            if (instance == null) {
                //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
                synchronized ( TaskManager.class) {
                    //未初始化，则初始instance变量
                    if (instance == null) {
                        instance = new  TaskManager();
                    }
                }
            }
            return instance;
        }

        /**
         * @param tag  线程标志
         * @param task 线程
         */
         void addTask(String tag, XHttpTask task) {
            if (mTaskManager == null) {
                mTaskManager = new HashMap<>();
            }
            mTaskManager.put(tag.toString(), task);
        }

        /**
         * @param tag
         */
        public void cancel(String tag) {
            if (mTaskManager == null) {
                return;
            }
            int taskSize = mTaskManager.size();
            if (taskSize > 0) {
                for (final Map.Entry<String, XHttpTask> entry : mTaskManager.entrySet()) {
                    if (entry.getKey().equals(tag.toString())) {
                        Log.i(TAG,"TaskManager.cancel :"+entry.getKey());
                        entry.getValue().cancleTask();
                    }
                }
            }
            mTaskManager.clear();
        }

        /**
         *
         */
        public void cancelAll() {
            Log.i("xander","TaskManager.取消取消取消取消取消 :");
            if (mTaskManager == null) {
                return;
            }
            int taskSize = mTaskManager.size();
            if (taskSize > 0) {
                for (final Map.Entry<String, XHttpTask> entry : mTaskManager.entrySet()) {
                    entry.getValue().cancleTask();
                    Log.i("xander","TaskManager.cancel :"+entry.getKey());
                }
            }
            mTaskManager.clear();
        }
    }

    private class XHttpTask extends AsyncTask<String, Void, byte[]> {

        /**
         * 取消标志,默认不取消
         */
        boolean mIntercept = false;
        /**
         * 请求结果
         */
        private Response mResponse;
        XHttpTask() {
            mResponse = new Response();
        }
        void cancleTask() {
            mIntercept = true;
            Log.i(TAG,"TaskManager.cancel :");
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected byte[] doInBackground(String... urlStr) {
            if (httpType == METHOD_GET) {
                urlStr[0] = urlStr[0] + "?" + sbuf.toString();
            }
            HttpURLConnection httpUrlCon = null;
            if (mIntercept) {
                mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                mIntercept = false;
                return null;
            }
            try {
                if (mIntercept) {
                    mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                    mIntercept = false;
                    return null;
                }
                URL httpUrl = new URL(urlStr[0]);
                if (mIntercept) {
                    mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                    mIntercept = false;
                    return null;
                }
                httpUrlCon = (HttpURLConnection) httpUrl.openConnection();
                if (mIntercept) {
                    mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                    mIntercept = false;
                    return null;
                }
                httpUrlCon.setConnectTimeout(mHttpTimeout);// 建立连接超时时间
                if (mIntercept) {
                    mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                    mIntercept = false;
                    return null;
                }
                httpUrlCon.setReadTimeout(mHttpTimeout);//数据传输超时时间，很重要，必须设置。
                if (mIntercept) {
                    mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                    mIntercept = false;
                    return null;
                }
                //设置请求头
                if (mHeads != null) {
                    for (Map.Entry<String, String> entry : mHeads.entrySet()) {
                        httpUrlCon.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                if (mIntercept) {
                    mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                    mIntercept = false;
                    return null;
                }
                switch (httpType) {
                    case METHOD_GET:
                        if (mIntercept) {
                            mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                            mIntercept = false;
                            return null;
                        }
                        httpUrlCon.setRequestMethod("GET");// 设置请求类型为
                        break;
                    case METHOD__POST:
                        // 1、重新对请求报文进行  编码
                        httpUrlCon.setRequestMethod("POST");// 设置请求类型为
                        byte[] postData = null;
                        try {
                            postData = sbuf.toString().getBytes(mCharset);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        httpUrlCon.setDoInput(true); // 向连接中写入数据
                        httpUrlCon.setDoOutput(true); // 从连接中读取数据
                        httpUrlCon.setUseCaches(false); // 禁止缓存
                        httpUrlCon.setInstanceFollowRedirects(true);
                        if (postData != null) {
                            httpUrlCon.setRequestProperty("Content-length", String.valueOf(postData.length));
                        }
                        DataOutputStream out;
                        if (mIntercept) {
                            mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                            mIntercept = false;
                            return null;
                        } else {
                            out = new DataOutputStream(httpUrlCon.getOutputStream()); // 获取输出流
                        }
                        if (postData != null) {
                            out.write(postData);// 将要传递的参数写入数据输出流
                        }
                        out.flush(); // 输出缓存
                        out.close(); // 关闭数据输出流
                        break;
                    default:
                        break;

                }
                if (mIntercept) {
                    mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                    mIntercept = false;
                    return null;
                }
                if (httpUrlCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    if (mIntercept) {
                        mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                        mIntercept = false;
                        return null;
                    }
                    InputStream inputStream;
                    inputStream = httpUrlCon.getInputStream();
                    if (mIntercept) {
                        mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                        return null;
                    }
                    return toByteArray(inputStream);
                } else {
                    mResponse.setErrorInfo(new AdException("服务器错误 "), CODE_CONNECT);
                    JJLogger.logError("服务器错误 :" + CODE_CONNECT + "请求地址 ：" + urlStr[0], TAG);
                    return null;
                }
            }
            catch (UnknownHostException e) {
                mResponse.setErrorInfo(e, CODE_CONNECT_UNKNOWN_HOST);
                JJLogger.logInfo(TAG, "errorCode :" + CODE_CONNECT_UNKNOWN_HOST + "请求地址 ：" + urlStr[0]);
                return null;
            }
            catch (SocketTimeoutException e) {
                mResponse.setErrorInfo(e, CODE_TIME_OUT);
                JJLogger.logInfo(TAG, "errorCode :" + CODE_TIME_OUT + "请求地址 ：" + urlStr[0]);
                return null;
            }
            catch (MalformedURLException e) {
                mResponse.setErrorInfo(e, CODE_REQUEST_URL);
                JJLogger.logInfo(TAG, "errorCode :" + CODE_REQUEST_URL + "请求地址 ：" + urlStr[0]);
                return null;
            } catch (final IOException e) {
                mResponse.setErrorInfo(e, CODE_CONNECT);
                JJLogger.logInfo(TAG, "errorCode :" + CODE_CONNECT + "请求地址 ：" + urlStr[0]);
                return null;
            } finally {
                if (httpUrlCon != null) {
                    httpUrlCon.disconnect(); // 断开连接
                }
            }
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
//            super.onPostExecute(bytes);
            Log.i(TAG,"onPostExecute.cancel :");
            if (mIntercept) {
                mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                mTaskCallback.onFailure(mResponse.getException(), mResponse.getErrorCode());
                mIntercept = false;
                return;
            }
            if (bytes == null) {
                mTaskCallback.onFailure(mResponse.getException(), mResponse.getErrorCode());
            } else {
                mResponse.setBytes(bytes);
                mTaskCallback.onSuccess(mResponse);
            }

        }
    }
}
