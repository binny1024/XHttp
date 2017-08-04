package com.dragon.xhttp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.dragon.xhttp.R;
import com.dragon.xhttp.bean.LoginInfo;
import com.dragon.xhttp.constant.Code;
import com.google.gson.Gson;
import com.jingjiu.http.util.http.callback.OnTaskCallback;
import com.jingjiu.http.util.http.core.manager.TaskManager;
import com.jingjiu.http.util.http.response.Response;
import com.jingjiu.http.util.logger.JJLogger;

import java.net.URLEncoder;

import static com.dragon.xhttp.web_api.WebApi.LOGIN_REGISTER_URL;
import static com.smart.holder.util.UtilWidget.getView;

public class RegisterActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();
    protected AutoCompleteTextView mAccountAct;
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
            JJLogger.logInfo(TAG, "LoginActivity.loginOrRegister :");
            TaskManager.getmInstance().initTask().get(LOGIN_REGISTER_URL)
                    .setParams("account", URLEncoder.encode(account))
                    .setParams("tag", Code.TAG_REGISTER)
                    .setParams("password", password)
                    .setParams("age", age)
                    .setOnTaskCallback(new OnTaskCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                            JJLogger.logInfo(TAG,"LoginActivity.onSuccess :"+response.toString());
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
                    }).execute();
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
