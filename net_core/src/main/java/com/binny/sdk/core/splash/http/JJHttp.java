package com.binny.sdk.core.splash.http;

import android.text.TextUtils;

import com.binny.core.logger.JJLogger;
import com.binny.sdk.cache.UtilCache;
import com.binny.sdk.common.ErrorCode;
import com.binny.sdk.common.Verification;
import com.binny.sdk.core.splash.constant.Configuration;
import com.binny.sdk.core.splash.constant.WebApi;
import com.binny.sdk.core.splash.http.callbak.OnHttpAdListCallback;
import com.binny.sdk.core.splash.http.callbak.OnHttpAdShowIdCallback;
import com.binny.sdk.core.splash.util.ParserSplashJson;
import com.binny.sdk.core.splash.util.compare.callback.OnDownloadCallback;
import com.binny.core.xhttp.XHttp;
import com.binny.core.xhttp.callback.OnXHttpCallback;
import com.binny.core.xhttp.response.Response;

import java.util.Map;
import java.util.regex.Pattern;

import static com.binny.sdk.common.CommonMethod.getAdAppId;
import static com.binny.sdk.common.CommonMethod.getUniquePsuedoID;

/**
 * function 用来缓存广告图片
 */

public class JJHttp implements IJJHttp {
    private final String TAG = this.getClass().getSimpleName();
    private static Pattern pattern;

    public JJHttp() {
        pattern = Pattern.compile(Verification.REGEX_URL);
    }

