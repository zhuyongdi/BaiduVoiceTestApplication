package com.zyd.baiduvoicetestapplication.voice.recog;

import com.zyd.baiduvoicetestapplication.bean.VoiceExtraBean;

public interface OnRecognizeListener {

    void onError(String msg, VoiceExtraBean voiceExtraBean);

    void onStart(VoiceExtraBean voiceExtraBean);

    void onEnd(VoiceExtraBean voiceExtraBean);

    void onStop(VoiceExtraBean voiceExtraBean);

    void onResult_ThisCalledOnce(String result, VoiceExtraBean voiceExtraBean);

    void onResult_ThisCalledMany(String result, VoiceExtraBean voiceExtraBean);

}