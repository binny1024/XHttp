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

import static com.dragon.xhttp.UtilWidget.getView;
import static com.dragon.xhttp.api.WebApi.LOGIN_REGISTER_URL;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();
    protected AutoCompleteTextView mAccountAct;
    protected EditText mPasswordEt;
    protected String errorInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAccountAct = getView(this, R.id.account);
        mPasswordEt = getView(this, R.id.password);
    }

    public void login(View view) {
        final String account = mAccountAct.getText().toString();
        final String password = mPasswordEt.getText().toString();

        if (checkAccountPassword(account, password)) {
            JJLogger.logInfo(TAG, "LoginActivity.loginOrRegister :");
            TaskManager.getmInstance().initTask().post(LOGIN_REGISTER_URL)
                    .setParams("account", account)
                    .setParams("tag", Code.TAG_LOGIN)
                    .setParams("password", password)
                    .setOnTaskCallback(new OnTaskCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                             JJLogger.logInfo(TAG,"LoginActivity.onSuccess :"+response.toString());
                            Gson gson = new Gson();
                            LoginInfo userBean = gson.fromJson(response.toString(), LoginInfo.class);

                            switch (userBean.getCode()) {
                                case "1003":
                                    Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                    break;
                                case "1001":
                                    Toast.makeText(LoginActivity.this, "未查询到您的注册信息，请先注册！", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                                    finish();
                                    break;
                                case "1004":
                                    Toast.makeText(LoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(final Exception ex, final String errorCode) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
}

