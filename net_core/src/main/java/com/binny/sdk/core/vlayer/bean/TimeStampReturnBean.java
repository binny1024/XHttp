package com.binny.sdk.core.vlayer.bean;

/**
 * author xander on  2017/9/7.
 * function
 */

public class TimeStampReturnBean {

    /**
     * body : {"timestamp":1504763429}
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
         * timestamp : 1504763429
         */

        private int timestamp;

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }
    }

    @Override
    public String toString() {
        return super.toString()+",\ncode = "+ code+",\nmessage = "+message+",\nbody.timestamp = "+getBody().getTimestamp();
    }
}
