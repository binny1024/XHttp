package com.jingjiu.sdk.core.ad.common;

/**
 * function sdk 错误码
 */

public final class ErrorCode {

    /**
     * 本地缓存相关错误码 :以 "1" 开头，奇数结尾的为回调错误码：即回调给用户（10001），偶数结尾的为内部错误码（10000）
     */

    public static final String CODE_NO_AD_ID_LOCAL = "10001";//广告ID信息不在本地（1）
    public static final String CODE_NO_AD_ID_IMAGE_LOCAL = "10003";//广告图片不在本地(1)
    public static final String CODE_NO_AD_LIST_LOCAL = "10005";//广告列表不在本地（1）
    public static final String CODE_BITMAP_OUT_OF_MEMORY = "10007";//图片导致内存泄漏（小概率事件）
    public static final String CODE_LOAD_LOCAL_BITMAP_ERROR = "10009";//本地图片加载失败（小概率事件）
    /**
     * 网络数据相关错误码：以 "2" 开头；奇数结尾的为回调错误码：即回调给用户（20001），偶数结尾的为内部错误码（20002）
     */
    public static final String CODE_PARSE_AD_ID_ERROR = "20001";//解析广告id失败,id格式不正确(1)
    public static final String CODE_AD_ID_NA = "20003";//没有需要显示的广告(1)
    public static final String CODE_NO_NETWORK = "20005";//没有网络连接(1)
    public static final String CODE_REQUEST_FAILURE_AD_ID = "20007";//请求开屏广告id失败(1)
    public static final String CODE_AD_ID_TIMEOUT = "20009";//请求广告Id超时(1)
    public static final String CODE_ERROR_SERVER = "20011";//服务器错误(1)
    public static final String CODE_CONNECT = "connect";//服务器错误
    public static final String CODE_CONNECT_UNKNOWN_HOST = "20013";//不识别的主机(1)

    /**
     * 以下错误只会显示在logcat中
     */
    // 网络相关
    public static final String CODE_TIME_OUT = "time_out";//基层框架使用
    public static final String CODE_PARSE_AD_LIST = "20002";//解析广告列表失败(1)
    public static final String CODE_REQUEST_IMAGE = "20004";//请求网络图片失败(1)
    public static final String CODE_AD_LIST_TIME_OUT = "20006";//请求列表数据超时(1)
    public static final String CODE_REQUEST_CPM = "20008"; //CPM统计请求失败（1）
    public static final String CODE_REQUEST_CPC = "20010";//CPC统计请求失败（1）
    public static final String CODE_REQUEST_URL = "20012";//url不正确（1）
    public static final String CODE_REQUEST_FAILURE_ADLIST_ERROR = "20014";//请求广告列表数据失败（1）
    public static final String CODE_NO_AD_LIST_NET = "20016";//第一次从网络拉数据，拉的列表为空
    public static final String CODE_CANCLE = "20022";//用户取消操作（1）
    public static final String CODE_REQUEST_IMAGE_URL = "20024";//img url 格式错误(1)

    public static final String CODE_INTEGRATION_ERROR = "30001";//用户接入 SDK 方式不对(1)
}
