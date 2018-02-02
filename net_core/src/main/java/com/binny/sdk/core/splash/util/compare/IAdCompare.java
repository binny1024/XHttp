package com.binny.sdk.core.splash.util.compare;

import com.binny.sdk.core.splash.bean.AdListBean;

import java.util.Map;

/**
 * function
 */

public interface IAdCompare {

    /**
     * @param totalAdListBeanOld
     * @param adListBeanNew
     */
    void checkAdListForUpdate(Map<String, AdListBean.DataBean.ListBean> totalAdListBeanOld, AdListBean adListBeanNew);
}
