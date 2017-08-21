package com.dragon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dragon.R;
import com.dragon.util.UtilWidget;

/**
 * author xander on  2017/8/21.
 * function
 */

public class BaseTitleBar extends RelativeLayout {
    public interface OnBaseTitleBarButtonListener{
        void onLeftButton();
        void onRightButton();
    }
    private ImageView mLeftBtn;
    private ImageView mRightBtn;
    private TextView mTitle;
    public BaseTitleBar(final Context context) {
        super(context);
    }

    public BaseTitleBar(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i("sss","");
        mLeftBtn = (ImageView) findViewById(R.id.left_btn);
        mRightBtn = (ImageView) findViewById(R.id.right_btn);
        mTitle = (TextView) findViewById(R.id.title);
    }

    public void setBaseTitleBar(final String title, final int leftBtn, final int rightBtn, final OnBaseTitleBarButtonListener listener) {
        mTitle.setText(title);
        mLeftBtn.setImageResource(leftBtn);
        mRightBtn.setImageResource(rightBtn);
        mRightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                UtilWidget.setViewAlphaAnimation(mRightBtn);
                listener.onRightButton();
            }
        });
        mLeftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                UtilWidget.setViewAlphaAnimation(mLeftBtn);
                listener.onLeftButton();
            }
        });
    }
}
