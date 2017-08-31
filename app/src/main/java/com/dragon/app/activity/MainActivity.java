package com.dragon.app.activity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.bean.xhttp.callback.OnXHttpCallback;
import com.bean.XHttp;
import com.bean.xhttp.response.Response;
import com.bean.logger.JJLogger;
import com.dragon.R;
import com.dragon.abs.activity.FullscreenActivity;
import com.dragon.api.WebApi;
import com.dragon.app.itemview.bean.BeanMainActivity;
import com.dragon.app.itemview.callback.ViewHolderItemClickedCallback;
import com.dragon.app.itemview.data.Data;
import com.dragon.app.itemview.helper.ViewHolderHelperMain;
import com.dragon.constant.Code;
import com.dragon.widget.BaseTitleBar;
import com.smart.holder.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.dragon.constant.Code.USER_NAME;
import static com.dragon.constant.Code.USER_PASSWORD;
import static com.dragon.util.UtilWidget.getView;
import static com.dragon.util.UtilWidget.showErrorInfo;


public class MainActivity extends FullscreenActivity implements ViewHolderItemClickedCallback {

    private final String TAG = "timeout";

    private EditText mName;//用于获取要查询的广告id的图片
    private EditText mAge;//用于获取要查询的广告id的图片
    private TextView mTextView1;//数据展示
    private TextView mTextView2;//数据展示
    private GridView gridView;

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mTitleBar = getView(this,R.id.base_title_bar);
        gridView = getView(this, R.id.main_grid);
        mName = getView(this, R.id.name);
        mAge = getView(this, R.id.age);
        mTextView1 = getView(this, R.id.result_data);
        mTextView2 = getView(this, R.id.result_data2);
    }

    @Override
    protected void initData() {
        mTitleBar.setBaseTitleBar("XHttp框架测试", R.mipmap.back, R.mipmap.share, new BaseTitleBar.OnBaseTitleBarButtonListener() {
            @Override
            public void onLeftButton() {
                finish();
            }

            @Override
            public void onRightButton() {
                showErrorInfo(MainActivity.this,  "分享");
            }
        });
        List<BeanMainActivity> mItemBeanList = new ArrayList<>();
        BeanMainActivity bean;
        for (int i = 0; i < Data.ITEMS_MAIN.length; i++) {
            bean = new BeanMainActivity();
            bean.setTextViewName(Data.ITEMS_MAIN[i]);
            mItemBeanList.add(bean);
        }
        gridView.setAdapter(new CommonAdapter<>(this, mItemBeanList, R.layout.main_item_view, new ViewHolderHelperMain(this)));
    }

    @Override
    public void onItemClickedInList(String itemName) {
        //item的 点击回调
        if (itemName.equals(getString(R.string.request_get))) {
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
                            showErrorInfo(MainActivity.this,  response.toString());
                        }

                        @Override
                        public void onFailure(final Exception ex, final String errorCode) {
                            JJLogger.logInfo(TAG, "MainActivity.onFailure :" + errorCode);
                        }
                    });
        } else if (itemName.equals(getString(R.string.request_post))) {
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
                            showErrorInfo(MainActivity.this,  response.toString());

                        }

                        @Override
                        public void onFailure(final Exception ex, final String errorCode) {
                            JJLogger.logInfo(TAG, "MainActivity.onFailure :" + ex.getMessage());
                        }
                    });
        }
        else if (itemName.equals(getString(R.string.request_upload_file))) {
            String basePtah = Environment.getExternalStorageDirectory().getPath();
            String[] path = new String[]{basePtah+ "/setting.cfg",basePtah+"/Screenshot.png"};
            JJLogger.logInfo(TAG,"MainActivity.onItemClickedInList :"+path);
            XHttp.getInstance().post(WebApi.UPLOAD_FILE_URL)
                    .uploadFiles(path)
                    .setHeads("platform","mobile_phone")
                    .setOnXHttpCallback(new OnXHttpCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                            JJLogger.logInfo(TAG,"MainActivity.onSuccess :"+
                                    response.toString());
                            showErrorInfo(MainActivity.this,  response.toString());

                        }

                        @Override
                        public void onFailure(final Exception ex, final String errorCode) {
                            JJLogger.logInfo(TAG,"MainActivity.onFailure :"+ex.getMessage());
                        }
                    });
        }
        else if (itemName.equals(getString(R.string.current_pool_test))) {
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
                                Log.i("task","MainActivity.onSuccess 任务"+ finalI +"完成:");
                                stringBuilder.append("并行 任务"+ finalI +"完成:").append("\n");
                                mTextView1.setText(stringBuilder.toString());
                            }

                            @Override
                            public void onFailure(final Exception ex, final String errorCode) {
                                JJLogger.logInfo(TAG, "MainActivity.onFailure :" + ex.getMessage());
                            }
                        });
            }
        }
        else if (itemName.equals(getString(R.string.serail_pool_test))) {
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
                                Log.i("task","MainActivity.onSuccess 任务"+ finalI +"完成:");
                                stringBuilder.append("串行 任务"+ finalI +"完成:").append("\n");
                                mTextView2.setText(stringBuilder.toString());
                            }

                            @Override
                            public void onFailure(final Exception ex, final String errorCode) {
                                JJLogger.logInfo(TAG, "MainActivity.onFailure :" + ex.getMessage());
                            }
                        });
            }
        }
    }

    public void clear(View view) {
        if (mTextView1 != null) {
            mTextView1.setText("");
        }
    }

}
