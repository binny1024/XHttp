package com.jingjiu.http.util.http.core.task;

import android.text.TextUtils;

import com.jingjiu.http.common.ErrorCode;
import com.jingjiu.http.exception.AdException;
import com.jingjiu.http.util.http.callback.OnTaskCallback;
import com.jingjiu.http.util.http.response.Response;
import com.jingjiu.http.util.logger.JJLogger;

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

import static com.jingjiu.http.common.Configuration.HANDLER;
import static com.jingjiu.http.common.ErrorCode.CODE_CANCLE;
import static com.jingjiu.http.common.ErrorCode.CODE_CONNECT;
import static com.jingjiu.http.common.ErrorCode.CODE_CONNECT_UNKNOWN_HOST;
import static com.jingjiu.http.common.ErrorCode.CODE_REQUEST_URL;
import static com.jingjiu.http.common.ErrorCode.CODE_TIME_OUT;
import static com.jingjiu.http.util.CommonMethod.toByteArray;

public class HttpTask implements Runnable, IHttpTask {

    private static final String TAG = "HttpTask";

    /**
     * 取消标志,默认不取消
     */
    private boolean mIntercept = false;

    /**
     * 请求结果
     */
    private Response mResponse;

    /**
     * 请求url
     */
    private String mUrl;
    /**
     * 接受请求参数
     */
    private Map<String, String> mParamsMap;
    /**
     * 请求参数
     */
    private String mParams;

    /**
     * 处理请求参数或请求体
     */
    private StringBuilder sbuf = new StringBuilder();
    /**
     * 默认字符集
     */
    private String mCharset = "utf-8";

    /**
     * 请求方式
     */
    private final int METHOD_GET = 0x10;
    private final int METHOD__POST = 0x11;
    private int mHttpType = METHOD_GET;

    /**
     * 超时时间，默认 2s
     */
    private int mHttpTimeout = 2000;

    /**
     * 接受请求头
     */
    private Map<String, String> mHeads;

    /**
     * 清请求结果的回调
     */
    private OnTaskCallback mTaskCallback;

    public HttpTask() {
        mResponse = new Response();
    }

    @Override
    public IHttpTask setCharset(String charset) {
        mCharset = charset;
        return this;
    }

    @Override
    public IHttpTask setTimeout(final int timeout) {
        mHttpTimeout = timeout;
        return this;
    }

    @Override
    public IHttpTask get(final String url) {
        mUrl = url;
        mHttpType = METHOD_GET;
        return this;
    }

    @Override
    public IHttpTask post(final String url) {
        mUrl = url;
        mHttpType = METHOD__POST;
        return this;
    }

    @Override
    public IHttpTask setParams(final Map<String, String> stringMap) {
        mParamsMap = stringMap;
        return this;
    }

    private void handleParams(final Map<String, String> stringMap) {
        /*
        * 处理请求参数
        * */
        if (stringMap != null) {
            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                sbuf.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            mParams = null;
            if (sbuf.length() > 0) {
                sbuf.deleteCharAt(sbuf.length() - 1);
            }

            mParams = sbuf.toString();
            JJLogger.logInfo(TAG, "HttpTask.handleParams :" + mParams);
        }
    }

    @Override
    public IHttpTask setParams(final String key, final String value) {
        if (mParamsMap == null) {
            mParamsMap = new HashMap<>();
        }
        mParamsMap.put(key, value);
        return this;
    }

    @Override
    public IHttpTask setHeads(final String key, final String value) {
        if (mHeads == null) {
            mHeads = new HashMap<>();
        }
        mHeads.put(key, value);
        return this;
    }

    @Override
    public IHttpTask setHeads(Map<String, String> heads) {
        mHeads = heads;
        return this;
    }

    @Override
    public IHttpTask setOnTaskCallback(OnTaskCallback taskCallback) {
        mTaskCallback = taskCallback;
        return this;
    }


