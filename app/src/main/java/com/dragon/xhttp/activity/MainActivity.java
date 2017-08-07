package com.dragon.xhttp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.dragon.xhttp.R;
import com.dragon.xhttp.itemview.bean.BeanMainActivity;
import com.dragon.xhttp.itemview.callback.ViewHolderItemClickedCallback;
import com.dragon.xhttp.itemview.data.Data;
import com.dragon.xhttp.itemview.helper.ViewHolderHelperMain;
import com.jingjiu.http.core.http.callback.OnTaskCallback;
import com.jingjiu.http.core.http.core.manager.TaskManager;
import com.jingjiu.http.core.http.response.Response;
import com.jingjiu.http.core.logger.JJLogger;
import com.smart.holder.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.dragon.xhttp.UtilWidget.getView;
import static com.dragon.xhttp.api.WebApi.LOGIN_URL;
import static com.dragon.xhttp.api.WebApi.UPLOAD_FILE_URL;


public class MainActivity extends Activity implements ViewHolderItemClickedCallback {

    private final String TAG = "timeout";

    private EditText mName;//用于获取要查询的广告id的图片
    private EditText mAge;//用于获取要查询的广告id的图片
    private TextView mTextView;//数据展示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GridView gridView = getView(this, R.id.main_grid);
        mName = getView(this, R.id.name);
        mAge = getView(this, R.id.age);
        mTextView = getView(this, R.id.result_data);

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
            TaskManager.getmInstance().initTask().get(LOGIN_URL)
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
            TaskManager.getmInstance().initTask().post(LOGIN_URL)
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
            String path = Environment.getExternalStorageDirectory().getPath() + "/crash_lzhy_moneyhll.txt";
            JJLogger.logInfo(TAG, "MainActivity.onItemClickedInList :" + path);
            TaskManager.getmInstance().initTask().post(UPLOAD_FILE_URL)
                    .uploadFile(path)
                    .setHeads("platform", "mobile_phone")
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
    }

    public void clear(View view) {
        if (mTextView != null) {
            mTextView.setText("");
        }
    }

}
