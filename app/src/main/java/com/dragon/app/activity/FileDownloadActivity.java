package com.dragon.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.binny.core.xhttp.XHttp;
import com.binny.core.xhttp.callback.OnXHttpCallback;
import com.binny.core.xhttp.response.Response;
import com.dragon.R;
import com.dragon.util.PermisionUtils;

import static com.dragon.util.UtilFile.createFileWithByte;

public class FileDownloadActivity extends AppCompatActivity implements View.OnClickListener, OnXHttpCallback {
    private EditText fileUrl;
    private Button downloadBtn;
    private Button cancelBtn;
    private TextView downloadProgress;
    private final String HBLG_MUSIC = "http://www.hbpu.edu.cn/ImgUpload/Main1/20159/20159811422888.mp3";
    private String mUrl;
    private String fileName;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-10-19 16:09:04 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        fileUrl = (EditText) findViewById(R.id.file_url);
        downloadBtn = (Button) findViewById(R.id.download_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        downloadProgress = (TextView) findViewById(R.id.download_progress);

        downloadBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-10-19 16:09:04 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {

        if (v == downloadBtn) {
            // 检查权限
            if (ContextCompat.checkSelfPermission(FileDownloadActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // 申请拨打电话的权限
                ActivityCompat.requestPermissions(FileDownloadActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
               call();
            }
            mUrl = "http://192.168.200.162:8080/HttpHelperWeb/"+fileUrl.getText().toString();
            if (TextUtils.isEmpty(mUrl)) {
                Toast.makeText(this, "url 出错！", Toast.LENGTH_SHORT).show();
                mUrl = HBLG_MUSIC;
            }
            fileName = mUrl.substring(mUrl.lastIndexOf("/") + 1);
            XHttp.getInstance()
                    .get(mUrl)
                    .setTag("cancle_download")
                    .setTimeout(5000)
                    .setOnXHttpCallback(this);
        } else if (v == cancelBtn) {
            PermisionUtils.verifyStoragePermissions(this);
        }
    }
    /**
     * 拥有权限时拨打电话
     */
    private void call() {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:10086"));
            startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    /**
     * 申请权限的回调
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "你拒绝了这个权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);
        findViews();
    }

    @Override
    public void onSuccess(Response response) {
        createFileWithByte(response.toBytes(), fileName);
    }

    @Override
    public void onFailure(Exception ex, String errorCode) {
        Log.i("xxx", "onFailure  " + ex.toString());
        Log.i("xxx", "onFailure  " + errorCode);
    }
}
