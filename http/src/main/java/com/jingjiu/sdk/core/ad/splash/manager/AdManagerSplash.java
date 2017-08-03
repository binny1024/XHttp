package com.jingjiu.sdk.core.ad.splash.manager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.jingjiu.sdk.core.AdSDK;
import com.jingjiu.sdk.core.ad.IManager;
import com.jingjiu.sdk.core.ad.bean.AdListBean;
import com.jingjiu.sdk.core.ad.callback.OnAdManagerCallback;
import com.jingjiu.sdk.core.ad.callback.OnShowAdDetailCallback;
import com.jingjiu.sdk.core.ad.common.ErrorCode;
import com.jingjiu.sdk.core.ad.exception.AdException;
import com.jingjiu.sdk.core.ad.http.HttpAdListResult;
import com.jingjiu.sdk.core.ad.http.JJHttp;
import com.jingjiu.sdk.core.ad.splash.check.CheckSplashAd;
import com.jingjiu.sdk.core.ad.splash.check.callback.OnCheckAdListCallback;
import com.jingjiu.sdk.util.http.core.manager.TaskManager;
import com.jingjiu.sdk.util.logger.JJLogger;

import java.util.Map;

import static com.jingjiu.sdk.core.ad.common.Verification.bWorking;
import static com.jingjiu.sdk.util.CommonMethod.isActiveConnected;


/**
 * function 开屏广告 管理器；
 * 用于，发起广告检查：检查列表，检查缓存，处理广告请求，
 */

public class AdManagerSplash implements IManager {

    /**
     * 定义一个静态私有变量(不初始化，不使用final关键字，使用volatile保证了多线程访问时instance变量的可见性
     * ，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用)
     */
    private static volatile AdManagerSplash mInstance;
    /**
     * 检查开屏广告
     */
    private CheckSplashAd mCheckSplashAd;
    /**
     * 点击广告，查看详情的回调
     */
    private OnAdManagerCallback mManagerCheckCallback;
    /**
     * 展示的广告的实体信息，实体信息为一个bean对象
     */
    private AdListBean.DataBean.ListBean mCurrentAdBean;

    /**
     * 本地广告信息
     */
    private Map<String, AdListBean.DataBean.ListBean> mMapLocalAdBean;
    /**
     * 请求广告列表
     */
    private JJHttp mHttpJson;
    /**
     * 广告图片
     */
    private Bitmap mBitmap;


    @SuppressLint("StaticFieldLeak")
    private AdManagerSplash() {
        super();
        mHttpJson = new JJHttp();
    }

    //定义一个共有的静态方法，返回该类型实例
    public static AdManagerSplash getManagerSplash() throws AdException {
        if (!bWorking) {
            throw new AdException("未正确接入SDK!请先初始化 SDK ！", ErrorCode.CODE_INTEGRATION_ERROR);
        }
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (mInstance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (AdManagerSplash.class) {
                //未初始化，则初始instance变量
                if (mInstance == null) {
                    mInstance = new AdManagerSplash();
                }
            }
        }
        return (AdManagerSplash) mInstance;
    }


