package com.binny.sdk.core.vlayer.bean;

/**
 * author xander on  2017/9/7.
 * function 登陆返回的实体信息
 */

public class LoginReturnBean {

    /**
     * body : {"next_connect_time":200}
     * code : 0
     * message : success
     */

    private BodyBean body;
    private int code;
    private String message;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

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

    public static class BodyBean {
        /**
         * next_connect_time : 200
         */

        private int next_connect_time;

        public int getNext_connect_time() {
            return next_connect_time;
        }

        public void setNext_connect_time(int next_connect_time) {
            this.next_connect_time = next_connect_time;
        }
    }

//    @Override
//    public String toString() {
//        return super.toString()+",\ncode = "+ code+",\nmessage = "+message+",\nbody.next_connect_time = "+ getBody().getNext_connect_time();
//    }

    @Override
    public String toString() {
        return "LoginReturnBean{" +
                "body=" + body.getNext_connect_time() +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
