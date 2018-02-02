package com.binny.sdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.binny.core.logger.JJLogger;
import com.binny.sdk.InitAdSDK;
import com.binny.sdk.core.vlayer.JJWSAdLayerView;
import com.binny.sdk.core.vlayer.callback.OnJJADLayerListener;
import com.binny.sdk.exception.SDKException;

import java.io.IOException;

public class VLayerActivity extends Activity {

    private JJWSAdLayerView mAdLayerView;
    private final String TAG = "test";

    private RelativeLayout mVideoViewGroup;
    private LinearLayout.LayoutParams lp;
    private Button mSkipBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vlayer);
        JJLogger.debugWithStackTrace(true);
        try {
            InitAdSDK.init(this);
        } catch (SDKException | IOException e) {
            e.printStackTrace();
        }
        mVideoViewGroup = (RelativeLayout) findViewById(R.id.rl_video_view_group);
        mSkipBtn = (Button) findViewById(R.id.skip);

        mAdLayerView = (JJWSAdLayerView) findViewById(R.id.web_view);
        lp = (LinearLayout.LayoutParams) mVideoViewGroup.getLayoutParams();
        if (Util.isScreenOriatationPortrait(this)) {
            lp.height = Util.getScreenWidth(this);//得到屏幕尺寸
            lp.width = Util.getScreenWidth(this);
        }
        mVideoViewGroup.setLayoutParams(lp);

        mAdLayerView.setOnJJADLayerListener(new OnJJADLayerListener() {
            @Override
            public void onAdClickedListener(final String url) {
                Intent intent = new Intent(VLayerActivity.this, WebActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }

            @Override
            public void onAdConfigError(final String msg) {
                Log.i(TAG, "onAdConfigError: " + msg);
            }
        });
        try {
            mAdLayerView.setRoomId("1");
        } catch (SDKException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "onCreate1: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdLayerView != null) {
            mAdLayerView.release();
        }

    }




    public void hide(View view) {
        mAdLayerView.hide();
    }

    public void show(View view) {
        mAdLayerView.show();
    }

    public void change(View view) {
        try {
            mAdLayerView.setRoomId("10");
            Toast.makeText(this, "切换", Toast.LENGTH_SHORT).show();
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }

    public void change1(View view) {
        try {
            mAdLayerView.setRoomId("1");
            Toast.makeText(this, "切换", Toast.LENGTH_SHORT).show();
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }

    public void release(View view) {
        mAdLayerView.release();
    }
}
