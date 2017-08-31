package com.bean.xhttp.core.task;

import com.bean.xhttp.core.IHttp;

/**
 * author xander on  2017/7/27.
 * function  定义线程任务
 */

interface IHttpTask extends IHttp<IHttpTask> {

    /**
     * 取消任务
     */
    void cancle();
}
