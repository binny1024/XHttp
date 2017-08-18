package com.dragon.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.dragon.R;
import com.dragon.abs.activity.FullscreenActivity;
import com.dragon.api.WebApi;
import com.dragon.app.bean.LoginInfo;
import com.dragon.constant.Code;
import com.dragon.util.ModifyDialog;
import com.google.gson.Gson;
import com.jingjiu.http.core.http.callback.OnTaskCallback;
import com.jingjiu.http.core.http.core.manager.TaskManager;
import com.jingjiu.http.core.http.response.Response;
import com.jingjiu.http.core.logger.JJLogger;

import static com.dragon.api.WebApi.MODIFY_URL;
import static com.dragon.manager.ManagerActivity.addActivityCST;
import static com.dragon.manager.ManagerActivity.finishAllCST;
import static com.dragon.util.UtilWidget.getView;
import static com.dragon.util.UtilWidget.setViewAlphaAnimation;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends FullscreenActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    protected final String TAG = this.getClass().getSimpleName();
    protected EditText mAccountAct;
    protected EditText mPasswordEt;
    protected String errorInfo;
    private VideoView mSpalshVideo;
    private TextView mItemTv;

    @Override
    protected int initLayout() {
        addActivityCST(this);
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        mSpalshVideo = getView(this, R.id.videoView);
        mAccountAct = getView(this, R.id.account);
        mPasswordEt = getView(this, R.id.password);
        mItemTv = getView(this, R.id.item);
    }

    @Override
    protected void initData() {
        initVideoView();
        String text = "登录即代表阅读并同意服务条款";
        int len = text.length();
        SpannableString spannableString = new SpannableString(text);
        mItemTv.setMovementMethod(LinkMovementMethod.getInstance());//必须设置否则无效
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(final View widget) {
                startActivity(new Intent(mActivity, ItemActivity.class));
            }

            @Override
            public void updateDrawState(final TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#0061ff"));
                ds.setUnderlineText(false);    //去除超链接的下划线
            }
        }, len - 4, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //改变选中文本的高亮颜色
//        mItemTv.setHighlightColor(Color.BLUE);
        mItemTv.setText(spannableString);
    }

    private void initVideoView() {
        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSpalshVideo.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.mqr);
        //设置相关的监听
        mSpalshVideo.setOnPreparedListener(this);
        mSpalshVideo.setOnCompletionListener(this);
    }

    public void login(View view) {
        setViewAlphaAnimation(view);
        final String name = mAccountAct.getText().toString();
        final String password = mPasswordEt.getText().toString();

        if (checkAccountPassword(name, password)) {
            TaskManager.getmInstance().initTask().post(WebApi.LOGIN_URL)
                    .setParams("name", name)
                    .setParams("tag", Code.TAG_LOGIN)
                    .setParams("platform", "mobile_phone")
                    .setParams("password", password)
                    .setOnTaskCallback(new OnTaskCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                            JJLogger.logInfo(TAG, "LoginActivity.onSuccess :" + response.toString());
                            Gson gson = new Gson();
                            LoginInfo userBean = gson.fromJson(response.toString(), LoginInfo.class);
                            switch (userBean.getCode()) {
                                case "1003":
                                    Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                    intoApp();
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, userBean.getMsg(), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(final Exception ex, final String errorCode) {
                            Log.i(TAG, "onFailure: " + ex.getMessage());
                            intoApp();
                        }
                    });
        } else {
            Toast.makeText(this, "错误： " + errorInfo + " !", Toast.LENGTH_SHORT).show();
        }
    }

    private void intoApp() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finishAllCST();
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
        mSpalshVideo.start();
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        //开始播放
        mSpalshVideo.start();
    }

    public void forgetPassword(View view) {

        new ModifyDialog(this).setOnModifyDialogListener(new ModifyDialog.OnModifyDialogListener() {
            @Override
            public void onSure(final String name, final String password, final String telephone) {
                TaskManager.getmInstance().initTask().post(MODIFY_URL)
                        .setParams("name", name)
                        .setParams("password", password)
                        .setParams("telephone", telephone)
                        .setOnTaskCallback(new OnTaskCallback() {
                            @Override
                            public void onSuccess(final Response response) {
                                JJLogger.logInfo(TAG, "LoginActivity.onSuccess :" + response.toString());
//                                Gson gson = new Gson();
//                                LoginInfo userBean = gson.fromJson(response.toString(), LoginInfo.class);
//                                switch (userBean.getCode()) {
//                                    case "1003":
//                                        Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
//                                        intoApp();
//                                        break;
//                                    default:
//                                        Toast.makeText(LoginActivity.this, userBean.getMsg(), Toast.LENGTH_SHORT).show();
//                                        break;
//                                }
                            }

                            @Override
                            public void onFailure(final Exception ex, final String errorCode) {
                                Log.i(TAG, "onFailure: " + ex.getMessage());
                                intoApp();
                            }
                        });
            }

            @Override
            public void onCancel() {

            }
        }).show();

    }

    public void registerNew(View view) {
        register();
    }

    private void register() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}

