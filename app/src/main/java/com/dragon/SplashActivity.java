package com.dragon;

import android.content.Intent;
import android.os.Handler;

import com.dragon.abs.activity.FullscreenActivity;
import com.dragon.app.qq.activity.QQLoginActivity;

import static com.dragon.constant.Code.DELAY_TIME;

public class SplashActivity extends FullscreenActivity {

    private SplashActivity mActivity = this;


    @Override
    protected int initLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mActivity, QQLoginActivity.class));
                finish();
            }
        },DELAY_TIME);
    }

}
