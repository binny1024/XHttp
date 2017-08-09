package com.dragon.abs.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 日志输出标志
     **/
    protected final String TAG = this.getClass().getSimpleName();

    /**
     * 是否全屏
     */
    protected boolean mFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSystemUI();
        setContentView(initLayout());
        initView();
        handleIntent();
        initData();
    }

    protected void handleIntent() {
        Log.i(TAG, "handleIntent:");
    }

    protected void initSystemUI() {
        Log.i(TAG, "initSystemUI");
    }

    ;

    /**
     * 初始化布局参数
     *
     * @return 布局文件ID
     */
    protected abstract int initLayout();

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();
}
