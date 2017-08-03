package com.jingjiu.sdk.core.ad.http;

import com.jingjiu.sdk.core.ad.bean.AdListBean;
import com.jingjiu.sdk.core.ad.common.ErrorCode;
import com.jingjiu.sdk.core.ad.compare.AdCompare;
import com.jingjiu.sdk.core.ad.http.callbak.OnHttpAdListCallback;
import com.jingjiu.sdk.util.logger.JJLogger;

import java.util.Map;

public class HttpAdListResult implements OnHttpAdListCallback {

    private Map<String, AdListBean.DataBean.ListBean> mMapLocalAdBean;

    public HttpAdListResult(Map<String, AdListBean.DataBean.ListBean> mapLocalAdBean) {
        mMapLocalAdBean = mapLocalAdBean;
        String TAG = this.getClass().getSimpleName();
        JJLogger.logInfo(TAG, "HttpAdListResult.HttpAdListResult :");
    }

    /**
     * 请求列表成功
     * 向sdk服务器请求需要缓存的数据列表，成功后回调
     * 请求成功，检测本地有无数据缓存
     * 有：检测更新
     * 无：保存清单数据，并缓存图片
     *
     * @param newAdListBean 新的广告列表
     */
    @Override
    public void onHttpAdListSuccess(final AdListBean newAdListBean) {
        /*
        * 1、检测本地有无更新数据
        * */
        new AdCompare().checkAdListForUpdate(mMapLocalAdBean, newAdListBean);

    }

    /**
     * 请求列表失败，则End
     *
     * @param e 异常信息
     * @param errorCode 错误码
     */
    @Override
    public void onHttpAdListFailure(Exception e, String errorCode) {

        switch (errorCode) {
            case ErrorCode.CODE_TIME_OUT://超时打印
                JJLogger.logError(ErrorCode.CODE_AD_LIST_TIME_OUT, "列表请求超时 ");
                break;
            case ErrorCode.CODE_ERROR_SERVER://服务器出错失败打印
                JJLogger.logError(ErrorCode.CODE_ERROR_SERVER, "列表服务器出错 ");
                break;
            case ErrorCode.CODE_CONNECT://服务器出错失败打印
                JJLogger.logError(ErrorCode.CODE_REQUEST_FAILURE_ADLIST_ERROR, "请求列表失败 ");
                break;
            default://请求失败其他回调
                JJLogger.logError(errorCode, "请查看错误码信息表 ");
                break;
        }
    }
}
