package com.dragon.xhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dragon.library_http.callback.OnHttpTaskCallback;
import com.dragon.library_http.core.HttpTask;
import com.dragon.library_http.response.Response;

import static com.dragon.library_http.core.ConfigHttp.METHOD_GET;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.img_view);
    }
    public void button(View view){
        new HttpTask().type(METHOD_GET)
                .setOnSpiderCallbackk(new OnHttpTaskCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        Log.i("xxx", "onSuccess" +response.toString());
                        mImageView.setImageBitmap(response.toBitmap());
                    }

                    @Override
                    public void onFailure(Exception ex, String errorCode) {
                        Log.i("xxx", "onFailure  " +ex.toString());
                        Log.i("xxx", "onFailure  " +errorCode);
                    }
                }).start("http://sdadadadasd");
    }
}