    @Override
    public void cancle() {
        mIntercept = true;
    }

    @Override
    public void run() {
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }
        handleParams(mParamsMap);
        if (mHttpType == METHOD_GET && !TextUtils.isEmpty(mParams)) {
            mUrl = mUrl + "?" + mParams;
        }

        HttpURLConnection httpUrlCon = null;
        InputStream inputStream;

        if (mIntercept) {
            mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
            mIntercept = false;
            postRun(mResponse);
            return;
        }
        try {
            JJLogger.logInfo(TAG, "HttpTask.run :" + mUrl);
            if (TextUtils.isEmpty(mUrl)) {
                mResponse.setErrorInfo(new AdException("url 为空！"),ErrorCode.CODE_REQUEST_URL);
                postRun(mResponse);
                return;
            }
            URL httpUrl = new URL(mUrl);
            httpUrlCon = (HttpURLConnection) httpUrl.openConnection();
            httpUrlCon.setConnectTimeout(mHttpTimeout);// 建立连接超时时间
            httpUrlCon.setReadTimeout(mHttpTimeout);//数据传输超时时间，很重要，必须设置。
            if (mIntercept) {
                mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                mIntercept = false;
                postRun(mResponse);
                return;
            }
            //设置请求头
            if (mHeads != null) {
                for (Map.Entry<String, String> entry : mHeads.entrySet()) {
                    httpUrlCon.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            switch (mHttpType) {
                case METHOD_GET:
                    httpUrlCon.setRequestMethod("GET");// 设置请求类型为
                    break;
                case METHOD__POST:
                    // 1、重新对请求报文进行  编码
                    httpUrlCon.setRequestMethod("POST");// 设置请求类型为
                    byte[] postData = null;
                    try {
                        if (!TextUtils.isEmpty(mParams)) {
                            postData = mParams.getBytes(mCharset);
                        }
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
                        postRun(mResponse);
                        return;
                    } else {
                        out = new DataOutputStream(httpUrlCon.getOutputStream()); // 获取输出流
                        if (postData != null) {
                            out.write(postData);// 将要传递的参数写入数据输出流
                        }
                        out.flush(); // 输出缓存
                        out.close(); // 关闭数据输出流
                    }
                    break;
                default:
                    break;
            }

            if (httpUrlCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpUrlCon.getInputStream();
                if (mIntercept) {
                    mResponse.setErrorInfo(new AdException("用户取消"), ErrorCode.CODE_CANCLE);
                    postRun(mResponse);
                    return;
                }
                final byte[] bytes = toByteArray(inputStream);
                mResponse.setBytes(bytes);
                postRun(mResponse);
            } else {
                mResponse.setErrorInfo(new AdException("找不到服务器 "), CODE_CONNECT);
                postRun(mResponse);
            }
        } catch (UnknownHostException e) {
            mResponse.setErrorInfo(e, CODE_CONNECT_UNKNOWN_HOST);
            postRun(mResponse);
        } catch (SocketTimeoutException e) {
            mResponse.setErrorInfo(e, CODE_TIME_OUT);
            postRun(mResponse);
        } catch (MalformedURLException e) {
            mResponse.setErrorInfo(e, CODE_REQUEST_URL);
            postRun(mResponse);
        } catch (final IOException e) {
            mResponse.setErrorInfo(e, CODE_CONNECT);
            postRun(mResponse);
        } finally {
            if (httpUrlCon != null) {
                httpUrlCon.disconnect(); // 断开连接
            }
        }
    }

    /**
     * @param response 请求结果
     */
    private void postRun(final Response response) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mIntercept) {
                    mTaskCallback.onFailure(response.getException(), ErrorCode.CODE_CANCLE);
                } else {
                    if (response.toBytes() == null) {
                        mTaskCallback.onFailure(response.getException(), response.getErrorCode());
                    } else {
                        mTaskCallback.onSuccess(response);
                    }
                }

            }
        });
    }
}
