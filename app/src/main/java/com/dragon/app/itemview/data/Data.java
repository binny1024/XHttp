package com.dragon.app.itemview.data;

import com.dragon.R;
import com.jingjiu.http.core.InitSDK;

/**
 * function
 */

public final class Data {
    public static final String[] ITEMS_MAIN = new String[]{
            InitSDK.getContext().getString(R.string.request_get),
            InitSDK.getContext().getString(R.string.request_post),
            InitSDK.getContext().getString(R.string.current_pool_test),
            InitSDK.getContext().getString(R.string.serail_pool_test),
            InitSDK.getContext().getString(R.string.request_upload_file)
    };
}
