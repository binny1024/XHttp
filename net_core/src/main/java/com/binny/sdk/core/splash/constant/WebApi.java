package com.binny.sdk.core.splash.constant;

/**
 * function webApi
 */

public final class WebApi {

    // FIXME: 2017/8/11 host should not use '/' partten.
    public static final String HOST = "http://sdk.yoyoad.yoyokx.com/api/";

    public static final String TOTAL_AD_LIST = HOST + "getAdResource";//获取广告资源列表接口

    public static final String SHOW_AD_ID = HOST + "getOnceAd/";//获取要展示哪个广告的url

    public static final int AD_LIST_TIME_OUT = 15000;
    public static final int AD_ID_TIME_OUT = 5000;
}
