package com.bean.http.core.http.response;

import android.graphics.Bitmap;

import java.io.InputStream;

/**
 * author xander on  2017/7/25.
 * function 网络请求成功的响应
 */

interface IResponse {
    /**
     * 将获取的输入流转换为字符串
     *
     * @return 字符串
     */
    String toString();

    /**
     * 将获取的输入流转换为字节数组
     *
     * @return 字节数组
     */
    byte[] toBytes();

    /**
     * 将获得的输入流转换为位图
     *
     * @return 位图
     */
    Bitmap toBitmap();

    /**
     * 获取输入流
     *
     * @return 输入流
     */
    InputStream toInputStream();

    /**
     * 获取异常信息
     *
     * @return 异常
     */
    Exception getException();

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    String getErrorCode();
}
