package com.binny.sdk.core.vlayer;

import com.binny.sdk.exception.SDKException;
import com.binny.sdk.core.vlayer.callback.OnJJADLayerListener;

public interface IJJAdLayerView {
    void show();

    void hide();

    void setRoomId(String var1) throws SDKException;

    void setOnJJADLayerListener(OnJJADLayerListener var1);
}
