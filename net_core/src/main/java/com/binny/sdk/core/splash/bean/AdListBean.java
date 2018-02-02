package com.binny.sdk.core.splash.bean;

import java.io.Serializable;
import java.util.List;

/**
 * function 获取广告资源列表接口
 */

public class AdListBean implements Serializable {

    /**
     * error_code : 0
     * system_message :
     * display_message :
     * data : {"list":[{"ad_id":"1001","ad_name":"网易阴阳师","start_time":"2017-06-07 12:00:00","end_time":"2017-06-10 23:00:00","ad_img_url":"http://adimg.jing9.com/e0/a5/qe34srfff34534fsdf.png","ad_return_url":"http://s.qq.com/return/ad_id=101011eghh6","ad_click_url":"http://s.qq.com/click/ad_id=101011eghh6","ad_target_url":"http://s.qq.com/target/ad_id=101011eghh6","ad_our_return_url":"http://c.jing9.com/return/ad_id=101011eghh6","ad_our_click_url":"http://c.jing9.com/click/ad_id=101011eghh6"},{"ad_id":"1002","ad_name":"腾讯王者荣耀","start_time":"2017-06-07 12:00:00","end_time":"2017-06-09 23:59:59","ad_img_url":"http://adimg.jing9.com/e0/a5/qe34srfff34534fsdf.png","ad_return_url":"http://s.qq.com/return/ad_id=1023411eghh6","ad_click_url":"http://s.qq.com/click/ad_id=101234eghh6","ad_target_url":"http://s.qq.com/target/ad_id=102341eghh6","ad_our_return_url":"http://c.jing9.com/return/ad_id=1012341eghh6","ad_our_click_url":"http://c.jing9.com/click/ad_id=101234eghh6"}],"total_num":"12"}
     */

    private String error_code;
    private String system_message;
    private String display_message;
    private DataBean data;

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getSystem_message() {
        return system_message;
    }

    public void setSystem_message(String system_message) {
        this.system_message = system_message;
    }

    public String getDisplay_message() {
        return display_message;
    }

    public void setDisplay_message(String display_message) {
        this.display_message = display_message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * list : [{"ad_id":"1001","ad_name":"网易阴阳师","start_time":"2017-06-07 12:00:00","end_time":"2017-06-10 23:00:00","ad_img_url":"http://adimg.jing9.com/e0/a5/qe34srfff34534fsdf.png","ad_return_url":"http://s.qq.com/return/ad_id=101011eghh6","ad_click_url":"http://s.qq.com/click/ad_id=101011eghh6","ad_target_url":"http://s.qq.com/target/ad_id=101011eghh6","ad_our_return_url":"http://c.jing9.com/return/ad_id=101011eghh6","ad_our_click_url":"http://c.jing9.com/click/ad_id=101011eghh6"},{"ad_id":"1002","ad_name":"腾讯王者荣耀","start_time":"2017-06-07 12:00:00","end_time":"2017-06-09 23:59:59","ad_img_url":"http://adimg.jing9.com/e0/a5/qe34srfff34534fsdf.png","ad_return_url":"http://s.qq.com/return/ad_id=1023411eghh6","ad_click_url":"http://s.qq.com/click/ad_id=101234eghh6","ad_target_url":"http://s.qq.com/target/ad_id=102341eghh6","ad_our_return_url":"http://c.jing9.com/return/ad_id=1012341eghh6","ad_our_click_url":"http://c.jing9.com/click/ad_id=101234eghh6"}]
         * total_num : 12
         */

        private String total_num;
        private List<ListBean> list;

        public String getTotal_num() {
            return total_num;
        }

        public void setTotal_num(String total_num) {
            this.total_num = total_num;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean implements Serializable {
            /**
             * ad_id : 1001
             * ad_name : 网易阴阳师
             * start_time : 2017-06-07 12:00:00
             * end_time : 2017-06-10 23:00:00
             * ad_img_url : http://adimg.jing9.com/e0/a5/qe34srfff34534fsdf.png
             * ad_return_url : http://s.qq.com/return/ad_id=101011eghh6
             * ad_click_url : http://s.qq.com/click/ad_id=101011eghh6
             * ad_target_url : http://s.qq.com/target/ad_id=101011eghh6
             * ad_our_return_url : http://c.jing9.com/return/ad_id=101011eghh6
             * ad_our_click_url : http://c.jing9.com/click/ad_id=101011eghh6
             */

            private String ad_id;
            private String ad_name;
            private String start_time;
            private String end_time;
            private String ad_img_url;
            private String ad_return_url;
            private String ad_click_url;
            private String ad_target_url;
            private String ad_our_return_url;
            private String ad_our_click_url;

            public String getAd_id() {
                return ad_id;
            }

            public void setAd_id(String ad_id) {
                this.ad_id = ad_id;
            }

            public String getAd_name() {
                return ad_name;
            }

            public void setAd_name(String ad_name) {
                this.ad_name = ad_name;
            }

            public String getStart_time() {
                return start_time;
            }

            public void setStart_time(String start_time) {
                this.start_time = start_time;
            }

            public String getEnd_time() {
                return end_time;
            }

            public void setEnd_time(String end_time) {
                this.end_time = end_time;
            }

            public String getAd_img_url() {
                return ad_img_url;
            }

            public void setAd_img_url(String ad_img_url) {
                this.ad_img_url = ad_img_url;
            }

            public String getAd_return_url() {
                return ad_return_url;
            }

            public void setAd_return_url(String ad_return_url) {
                this.ad_return_url = ad_return_url;
            }

            public String getAd_click_url() {
                return ad_click_url;
            }

            public void setAd_click_url(String ad_click_url) {
                this.ad_click_url = ad_click_url;
            }

            public String getAd_target_url() {
                return ad_target_url;
            }

            public void setAd_target_url(String ad_target_url) {
                this.ad_target_url = ad_target_url;
            }

            public String getAd_our_return_url() {
                return ad_our_return_url;
            }

            public void setAd_our_return_url(String ad_our_return_url) {
                this.ad_our_return_url = ad_our_return_url;
            }

            public String getAd_our_click_url() {
                return ad_our_click_url;
            }

            public void setAd_our_click_url(String ad_our_click_url) {
                this.ad_our_click_url = ad_our_click_url;
            }
        }
    }
}
