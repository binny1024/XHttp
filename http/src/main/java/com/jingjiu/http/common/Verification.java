package com.jingjiu.http.common;

/**
 * author xander on  2017/7/28.
 * function 数据校验类
 */

public final class Verification {

    /**
     * 在初始化失败时，为false : true 为 SDK 初始化后面的工作开始的依据
     * 初始化检查，发现异常则停止SDK后面的工作
     */
    public static boolean bWorking = true;
    /**
     *  校验 url 合适是否正确
     */
    public static final String REGEX_URL = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";



}
