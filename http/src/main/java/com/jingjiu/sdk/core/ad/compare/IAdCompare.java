package com.jingjiu.sdk.core.ad.compare;

import com.jingjiu.sdk.core.ad.bean.AdListBean;

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
