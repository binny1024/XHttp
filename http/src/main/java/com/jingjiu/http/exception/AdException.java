package com.jingjiu.http.exception;

/**
 * function
 */

public class AdException extends Exception {
    String mErrorCode;

    public String getErrorCode() {
        return mErrorCode;
    }

    public AdException(String detailMessage) {
        super(detailMessage);
    }
    public AdException(String detailMessage, String errorCode){
        super(detailMessage);
        mErrorCode = errorCode;
    }
}
