package com.jingjiu.http.core.http.core.task;

import android.text.TextUtils;

import com.jingjiu.http.common.ErrorCode;
import com.jingjiu.http.core.http.callback.OnTaskCallback;
import com.jingjiu.http.core.http.response.Response;
import com.jingjiu.http.core.logger.JJLogger;
import com.jingjiu.http.exception.AdException;

import java.io.DataOutputStream;
import java.io.FileInputStream;
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
import java.util.UUID;

import static com.jingjiu.http.common.CommonMethod.toByteArray;
import static com.jingjiu.http.common.Configuration.HANDLER;
import static com.jingjiu.http.common.ErrorCode.CODE_CANCLE;
import static com.jingjiu.http.common.ErrorCode.CODE_CONNECT;
import static com.jingjiu.http.common.ErrorCode.CODE_CONNECT_UNKNOWN_HOST;
import static com.jingjiu.http.common.ErrorCode.CODE_REQUEST_URL;
import static com.jingjiu.http.common.ErrorCode.CODE_TIME_OUT;

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
    private String mParamsPost;

    /**
     * 处理请求参数或请求体
     */
    private StringBuilder mParamsBuilder = new StringBuilder();
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
    private String mRandomNum = UUID.randomUUID().toString();//随机数
    private String mNewLine = "\r\n";
    private String mSplitLine = "-----------------------------" + mRandomNum + mNewLine;//数据分隔符
    private String mEndLine = "-----------------------------" + mRandomNum + "--";//结束符
    private String[] mUploadFilePaths;
    private boolean upload_file;

    public HttpTask() {
        mResponse = new Response();
    }

    @Override
    public IHttpTask setCharset(String charset) {
        mCharset = charset;
        return this;
    }

    @Override
    public IHttpTask uploadFiles(final String[] uploadFilePaths) {
        this.mUploadFilePaths = uploadFilePaths;
        upload_file = true;
        uploadFileHeads();
        return this;
    }

    @Override
    public IHttpTask uploadFile(final String uploadFilePath) {
        upload_file = true;
        mUploadFilePaths = new String[]{uploadFilePath};
        uploadFileHeads();
        return this;
    }

    private void uploadFileHeads() {
        this.setHeads("Accept", "*/*");
        this.setHeads("Connection", "Keep-Alive");
        this.setHeads("User-agent", "Android_xander");
        this.setHeads("Charsert", "UTF-8");
        this.setHeads("Accept-Encoding", "gzip,deflate");
        this.setHeads("Content-Type", "multipart/form-data;boundary=" + mSplitLine);
        JJLogger.logInfo(TAG, "HttpTask.uploadFileHeads :");
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

    private void handleParams(final Map<String, String> paramsMap) {
        /*
        * 处理请求参数
        * */
        if (paramsMap != null) {
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                mParamsBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }

            if (mParamsBuilder.length() > 0) {
                mParamsBuilder.deleteCharAt(mParamsBuilder.length() - 1);
            }
            mUrl = mUrl + "?" + mParamsBuilder.toString();
            JJLogger.logInfo(TAG, "HttpTask.handleParams :" + mUrl);
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
        mParamsPost = null;
        handleParams(mParamsMap);

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
                mResponse.setErrorInfo(new AdException("url 为空！"), ErrorCode.CODE_REQUEST_URL);
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
                    JJLogger.logInfo(TAG, entry.getKey() + ":" + entry.getValue());
                }
            }
            switch (mHttpType) {
                case METHOD_GET:
                    httpUrlCon.setRequestMethod("GET");// 设置请求类型为
                    break;
                case METHOD__POST:
                    // 1、重新对请求报文进行  编码
                    httpUrlCon.setRequestMethod("POST");// 设置请求类型为
                    byte[] postParam = null;//请求参数
                    try {
                        if (!TextUtils.isEmpty(mParamsPost)) {
                            postParam = mParamsPost.getBytes(mCharset);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    httpUrlCon.setDoInput(true); // 向连接中写入数据
                    httpUrlCon.setDoOutput(true); // 从连接中读取数据
                    httpUrlCon.setUseCaches(false); // 禁止缓存
                    DataOutputStream out;
                    if (mIntercept) {
                        mResponse.setErrorInfo(new AdException("用户取消操作"), CODE_CANCLE);
                        mIntercept = false;
                        postRun(mResponse);
                        return;
                    } else {
                        out = new DataOutputStream(httpUrlCon.getOutputStream()); // 获取输出流
                        if (postParam != null) {
                            //一般的post请求
                            out.write(postParam);// 将要传递的参数写入数据输出流
                            out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
                        }
                        if (upload_file) {
                            /*
                            * 文件上传
                            * */
                            upload_file = false;
                            for (int i = 0; i < mUploadFilePaths.length; i++) {
                                /*
                                * 文件名
                                * */
                                String filename = mUploadFilePaths[i].substring(mUploadFilePaths[i].lastIndexOf("//") + 1);
                                JJLogger.logInfo(TAG, "HttpTask.run :" + filename);
                                //-------------------------------------子域--------------
                                mParamsBuilder.append(mSplitLine);
                                mParamsBuilder.append("Content-Disposition: form-data; " + "name=\"file").append(i).append("\";filename=\"").append(filename).append("\"");
                                mParamsBuilder.append(mNewLine).append(mNewLine);
                                out.writeBytes(mParamsBuilder.toString());
                                //文件流处理
                                FileInputStream fileInputStream = new FileInputStream(mUploadFilePaths[i]);
                                int bufferSize = 1024;
                                byte[] buffer = new byte[bufferSize];
                                int length = -1;
                                while ((length = fileInputStream.read(buffer)) != -1) {
                                    out.write(buffer, 0, length);
                                }
                                out.writeBytes(new String(buffer));
                                fileInputStream.close();
                                //-------------------------------------子域--------------
                            }
                            out.writeBytes(mEndLine);
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
            JJLogger.logInfo(TAG, "HttpTask.run :设置模式");
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
