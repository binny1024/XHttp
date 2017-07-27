package com.dragon.library_http.core.manager;

import android.util.Log;

import com.dragon.library_http.core.task.HttpTask;

import java.util.HashMap;
import java.util.Map;

/**
     * 异步任务管理器
     */
public  class TaskManager implements ITaskManager {
        private static final String TAG = "TaskManager";
    /**
     * 异步任务管理器
     */
    private Map<String, HttpTask> mTaskManager;
        // 定义一个私有构造方法
        private TaskManager() {
        }

        //定义一个静态私有变量(不初始化，不使用final关键字，使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用)
        private static volatile  TaskManager instance;

        //定义一个共有的静态方法，返回该类型实例
        public static TaskManager getIstance() {
            // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
            if (instance == null) {
                //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
                synchronized ( TaskManager.class) {
                    //未初始化，则初始instance变量
                    if (instance == null) {
                        instance = new  TaskManager();
                    }
                }
            }
            return instance;
        }

        @Override
        public void addTask(String tag, HttpTask task) {
            if (mTaskManager == null) {
                mTaskManager = new HashMap<>();
            }
            mTaskManager.put(tag, task);
        }

        @Override
        public void cancel(String tag) {
            if (mTaskManager == null) {
                return;
            }
            int taskSize = mTaskManager.size();
            if (taskSize > 0) {
                for (final Map.Entry<String, HttpTask> entry : mTaskManager.entrySet()) {
                    if (entry.getKey().equals(tag)) {
                        Log.i(TAG,"TaskManager.cancel :"+entry.getKey());
                        entry.getValue().cancle();
                    }
                }
            }
            mTaskManager.clear();
        }

        @Override
        public void cancelAll() {
            if (mTaskManager == null) {
                return;
            }
            int taskSize = mTaskManager.size();
            if (taskSize > 0) {
                for (final Map.Entry<String, HttpTask> entry : mTaskManager.entrySet()) {
                    entry.getValue().cancle();
                    Log.i("xander","TaskManager.cancel :"+entry.getKey());
                }
            }
            mTaskManager.clear();
        }
    }
