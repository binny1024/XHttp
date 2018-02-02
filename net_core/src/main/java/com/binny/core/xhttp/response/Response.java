package com.binny.core.xhttp.response;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * author xander on  2017/7/25.
 * function
 */

public class Response implements IResponse {
    private byte[] mBytes;
    private Exception mException;
    private String mErrorCode;

    public Response() {

    }

    public void setErrorInfo(Exception exception, String errorCode) {
        mException = exception;
        mErrorCode = errorCode;
    }

    public void setBytes(byte[] bytes) {
        mBytes = bytes;
    }

    public Response(Exception exception, String errorCode) {
        mException = exception;
        mErrorCode = errorCode;
    }

    @Override
    public String toString() {
        return new String(mBytes);
    }

    @Override
    public byte[] toBytes() {
        return mBytes;
    }

    @Override
    public Bitmap toBitmap() {
        return BitmapFactory.decodeByteArray(mBytes, 0, mBytes.length);
    }

    @Override
    public InputStream toInputStream() {
        return new ByteArrayInputStream(mBytes);
    }

    @Override
    public Exception getException() {
        return mException;
    }

    @Override
    public String getErrorCode() {
        return mErrorCode;
    }
}
