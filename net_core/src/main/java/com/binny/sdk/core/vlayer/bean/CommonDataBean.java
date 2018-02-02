package com.binny.sdk.core.vlayer.bean;

/**
 * author xander on  2017/9/7.
 * function 解析data 的公共部分
 */

public class CommonDataBean extends SizeCmd {
    private byte[] mBodyBytes;

    public byte[] getBodyBytes() {
        return mBodyBytes;
    }

    public void setBodyBytes(final byte[] bodyBytes) {
        mBodyBytes = bodyBytes;
    }

    @Override
    public String toString() {
        return super.toString()+",\nmBodyBytes = "+ new String(mBodyBytes);
    }
}
