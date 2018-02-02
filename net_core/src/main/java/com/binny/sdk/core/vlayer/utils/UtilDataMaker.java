package com.binny.sdk.core.vlayer.utils;


import com.binny.core.logger.JJLogger;

/**
 * author xander on  2017/9/7.
 * function  ：构造发送数据包，分三步组装；
 * <p>
 * 第一步：构造body字符串
 * 第二步：字符串转字节数组
 * 第三部：根据报文格式，构造数据包
 * @author smart
 */

public class UtilDataMaker {
    private static final String TAG = "UtilDataMaker";
    /*******************************************************************************************************************
     * 登陆数据包的组建
     *******************************************************************************************************************/

    public static String getLoginBodyString(String clientType, String playerType, int platId, String roomId, int height,int width,String other) {
        String body = "{" +
                "\"client_type\":\"" + clientType + "\"," +
                "\"player_type\":\"" + playerType + "\"," +
                "\"plat_id\":" + platId + "," +
                "\"room_id\":\"" + roomId + "\"," +
                "\"height\":" + height + "," +
                "\"width\":" + width + "," +
                "\"other\":\"" + other + "\"" +
                "}";
        JJLogger.logInfo("body",body);
        return body;
    }


    /*******************************************************************************************************************
     * 心跳包的组建
     *******************************************************************************************************************/
    /**
     * 构造body 字符串
     *
     * @param tickCount 时间戳
     * @return bodyString
     */
    public static String getKeepAliveBodyString(long tickCount) {
        return "{" +
                "\"body\":{" +
                "\"tick_count\":" + tickCount +
                "}" +
                "}";
    }

    /*******************************************************************************************************************
     * 播放结果  回馈给服务器的信息组建
     *******************************************************************************************************************/
    public static String getNotifyPlayFeedbackBodyString(int code, String message) {
        return "{" +
                "\"code\":" + code + "," +
                "\"message\":\"" + message + "\"," +
                "\"body\":{}" +
                "}";
    }


    public static String getResizeBodyString(int newHeight,int newWidth){
        String resize = "{" +
                "\"height\":" + newHeight + "," +
                "\"width\":" + newWidth +
                "}";
        JJLogger.logInfo("resize",resize);
        return resize;
    }


    public static byte[] makeBytes(int cmd,String jsonBody){
        //cmd
        byte[] cmdBytes = UtilConvert.intToByteArray(cmd);
        int cmdBytesLen = cmdBytes.length;//cmd 字节数

        //jsonBody
        byte[] bodyBytes = jsonBody.getBytes();
        int bodyBytesLen = bodyBytes.length;//jsonBody 字节数

        //size
        int dataBytesLen = cmdBytesLen + bodyBytesLen + 4;//报文总大小
        byte[] dataSizeBytes = UtilConvert.intToByteArray(dataBytesLen);//

        //data
        byte[] data = new byte[dataBytesLen];
        JJLogger.logInfo(TAG,"makeBytes :"+data.length);
        System.arraycopy(dataSizeBytes,0,data,0,4);
        System.arraycopy(cmdBytes,0,data,4,4);
        System.arraycopy(bodyBytes,0,data,8,bodyBytesLen);
        return data;

    }
}
