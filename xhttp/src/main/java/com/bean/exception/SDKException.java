package com.bean.exception;

/**
 * function
 */

public class SDKException extends Exception {
    private String mErrorCode;

    public String getErrorCode() {
        return mErrorCode;
    }

    public SDKException(String detailMessage) {
        super(detailMessage);
    }
    public SDKException(String detailMessage, String errorCode){
        super(detailMessage);
        mErrorCode = errorCode;
    }
}
