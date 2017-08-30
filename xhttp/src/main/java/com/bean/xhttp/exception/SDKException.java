package com.bean.xhttp.exception;

/**
 * function
 */

public class SDKException extends Exception {
    String mErrorCode;

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
