package com.binny.sdk.exception;

/**
 * function
 */
public class SDKException extends Exception {
    String mErrorCode;

    public String getErrorCode() {
        return this.mErrorCode;
    }

    public SDKException(String detailMessage) {
        super(detailMessage);
    }

    public SDKException(String detailMessage, String errorCode) {
        super(detailMessage);
        this.mErrorCode = errorCode;
    }
}

