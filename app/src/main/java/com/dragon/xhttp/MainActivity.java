package com.dragon.xhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingjiu.sdk.core.InitSDK;
import com.jingjiu.sdk.core.ad.exception.AdException;
import com.jingjiu.sdk.util.http.callback.OnTaskCallback;
import com.jingjiu.sdk.util.http.core.manager.TaskManager;
import com.jingjiu.sdk.util.http.response.Response;
import com.jingjiu.sdk.util.logger.JJLogger;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            InitSDK.init(this);
            JJLogger.debug(true);
        } catch (AdException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.img_view);
        mTextView = (TextView) findViewById(R.id.text_view);
    }
    public void requestImg(View view){
        TaskManager.getmInstance().initTask().get("http://sdadadadasd")
                .setTag("bbb")
                .setTimeout(5000)
                .setOnTaskCallback(new OnTaskCallback() {
                    @Override
                    public void onSuccess(final Response response) {
                        mImageView.setImageBitmap(response.toBitmap());
                        Log.i("xxx", "onSuccess" );
                    }

                    @Override
                    public void onFailure(Exception ex, String errorCode) {
                        Log.i("xxx", "onFailure  " +ex.toString());
                        Log.i("xxx", "onFailure  " +errorCode);
                    }
                }).execute();
    }
    public void requestJson(View view){
        TaskManager.getmInstance().initTask().get("http://3434343434")
                .setTag("aaa")
                .setOnTaskCallback(new OnTaskCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        Log.i("xxx", "response  " +response.toString());
                        mTextView.setText(response.toString());
                    }

                    @Override
                    public void onFailure(Exception ex, String errorCode) {
                        Log.i("xxx", "onFailure  " +ex.toString());
                        Log.i("xxx", "onFailure  " +errorCode);
                    }
                }).execute();
    }
    public void cancel(View view){
        TaskManager.getmInstance().cancel("bbb");
    }
}
