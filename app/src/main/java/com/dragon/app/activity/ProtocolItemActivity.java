package com.dragon.app.activity;

import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dragon.R;
import com.dragon.abs.activity.BaseActivity;

/**
 * author xander on  2017/8/9.
 * function
 */

public class ProtocolItemActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    protected void afterInit() {

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_item;
    }

    @Override
    protected void initView() {
//        setTitle("服务条款");
        mWebView = (WebView) findViewById(R.id.wv_xieyi);
    }

    @Override
    protected void initData() {
        final String loadUrl = "http://www.hbpu.edu.cn/";
//        final String loadUrl = "file:///android_asset/RegisterProtocol.html";
        try {
            // 本地文件处理(如果文件名中有空格需要用+来替代)  
            /************************************************************
             *修改者;  龙之游 @ xu 596928539@qq.com
             *修改时间:2016/12/26 9:29
             *bug:加载本地页面后 跳转到官网  图片上面显示大片灰色
             *修复:  CommonUtil.setWebView(mWebView);
             ************************************************************/
            WebSettings webSettings = mWebView.getSettings();
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
            mWebView.loadUrl(loadUrl);
            mWebView.setWebViewClient(new WebViewClient());
            mWebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    setTitle(title);
                }
            });
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
