package com.dragon;

import android.app.Application;

import com.jingjiu.http.core.InitSDK;
import com.jingjiu.http.core.logger.JJLogger;
import com.jingjiu.http.exception.SDKException;

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
            JJLogger.debug(false);
        } catch (SDKException | IOException e) {
            e.printStackTrace();
        }
    }
//    /************************************************************
//     *Author; 龙之游 @ xu 596928539@qq.com
//     * 时间:2017/1/13 12:03
//     * 注释: 用于获取 栈顶 activity  //// Activity的生命周期事件进行集中处理
//     ***********************************************************
//     * param context*/
//    private static void activityLifecycleCallbacks(Application context) {
//        context.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
//            @Override
//            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//
//            }
//
//            @Override
//            public void onActivityStarted(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityResumed(Activity activity) {
//                //监听onActivityResumed()方法
//                ManagerActivity.getInstance().setCurrentActivity(activity);
//            }
//
//            @Override
//            public void onActivityPaused(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityStopped(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//
//            }
//
//            @Override
//            public void onActivityDestroyed(Activity activity) {
//
//            }
//        });
//    }
}
