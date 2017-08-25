package com.dragon.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bean.http.core.http.callback.OnXHttpCallback;
import com.bean.http.core.http.core.manager.XHttp;
import com.bean.http.core.http.response.Response;
import com.bean.http.core.logger.JJLogger;
import com.dragon.R;
import com.dragon.abs.activity.FullscreenActivity;
import com.dragon.app.bean.LoginInfo;
import com.google.gson.Gson;

import static com.dragon.api.WebApi.REGISTER_URL;
import static com.dragon.constant.Code.USER_NAME;
import static com.dragon.constant.Code.USER_PASSWORD;
import static com.dragon.manager.ManagerActivity.addActivityCST;
import static com.dragon.manager.ManagerActivity.finishAllCST;
import static com.dragon.util.UtilWidget.getView;
import static com.dragon.util.UtilWidget.setViewAlphaAnimation;
import static com.dragon.util.UtilWidget.showErrorInfo;

public class RegisterActivity extends FullscreenActivity {
    protected final String TAG = this.getClass().getSimpleName();
    protected EditText mAccountAct;
    protected EditText mPasswordEt;
    protected EditText mAge;
    protected EditText mTeltphone;

    @Override
    protected int initLayout() {
        addActivityCST(this);
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        mAccountAct = getView(this, R.id.account);
        mPasswordEt = getView(this, R.id.password);
        mAge = getView(this, R.id.age);
        mTeltphone = getView(this, R.id.teltphone);
    }

    @Override
    protected void initData() {

    }

    public void register(View view) {
        setViewAlphaAnimation(view);
        final String name = mAccountAct.getText().toString();
        final String password = mPasswordEt.getText().toString();
        final String age = mAge.getText().toString();
        final String telephone = mTeltphone.getText().toString();
        Log.i(TAG, "splash_kiss: "+telephone);
        if (checkAccountPassword(name, password, age)) {
            JJLogger.logInfo(TAG, "LoginActivity.loginOrRegister :");
            XHttp.getInstance()
                    .get(REGISTER_URL)
                    .setParams(USER_NAME, name)
                    .setParams(USER_PASSWORD, password)
                    .setParams("age", age)
                    .setParams("telephone", telephone)
                    .setOnXHttpCallback(new OnXHttpCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                            JJLogger.logInfo(TAG,"LoginActivity.onSuccess :"+response.toString());
                            Gson gson = new Gson();
                            LoginInfo userBean = gson.fromJson(response.toString(), LoginInfo.class);

                            switch (userBean.getCode()) {
                                case "1007":
                                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finishAllCST();
                                    break;
                                default:
                                    showErrorInfo(RegisterActivity.this, userBean.getMsg());
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(final Exception ex, final String errorCode) {
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        }
                    });
        } else {
            showErrorInfo(RegisterActivity.this, mErrorInfo);

        }
    }

    protected boolean checkAccountPassword(final String account, final String password, final CharSequence age) {
        if (TextUtils.isEmpty(account)) {
            mErrorInfo = "账号为空";
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            mErrorInfo = "密码为空";
            return false;
        }
        return true;
    }
}
