package com.dragon.abs.activity;

import android.support.v7.app.ActionBar;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public abstract class FullscreenActivity extends BaseActivity  {
    protected String mErrorInfo;
    @Override
    protected void initSystemUI() {
        super.initSystemUI();
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
