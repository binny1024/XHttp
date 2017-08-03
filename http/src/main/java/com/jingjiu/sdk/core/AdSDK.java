package com.jingjiu.sdk.core;

import android.content.Context;

import com.jingjiu.sdk.util.cache.DiskLruCacheHelper;

/**

 */

public final class AdSDK {

    /**
     * 获取Context
     */
    public static Context getContext() {
        return InitSDK.mContext;
    }

    /**
     * 该对象同来获取缓存广告的图片
     * 如果，在SDK 外部调用，而没有初始化SDK，则会捕获空指针异常
     */
    public static DiskLruCacheHelper getAdBitmapCacheHelper() {
        return InitSDK.mHelperAdImgCache;
    }

    /**
     * 根据不同 key 来获取广告缓存的信息
     * 1、可以用来获取 缓存的广告列表
     * 2、可以用来获取 缓存的默认广告id
     * 3、可以用来获取 缓存的单个广告实体信息 bean
     */
    public static DiskLruCacheHelper getAdInfoCacheHelper() {
        return InitSDK.mHelperAdListAndAdInfoCache;
    }
}
