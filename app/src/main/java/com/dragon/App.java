package com.dragon;

import android.app.Application;

import com.jingjiu.http.core.InitSDK;
import com.jingjiu.http.core.logger.JJLogger;
import com.jingjiu.http.exception.AdException;

import java.io.IOException;

/**
 * author xander on  2017/8/3.
 * function
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            InitSDK.init(this);
            JJLogger.debug(true);
        } catch (AdException | IOException e) {
            e.printStackTrace();
        }
    }
}
