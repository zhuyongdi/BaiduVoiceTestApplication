package com.zyd.baiduvoicetestapplication.voice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.zyd.baiduvoicetestapplication.bean.VoiceExtraBean;
import com.zyd.baiduvoicetestapplication.voice.recog.OnRecognizeListener;
import com.zyd.baiduvoicetestapplication.voice.recog.RecogResult;
import com.zyd.baiduvoicetestapplication.voice.recog.Volume;
import com.zyd.baiduvoicetestapplication.voice.speak.OnSpeakListener;
import com.zyd.baiduvoicetestapplication.voice.wakeup.OnWakeListener;
import com.zyd.baiduvoicetestapplication.voice.wakeup.WakeUpResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoiceManager {

    private static final String APP_ID = "24772370";
    private static final String API_KEY = "WVzkBSb1iX21B9qrcGKrxOe8";
    private static final String SECRET_KEY = "IjDlYZGtU6YEDyvHuU3YaOxFUFC20PEL";

    private EventManager mRecognizer;
    private EventManager mWakeuper;
    private SpeechSynthesizer mSpeaker;

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Context mContext;

    private List<OnRecognizeListener> mOnRecognizeListenerList;
    private List<OnWakeListener> mOnWakeListenerList;
    private List<OnSpeakListener> mOnSpeakListenerList;

    private static final int INIT_RECOGNIZER = 1;
    private static final int INIT_WAKEUPER = 2;
    private static final int INIT_SPEAKER = 3;

    private boolean mIsRecognizerInitialized;
    private boolean mIsWakeuperInitialized;
    private boolean mIsSpeakerInitialized;

    private VoiceExtraBean mRecognizeVoiceExtraBean;
    private VoiceExtraBean mSpeakVoiceExtraBean;

    private static final String TAG = "VoiceManager";
    private String mRecognizedText;

    public static VoiceManager getInstance() {
        return CH.INST;
    }

    private VoiceManager() {
        initInternal();
    }

    private static class CH {
        @SuppressLint("StaticFieldLeak")
        private static final VoiceManager INST = new VoiceManager();
    }

    public void addOnRecognizeListener(OnRecognizeListener listener) {
        if (listener != null) {
            mOnRecognizeListenerList.add(listener);
        }
    }

    public void removeOnRecognizeListener(OnRecognizeListener listener) {
        if (listener != null) {
            mOnRecognizeListenerList.remove(listener);
        }
    }

    public void addOnWakeupListener(OnWakeListener listener) {
        if (listener != null) {
            mOnWakeListenerList.add(listener);
        }
    }

    public void removeOnWakeupListener(OnWakeListener listener) {
        if (listener != null) {
            mOnWakeListenerList.remove(listener);
        }
    }

    public void addOnSpeakListener(OnSpeakListener listener) {
        if (listener != null) {
            mOnSpeakListenerList.add(listener);
        }
    }

    public void removeOnSpeakListener(OnSpeakListener listener) {
        if (listener != null) {
            mOnSpeakListenerList.remove(listener);
        }
    }

    private void initInternal() {
        mOnRecognizeListenerList = new ArrayList<>();
        mOnWakeListenerList = new ArrayList<>();
        mOnSpeakListenerList = new ArrayList<>();

        mHandlerThread = new HandlerThread("Thread-VoiceManager");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case INIT_RECOGNIZER:
                        initRecognizer();
                        break;
                    case INIT_WAKEUPER:
                        initWakeuper();
                        break;
                    case INIT_SPEAKER:
                        initSpeaker();
                        break;
                }
            }
        };
    }

    public void initEngine(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        if (!mIsRecognizerInitialized) {
            mHandler.sendEmptyMessage(INIT_RECOGNIZER);
        }
        if (!mIsWakeuperInitialized) {
            mHandler.sendEmptyMessage(INIT_WAKEUPER);
        }
        if (!mIsSpeakerInitialized) {
            mHandler.sendEmptyMessage(INIT_SPEAKER);
        }
    }

    private void initRecognizer() {
        mRecognizer = EventManagerFactory.create(mContext, "asr");
        mIsRecognizerInitialized = true;
        mRecognizer.registerListener((type, param, data, offset, length) -> {
            //?????????????????????????????????
            if (SpeechConstant.CALLBACK_EVENT_ASR_LOADED.equals(type)) {
                Log.e(TAG, "recognizer--?????????????????????????????????");
            }
            //?????????????????????????????????
            else if (SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED.equals(type)) {
                Log.e(TAG, "recognizer--?????????????????????????????????");
            }
            //??????????????????
            else if (SpeechConstant.CALLBACK_EVENT_ASR_READY.equals(type)) {
                Log.e(TAG, "recognizer--??????????????????");
                CallbackUtils.notifyOnRecognizeListener_onStart(mOnRecognizeListenerList, mRecognizeVoiceExtraBean);
            }
            //???????????????????????????
            else if (SpeechConstant.CALLBACK_EVENT_ASR_BEGIN.equals(type)) {
                Log.e(TAG, "recognizer--??????????????????");
            }
            //???????????????????????????
            else if (SpeechConstant.CALLBACK_EVENT_ASR_END.equals(type)) {
                Log.e(TAG, "recognizer--??????????????????");
            }
            //????????????
            else if (SpeechConstant.CALLBACK_EVENT_ASR_ERROR.equals(type)) {
                Log.e(TAG, "recognizer--????????????");
                CallbackUtils.notifyOnRecognizeListener_onError(mOnRecognizeListenerList, param, mRecognizeVoiceExtraBean);
            }
            //???????????????
            else if (SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL.equals(type)) {
                RecogResult recogResult = RecogResult.parseJson(param);
                //??????????????????
                if (recogResult.isFinalResult()) {
                    Log.e(TAG, "recognizer--??????????????????????????????" + recogResult);
                    mRecognizedText = recogResult.getResultsRecognition()[0];
                }
                //??????????????????
                else if (recogResult.isPartialResult()) {
                    Log.e(TAG, "recognizer--??????????????????????????????" + recogResult);
                    CallbackUtils.notifyOnRecognizeListener_onResult_ThisCalledMany(mOnRecognizeListenerList, recogResult.getResultsRecognition()[0], mRecognizeVoiceExtraBean);
                }
                //??????????????????
                else if (recogResult.isNluResult()) {
                    Log.e(TAG, "recognizer--??????????????????????????????" + recogResult);
                }
                //??????
                else {
                    Log.e(TAG, "recognizer--??????????????????????????????" + recogResult);
                }
            }
            //????????????
            else if (SpeechConstant.CALLBACK_EVENT_ASR_FINISH.equals(type)) {
                RecogResult recogResult = RecogResult.parseJson(param);
                //????????????????????????
                if (recogResult.hasError()) {
                    Log.e(TAG, "recognizer--???????????????????????????" + recogResult);
                    //?????????????????????
                    if (recogResult.getSubError() == 7001) {
                        CallbackUtils.notifyOnRecognizeListener_onResult_ThisCalledOnce(mOnRecognizeListenerList, "", mRecognizeVoiceExtraBean);
                    }
                    //????????????
                    else {
                        CallbackUtils.notifyOnRecognizeListener_onError(mOnRecognizeListenerList, param, mRecognizeVoiceExtraBean);
                    }
                }
                //????????????????????????
                else {
                    Log.e(TAG, "recognizer--???????????????????????????" + recogResult);
                    CallbackUtils.notifyOnRecognizeListener_onResult_ThisCalledOnce(mOnRecognizeListenerList, mRecognizedText, mRecognizeVoiceExtraBean);
                }
            }
            //?????????????????????
            else if (SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH.equals(type)) {
                Log.e(TAG, "recognizer--?????????????????????");
            }
            //????????????????????????????????????
            else if (SpeechConstant.CALLBACK_EVENT_ASR_EXIT.equals(type)) {
                Log.e(TAG, "recognizer--????????????????????????????????????");
            }
            //????????????
            else if (SpeechConstant.CALLBACK_EVENT_ASR_VOLUME.equals(type)) {
                Volume volume = Volume.parseVolumeJson(param);
                Log.e(TAG, "recognizer--???????????????" + volume);
            }
            //ASR_AUDIO
            else if (SpeechConstant.CALLBACK_EVENT_ASR_AUDIO.equals(type)) {
                if (data.length != length) {
                    Log.e(TAG, "recognizer--ASR_AUDIO???????????????");
                } else {
                    Log.e(TAG, "recognizer--ASR_AUDIO???????????????");
                }
            }
            //??????
            else {
                Log.e(TAG, "recognizer--?????????type=" + type);
            }
        });
        Log.e(TAG, "?????????Recognizer??????");
    }

    private void initWakeuper() {
        mWakeuper = EventManagerFactory.create(mContext, "wp");
        mIsWakeuperInitialized = true;
        mWakeuper.registerListener((type, params, data, offset, length) -> {
            //????????????
            if (SpeechConstant.CALLBACK_EVENT_WAKEUP_STARTED.equals(type)) {
                Log.e(TAG, "wakeuper--????????????");
                CallbackUtils.notifyOnWakeListener_onStart(mOnWakeListenerList);
            }
            //????????????
            else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS.equals(type)) {
                WakeUpResult result = WakeUpResult.parseJson(type, params);
                //????????????
                if (result.hasError()) {
                    Log.e(TAG, "wakeuper--??????????????????????????????" + result);
                }
                //????????????
                else {
                    Log.e(TAG, "wakeuper--??????????????????????????????" + result);
                }
                CallbackUtils.notifyOnWakeListener_onWakeUp(mOnWakeListenerList);
            }
            //????????????
            else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_ERROR.equals(type)) {
                WakeUpResult result = WakeUpResult.parseJson(type, params);
                Log.e(TAG, "wakeuper--???????????????" + result);
                CallbackUtils.notifyOnWakeListener_onError(mOnWakeListenerList, params);
            }
            //????????????
            else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_STOPED.equals(type)) {
                Log.e(TAG, "wakeuper--????????????");
            }
            //????????????
            else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_AUDIO.equals(type)) { // ????????????
                Log.e(TAG, "wakeuper--????????????");
            }
            //??????
            else {
                Log.e(TAG, "wakeuper--?????????type=" + type);
            }
        });
        Log.e(TAG, "?????????Wakeuper??????");
    }

    private void initSpeaker() {
        mSpeaker = SpeechSynthesizer.getInstance();
        mSpeaker.setContext(mContext);
        mSpeaker.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
            @Override
            public void onSynthesizeStart(String s) {
                Log.e(TAG, "Speaker---???????????????s=" + s);
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i, int i1) {
                Log.e(TAG, "Speaker---???????????????s=" + s);
            }

            @Override
            public void onSynthesizeFinish(String s) {
                Log.e(TAG, "Speaker---???????????????s=" + s);
            }

            @Override
            public void onSpeechStart(String s) {
                Log.e(TAG, "Speaker---???????????????s=" + s);
                CallbackUtils.notifyOnSpeakListener_onStart(mOnSpeakListenerList, mSpeakVoiceExtraBean);
            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {
                Log.e(TAG, "Speaker---???????????????s=" + s + "???i=" + i);
            }

            @Override
            public void onSpeechFinish(String s) {
                Log.e(TAG, "Speaker---???????????????s=" + s);
                CallbackUtils.notifyOnSpeakListener_onEnd(mOnSpeakListenerList, mSpeakVoiceExtraBean);
            }

            @Override
            public void onError(String s, SpeechError speechError) {
                Log.e(TAG, "Speaker---???????????????s=" + s + "???error=" + speechError.toString());
                CallbackUtils.notifyOnSpeakListener_onError(mOnSpeakListenerList, speechError.toString(), mSpeakVoiceExtraBean);
            }
        });
        mSpeaker.setAppId(APP_ID);
        mSpeaker.setApiKey(API_KEY, SECRET_KEY);
        mSpeaker.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        mSpeaker.setParam(SpeechSynthesizer.PARAM_VOLUME, "15");
        mSpeaker.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        mSpeaker.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        int code = mSpeaker.initTts(TtsMode.ONLINE);
        if (code == 0) {
            mIsSpeakerInitialized = true;
            Log.e(TAG, "?????????Speaker??????");
        } else {
            Log.e(TAG, "?????????Speaker??????????????????=" + code);
        }
    }

    public void startRecognize(VoiceExtraBean voiceExtraBean) {
        mRecognizeVoiceExtraBean = voiceExtraBean;
        if (!mIsRecognizerInitialized) {
            CallbackUtils.notifyOnRecognizeListener_onError(mOnRecognizeListenerList, "Recognizer????????????", mSpeakVoiceExtraBean);
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("accept-audio-volume", false);
        mRecognizer.send(SpeechConstant.ASR_START, new JSONObject(param).toString(), null, 0, 0);
    }

    public void stopRecognize() {
        if (!mIsRecognizerInitialized) {
            Log.e(TAG, "Recognizer???????????????Recognizer????????????");
            return;
        }
        mRecognizer.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
    }

    public void cancelRecognize() {
        if (!mIsRecognizerInitialized) {
            Log.e(TAG, "Recognizer???????????????Recognizer????????????");
            return;
        }
        mRecognizer.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    public void startWakeup() {
        if (!mIsWakeuperInitialized) {
            CallbackUtils.notifyOnWakeListener_onError(mOnWakeListenerList, "Wakeuper????????????");
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        mWakeuper.send(SpeechConstant.WAKEUP_START, new JSONObject(params).toString(), null, 0, 0);
    }

    public void stopWakeup() {
        if (!mIsWakeuperInitialized) {
            Log.e(TAG, "Wakeuper???????????????Wakeuper????????????");
            return;
        }
        mWakeuper.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
    }

    public void startSpeak(String text, VoiceExtraBean voiceExtraBean) {
        mSpeakVoiceExtraBean = voiceExtraBean;
        if (TextUtils.isEmpty(text)) {
            CallbackUtils.notifyOnSpeakListener_onError(mOnSpeakListenerList, "??????????????????", mSpeakVoiceExtraBean);
            return;
        }
        if (!mIsSpeakerInitialized) {
            CallbackUtils.notifyOnSpeakListener_onError(mOnSpeakListenerList, "Speaker????????????", mSpeakVoiceExtraBean);
            return;
        }
        int code = mSpeaker.speak(text);
        if (code != 0) {
            Log.e(TAG, "Speaker????????????????????????=" + code);
            CallbackUtils.notifyOnSpeakListener_onError(mOnSpeakListenerList, "????????????????????????=" + code, mSpeakVoiceExtraBean);
        }
    }

    public void pauseSpeak() {
        if (!mIsSpeakerInitialized) {
            Log.e(TAG, "Speaker???????????????Speaker????????????");
            return;
        }
        int code = mSpeaker.pause();
        if (code != 0) {
            Log.e(TAG, "Speaker????????????????????????=" + code);
        } else {
            Log.e(TAG, "Speaker????????????");
        }
    }

    public void resumeSpeak() {
        if (!mIsSpeakerInitialized) {
            Log.e(TAG, "Speaker???????????????Speaker????????????");
            return;
        }
        int code = mSpeaker.resume();
        if (code != 0) {
            Log.e(TAG, "Speaker????????????????????????=" + code);
        } else {
            Log.e(TAG, "Speaker????????????");
        }
    }

    public void stopSpeak() {
        if (!mIsSpeakerInitialized) {
            Log.e(TAG, "Speaker???????????????Speaker????????????");
            return;
        }
        int code = mSpeaker.stop();
        if (code != 0) {
            Log.e(TAG, "Speaker????????????????????????=" + code);
        } else {
            Log.e(TAG, "Speaker????????????");
        }
    }
}
