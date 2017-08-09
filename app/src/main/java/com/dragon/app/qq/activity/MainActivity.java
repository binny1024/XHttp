package com.dragon.app.qq.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.dragon.R;
import com.dragon.util.UtilWidget;
import com.dragon.api.WebApi;
import com.dragon.app.qq.itemview.bean.BeanMainActivity;
import com.dragon.app.qq.itemview.callback.ViewHolderItemClickedCallback;
import com.dragon.app.qq.itemview.data.Data;
import com.dragon.app.qq.itemview.helper.ViewHolderHelperMain;
import com.jingjiu.http.core.http.callback.OnTaskCallback;
import com.jingjiu.http.core.http.core.manager.TaskManager;
import com.jingjiu.http.core.http.response.Response;
import com.jingjiu.http.core.logger.JJLogger;
import com.smart.holder.CommonAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements ViewHolderItemClickedCallback {

    private final String TAG = "timeout";

    private EditText mName;//用于获取要查询的广告id的图片
    private EditText mAge;//用于获取要查询的广告id的图片
    private TextView mTextView;//数据展示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("QQ");
        GridView gridView = UtilWidget.getView(this, R.id.main_grid);
        mName = UtilWidget.getView(this, R.id.name);
        mAge = UtilWidget.getView(this, R.id.age);
        mTextView = UtilWidget.getView(this, R.id.result_data);

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
        mTextView.setText("");
        if (itemName.equals(getString(R.string.request_get))) {
            JJLogger.logInfo(TAG, "MainActivity.onItemClickedInList :");
            TaskManager.getmInstance().initTask().get(WebApi.LOGIN_URL)
                    .setParams("account", mName.getText().toString())
                    .setParams("password", mAge.getText().toString())
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
        } else if (itemName.equals(getString(R.string.request_upload_file))) {
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
    }

    public void clear(View view) {
        if (mTextView != null) {
            mTextView.setText("");
        }
    }

}
