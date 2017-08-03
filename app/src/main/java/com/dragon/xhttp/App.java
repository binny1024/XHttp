package com.dragon.xhttp;

import android.app.Application;

import com.jingjiu.http.exception.AdException;
import com.jingjiu.http.util.InitSDK;
import com.jingjiu.http.util.logger.JJLogger;

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
