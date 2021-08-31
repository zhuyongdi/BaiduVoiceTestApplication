package com.zyd.baiduvoicetestapplication.voice;

import com.zyd.baiduvoicetestapplication.bean.VoiceExtraBean;
import com.zyd.baiduvoicetestapplication.voice.recog.OnRecognizeListener;
import com.zyd.baiduvoicetestapplication.voice.speak.OnSpeakListener;
import com.zyd.baiduvoicetestapplication.voice.wakeup.OnWakeListener;

import java.util.List;

public class CallbackUtils {

    public static void notifyOnRecognizeListener_onStart(List<OnRecognizeListener> onRecognizeListenerList, VoiceExtraBean extraBean) {
        for (OnRecognizeListener listener : onRecognizeListenerList) {
            if (listener != null) {
                listener.onStart(extraBean);
            }
        }
    }

    public static void notifyOnRecognizeListener_onError(List<OnRecognizeListener> onRecognizeListenerList, String errorMsg, VoiceExtraBean voiceExtraBean) {
        for (OnRecognizeListener listener : onRecognizeListenerList) {
            if (listener != null) {
                listener.onError(errorMsg, voiceExtraBean);
            }
        }
    }

    public static void notifyOnRecognizeListener_onEnd(List<OnRecognizeListener> onRecognizeListenerList, VoiceExtraBean voiceExtraBean) {
        for (OnRecognizeListener listener : onRecognizeListenerList) {
            if (listener != null) {
                listener.onEnd(voiceExtraBean);
            }
        }
    }

    public static void notifyOnRecognizeListener_onStop(List<OnRecognizeListener> onRecognizeListenerList, VoiceExtraBean voiceExtraBean) {
        for (OnRecognizeListener listener : onRecognizeListenerList) {
            if (listener != null) {
                listener.onStop(voiceExtraBean);
            }
        }
    }

    public static void notifyOnRecognizeListener_onResult_ThisCalledOnce(List<OnRecognizeListener> onRecognizeListenerList,
                                                                         String result,
                                                                         VoiceExtraBean voiceExtraBean) {
        for (OnRecognizeListener listener : onRecognizeListenerList) {
            if (listener != null) {
                listener.onResult_ThisCalledOnce(result, voiceExtraBean);
            }
        }
    }

    public static void notifyOnRecognizeListener_onResult_ThisCalledMany(List<OnRecognizeListener> onRecognizeListenerList,
                                                                         String result,
                                                                         VoiceExtraBean voiceExtraBean) {
        for (OnRecognizeListener listener : onRecognizeListenerList) {
            if (listener != null) {
                listener.onResult_ThisCalledMany(result, voiceExtraBean);
            }
        }
    }

    public static void notifyOnWakeListener_onError(List<OnWakeListener> onWakeListenerList, String errorMsg) {
        for (OnWakeListener listener : onWakeListenerList) {
            if (listener != null) {
                listener.onError(errorMsg);
            }
        }
    }

    public static void notifyOnWakeListener_onStart(List<OnWakeListener> onWakeListenerList) {
        for (OnWakeListener listener : onWakeListenerList) {
            if (listener != null) {
                listener.onStart();
            }
        }
    }

    public static void notifyOnWakeListener_onWakeUp(List<OnWakeListener> onWakeListenerList) {
        for (OnWakeListener listener : onWakeListenerList) {
            if (listener != null) {
                listener.onWakeUp();
            }
        }
    }

    public static void notifyOnSpeakListener_onError(List<OnSpeakListener> onSpeakListenerList, String errorMsg, VoiceExtraBean voiceExtraBean) {
        for (OnSpeakListener listener : onSpeakListenerList) {
            if (listener != null) {
                listener.onError(errorMsg, voiceExtraBean);
            }
        }
    }

    public static void notifyOnSpeakListener_onStart(List<OnSpeakListener> onSpeakListenerList, VoiceExtraBean voiceExtraBean) {
        for (OnSpeakListener listener : onSpeakListenerList) {
            if (listener != null) {
                listener.onStart(voiceExtraBean);
            }
        }
    }

    public static void notifyOnSpeakListener_onEnd(List<OnSpeakListener> onSpeakListenerList, VoiceExtraBean voiceExtraBean) {
        for (OnSpeakListener listener : onSpeakListenerList) {
            if (listener != null) {
                listener.onEnd(voiceExtraBean);
            }
        }
    }

}
