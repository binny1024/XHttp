package com.binny.sdk.core.splash.http;

import com.binny.sdk.core.splash.util.compare.callback.OnDownloadCallback;
import com.binny.sdk.core.splash.http.callbak.OnHttpAdListCallback;
import com.binny.sdk.core.splash.http.callbak.OnHttpAdShowIdCallback;

import java.util.Map;

/**
 * function  下载图片并缓存
 */

public interface IJJHttp {

    /**
     * 获取广告资源列表
     */
    void requestAdList(final OnHttpAdListCallback httpAdListCallback);

    /**
     * 获取要展示的广告id
     *
     * @param pos 要展示的位置
     */

    void requestShowAdID(final String pos, final OnHttpAdShowIdCallback httpJsonAdIdCallback);

    /**
     * @param downloadCallback
     * @param map              以广告id为key,一图片为value
     */
    void download(final OnDownloadCallback downloadCallback, final Map<String, String> map);

    void sendCPC(final String adId, final String targetUrl, final String adPostion, final String adAppId);

    void sendCPM(final String adId, final String targetUrl, final String adPostion, final String adAppId);
}
