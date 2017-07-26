package com.dragon.xhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragon.library_http.callback.OnHttpTaskCallback;
import com.dragon.library_http.core.TaskBuilder;
import com.dragon.library_http.response.Response;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.img_view);
        mTextView = (TextView) findViewById(R.id.text_view);
    }
    public void requestImg(View view){
        TaskBuilder.getIstance().get("http://sdadadadasd")
                .tag("bbb")
                .setTimeout(5000)
                .setOnHttpTaskCallback(new OnHttpTaskCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        mImageView.setImageBitmap(response.toBitmap());
                    }

                    @Override
                    public void onFailure(Exception ex, String errorCode) {
                        Log.i("xxx", "onFailure  " +ex.toString());
                        Log.i("xxx", "onFailure  " +errorCode);
                    }
                })
        .build();
    }
    public void requestJson(View view){
        TaskBuilder.getIstance().get("http://3434343434")
                .tag("aaa")
                .setOnHttpTaskCallback(new OnHttpTaskCallback() {
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
                })
        .build();
    }
    public void cancel(View view){
        TaskBuilder.getIstance().cancel("bbb");
    }
}
