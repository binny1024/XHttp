package com.dragon.app.bean;

import java.io.Serializable;
/**
 * Created by smart on 2017/4/24.
 */

public class MoocBean implements Serializable {
    private int status;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
