package com.dragon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by zhuyong on 2017/7/20.
 * 自定义VideoView解决全屏问题
 */
public class SplashVideoView extends VideoView {
    public SplashVideoView(Context context) {
        this(context, null);
    }

    public SplashVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
