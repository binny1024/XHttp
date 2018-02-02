package com.binny.sdk.core.vlayer.bean;

import java.io.Serializable;

/**
 * author xander on  2017/9/7.
 * function 数据包中，"头部信息" size cmd
 */

public class SizeCmd implements Serializable {
    protected int mDataSize;
    protected int mDataCmd;

    public int getDataSize() {
        return mDataSize;
    }

    public void setDataSize(final int dataSize) {
        mDataSize = dataSize;
    }

    public int getDataCmd() {
        return mDataCmd;
    }

    public void setDataCmd(final int dataCmd) {
        mDataCmd = dataCmd;
    }

    @Override
    public String toString() {
        return "SizeCmd{" +
                "mDataSize=" + mDataSize +
                ", mDataCmd=" + mDataCmd +
                '}';
    }
}
