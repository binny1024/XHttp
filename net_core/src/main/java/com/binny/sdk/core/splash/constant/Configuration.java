package com.binny.sdk.core.splash.constant;

/**
 * function sdk 所需的配置参数
 */

public final class Configuration {

    /**
     * 解析数据出错时的；默认值:""
     */
    public static final String DEFAULT_STRING_VALUE = "";

    /**
     * 广告的位置
     */
    public static final String POS_SPLASH = "1";

    /**
     * 广告图片的缓存文件夹
     */
    public static final String AD_BITMAP_CACHE = "ad_splash_img";
    /**
     * 广告信息的缓存文件夹；
     * 广告列表和广告实体信息
     */
    public static final String AD_LIST_CACHE = "ad_list";

    /**
     * 取消list请求的tag
     */
    public static final String CANCEL_LIST_TAG = "请求广告列表";
    /**
     * 取消请求展示广告id 的tag
     */
    public static final String CANCEL_AD_ID_TAG = "请求广告id";
    /**
     * 取消请求展示广告id 的tag
     */
    public static final String CANCEL_CPM_TAG = "请求  cpm";
    /**
     * 取消请求展示广告id 的tag
     */
    public static final String CANCEL_CPC_TAG = "请求  cpc";

}
