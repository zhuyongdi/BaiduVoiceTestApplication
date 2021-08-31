package com.zyd.baiduvoicetestapplication.voice.wakeup;

public interface OnWakeListener {
    void onError(String msg);

    void onStart();

    void onWakeUp();
}