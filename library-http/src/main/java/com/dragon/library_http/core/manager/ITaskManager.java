package com.dragon.library_http.core.manager;


import com.dragon.library_http.core.task.HttpTask;

public interface ITaskManager {
        /**
         * @param tag  线程标志
         * @param task 线程
         */
        void addTask(String tag, HttpTask task);

        /**
         *取消任务
         * @param tag
         */
        void cancel(String tag);
        /**
         *取消所有任务
         */
        void cancelAll();
    }
