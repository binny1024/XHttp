package com.jingjiu.sdk.core.ad.splash.check;

import android.os.Handler;
import android.text.TextUtils;

import com.jingjiu.sdk.core.ad.bean.AdListBean;
import com.jingjiu.sdk.core.ad.common.ErrorCode;
import com.jingjiu.sdk.core.ad.http.JJHttp;
import com.jingjiu.sdk.core.ad.http.callbak.OnHttpAdShowIdCallback;
import com.jingjiu.sdk.core.ad.splash.check.callback.OnCheckAdListCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jingjiu.sdk.core.ad.common.ErrorCode.CODE_AD_ID_NA;
import static com.jingjiu.sdk.util.CommonMethod.getAdInfoCache;

/**
 * function  用于开屏广告检查;
 */

public class CheckSplashAd implements ICheckLocalAdList {


    private OnCheckAdListCallback mCheckResultCallback;//回调manager中的实现

    private JJHttp mHttpAdId;//用于发起广告请求
    private Handler mHandler;
    private final String TAG = "CheckSplashAd";

    /**
     * 缓存本地广告数据 ： map 形式
     **/
    private Map<String, AdListBean.DataBean.ListBean> mMapLocalAdBean;

    public CheckSplashAd(OnCheckAdListCallback resultCallback) {
        mCheckResultCallback = resultCallback;
        mHttpAdId = new JJHttp();
        mHandler = new Handler();
    }


    /**
     * 检查广告列表 是否存在。需求需要：该函数运行在子线程
     * 检测列表
     *
     * @param pos 广告类型：开屏、banner
     */
    @Override
    public void checkLocalAdList(String pos) {
        AdListBean mLocalAdList = getAdInfoCache();
        if (mLocalAdList != null) {
            /**广告列表 存在
             * 请求服务器获取需要展示哪一张广告
             * */
            mMapLocalAdBean = new HashMap<>();

            List<AdListBean.DataBean.ListBean> listBeanLocal = mLocalAdList.getData().getList();
            for (AdListBean.DataBean.ListBean listBean : listBeanLocal) {
                mMapLocalAdBean.put(listBean.getAd_id(), listBean);
            }
            mHttpAdId.requestShowAdID(pos, new HttpAdIdResult());
        } else {

            /**没有广告列表缓存
             * 1、回调 onDoNotLoadAd()，此处切逻辑抛给回主线程;
             * 2、同时向服务请求清单;
             * */
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCheckResultCallback.onDoNotLoadAd(ErrorCode.CODE_NO_AD_LIST_LOCAL);
                }
            });
        }

    }


    /**
     * 请求要展示的广告id
     */
    private class HttpAdIdResult implements OnHttpAdShowIdCallback {

        /**
         * 请求展示广告一张广告，成功
         *
         * @param showValidAdId 毁掉这一步的条件是id有效
         */
        @Override
        public void onHttpAdIdSuccess(String showValidAdId) {
            if (!TextUtils.isEmpty(showValidAdId)) {
                mCheckResultCallback.onLocalListExist(mMapLocalAdBean, showValidAdId);
            } else {
                mCheckResultCallback.onDoNotLoadAd(CODE_AD_ID_NA);
            }
        }

        /**
         * 该函数做了两件事情
         * 1、异步处理：向sdk服务器请求需要缓存的数据列表
         * 2、回调使用者实现的广告不存在函数
         */
        @Override
        public void onHttpAdIdFailure(Exception e, String errorCode) {
            mCheckResultCallback.onDoNotLoadAd(errorCode);
//            mHttpAdList.requestAdList(new HttpAdListResult(mMapLocalAdBean));//  2、有本地列表缓存，请求ID：请求失败，加载list;
        }
    }

}
