package com.dragon.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.dragon.app.qq.activity.QQLoginActivity;
import com.dragon.qq.R;

import static com.dragon.app.constant.Code.DELAY_TIME;

public class SplashActivity extends AppCompatActivity {

    private SplashActivity mActivity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mActivity, QQLoginActivity.class));
            }
        },DELAY_TIME);
    }
}
