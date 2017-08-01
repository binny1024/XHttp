package com.dragon.xhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragon.library_http.http.callback.OnTaskCallback;
import com.dragon.library_http.http.core.TaskManager;
import com.dragon.library_http.http.response.Response;

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
        TaskManager.getmInstance().initGet("http://sdadadadasd")
                .setTag("bbb")
                .setTimeout(5000)
                .execute(new OnTaskCallback() {
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
                });
    }
    public void requestJson(View view){
        TaskManager.getmInstance().initGet("http://3434343434")
                .setTag("aaa")
                .execute(new OnTaskCallback() {
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
                });
    }
    public void cancel(View view){
        TaskManager.getmInstance().cancel("bbb");
    }
}
