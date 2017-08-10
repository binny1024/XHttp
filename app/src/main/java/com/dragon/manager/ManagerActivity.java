package com.dragon.manager;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by xu on 2016/11/25.
 * 用于管理销售类activityd进入订单结果的出栈
 */

public final class ManagerActivity {
    private static Stack<Activity> activitiesCST;
//    public static List<Activity> activitiesCST = new ArrayList<Activity>();

    /**
     * add Activity 添加Activity到栈
     */
    public static void addActivityCST(Activity activity){
        if(activitiesCST ==null){
            activitiesCST =new Stack<Activity>();
        }
//        activitiesCST.add(activity);
          activitiesCST.add(0,activity);
    }
    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public static Activity currentActivity() {
        Activity activity = activitiesCST.lastElement();
        return activity;
    }
    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public static void finishActivity() {
        Activity activity = activitiesCST.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {
            activitiesCST.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        for (Activity activity : activitiesCST) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        for (int i = 0, size = activitiesCST.size(); i < size; i++) {
            if (null != activitiesCST.get(i)) {
                activitiesCST.get(i).finish();
            }
        }
        activitiesCST.clear();
    }

    /**
     * 退出应用程序
     */
    public static void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
        }
    }



    public static void removeActivityCST(Activity activity){
        activitiesCST.remove(activity);
    }
    //退出所有的Activity
    public static void finishAllCST(){
        for(Activity activity:activitiesCST){
            if(!activity.isFinishing()){
                activity.finish();//退出Activity
            }
        }
    }
    // 遍历所有Activity并finish
    public static void exit() {
        if (activitiesCST != null && activitiesCST.size() > 0) {
            for (Activity activity : activitiesCST) {
                activity.finish();
            }
//            activitiesCST = null;
        }
    }
}
