package com.dragon.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.xhttp.XHttp;
import com.bean.xhttp.callback.OnXHttpCallback;
import com.bean.xhttp.response.Response;
import com.dragon.R;

import static com.dragon.util.UtilFile.createFileWithByte;

public class FileDownloadActivity extends Activity implements View.OnClickListener {
    private EditText fileUrl;
    private Button downloadBtn;
    private Button cancelBtn;
    private TextView downloadProgress;
    private final String HBLG_MUSIC = "http://www.hbpu.edu.cn/ImgUpload/Main1/20159/20159811422888.mp3";

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
            String url = fileUrl.getText().toString();
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(this, "url 出错！", Toast.LENGTH_SHORT).show();
                url = HBLG_MUSIC;
            }
            final String fileName =  url.substring(url.lastIndexOf("/") + 1);
            XHttp.getInstance()
                    .get(url)
                    .setTag("cancle_download")
                    .setTimeout(5000)
                    .setOnXHttpCallback(new OnXHttpCallback() {
                        @Override
                        public void onSuccess(final Response response) {
                            Toast.makeText(FileDownloadActivity.this, "请求成功"+fileName, Toast.LENGTH_SHORT).show();
                            createFileWithByte(response.toBytes(),fileName);
                        }

                        @Override
                        public void onFailure(Exception ex, String errorCode) {
                            Log.i("xxx", "onFailure  " + ex.toString());
                            Log.i("xxx", "onFailure  " + errorCode);
                        }
                    });
        } else if (v == cancelBtn) {
            XHttp.getInstance().cancel("cancle_download");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);
        findViews();
    }
}
