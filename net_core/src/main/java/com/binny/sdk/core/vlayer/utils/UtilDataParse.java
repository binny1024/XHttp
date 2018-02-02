package com.binny.sdk.core.vlayer.utils;

import com.binny.sdk.core.vlayer.bean.CommonDataBean;
import com.binny.sdk.core.vlayer.bean.IncorrectMessage;
import com.binny.sdk.core.vlayer.bean.NotifyPlayBean;
import com.binny.core.logger.JJLogger;
import com.binny.sdk.core.vlayer.bean.KeepAliveReturnBean;
import com.binny.sdk.core.vlayer.bean.LoginReturnBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;

/**
 * author xander on  2017/9/7.
 * function
 */

public final class UtilDataParse {

    private static final String TAG = "UtilDataParse";

    /*******************************************************************************************************************
     * 公共数据解析
     *******************************************************************************************************************/
    public static CommonDataBean parseData(final byte[] data) {
         /*
        * 由于报文中有size"字段"。这里的长度后缀使用len
        * */
        CommonDataBean parseCommonBean = new CommonDataBean();
        int dataLen = data.length;

        int sizeLen = 4;//size ：int型，表示 整个报文的大小，占 4 个字节,位于data 中的 0 ～3(0,1,2,3)
        byte[] sizeBytes = new byte[sizeLen];//解析出来，转int
        int cmdLen = sizeLen;
        byte[] cmdBytes = new byte[cmdLen];//解析出来，转int


        int bodyLen = dataLen - sizeLen - sizeLen;
        int bodyStartPos = sizeLen + sizeLen;//8～　．．．（8,9,10,11....）
        byte[] bodyBytes = new byte[bodyLen];//body ,不定长，由data 决定

        for (int i = 0; i < dataLen; i++) {
            if (i < sizeLen) {//(0,1,2,3)
                sizeBytes[i] = data[i];
            } else if (i >= sizeLen && i < bodyStartPos) {//(4,5,6,7)
                cmdBytes[i - sizeLen] = data[i];
            } else {
                bodyBytes[i - bodyStartPos] = data[i];//body
            }
        }
        //取值
        int sizeValue = UtilConvert.byteArrayToInt(sizeBytes);
        int cmdValue = UtilConvert.byteArrayToInt(cmdBytes);

        parseCommonBean.setDataSize(sizeValue);
        parseCommonBean.setDataCmd(cmdValue);
        parseCommonBean.setBodyBytes(bodyBytes);
        return parseCommonBean;
    }

    /*******************************************************************************************************************
     * 登陆信息解析
     *******************************************************************************************************************/

    /**
     * @param body body 数据
     * @return 登陆结果
     */
    public static LoginReturnBean parseLoginReturnData(String body) {
        /*
        * 由于报文中有size"字段"。这里的长度后缀使用len
        * */
        LoginReturnBean bean = new LoginReturnBean();
        LoginReturnBean.BodyBean bodyBean = new LoginReturnBean.BodyBean();


        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(body);
            bean.setCode(jsonObject.getInt("code"));
            bean.setMessage(jsonObject.getString("message"));
            jsonObject = jsonObject.getJSONObject("body");
            bodyBean.setNext_connect_time(jsonObject.getInt("next_connect_time"));
            bean.setBody(bodyBean);

        } catch (JSONException e) {
            JJLogger.logInfo("data", e.getMessage());
            return null;
        }
        return bean;
    }


    /*******************************************************************************************************************
     * 播放通知信息解析
     *******************************************************************************************************************/


    public static NotifyPlayBean parseNotifyReturnData(String body) {
        NotifyPlayBean bean = new NotifyPlayBean();
        NotifyPlayBean.ConfigBean configBean = new NotifyPlayBean.ConfigBean();
        /**
         * play_url : host:port/api?param=sth
         * play_time : 10
         * callbackurl : host:port/api?param=sth
         */

        JJLogger.json(TAG,"parseNotifyReturnData :"+body);
        //解析body
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(body);
            bean.setPlay_time(jsonObject.getInt("play_time"));
            bean.setImg_url(URLDecoder.decode(jsonObject.getString("img_url")));
            bean.setDst_url(URLDecoder.decode(jsonObject.getString("dst_url")));
            bean.setPage_url(URLDecoder.decode(jsonObject.getString("page_url")));

            bean.setClient_height(jsonObject.getInt("client_height"));
            bean.setClient_width(jsonObject.getInt("client_width"));

            int height = jsonObject.getInt("height");
            int width = jsonObject.getInt("width");
            bean.setHeight(height);
            bean.setWidth(width);

            JJLogger.logInfo(TAG, "parseNotifyReturnData :" + height);
            JJLogger.logInfo(TAG, "parseNotifyReturnData :" + width);
            bean.setXpos(jsonObject.getInt("xpos"));
            bean.setYpos(jsonObject.getInt("ypos"));


            jsonObject = jsonObject.getJSONObject("config");
            configBean.setClick_effect(jsonObject.getInt("click_effect"));
            configBean.setClose_effect(jsonObject.getInt("close_effect"));
            configBean.setIgnore_effect(jsonObject.getInt("ignore_effect"));
            configBean.setReconnect_time(jsonObject.getInt("reconnect_time"));
            bean.setConfig(configBean);

        } catch (JSONException e) {
            JJLogger.logInfo("data", e.getMessage());
            return null;
        }
        return bean;

    }


    /*******************************************************************************************************************
     * 错误信息解析
     *******************************************************************************************************************/
    public static IncorrectMessage parseErrorMessage(String body) {
          /*
        * 由于报文中有size"字段"。这里的长度后缀使用len
        * */
        IncorrectMessage bean = new IncorrectMessage();
        IncorrectMessage.BodyBean bodyBean = new IncorrectMessage.BodyBean();

        //解析body
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(body);
            bean.setCode(jsonObject.getInt("code"));
            bean.setMessage(jsonObject.getString("message"));
//            jsonObject = jsonObject.getJSONObject("body");
            bean.setBody(bodyBean);

        } catch (JSONException e) {
            JJLogger.logInfo("data", e.getMessage());
            return null;
        }
        return bean;

    }

    /*******************************************************************************************************************
     * 心跳包的解析
     *******************************************************************************************************************/
    public static KeepAliveReturnBean parseKeepAliveReturnData(String body) {
        KeepAliveReturnBean bean = new KeepAliveReturnBean();
        KeepAliveReturnBean.BodyBean bodyBean = new KeepAliveReturnBean.BodyBean();

        //解析body
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(body);
            jsonObject = jsonObject.getJSONObject("body");
            bodyBean.setTick_count(jsonObject.getInt("tick_count"));
            bean.setBody(bodyBean);
        } catch (JSONException e) {
            JJLogger.logInfo("data", e.getMessage());
            return null;
        }
        return bean;

    }

}
