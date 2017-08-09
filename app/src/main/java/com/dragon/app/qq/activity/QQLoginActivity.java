package com.dragon.app.qq.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.dragon.app.qq.UtilWidget;
import com.dragon.app.qq.api.WebApi;
import com.dragon.app.qq.bean.LoginInfo;
import com.dragon.app.constant.Code;
import com.dragon.qq.R;
import com.google.gson.Gson;
import com.jingjiu.http.core.http.callback.OnTaskCallback;
import com.jingjiu.http.core.http.core.manager.TaskManager;
import com.jingjiu.http.core.http.response.Response;
import com.jingjiu.http.core.logger.JJLogger;

/**
 * A login screen that offers login via email/password.
 */
public class QQLoginActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    protected final String TAG = this.getClass().getSimpleName();
    protected EditText mAccountAct;
    protected EditText mPasswordEt;
    protected String errorInfo;
    private VideoView mVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mVideoView = UtilWidget.getView(this, R.id.videoView);
        mAccountAct = UtilWidget.getView(this, R.id.account);
        mPasswordEt = UtilWidget.getView(this, R.id.password);

        initVideoView();
    }
    private void initVideoView() {
        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mVideoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.mqr);
        //设置相关的监听
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
    }
    public void login(View view) {
        final String account = mAccountAct.getText().toString();
        final String password = mPasswordEt.getText().toString();

        if (checkAccountPassword(account, password)) {
            TaskManager.getmInstance().initTask().post(WebApi.LOGIN_URL)
                    .setParams("account", account)
                    .setParams("tag", Code.TAG_LOGIN)
                    .setParams("platform","mobile_phone")
                    .setParams("password", password)
                    .setOnTaskCallback(new OnTaskCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                             JJLogger.logInfo(TAG,"QQLoginActivity.onSuccess :"+response.toString());
                            Gson gson = new Gson();
                            LoginInfo userBean = gson.fromJson(response.toString(), LoginInfo.class);
                            switch (userBean.getCode()) {
                                case "1003":
                                    Toast.makeText(QQLoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(QQLoginActivity.this, MainActivity.class));
                                    finish();
                                    break;
                                case "1001":
                                    Toast.makeText(QQLoginActivity.this, "未查询到您的注册信息，请先注册！", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(QQLoginActivity.this, RegisterActivity.class));
                                    finish();
                                    break;
                                case "1004":
                                    Toast.makeText(QQLoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(final Exception ex, final String errorCode) {
                             JJLogger.logInfo(TAG,"QQLoginActivity.onFailure :"+errorCode);
                            startActivity(new Intent(QQLoginActivity.this, MainActivity.class));
                        }
                    });
        } else {
            Toast.makeText(this, "您的 " + errorInfo + " !", Toast.LENGTH_SHORT).show();
        }
    }

    protected boolean checkAccountPassword(final String account, final String password) {
        if (TextUtils.isEmpty(account)) {
            errorInfo = "账号为空";
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            errorInfo = "密码为空";
            return false;
        }
        return true;
    }

    @Override
    public void onCompletion(final MediaPlayer mp) {
        //开始播放
        mVideoView.start();
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        //开始播放
        mVideoView.start();
    }
}

