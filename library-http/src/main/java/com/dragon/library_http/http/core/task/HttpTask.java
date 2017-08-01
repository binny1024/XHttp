package com.dragon.library_http.http.core.task;

import android.text.TextUtils;

import com.dragon.library_http.ErrorCode;
import com.dragon.library_http.exception.AdException;
import com.dragon.library_http.http.callback.OnTaskCallback;
import com.dragon.library_http.http.response.Response;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

import static com.dragon.library_http.ErrorCode.CODE_CANCLE;
import static com.dragon.library_http.ErrorCode.CODE_CONNECT;
import static com.dragon.library_http.ErrorCode.CODE_CONNECT_UNKNOWN_HOST;
import static com.dragon.library_http.ErrorCode.CODE_REQUEST_URL;
import static com.dragon.library_http.ErrorCode.CODE_TIME_OUT;
import static com.dragon.library_http.Util.toByteArray;
import static com.dragon.library_http.http.core.ConfigHttp.HANDLER;
import static com.dragon.library_http.http.core.ConfigHttp.METHOD_GET;
import static com.dragon.library_http.http.core.ConfigHttp.METHOD__POST;
import static com.dragon.library_http.http.core.ConfigHttp.httpType;
import static com.dragon.library_http.http.core.ConfigHttp.mHttpTimeout;


public class HttpTask implements Runnable, IHttpTask {

    private static final String TAG = "HttpTask";
    /**
     * 标志
     */
    private String mTag;


    /**
     * 优先级
     */
    private int mPriority;
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
     * 请求参数
     */
    private String mParams;

    /**
     * 默认字符集
     */
    private String mCharset = "utf-8";
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

    private void postRun(final Response response) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (response.toBytes() == null) {
                    if (mIntercept) {
                        mTaskCallback.onFailure(response.getException(), CODE_CANCLE);
                    } else {
                        mTaskCallback.onFailure(response.getException(), response.getErrorCode());
                    }
                } else {
                    mTaskCallback.onSuccess(response);
                }
            }
        });

    }

    @Override
    public void run() {
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }
        if (httpType == METHOD_GET) {
            mUrl = mUrl + "?" + mParams;
        }
        HttpURLConnection httpUrlCon = null;
        InputStream inputStream;

        if (mIntercept) {
            mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
            mIntercept = false;
            postRun(mResponse);
        }
        try {
            URL httpUrl = new URL(mUrl);
            httpUrlCon = (HttpURLConnection) httpUrl.openConnection();
            httpUrlCon.setConnectTimeout(mHttpTimeout);// 建立连接超时时间
            httpUrlCon.setReadTimeout(mHttpTimeout);//数据传输超时时间，很重要，必须设置。
            if (mIntercept) {
                mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                mIntercept = false;
                postRun(mResponse);
            }
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
                        postData = mParams.getBytes(mCharset);
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
                    mResponse.setErrorInfo(new AdException("用户取消"), CODE_CANCLE);
                    postRun(mResponse);
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
        }
        catch (MalformedURLException e) {
            mResponse.setErrorInfo(e, CODE_REQUEST_URL);
            postRun(mResponse);
        }
        catch (final IOException e) {
            mResponse.setErrorInfo(e, CODE_CONNECT);
            postRun(mResponse);
        } finally {
            if (httpUrlCon != null) {
                httpUrlCon.disconnect(); // 断开连接
            }
        }
    }

    @Override
    public void setPriority(int priority) {
        mPriority = priority;
    }

    @Override
    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public void setTag(String tag) {
        mTag = tag;
    }

    @Override
    public void setCharset(String charset) {
        mCharset = charset;
    }

    @Override
    public void setHeads(Map<String, String> heads) {
        mHeads = heads;
    }

    @Override
    public void addParams(String params) {
        mParams = params;
    }

    @Override
    public void setOnTaskCallback(OnTaskCallback taskCallback) {
        mTaskCallback = taskCallback;
    }

    @Override
    public void cancle() {
        mIntercept = true;
    }

    public String getTag() {
        return mTag;
    }

    public int getPriority() {
        return mPriority;
    }

}
