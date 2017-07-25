package com.dragon.library_http.core;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.dragon.library_http.JJLogger;
import com.dragon.library_http.Util;
import com.dragon.library_http.callback.OnHttpTaskCallback;
import com.dragon.library_http.response.Response;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
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
public class HttpTask extends AsyncTask<String, Void, byte[]> {

    private OnHttpTaskCallback mSpiderCallback;
    private Response mResponse;
    private Map<String, String> mParams;//接受请求参数
    private Map<String, String> mHeads;//接受请求参数

    private String mCharset = "utf-8";
    private HttpTask instance;

    protected boolean interceptFlag = false;//取消标志,默认不取消
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    protected static ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100));

    public boolean isInterceptFlag() {
        return interceptFlag;
    }

    public HttpTask() {
        instance = this;
    }

    public HttpTask type(int type) {
        httpType = type;
        mResponse = new Response();
        return instance;
    }

    public HttpTask addParams(Map<String, String> params) {
        mParams = params;
        return instance;
    }

    public HttpTask addParams(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return instance;
        }
        if (mParams == null) {
            mParams = new HashMap<>();
        }
        mParams.put(key, value);

        return instance;
    }

    public HttpTask addHeads(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return instance;
        }
        if (mHeads == null) {
            mHeads = new HashMap<>();
        }
        mHeads.put(key, value);

        return instance;
    }

    public HttpTask addHeads(Map<String, String> heads) {
        mHeads = heads;
        return instance;
    }

    public HttpTask setTimeout(int timeout) {
        mHttpTimeout = timeout;
        return instance;
    }

    public HttpTask setParams(Map<String, String> params) {
        mParams = params;
        return this;
    }

    public HttpTask setOnSpiderCallbackk(OnHttpTaskCallback spiderCallback) {
        mSpiderCallback = spiderCallback;
        return instance;
    }

    public HttpTask setHeads(Map<String, String> heads) {
        mHeads = heads;
        return instance;
    }

    public HttpTask setCharset(String charset) {
        mCharset = charset;
        return instance;
    }

    public HttpTask start(String url) {
        executeOnExecutor(executor, url);
        return instance;
    }

    public void cancel() {
        interceptFlag = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected byte[] doInBackground(String... urlStr) {

        StringBuilder sbuf = new StringBuilder();
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
            JJLogger.i("param","请求参数 ："+sbuf.toString());
            if (httpType == METHOD_GET) {
                urlStr[0] = urlStr[0] + "?" + sbuf.toString();
            }
        }
        HttpURLConnection httpUrlCon = null;

        try {
            URL httpUrl = new URL(urlStr[0]);
            JJLogger.i("param",""+urlStr[0]);
            httpUrlCon = (HttpURLConnection) httpUrl.openConnection();
            httpUrlCon.setConnectTimeout(mHttpTimeout);// 建立连接超时时间
            httpUrlCon.setReadTimeout(mHttpTimeout);//数据传输超时时间，很重要，必须设置。

            //设置请求头
            if (mHeads != null) {
                for (Map.Entry<String, String> entry : mHeads.entrySet()) {
                    httpUrlCon.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            switch (httpType) {
                case METHOD_GET:
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
                    if (!interceptFlag) {
                        out = new DataOutputStream(httpUrlCon.getOutputStream()); // 获取输出流
                    } else {
                        return null;
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
            if (!interceptFlag) {//用户没有取消
                httpUrlCon.connect();
            } else {
                return null;
            }
            if (httpUrlCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream;
                if (!interceptFlag) {
                    inputStream = httpUrlCon.getInputStream();
                } else {
                    interceptFlag = false;
                    return null;
                }
                return Util.toByteArray(inputStream);
            } else {
                return null;
            }
        } catch (final IOException e) {
            return null;
        } finally {
            if (httpUrlCon != null) {
                httpUrlCon.disconnect(); // 断开连接
                interceptFlag = false;
            }
        }
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
        if (bytes == null) {
            mSpiderCallback.onFailure(mResponse.getException(), mResponse.getErrorCode());
        } else {
            mResponse.setBytes(bytes);
            mSpiderCallback.onSuccess(mResponse);
        }
    }

}
