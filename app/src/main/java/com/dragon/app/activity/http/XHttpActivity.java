package com.dragon.app.activity.http;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bean.logger.JJLogger;
import com.bean.xhttp.XHttp;
import com.bean.xhttp.callback.OnXHttpCallback;
import com.bean.xhttp.response.Response;
import com.dragon.R;
import com.dragon.abs.activity.FullscreenActivity;
import com.dragon.api.WebApi;
import com.dragon.constant.Code;

import static com.dragon.constant.Code.USER_NAME;
import static com.dragon.constant.Code.USER_PASSWORD;
import static com.dragon.util.UtilWidget.getView;
import static com.dragon.util.UtilWidget.showErrorInfo;

public class XHttpActivity extends FullscreenActivity {
    private EditText mName;//用于获取要查询的广告id的图片
    private EditText mAge;//用于获取要查询的广告id的图片
    private TextView mTextView1;//数据展示
    private TextView mTextView2;//数据展示
    private ImageView mImageView;//数据展示

    @Override
    protected void afterInit() {

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_xhttp;
    }

    @Override
    protected void initView() {
        mName = getView(this, R.id.name);
        mAge = getView(this, R.id.age);
        mTextView1 = getView(this, R.id.result_data);
        mTextView2 = getView(this, R.id.result_data2);
        mImageView = getView(this, R.id.image);
    }

    @Override
    protected void initData() {

    }


    public void clear(View view) {
        if (mTextView1 != null) {
            mTextView1.setText("");
        }
        if (mTextView2 != null) {
            mTextView2.setText("");
        }
    }

    public void sendGetRequest(View view) {
        JJLogger.logInfo(TAG, "MainActivity.onItemClickedInList :");
        XHttp.getInstance().get(WebApi.LOGIN_URL)
                .setParams(USER_NAME, mName.getText().toString())
                .setParams(USER_PASSWORD, mAge.getText().toString())
                .setParams("tag", Code.TAG_LOGIN)
                .setParams("platform", "mobile_phone")
                .setOnXHttpCallback(new OnXHttpCallback() {
                    @Override
                    public void onSuccess(final Response response) {
                        JJLogger.logInfo(TAG, "MainActivity.onSuccess :" +
                                response.toString());
                        showErrorInfo(XHttpActivity.this, response.toString(), "");
                    }

                    @Override
                    public void onFailure(final Exception ex, final String errorCode) {
                        JJLogger.logInfo(TAG, "MainActivity.onFailure :" + errorCode);
                    }
                });
    }

    public void sendPostRequest(View view) {
        XHttp.getInstance().post(WebApi.LOGIN_URL)
                .setParams(USER_NAME, mName.getText().toString())
                .setParams(USER_PASSWORD, mAge.getText().toString())
                .setParams("tag", Code.TAG_LOGIN)
                .setParams("platform", "mobile_phone")
                .setOnXHttpCallback(new OnXHttpCallback() {
                    @Override
                    public void onSuccess(final Response response) {
                        JJLogger.logInfo(TAG, "MainActivity.onSuccess :" +
                                response.toString());
                        showErrorInfo(XHttpActivity.this, response.toString(), "");

                    }

                    @Override
                    public void onFailure(final Exception ex, final String errorCode) {
                        JJLogger.logInfo(TAG, "MainActivity.onFailure :" + ex.getMessage());
                    }
                });
    }

    public void serialPool(View view) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            final int finalI = i;
            XHttp.getInstance().post(WebApi.LOGIN_URL)
                    .setParams(USER_NAME, mName.getText().toString())
                    .setParams(USER_PASSWORD, mAge.getText().toString())
                    .startSerialThreadPool()
                    .setOnXHttpCallback(new OnXHttpCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                            Log.i("task", "MainActivity.onSuccess 任务" + finalI + "完成:");
                            stringBuilder.append("串行 任务" + finalI + "完成:").append("\n");
                            mTextView2.setText(stringBuilder.toString());
                        }

                        @Override
                        public void onFailure(final Exception ex, final String errorCode) {
                            JJLogger.logInfo(TAG, "MainActivity.onFailure :" + ex.getMessage());
                        }
                    });
        }
    }
    public void getPicture(View view) {
        XHttp.getInstance().post(WebApi.BITMAP_URL)
                .setParams(USER_NAME, mName.getText().toString())
                .setParams(USER_PASSWORD, mAge.getText().toString())
                .startSerialThreadPool()
                .setOnXHttpCallback(new OnXHttpCallback() {
                    @Override
                    public void onSuccess(final Response response) {
                        if (response != null) {
                            mImageView.setImageBitmap(response.toBitmap());
                        }

                    }

                    @Override
                    public void onFailure(final Exception ex, final String errorCode) {
                        mImageView.setImageResource(R.drawable.load_error);
                    }
                });
    }

    public void concurrentPool(View view) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            final int finalI = i;
            XHttp.getInstance().post(WebApi.LOGIN_URL)
                    .setParams(USER_NAME, mName.getText().toString())
                    .setParams(USER_PASSWORD, mAge.getText().toString())
                    .startConcurrenceThreadPool()
                    .setOnXHttpCallback(new OnXHttpCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                            Log.i("task", "MainActivity.onSuccess 任务" + finalI + "完成:");
                            stringBuilder.append("并行 任务" + finalI + "完成:").append("\n");
                            mTextView1.setText(stringBuilder.toString());
                        }

                        @Override
                        public void onFailure(final Exception ex, final String errorCode) {
                            JJLogger.logInfo(TAG, "MainActivity.onFailure :" + ex.getMessage());
                        }
                    });
        }
    }
}
