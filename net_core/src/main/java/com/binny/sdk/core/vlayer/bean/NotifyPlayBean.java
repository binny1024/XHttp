package com.binny.sdk.core.vlayer.bean;

/**
 * author xander on  2017/9/7.
 * function
 */

public class NotifyPlayBean {


    /**
     * img_url : http://gameimg.yoyojie.com/icon/6716/icon_160_160.png
     * dst_url : https://www.xiaohulu.com
     * page_url : http://download.xiaohulu.com/temp/test.html
     * xpos : 300
     * ypos : 400
     * height : 300
     * width : 400
     * client_height : 1080
     * client_width : 1920
     * play_time : 10
     * config : {"click_effect":0,"close_effect":0,"ignore_effect":0,"reconnect_time":300}
     */

    private String img_url;
    private String dst_url;
    private String page_url;
    private int xpos;
    private int ypos;
    private int height;
    private int width;
    private int client_height;
    private int client_width;
    private int play_time;
    private ConfigBean config;

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDst_url() {
        return dst_url;
    }

    public void setDst_url(String dst_url) {
        this.dst_url = dst_url;
    }

    public String getPage_url() {
        return page_url;
    }

    public void setPage_url(String page_url) {
        this.page_url = page_url;
    }

    public int getXpos() {
        return xpos;
    }

    public void setXpos(int xpos) {
        this.xpos = xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public void setYpos(int ypos) {
        this.ypos = ypos;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getClient_height() {
        return client_height;
    }

    public void setClient_height(int client_height) {
        this.client_height = client_height;
    }

    public int getClient_width() {
        return client_width;
    }

    public void setClient_width(int client_width) {
        this.client_width = client_width;
    }

    public int getPlay_time() {
        return play_time;
    }

    public void setPlay_time(int play_time) {
        this.play_time = play_time;
    }

    public ConfigBean getConfig() {
        return config;
    }

    public void setConfig(ConfigBean config) {
        this.config = config;
    }

    public static class ConfigBean {
        /**
         * click_effect : 0
         * close_effect : 0
         * ignore_effect : 0
         * reconnect_time : 300
         */

        private int click_effect;
        private int close_effect;
        private int ignore_effect;
        private int reconnect_time;

        public int getClick_effect() {
            return click_effect;
        }

        public void setClick_effect(int click_effect) {
            this.click_effect = click_effect;
        }

        public int getClose_effect() {
            return close_effect;
        }

        public void setClose_effect(int close_effect) {
            this.close_effect = close_effect;
        }

        public int getIgnore_effect() {
            return ignore_effect;
        }

        public void setIgnore_effect(int ignore_effect) {
            this.ignore_effect = ignore_effect;
        }

        public int getReconnect_time() {
            return reconnect_time;
        }

        public void setReconnect_time(int reconnect_time) {
            this.reconnect_time = reconnect_time;
        }
    }
}
