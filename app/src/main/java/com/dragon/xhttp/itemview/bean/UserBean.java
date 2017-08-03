package com.dragon.xhttp.itemview.bean;

import java.util.List;

/**
 * author xander on  2017/8/3.
 * function
 */

public class UserBean {

    /**
     * errorCode :
     * msg : 登陆成功！
     * userInfoList : [{"userName":"xubinbin","userPassword":"122819"}]
     */

    private String errorCode;
    private String msg;
    private List<UserInfoListBean> userInfoList;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<UserInfoListBean> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(List<UserInfoListBean> userInfoList) {
        this.userInfoList = userInfoList;
    }

    public static class UserInfoListBean {
        /**
         * userName : xubinbin
         * userPassword : 122819
         */

        private String userName;
        private String userPassword;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPassword() {
            return userPassword;
        }

        public void setUserPassword(String userPassword) {
            this.userPassword = userPassword;
        }
    }
}
