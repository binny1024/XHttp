package com.dragon.api;

/**
 * author xander on  2017/8/3.
 * function
 */

public class WebApi {
    public static final String HOST =  "http://192.168.200.162:8080/HttpHelperWeb";
//    private static final String HOST =  "http://itbean.ngrok.cc/HttpHelperWeb/";// 外网使用，
    public  static final String LOGIN_URL = HOST+"/user";
    public  static final String MODIFY_URL = HOST+"/modify";
    public  static final String UPLOAD_FILE_URL = HOST+"/upload";
}