    /**
     * 检查本地是否有缓存列表
     *
     * @param adPos             广告的位置
     * @param adManagerCallback
     * @return 本类实例
     */
    public AdManagerSplash checkLocalAd(final String adPos, OnAdManagerCallback adManagerCallback) throws AdException {
        if (!bWorking) {
            JJLogger.logError(ErrorCode.CODE_INTEGRATION_ERROR, "未正确接入SDK!");
            return this;
        }
        if (adManagerCallback == null) {// 使用之前，初始化检查
            throw new AdException("请查看使用文档，以便正确接入SDK!!!", ErrorCode.CODE_INTEGRATION_ERROR);
        }
        mManagerCheckCallback = adManagerCallback;

        //参数检查
        if (TextUtils.isEmpty(adPos)) {
            throw new AdException("请检查 开屏 是否传入了正确的参数 ：POS_SPLASH");
        }
        // 初始化检查
        if (AdSDK.getContext() == null || AdSDK.getAdInfoCacheHelper() == null || AdSDK.getAdBitmapCacheHelper() == null) {
            throw new AdException("init(Context context)未调用!请仔细阅读使用文档，以便正确初始化SDK!!!", ErrorCode.CODE_INTEGRATION_ERROR);
        }

        //网络检查
        if (!isActiveConnected()) {
            throw new AdException("没有网络连接", ErrorCode.CODE_NO_NETWORK);
        }
        mCheckSplashAd = new CheckSplashAd(new CheckSplashLocalResult());
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCheckSplashAd.checkLocalAdList(adPos);//检测本地有无开屏的广告列表;
            }
        }).start();

        return this;
    }

    private static final String TAG = "AdManagerSplash";

    /**
     * @param imageView              sdk使用者 传递过来广告位
     * @param onShowAdDetailCallback 广告详情的回调
     * @return 本类实例
     */
    public AdManagerSplash loadAd(final ImageView imageView, final OnShowAdDetailCallback onShowAdDetailCallback) throws AdException {

        if (imageView == null) {
            /** mMapLocalAdBean 此时  不会为空 ：除非用户直接调用
             * 当广告位为空的时候，不加载广告；
             **/
            JJLogger.logInfo(TAG, "AdManagerSplash.loadAd :");
            mHttpJson.requestAdList(new HttpAdListResult(mMapLocalAdBean));//3、有本地列表缓存，请求ID：请求成功，有ID信息缓存并且成功显示，加载list;
            throw new AdException("广告位 imageView 不能为 null");
        } else {
            final String url = mCurrentAdBean.getAd_target_url();
            if (!TextUtils.isEmpty(url)) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetJavaScriptEnabled")
                    @Override
                    public void onClick(View v) {
                        JJLogger.logInfo("PPPPP", "跳转url :" + url);
                        onShowAdDetailCallback.onShowAdDetail(url);
                        mHttpJson.sendCPC(null, mCurrentAdBean.getAd_click_url(), null, null);
                        mHttpJson.sendCPC(null, mCurrentAdBean.getAd_our_click_url(), null, null);
                    }
                });
            }
        }
        if (onShowAdDetailCallback == null) {//检查参数是是否为空
            throw new AdException("setOnShowAdDetailCallback(OnShowAdDetailCallback showAdDetailListener)  中的函数签名'showAdDetailListener'为空！ ");
        }
        Bitmap bitmap = null;
        try {
            // 实例化Bitmap
            bitmap = AdSDK.getAdBitmapCacheHelper().getBitmap(mCurrentAdBean.getAd_id());
            mBitmap = bitmap;
        } catch (NullPointerException e) {
            if (mManagerCheckCallback != null) {
                mManagerCheckCallback.onAdNotExist(ErrorCode.CODE_LOAD_LOCAL_BITMAP_ERROR);//使用前
            }
        } catch (OutOfMemoryError e) {
            if (mManagerCheckCallback != null) {
                mManagerCheckCallback.onAdNotExist(ErrorCode.CODE_BITMAP_OUT_OF_MEMORY);//使用前
            }
        }

        if (bitmap != null) {
            imageView.setImageBitmap(mBitmap);
            mHttpJson.sendCPM(null, mCurrentAdBean.getAd_return_url(), null, null);
            mHttpJson.sendCPM(null, mCurrentAdBean.getAd_our_return_url(), null, null);
        } else {
            if (mManagerCheckCallback != null) {
                mManagerCheckCallback.onAdNotExist(ErrorCode.CODE_NO_AD_ID_IMAGE_LOCAL);
            }
        }
        JJLogger.logInfo(TAG, "AdManagerSplash.loadAd :");
        mHttpJson.requestAdList(new HttpAdListResult(mMapLocalAdBean));//3、有本地列表缓存，请求ID：请求成功，有ID信息缓存并且成功显示，加载list;
        return this;
    }


    /**
     * 广告检查的回调
     */
    private class CheckSplashLocalResult implements OnCheckAdListCallback {
        /**
         * @param listBeanMap 缓存广告信息的 map
         * @param showAdId    要展示的广告 id
         */
        @Override
        public void onLocalListExist(Map<String, AdListBean.DataBean.ListBean> listBeanMap, String showAdId) {
            mMapLocalAdBean = listBeanMap;
            if (listBeanMap.containsKey(showAdId)) {
                mCurrentAdBean = listBeanMap.get(showAdId);
                boolean imgExist = AdSDK.getAdBitmapCacheHelper().fileCacheExist(showAdId);
                if (mManagerCheckCallback != null) {
                    if (!imgExist) {
                        JJLogger.logInfo(TAG, "AdManagerSplash.loadAd :");
                        mHttpJson.requestAdList(new HttpAdListResult(mMapLocalAdBean));//....
                        mManagerCheckCallback.onAdNotExist(ErrorCode.CODE_NO_AD_ID_IMAGE_LOCAL);
                    } else {
                        mManagerCheckCallback.onAdExist();//广告存在
                    }
                }
            } else {
                /** CODE_NO_AD_ID_LOCAL
                 * 无 id 缓存信息，则请求列表更新清单数据
                 **/
                if (mManagerCheckCallback != null) {
                    JJLogger.logInfo(TAG, "AdManagerSplash.loadAd :");
                    mHttpJson.requestAdList(new HttpAdListResult(mMapLocalAdBean));//
                    mManagerCheckCallback.onAdNotExist(ErrorCode.CODE_NO_AD_ID_LOCAL);
                }
            }
        }

        /**
         * @param errorCode 错误码，不展示广告的原因
         */
        @Override
        public void onDoNotLoadAd(String errorCode) {
            /**  mMapLocalAdBean 可能为空
             * 回调此处的方式有 两种：
             * 1、检测本地无缓存广告列表；
             * 2、检测本地有缓存列表，并且成功请求到要展示的广告后；
             *    要展示的广告没有缓存；
             * */
            if (mManagerCheckCallback != null) {
                mManagerCheckCallback.onAdNotExist(errorCode);//广告不存在
            }
            JJLogger.logInfo(TAG, "AdManagerSplash.loadAd :");
            mHttpJson.requestAdList(new HttpAdListResult(mMapLocalAdBean));// 1、无本地列表缓存，加载list;
        }
    }

    @Override
    public void cancelAll() {
//        this.release();
        TaskManager.getmInstance().cancelAll();
    }

    @Override
    public void cancel(String tag) {
        this.release();
        TaskManager.getmInstance().cancel(tag);
    }

    @Override
    public void release() {
        // 先判断是否已经回收
        if (mBitmap != null) {
            // 回收并且置为null
//            mBitmap.recycle();
            mBitmap = null;
        }
        mCheckSplashAd = null;
//        mInstance = null;
    }
}
