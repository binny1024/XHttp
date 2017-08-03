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
import com.dragon.xhttp.itemview.bean.UserBean;
import com.google.gson.Gson;
import com.jingjiu.http.util.http.callback.OnTaskCallback;
import com.jingjiu.http.util.http.core.manager.TaskManager;
import com.jingjiu.http.util.http.response.Response;
import com.jingjiu.http.util.logger.JJLogger;

import static com.dragon.xhttp.web_api.WebApi.LOGIN_URL;
import static com.smart.holder.util.UtilWidget.getView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private AutoCompleteTextView mAccountAct;
    private EditText mPasswordEt;
    private String errorInfo;

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
            TaskManager.getmInstance().initTask().get(LOGIN_URL)
                    .setParams("account", account)
                    .setParams("password", password)
                    .setOnTaskCallback(new OnTaskCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                            Gson gson = new Gson();
                            UserBean userBean = gson.fromJson(response.toString(), UserBean.class);

                            if (userBean.getErrorCode().equals("100")) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "未查询到您的注册信息，请先注册！", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(final Exception ex, final String errorCode) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }).execute();
        } else {
            Toast.makeText(this, "您的 " + errorInfo + " !", Toast.LENGTH_SHORT).show();
        }
    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private boolean checkAccountPassword(final String account, final String password) {
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