    /**
     * 请求广告资源列表；发起该请求的情况有4种
     * 1、无本地列表缓存，加载list;
     * 2、有本地列表缓存，请求ID：请求失败，加载list;
     * 3、有本地列表缓存，请求ID：请求成功，有ID信息缓存并且成功显示，加载list;
     * 4、有本地列表缓存，请求ID：请求成功，无ID信息缓存，加载list;
     */
    @Override
    public void requestAdList(final OnHttpAdListCallback httpAdListCallback) {
        XHttp.getInstance().get(WebApi.TOTAL_AD_LIST)
                .setTag(Configuration.CANCEL_LIST_TAG)
                .setParams("device_id", getUniquePsuedoID())
                .setParams("ad_app_id", getAdAppId())
                .setTimeout(WebApi.AD_LIST_TIME_OUT)
                .setOnXHttpCallback(new OnXHttpCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        //请求SDK服务器加载需要缓存的数据列表成功，回调给Check
                        ParserSplashJson.parseAdList(httpAdListCallback, response.toString());
                    }

                    @Override
                    public void onFailure(Exception ex, String errorCode) {
                        JJLogger.logInfo(TAG, "JJHttp.onFailure :");
                        if (!TextUtils.isEmpty(errorCode)) {
                            httpAdListCallback.onHttpAdListFailure(ex, errorCode);
                            return;
                        }
                        switch (errorCode) {
                            case ErrorCode.CODE_TIME_OUT://超时打印
                                JJLogger.logError(ErrorCode.CODE_AD_LIST_TIME_OUT, "请求列表超时 ");
                                break;
                            case ErrorCode.CODE_REQUEST_URL://请求列表失败打印
                                JJLogger.logError(ErrorCode.CODE_REQUEST_URL, "请求列表的 url出错 requestAdList");
                                break;
                            default://请求失败其他回调
                                httpAdListCallback.onHttpAdListFailure(ex, errorCode);
                                break;
                        }
                    }
                });
    }

    @Override
    public void requestShowAdID(String pos, final OnHttpAdShowIdCallback httpJsonAdIdCallback) {

        XHttp.getInstance().get(WebApi.SHOW_AD_ID)
                .setTag(Configuration.CANCEL_AD_ID_TAG)
                .setParams("device_id", getUniquePsuedoID())
                .setParams("ad_app_id", getAdAppId())
                .setParams("ad_pos_id", pos)
                .setTimeout(WebApi.AD_ID_TIME_OUT)
                .setOnXHttpCallback(new OnXHttpCallback() {

                    @Override
                    public void onSuccess(final Response response) {
                        ParserSplashJson.parseCurrentShowAdId(httpJsonAdIdCallback, response.toString());
                    }

                    @Override
                    public void onFailure(Exception ex, String errorCode) {
                        JJLogger.logInfo("onFailure", "JJHttp.onFailure :" + errorCode);
                        if (!TextUtils.isEmpty(errorCode)) {
                            switch (errorCode) {
                                case ErrorCode.CODE_TIME_OUT:
                                    httpJsonAdIdCallback.onHttpAdIdFailure(ex, ErrorCode.CODE_AD_ID_TIMEOUT);
                                    break;
                                case ErrorCode.CODE_CONNECT:
                                    httpJsonAdIdCallback.onHttpAdIdFailure(ex, ErrorCode.CODE_REQUEST_FAILURE_AD_ID);
                                    break;
                                case ErrorCode.CODE_REQUEST_URL:
                                    httpJsonAdIdCallback.onHttpAdIdFailure(ex, ErrorCode.CODE_ERROR_SERVER);
                                    break;
                                default://请求失败其他回调
                                    httpJsonAdIdCallback.onHttpAdIdFailure(ex, errorCode);
                                    break;
                            }
                        }

                    }
                });
    }

    /**
     * @param downloadCallback 下载结果的回调
     * @param map              以广告id为key,一图片 url 为value
     */
    @Override
    public void download(final OnDownloadCallback downloadCallback, final Map<String, String> map) {
        if (map == null) {
            return;
        }
        int len = map.size();
        if (len == 0) {
            return;
        }
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            if (pattern.matcher(entry.getValue()).matches()) {
                XHttp.getInstance().startConcurrenceThreadPool()//启用线程池
                        .get(entry.getValue())
                        .setTag(entry.getKey())
                        .setOnXHttpCallback(new OnXHttpCallback() {

                            @Override
                            public void onSuccess(final Response response) {
                                UtilCache.getAdBitmapCacheHelper().putInputStream(entry.getKey(), response.toInputStream());
                                downloadCallback.onSuccess(entry.getKey());
                            }

                            @Override
                            public void onFailure(Exception ex, String errorCode) {
                                //图片下载失败
                                if (!TextUtils.isEmpty(errorCode) && errorCode.equals(ErrorCode.CODE_CONNECT)) {
                                    downloadCallback.onFailure(entry.getKey() + " " + ex.getMessage(), ErrorCode.CODE_REQUEST_IMAGE);
                                } else {
                                    downloadCallback.onFailure(entry.getKey() + " " + ex.getMessage(), errorCode);
                                }
                            }
                        });

            } else {
                JJLogger.logInfo(TAG, "JJHttp.download :是不正确的网址");
                downloadCallback.onFailure(entry.getKey() + " url 格式不正确 ", ErrorCode.CODE_REQUEST_IMAGE_URL);
            }
        }
    }

    @Override
    public void sendCPC(String adId, final String targetUrl, String adPostion, String adAppId) {
        if (pattern.matcher(targetUrl).matches()) {
            XHttp.getInstance().get(targetUrl)
                    .setTag(Configuration.CANCEL_CPC_TAG)
                    .setOnXHttpCallback(new OnXHttpCallback() {

                        @Override
                        public void onSuccess(final Response response) {
                            JJLogger.logInfo("PPPPP", "JJHttp.onSuccess : CPC");
                        }

                        @Override
                        public void onFailure(Exception ex, String errorCode) {
                            JJLogger.logInfo("PPPPP", "JJHttp.onFailure : CPC ");
                            if (!TextUtils.isEmpty(errorCode)) {
                                switch (errorCode) {
                                    case ErrorCode.CODE_REQUEST_URL://请求列表失败打印
                                        JJLogger.logError(ErrorCode.CODE_REQUEST_URL, "CPC URL 错误 ：请求地址 ：" + targetUrl);
                                        break;
                                    default://其他回调
                                        JJLogger.logError(ErrorCode.CODE_REQUEST_CPC, "CPC 其他错误：请求地址 ：" + targetUrl);
                                        break;
                                }
                            }
                        }
                    });
            ;
        } else {
            JJLogger.logError(ErrorCode.CODE_REQUEST_URL, "CPC url 格式不正确  ：" + targetUrl);
        }
    }

    @Override
    public void sendCPM(String adId, final String targetUrl, String adPostion, String adAppId) {
        if (pattern.matcher(targetUrl).matches()) {
            XHttp.getInstance().get(targetUrl)
                    .setTag(Configuration.CANCEL_CPM_TAG)
                    .setOnXHttpCallback(new OnXHttpCallback() {

                        @Override
                        public void onSuccess(final Response response) {
                            JJLogger.logInfo("response", "JJHttp.onSuccess : CPM");
                        }

                        @Override
                        public void onFailure(Exception ex, String errorCode) {
                            JJLogger.logInfo("onFailure", "CPM ：发送失败");
                            if (!TextUtils.isEmpty(errorCode)) {
                                switch (errorCode) {
                                    case ErrorCode.CODE_REQUEST_URL://请求列表失败打印
                                        JJLogger.logError(ErrorCode.CODE_REQUEST_URL, "CPM 错误 请求地址 ：" + targetUrl);
                                        break;

                                    default://其他回调
                                        JJLogger.logError(ErrorCode.CODE_REQUEST_CPM, "CPM 其他错误请求地址 ：" + targetUrl);
                                        break;
                                }
                            }
                        }
                    });
            ;
        } else {
            JJLogger.logError(ErrorCode.CODE_REQUEST_URL, "CPM 格式不正确 ：" + targetUrl);
        }
    }
}
