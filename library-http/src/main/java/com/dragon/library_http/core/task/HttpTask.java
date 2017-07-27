package com.dragon.library_http.core.task;

import android.os.Handler;

import com.dragon.library_http.JJLogger;
import com.dragon.library_http.callback.OnTaskCallback;
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
import java.util.Map;

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

public class HttpTask implements Runnable, IHttpTask {

    /**
     * 取消标志,默认不取消
     */
    boolean mIntercept = false;
    /**
     * 请求结果
     */
    private Response mResponse;

    /**
     * 主线程消息推送
     */
    private static final Handler HANDLER = new Handler();
    /**
     * 请求url
     */
    private String mUrl;
    /**
     * 请求参数
     */
    private String mParams;
    /**
     * 字符集
     */
    private String mCharset;
    /**
     * 接受请求头
     */
    private Map<String, String> mHeads;
    private OnTaskCallback mTaskCallback;

    public HttpTask() {
        mResponse = new Response();
    }
    void onPostExecute(byte[] bytes) {
        if (bytes == null) {
            mTaskCallback.onFailure(mResponse.getException(), mResponse.getErrorCode());
        } else {
            mResponse.setBytes(bytes);
            mTaskCallback.onSuccess(mResponse);
        }
    }

    @Override
    public void run() {
        if (httpType == METHOD_GET) {
            mUrl = mUrl + "?" + mParams;
        }
        HttpURLConnection httpUrlCon = null;
        InputStream inputStream = null;

        if (mIntercept) {
            mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
            mIntercept = false;
            HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    onPostExecute(null);
                }
            });
        }
        try {
            URL httpUrl = new URL(mUrl);
            httpUrlCon = (HttpURLConnection) httpUrl.openConnection();
            httpUrlCon.setConnectTimeout(mHttpTimeout);// 建立连接超时时间
            httpUrlCon.setReadTimeout(mHttpTimeout);//数据传输超时时间，很重要，必须设置。
            JJLogger.logInfo("param", "" + mUrl);
            if (mIntercept) {
                mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                mIntercept = false;
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(null);
                    }
                });
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
                        HANDLER.post(new Runnable() {
                            @Override
                            public void run() {
                                onPostExecute(null);
                            }
                        });
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
                    mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                    mIntercept = false;
                    HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            onPostExecute(null);
                        }
                    });
                }
                final byte[] bytes = toByteArray(inputStream);
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(bytes);
                    }
                });
            } else {
                mResponse.setErrorInfo(new AdException("服务器错误 "), CODE_CONNECT);
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(null);
                    }
                });
            }
        } catch (UnknownHostException e) {
            mResponse.setErrorInfo(e, CODE_CONNECT_UNKNOWN_HOST);
            HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    onPostExecute(null);
                }
            });
        } catch (SocketTimeoutException e) {
            mResponse.setErrorInfo(e, CODE_TIME_OUT);
            HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    onPostExecute(null);
                }
            });
        } catch (MalformedURLException e) {
            mResponse.setErrorInfo(e, CODE_REQUEST_URL);
            HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    onPostExecute(null);
                }
            });
        } catch (final IOException e) {
            mResponse.setErrorInfo(e, CODE_CONNECT);
            HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    onPostExecute(null);
                }
            });
        } finally {
            if (httpUrlCon != null) {
                httpUrlCon.disconnect(); // 断开连接
            }
        }
    }

    @Override
    public void setUrl(String url) {
        mUrl = url;
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
}
