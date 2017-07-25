package com.dragon.library_http.exception;

/**
 * author xander on  2017/7/25.
 * function
 */

public class HttpException extends Exception {
    public HttpException() {
        super();
    }

    public HttpException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
