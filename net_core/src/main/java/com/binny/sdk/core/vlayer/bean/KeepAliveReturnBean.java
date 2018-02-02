package com.binny.sdk.core.vlayer.bean;

/**
 * author xander on  2017/9/7.
 * function 心跳反馈的实体信息
 */

public class KeepAliveReturnBean {

    /**
     * body : {"tick_count":1554440}
     */

    private BodyBean body;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean {
        /**
         * tick_count : 1554440
         */

        private int tick_count;

        public int getTick_count() {
            return tick_count;
        }

        public void setTick_count(int tick_count) {
            this.tick_count = tick_count;
        }
    }

    @Override
    public String toString() {
        return "心跳包返回数据 ：{" +
                "body=" + body +
                '}';
    }
}
