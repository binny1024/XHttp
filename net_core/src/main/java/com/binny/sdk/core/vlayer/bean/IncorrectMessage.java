package com.binny.sdk.core.vlayer.bean;

/**
 * author xander on  2017/9/7.
 * function  服务器返回的错误信息
 */

public class IncorrectMessage {

    /**
     * code : 0
     * message : success
     * body : {}
     */

    private int code;
    private String message;
    private BodyBean body;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean {
    }

    @Override
    public String toString() {
        return "IncorrectMessage{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", body={}" +
                '}';
    }
}
