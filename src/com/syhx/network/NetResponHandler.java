package com.syhx.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by wufeifei on 2016/11/18.
 */

public class NetResponHandler extends Handler {
    public NetResponHandler() {
        super(Looper.getMainLooper());
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            default:
                break;
        }
    }
}
