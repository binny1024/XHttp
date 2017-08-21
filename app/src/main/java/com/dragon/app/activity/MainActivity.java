package com.dragon.app.activity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.dragon.R;
import com.dragon.abs.activity.FullscreenActivity;
import com.dragon.api.WebApi;
import com.dragon.app.itemview.bean.BeanMainActivity;
import com.dragon.app.itemview.callback.ViewHolderItemClickedCallback;
import com.dragon.app.itemview.data.Data;
import com.dragon.app.itemview.helper.ViewHolderHelperMain;
import com.dragon.constant.Code;
import com.dragon.widget.BaseTitleBar;
import com.jingjiu.http.core.http.callback.OnTaskCallback;
import com.jingjiu.http.core.http.core.manager.TaskManager;
import com.jingjiu.http.core.http.response.Response;
import com.jingjiu.http.core.logger.JJLogger;
import com.smart.holder.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.dragon.util.UtilWidget.getView;


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
                Toast.makeText(MainActivity.this, "分享", Toast.LENGTH_SHORT).show();
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
            TaskManager.getmInstance().initTask().get(WebApi.LOGIN_URL)
                    .setParams("account", mName.getText().toString())
                    .setParams("password", mAge.getText().toString())
                    .setParams("tag", Code.TAG_LOGIN)
                    .setParams("platform", "mobile_phone")
                    .setOnTaskCallback(new OnTaskCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                            JJLogger.logInfo(TAG, "MainActivity.onSuccess :" +
                                    response.toString());
                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(final Exception ex, final String errorCode) {
                            JJLogger.logInfo(TAG, "MainActivity.onFailure :" + errorCode);
                        }
                    });
        } else if (itemName.equals(getString(R.string.request_post))) {
            TaskManager.getmInstance().initTask().post(WebApi.LOGIN_URL)
                    .setParams("account", mName.getText().toString())
                    .setParams("password", mAge.getText().toString())
                    .setParams("tag", Code.TAG_LOGIN)
                    .setParams("platform", "mobile_phone")
                    .setOnTaskCallback(new OnTaskCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                            JJLogger.logInfo(TAG, "MainActivity.onSuccess :" +
                                    response.toString());
                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();

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
            TaskManager.getmInstance().initTask().post(WebApi.UPLOAD_FILE_URL)
                    .uploadFiles(path)
                    .setHeads("platform","mobile_phone")
                    .setOnTaskCallback(new OnTaskCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                            JJLogger.logInfo(TAG,"MainActivity.onSuccess :"+
                                    response.toString());
                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();

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
                TaskManager.getmInstance().initTask().post(WebApi.LOGIN_URL)
                        .setParams("account", mName.getText().toString())
                        .setParams("password", mAge.getText().toString())
                        .startConcurrenceThreadPool()
                        .setOnTaskCallback(new OnTaskCallback() {
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
                TaskManager.getmInstance().initTask().post(WebApi.LOGIN_URL)
                        .setParams("account", mName.getText().toString())
                        .setParams("password", mAge.getText().toString())
                        .startSerialThreadPool()
                        .setOnTaskCallback(new OnTaskCallback() {
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
