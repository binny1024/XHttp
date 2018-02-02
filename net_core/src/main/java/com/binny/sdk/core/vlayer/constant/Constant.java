package com.binny.sdk.core.vlayer.constant;

/**
 * author xander on  2017/9/6.
 * function
 */

public  final class Constant {

    public static final String WS_HOST = "ws://videoclick.xiaohulu.com:14001/echo";
//    public static final String WS_HOST = "ws://192.168.200.162:8181";//测试服务器
//    public static final String WS_HOST = "ws://113.107.166.5:14001/echo";//测试服务器
    public static final int LOGON_SEND = 20001;//登陆消息
    public static final int LOGON_RET = 20002;//登陆返回结果


    /*
    * 广告消失后，是否断开链接
    * */
    public static final int CLOSE_SOCKET = 0;
    public static final int KEEP_ALIVE = 1;

    /**
     * 心跳消息
     */
    public static final int KEEP_ALIVE_SEND = 20003;

    public static final int KEEP_ALIVE_RET = 20004;

    public static final int NOTIFY_PLAY = 20005;//服务器发送过来信息
    public static final int NOTIFY_FEEDBACK = 20006;//20005 完成之后，发送给服务器

    public static final int INCORRECT_BAG = 30000;//返回的错误信息
    public static final int RESIZE_RETURN = 20011;//5.5.	Resize Ret
    public static final int SCREEN_CHANGED = 20010;//返回的错误信息


    /*
    * 播放完成，成功
    * */
    public static final int PLAY_SUCCESS_CODE = 0;
    public static final int PLAY_FAILURE_CODE = 0;
    public static final String PLAY_SUCCESS_MESSAGE = "success";

    public static final int CODE_REPEAT_REQUEST_AD_DATA = -1;
    public static final int CODE_LOGIN_SUCCESS = 0;

    public static final int DEFAULT_REPEAT_CONNECT_TIME = 30;

    /**
     * 空白页
     */
    public static final java.lang.String BLANK_PAGE = "about:blank";
}
