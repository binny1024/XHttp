package com.dragon.app.qq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dragon.app.qq.bean.LoginInfo;
import com.dragon.qq.R;
import com.dragon.app.constant.Code;
import com.google.gson.Gson;
import com.jingjiu.http.core.http.callback.OnTaskCallback;
import com.jingjiu.http.core.http.core.manager.TaskManager;
import com.jingjiu.http.core.http.response.Response;
import com.jingjiu.http.core.logger.JJLogger;

import java.net.URLEncoder;

import static com.dragon.app.qq.UtilWidget.getView;
import static com.dragon.app.qq.api.WebApi.LOGIN_URL;

public class RegisterActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();
    protected EditText mAccountAct;
    protected EditText mPasswordEt;
    protected String errorInfo;
    protected EditText mAge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAccountAct = getView(this, R.id.account);
        mPasswordEt = getView(this, R.id.password);
        mAge = getView(this, R.id.age);
    }
    public void register(View view) {
        final String account = mAccountAct.getText().toString();
        final String password = mPasswordEt.getText().toString();
        final String age = mAge.getText().toString();

        if (checkAccountPassword(account, password, age)) {
            JJLogger.logInfo(TAG, "QQLoginActivity.loginOrRegister :");
            TaskManager.getmInstance().initTask().get(LOGIN_URL)
                    .setParams("account", URLEncoder.encode(account))
                    .setParams("tag", Code.TAG_REGISTER)
                    .setParams("password", password)
                    .setParams("age", age)
                    .setOnTaskCallback(new OnTaskCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                            JJLogger.logInfo(TAG,"QQLoginActivity.onSuccess :"+response.toString());
                            Gson gson = new Gson();
                            LoginInfo userBean = gson.fromJson(response.toString(), LoginInfo.class);

                            switch (userBean.getCode()) {
                                case "1007":
                                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                    break;
                                default:
                                    Toast.makeText(RegisterActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(final Exception ex, final String errorCode) {
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        }
                    });
        } else {
            Toast.makeText(this, "您的 " + errorInfo + " !", Toast.LENGTH_SHORT).show();
        }
    }

    protected boolean checkAccountPassword(final String account, final String password, final CharSequence age) {
        if (TextUtils.isEmpty(account)) {
            errorInfo = "账号为空";
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            errorInfo = "密码为空";
            return false;
        }
        if (TextUtils.isEmpty(age)) {
            errorInfo = "密码为空";
            return false;
        }
        return true;
    }
}
