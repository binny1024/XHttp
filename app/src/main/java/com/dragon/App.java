package com.dragon;

import android.app.Application;

import com.bean.logger.JJLogger;
import com.bean.exception.SDKException;

import java.io.IOException;

/**
 * author xander on  2017/8/3.
 * function
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        activityLifecycleCallbacks(this);
        try {
            InitSDK.init(this);
            JJLogger.debug(true);
        } catch (SDKException | IOException e) {
            e.printStackTrace();
        }
    }

}
