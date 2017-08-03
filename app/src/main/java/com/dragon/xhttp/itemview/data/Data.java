package com.dragon.xhttp.itemview.data;

import com.dragon.xhttp.R;
import com.jingjiu.http.util.InitSDK;

/**
 * function
 */

public final class Data {
    public static final String[] ITEMS_MAIN = new String[]{
            InitSDK.getContext().getString(R.string.request_get),
            InitSDK.getContext().getString(R.string.request_post),

            InitSDK.getContext().getString(R.string.request_upload_file)
    };
}
