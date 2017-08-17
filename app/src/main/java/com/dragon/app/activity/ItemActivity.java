package com.dragon.app.activity;

import android.webkit.WebSettings;
import android.webkit.WebView;

import com.dragon.R;
import com.dragon.abs.activity.BaseActivity;

/**
 * author xander on  2017/8/9.
 * function
 */

public class ItemActivity extends BaseActivity {

    private WebView wvXieYi;

    @Override
    protected int initLayout() {
        return R.layout.activity_item;
    }

    @Override
    protected void initView() {
        setTitle("服务条款");
        wvXieYi = (WebView) findViewById(R.id.wv_xieyi);
    }

    @Override
    protected void initData() {
        final String loadUrl = "file:///android_asset/RegisterProtocol.html";
        try {
            // 本地文件处理(如果文件名中有空格需要用+来替代)  
            /************************************************************
             *修改者;  龙之游 @ xu 596928539@qq.com
             *修改时间:2016/12/26 9:29
             *bug:加载本地页面后 跳转到官网  图片上面显示大片灰色
             *修复:  CommonUtil.setWebView(wvXieYi);
             ************************************************************/
            WebSettings webSettings = wvXieYi.getSettings();
            //支持javascript
            webSettings.setJavaScriptEnabled(true);
            // 设置可以支持缩放
            webSettings.setSupportZoom(true);
            // 设置缩放
            webSettings.setBuiltInZoomControls(true);
            //隐藏缩放控件
            webSettings.setDisplayZoomControls(false);
            //扩大比例的缩放
            webSettings.setUseWideViewPort(true);
            //自适应屏幕
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            webSettings.setLoadWithOverviewMode(true);
            wvXieYi.loadUrl(loadUrl);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
