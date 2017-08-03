package com.jingjiu.sdk.core.ad.compare;

import com.jingjiu.sdk.core.AdSDK;
import com.jingjiu.sdk.core.ad.bean.AdListBean;
import com.jingjiu.sdk.core.ad.compare.callback.OnDownloadCallback;
import com.jingjiu.sdk.core.ad.http.JJHttp;
import com.jingjiu.sdk.util.logger.JJLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jingjiu.sdk.core.ad.common.Configuration.AD_LIST_CACHE;
import static com.jingjiu.sdk.core.ad.common.ErrorCode.CODE_NO_AD_LIST_NET;
import static com.jingjiu.sdk.util.CommonMethod.removeAll;

/**
 * function 用于广告信息的对比更新
 */

public class AdCompare implements IAdCompare, OnDownloadCallback {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void checkAdListForUpdate(Map<String, AdListBean.DataBean.ListBean> listBeanMapLocal, AdListBean adListBeanNet) {

        if (listBeanMapLocal != null) {
            //有数据清单缓存，则对比新旧数据
            //有，则对比本地清单
            if (adListBeanNet.getData().getTotal_num().equals("0") || adListBeanNet.getData().getList().size() <= 0) {//list为空
                removeAll(listBeanMapLocal);//清空旧数据
                return;
            }
            //新旧对比
            compareAdList(listBeanMapLocal, adListBeanNet);
        } else {
            List<AdListBean.DataBean.ListBean> listBeanNet = adListBeanNet.getData().getList();
            if (listBeanNet == null || listBeanNet.size() <= 0) {//不缓存数据
                JJLogger.logError(CODE_NO_AD_LIST_NET, "首次网络拉取的广告列表数据为空 :");
                return;
            }

            JJLogger.logInfo(TAG, "AdCompare:checkAdListForUpdate :" + " first 没有缓存数据，保存数据，同时缓存图片资源");
            //保存数据，同时缓存图片资源
            AdSDK.getAdInfoCacheHelper().putSerializableBean(AD_LIST_CACHE, adListBeanNet);
            //缓存图片
            int len = adListBeanNet.getData().getList().size();

            /*  idImgUrlMap
            *  key：广告id
            *  value：广告图片的url
            *  */
            Map<String, String> adImgUrlMap = new HashMap<>();
            //取出图片的url
            for (int i = 0; i < len; i++) {
                String id = adListBeanNet.getData().getList().get(i).getAd_id();
                String url = adListBeanNet.getData().getList().get(i).getAd_img_url();

                adImgUrlMap.put(id, url);
            }
            //缓存图片
            JJLogger.map("first", adImgUrlMap);
            new JJHttp().download(this, adImgUrlMap);
        }
    }

    /**
     * 用于对比本地数据和网络数据
     *
     * @param adListNet    从网络获取的新的广告列表
     * @param listMapLocal 从本地获取的旧的广告列表
     */
    private void compareAdList(Map<String, AdListBean.DataBean.ListBean> listMapLocal, AdListBean adListNet) {
        Map<String, String> stringMapAddedOrUpdate = new HashMap<>();//新增的广告 id - imgUrl
        /*
        * 新数据中的 list
        * */
        List<AdListBean.DataBean.ListBean> listBeanNew = adListNet.getData().getList();
        Map<String, String> adListMapNet = new HashMap<>();//新增的广告 id - imgUrl
        for (AdListBean.DataBean.ListBean listBean : listBeanNew) {
            adListMapNet.put(listBean.getAd_id(), listBean.getAd_img_url());
        }
        /*
        * 旧数据
        * */
        for (final Map.Entry<String, AdListBean.DataBean.ListBean> entry : listMapLocal.entrySet()) {
            String idOld = entry.getKey();
            String imgUrlOld = entry.getValue().getAd_img_url();
            if (!adListMapNet.containsKey(idOld)) {//第一步：移除缓存数据
                //移除新列表中没有的数据的缓存
                AdSDK.getAdBitmapCacheHelper().remove(idOld);//从广告图片缓存文件夹下移除广告图片
                JJLogger.logInfo(TAG, "update 要删除的 id ：" + idOld);
            } else {
                //新旧相同的数据
                String imgUrlNew = adListMapNet.get(idOld);
                if (imgUrlNew.equals(imgUrlOld) && AdSDK.getAdBitmapCacheHelper().fileCacheExist(idOld)) {
                    adListMapNet.remove(idOld);//说明不需要更新图片
                    JJLogger.logInfo(TAG, "update1 不需要更新:" + imgUrlNew);
                } else {
                    JJLogger.logInfo(TAG, idOld + "update 新的:" + imgUrlNew);
                    JJLogger.logInfo(TAG, idOld + "update 旧的:" + imgUrlOld);
                    AdSDK.getAdBitmapCacheHelper().remove(idOld);//从广告图片缓存文件夹下移除广告图片
                }
            }
        }


        /**  mapNewAdList
         * 到这里，mapNewAdList，里面的剩下的数据为 新增和要更新的数据
         * 去更新图片和下载新图片
         * */
        if (!adListMapNet.isEmpty()) {
            JJLogger.map("update", adListMapNet);
            new JJHttp().download(this, adListMapNet);
        }
        /**
         * 缓存最新的广告列表 和 广告实体数据
         * */
        AdSDK.getAdInfoCacheHelper().putSerializableBean(AD_LIST_CACHE, adListNet);
    }


    /**
     * @param adId 广告id
     */
    @Override
    public void onSuccess(String adId) {
        JJLogger.logInfo("update", "--------------------------------------------------广告 id = " + adId + "的广告，" + "保存成功 ？" + AdSDK.getAdBitmapCacheHelper().fileCacheExist(adId));
    }


    /**
     * @param adId
     * @param errorCode 错误码
     */
    @Override
    public void onFailure(String adId, String errorCode) {
        JJLogger.logError(errorCode, "广告id = " + adId + " ");
    }
}
