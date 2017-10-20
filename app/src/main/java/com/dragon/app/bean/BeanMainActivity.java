package com.dragon.app.bean;

import java.io.Serializable;

/**
 * function
 */

public class BeanMainActivity implements Serializable {
    private String mTextViewName;

    public String getTextViewName() {
        return mTextViewName;
    }

    public void setTextViewName(String textViewName) {
        this.mTextViewName = textViewName;
    }
}
