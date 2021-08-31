package com.zyd.baiduvoicetestapplication;

import com.zyd.baiduvoicetestapplication.voice.VoiceManager;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VoiceManager.getInstance().initEngine(this);
    }
}
