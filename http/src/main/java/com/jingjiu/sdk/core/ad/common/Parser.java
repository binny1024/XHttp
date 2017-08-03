package com.jingjiu.sdk.core.ad.common;

import com.jingjiu.sdk.core.ad.bean.AdListBean;
import com.jingjiu.sdk.core.ad.exception.AdException;
import com.jingjiu.sdk.core.ad.http.callbak.OnHttpAdListCallback;
import com.jingjiu.sdk.core.ad.http.callbak.OnHttpAdShowIdCallback;
import com.jingjiu.sdk.util.logger.JJLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * function 用于解析不同的json数据
 */

public final class Parser {

    /**
     * @param httpAdListCallback
     * @param result             清单的json字符串
     */
    /*
  * 处理广告资源列表接口
  * */
    public static void parseAdList(OnHttpAdListCallback httpAdListCallback, String result) {
        final String TAG = "jsondata";
        JJLogger.json(TAG, result);
        AdListBean adListBean = new AdListBean();
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has("data")) {
                JJLogger.logError(ErrorCode.CODE_PARSE_AD_LIST, TAG);
                return;
            }

            /*
            * 第一层解析
            * */
            //获取错误码
            String error_code = jsonObject.getString("error_code");

            if (error_code.equals("0")) {
                adListBean.setError_code(error_code);
                //获取系统信息
                String system_message = jsonObject.getString("system_message");
                adListBean.setSystem_message(system_message);
                //获取展示信息
                String display_message = jsonObject.getString("display_message");
                adListBean.setDisplay_message(display_message);

            /*
            * 第二层解析
            * */
                //获取data数据

                jsonObject = jsonObject.getJSONObject("data");

                AdListBean.DataBean dataBean = new AdListBean.DataBean();

                adListBean.setData(dataBean);
                //获取展示的总数
                String total_num = jsonObject.optString("total_num", Configuration.DEFAULT_STRING_VALUE);
                dataBean.setTotal_num(total_num);

                //获取广告列表

                List<AdListBean.DataBean.ListBean> listBeanList = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("list");

                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    AdListBean.DataBean.ListBean listBean = new AdListBean.DataBean.ListBean();
                    listBean.setAd_id(jsonObject.optString("ad_id", Configuration.DEFAULT_STRING_VALUE));
                    listBean.setAd_name(jsonObject.optString("ad_name", Configuration.DEFAULT_STRING_VALUE));
                    listBean.setStart_time(jsonObject.optString("start_time", Configuration.DEFAULT_STRING_VALUE));
                    listBean.setEnd_time(jsonObject.optString("end_time", Configuration.DEFAULT_STRING_VALUE));
                    listBean.setAd_img_url(jsonObject.optString("ad_img_url", Configuration.DEFAULT_STRING_VALUE));
                    listBean.setAd_return_url(jsonObject.optString("ad_return_url", Configuration.DEFAULT_STRING_VALUE));
                    listBean.setAd_click_url(jsonObject.optString("ad_click_url", Configuration.DEFAULT_STRING_VALUE));
                    listBean.setAd_target_url(jsonObject.optString("ad_target_url", Configuration.DEFAULT_STRING_VALUE));
                    listBean.setAd_our_return_url(jsonObject.optString("ad_our_return_url", Configuration.DEFAULT_STRING_VALUE));
                    listBean.setAd_our_click_url(jsonObject.optString("ad_our_click_url", Configuration.DEFAULT_STRING_VALUE));
                    listBeanList.add(listBean);
                }
                dataBean.setList(listBeanList);

                httpAdListCallback.onHttpAdListSuccess(adListBean);
            } else {
                httpAdListCallback.onHttpAdListFailure(new AdException("服务器出错！" + error_code), ErrorCode.CODE_ERROR_SERVER);
            }

        } catch (JSONException e) {
            /**
             * 数据解析失败
             * */
            JJLogger.logError(ErrorCode.CODE_PARSE_AD_LIST, TAG);
        }

    }

    /*
  * 处理此次广告展示信息
  * */
    public static void parseCurrentShowAdId(OnHttpAdShowIdCallback httpAdShowIdCallback, String result) {

        final String TAG = "jsondata";
        JJLogger.json(TAG, result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has("error_code")) {
                httpAdShowIdCallback.onHttpAdIdFailure(new AdException("error_code 字段不存在！"), ErrorCode.CODE_PARSE_AD_ID_ERROR);
                return;
            }
            if (!jsonObject.has("data")) {
                httpAdShowIdCallback.onHttpAdIdFailure(new AdException("data 字段不存在！"), ErrorCode.CODE_PARSE_AD_ID_ERROR);
                return;
            }
            /*
            * 第一层解析
            * */
            //获取错误码
            String error_code = jsonObject.getString("error_code");
            if (error_code.equals("0")) {

             /*
            * 第二层解析
            * */
                //获取data数据
                jsonObject = jsonObject.getJSONObject("data");
                if (!jsonObject.has("ad_id")) {//没有要显示的广告
                    httpAdShowIdCallback.onHttpAdIdFailure(new AdException("ad_id 字段不存在！"), ErrorCode.CODE_AD_ID_NA);
                    return;
                }

                String id = jsonObject.getString("ad_id");
                httpAdShowIdCallback.onHttpAdIdSuccess(id);
            } else {
                httpAdShowIdCallback.onHttpAdIdFailure(new AdException("服务器出错！" + error_code), ErrorCode.CODE_ERROR_SERVER);
            }

        } catch (JSONException e) {
            /*
            * 数据解析失败
            * */
            httpAdShowIdCallback.onHttpAdIdFailure(new AdException("数据格式错误！"), ErrorCode.CODE_PARSE_AD_ID_ERROR);

        }

    }

}
