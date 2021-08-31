package com.zyd.baiduvoicetestapplication.voice.speak;

import com.zyd.baiduvoicetestapplication.bean.VoiceExtraBean;

public interface OnSpeakListener {

    void onError(String msg, VoiceExtraBean voiceExtraBean);

    void onStart(VoiceExtraBean voiceExtraBean);

    void onEnd(VoiceExtraBean voiceExtraBean);
}