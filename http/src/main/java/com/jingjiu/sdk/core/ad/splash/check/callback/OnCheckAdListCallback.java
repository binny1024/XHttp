package com.jingjiu.sdk.core.ad.splash.check.callback;

import com.jingjiu.sdk.core.ad.bean.AdListBean;

import java.util.Map;

/**
 * function 广告检查结果回调接口
 */


public interface OnCheckAdListCallback extends OnDoNotLoadAdCallback {
    /**
     * @param listBeanMap 缓存广告信息的 map
     * @param showAdId    要展示的广告 id
     */
    void onLocalListExist(Map<String, AdListBean.DataBean.ListBean> listBeanMap, String showAdId);

}
