package com.bean.http.core.http.core.task;

import android.text.TextUtils;
import android.util.Log;

import com.bean.http.core.http.callback.OnTaskCallback;
import com.bean.http.core.http.response.Response;
import com.bean.http.core.logger.JJLogger;
import com.bean.http.exception.SDKException;

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

import static com.bean.http.common.CommonMethod.toByteArray;
import static com.bean.http.common.Configuration.HANDLER;
import static com.bean.http.common.ErrorCode.CODE_CANCLE;
import static com.bean.http.common.ErrorCode.CODE_CONNECT;
import static com.bean.http.common.ErrorCode.CODE_CONNECT_UNKNOWN_HOST;
import static com.bean.http.common.ErrorCode.CODE_REQUEST_URL;
import static com.bean.http.common.ErrorCode.CODE_TIME_OUT;

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
     * 处理上传文件的数据
     */
    private StringBuilder mPostDataBuilder = new StringBuilder();
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

    // 产生随机分隔内容
    private final String mBoundary = java.util.UUID.randomUUID().toString();
    private final String mPrefix = "--";//文件分隔符开始
    private final String mChangeNewLine = "\r\n";//空行
    private final String mSplitLine = mPrefix + mBoundary + mChangeNewLine;
    private final String mMultipartFromData = "multipart/form-data";

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
        this.setHeads("Charsert", mCharset);
        this.setHeads("Accept-Encoding", "gzip,deflate");
        this.setHeads("Content-Type", mMultipartFromData
                + ";boundary=" + mBoundary);
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
        StringBuilder paramBuilder = new StringBuilder();
        if (paramsMap != null) {
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                paramBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            if (paramBuilder.length() > 0) {
                paramBuilder.deleteCharAt(paramBuilder.length() - 1);
            }
            mParams = paramBuilder.toString();
            if (mHttpType == METHOD_GET) {
                mUrl = mUrl + "?" + mParams;
            }
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
        JJLogger.logInfo(TAG, "HttpTask.setHeads :" + mHeads.size());
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
        HttpURLConnection httpUrlCon = null;
        InputStream inputStream;

        if (mIntercept) {
            mResponse.setErrorInfo(new SDKException("用户取消操作"), CODE_CANCLE);
            mIntercept = false;
            postRun(mResponse, CODE_CANCLE,"");
            return;
        }
        int responseCode = 0;
        String responseCodeStr ="Before";
        String redirection = "";//重定向链接
        try {
            JJLogger.logInfo(TAG, "HttpTask.run :" + mUrl);
            if (TextUtils.isEmpty(mUrl)) {
                mResponse.setErrorInfo(new SDKException("url 为空！"), CODE_REQUEST_URL);
                postRun(mResponse, CODE_REQUEST_URL, "");
                return;
            }
            URL httpUrl = new URL(mUrl);
            httpUrlCon = (HttpURLConnection) httpUrl.openConnection();

            httpUrlCon.setConnectTimeout(mHttpTimeout);// 建立连接超时时间
            httpUrlCon.setReadTimeout(mHttpTimeout);//数据传输超时时间，很重要，必须设置。
            if (mIntercept) {
                mResponse.setErrorInfo(new SDKException("用户取消操作"), CODE_CANCLE);
                mIntercept = false;
                postRun(mResponse, responseCodeStr, responseCodeStr);
                return;
            }
            //设置请求头
            if (mHeads != null) {
                JJLogger.logInfo(TAG, "HttpTask.run :" + mHeads.size());
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
                    byte[] postParam = null;//请求参数
                    try {
                        if (!TextUtils.isEmpty(mParams)) {
                            postParam = mParams.getBytes(mCharset);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    httpUrlCon.setDoInput(true); // 向连接中写入数据
                    httpUrlCon.setDoOutput(true); // 从连接中读取数据
                    httpUrlCon.setUseCaches(false); // 禁止缓存
                    DataOutputStream outputStream;
                    if (mIntercept) {
                        mResponse.setErrorInfo(new SDKException("用户取消操作"), CODE_CANCLE);
                        mIntercept = false;
                        postRun(mResponse, responseCodeStr, "");
                        return;
                    } else {
                        outputStream = new DataOutputStream(httpUrlCon.getOutputStream()); // 获取输出流
                        if (postParam != null) {
                            //一般 post 请求
                            outputStream.write(postParam);// 将要传递的参数写入数据输出流
                        } else if (upload_file) {
                            /*
                            * 文件上传
                            * */
                            upload_file = false;
//                            mPostDataBuilder.append(mChangeNewLine);
                            for (int i = 0; i < mUploadFilePaths.length; i++) {
                                String uploadFile = mUploadFilePaths[i];
                                String filename = uploadFile.substring(uploadFile.lastIndexOf("/") + 1);
                                JJLogger.logInfo(TAG, "HttpTask.run :" + filename);
                                //-------------------------------------子域--------------
                                mPostDataBuilder.append(mSplitLine);//加入分割线
                                mPostDataBuilder.append("Content-Disposition: form-data; " + "name=\"file")
                                        .append(i).append("\";filename=\"").append(filename).append("\"").append(mChangeNewLine)
                                        .append(mChangeNewLine);//回车换行
                                // 写入输出流中
                                outputStream.write(mPostDataBuilder.toString().getBytes());
                                mPostDataBuilder.append(mChangeNewLine);//加入空行，下面就是数据
                                //文件流处理
                                FileInputStream fileInputStream = new FileInputStream(uploadFile);
                                byte[] buffer = new byte[1024];
                                int length;
                                while ((length = fileInputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, length);
                                }
                                outputStream.write(mChangeNewLine.getBytes());//加入换行符（必须）
                                fileInputStream.close();
                            }
                            // 请求结束标志
                            byte[] end_data = (mPrefix + mBoundary + mPrefix + mChangeNewLine).getBytes();//文件数据结尾
                            outputStream.write(end_data);
                        }
                        outputStream.flush(); // 输出缓存
                        outputStream.close(); // 关闭数据输出流
                    }
                    break;
                default:
                    break;
            }
            redirection =httpUrlCon.getHeaderField("location");
            responseCode = httpUrlCon.getResponseCode();
            responseCodeStr = String.valueOf(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpUrlCon.getInputStream();
                final byte[] bytes = toByteArray(inputStream);
                mResponse.setBytes(bytes);
                postRun(mResponse, String.valueOf(responseCode), "");
            } else {
                mResponse.setErrorInfo(new SDKException("找不到服务器 "), CODE_CONNECT);
                postRun(mResponse, String.valueOf(responseCode), redirection);
            }
        } catch (UnknownHostException e) {
            mResponse.setErrorInfo(e, CODE_CONNECT_UNKNOWN_HOST);
            postRun(mResponse, responseCodeStr, "");
        } catch (SocketTimeoutException e) {
            mResponse.setErrorInfo(e, CODE_TIME_OUT);
            postRun(mResponse, responseCodeStr, "");
        } catch (MalformedURLException e) {
            mResponse.setErrorInfo(e, CODE_REQUEST_URL);
            postRun(mResponse, responseCodeStr, "");
        } catch (final IOException e) {
            mResponse.setErrorInfo(e, CODE_CONNECT);
            postRun(mResponse, responseCodeStr, "");
        } finally {
            if (httpUrlCon != null) {
                httpUrlCon.disconnect(); // 断开连接
            }
        }
    }

    /**
     * @param response 请求结果
     * @param responseCode
     * @param redirectUrl
     */
    private void postRun(final Response response, final String responseCode, final String redirectUrl) {
        if (responseCode.equals( "302")) {
            new Thread(this).start();
            mUrl = redirectUrl;
            Log.i(TAG, "重定向地址: "+redirectUrl);
            return;
        }
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mIntercept) {
                    mTaskCallback.onFailure(response.getException(), CODE_CANCLE);
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
