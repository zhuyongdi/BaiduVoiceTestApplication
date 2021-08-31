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
            //离线命令词资源加载成功
            if (SpeechConstant.CALLBACK_EVENT_ASR_LOADED.equals(type)) {
                Log.e(TAG, "recognizer--离线命令词资源加载成功");
            }
            //离线命令词资源释放成功
            else if (SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED.equals(type)) {
                Log.e(TAG, "recognizer--离线命令词资源释放成功");
            }
            //引擎准备完毕
            else if (SpeechConstant.CALLBACK_EVENT_ASR_READY.equals(type)) {
                Log.e(TAG, "recognizer--引擎准备完毕");
                CallbackUtils.notifyOnRecognizeListener_onStart(mOnRecognizeListenerList, mRecognizeVoiceExtraBean);
            }
            //检查到用户开始说话
            else if (SpeechConstant.CALLBACK_EVENT_ASR_BEGIN.equals(type)) {
                Log.e(TAG, "recognizer--用户开始说话");
            }
            //检测到用户停止说话
            else if (SpeechConstant.CALLBACK_EVENT_ASR_END.equals(type)) {
                Log.e(TAG, "recognizer--用户停止说话");
            }
            //识别出错
            else if (SpeechConstant.CALLBACK_EVENT_ASR_ERROR.equals(type)) {
                Log.e(TAG, "recognizer--识别出错");
                CallbackUtils.notifyOnRecognizeListener_onError(mOnRecognizeListenerList, param, mRecognizeVoiceExtraBean);
            }
            //识别到结果
            else if (SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL.equals(type)) {
                RecogResult recogResult = RecogResult.parseJson(param);
                //最终识别结果
                if (recogResult.isFinalResult()) {
                    Log.e(TAG, "recognizer--识别到结果（最终）：" + recogResult);
                    mRecognizedText = recogResult.getResultsRecognition()[0];
                }
                //临时识别结果
                else if (recogResult.isPartialResult()) {
                    Log.e(TAG, "recognizer--识别到结果（临时）：" + recogResult);
                    CallbackUtils.notifyOnRecognizeListener_onResult_ThisCalledMany(mOnRecognizeListenerList, recogResult.getResultsRecognition()[0], mRecognizeVoiceExtraBean);
                }
                //语义理解结果
                else if (recogResult.isNluResult()) {
                    Log.e(TAG, "recognizer--识别到结果（语义）：" + recogResult);
                }
                //其他
                else {
                    Log.e(TAG, "recognizer--识别到结果（其他）：" + recogResult);
                }
            }
            //识别结束
            else if (SpeechConstant.CALLBACK_EVENT_ASR_FINISH.equals(type)) {
                RecogResult recogResult = RecogResult.parseJson(param);
                //识别结束（出错）
                if (recogResult.hasError()) {
                    Log.e(TAG, "recognizer--识别结束（出错）：" + recogResult);
                    //没有识别到内容
                    if (recogResult.getSubError() == 7001) {
                        CallbackUtils.notifyOnRecognizeListener_onResult_ThisCalledOnce(mOnRecognizeListenerList, "", mRecognizeVoiceExtraBean);
                    }
                    //其他异常
                    else {
                        CallbackUtils.notifyOnRecognizeListener_onError(mOnRecognizeListenerList, param, mRecognizeVoiceExtraBean);
                    }
                }
                //识别结束（正常）
                else {
                    Log.e(TAG, "recognizer--识别结束（正常）：" + recogResult);
                    CallbackUtils.notifyOnRecognizeListener_onResult_ThisCalledOnce(mOnRecognizeListenerList, mRecognizedText, mRecognizeVoiceExtraBean);
                }
            }
            //长语音识别结束
            else if (SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH.equals(type)) {
                Log.e(TAG, "recognizer--长语音识别结束");
            }
            //引擎完成整个识别，空闲中
            else if (SpeechConstant.CALLBACK_EVENT_ASR_EXIT.equals(type)) {
                Log.e(TAG, "recognizer--引擎完成整个识别，空闲中");
            }
            //音量回调
            else if (SpeechConstant.CALLBACK_EVENT_ASR_VOLUME.equals(type)) {
                Volume volume = Volume.parseVolumeJson(param);
                Log.e(TAG, "recognizer--音量回调：" + volume);
            }
            //ASR_AUDIO
            else if (SpeechConstant.CALLBACK_EVENT_ASR_AUDIO.equals(type)) {
                if (data.length != length) {
                    Log.e(TAG, "recognizer--ASR_AUDIO：长度异常");
                } else {
                    Log.e(TAG, "recognizer--ASR_AUDIO：结果正常");
                }
            }
            //其他
            else {
                Log.e(TAG, "recognizer--其他，type=" + type);
            }
        });
        Log.e(TAG, "初始化Recognizer成功");
    }

    private void initWakeuper() {
        mWakeuper = EventManagerFactory.create(mContext, "wp");
        mIsWakeuperInitialized = true;
        mWakeuper.registerListener((type, params, data, offset, length) -> {
            //唤醒开始
            if (SpeechConstant.CALLBACK_EVENT_WAKEUP_STARTED.equals(type)) {
                Log.e(TAG, "wakeuper--开始唤醒");
                CallbackUtils.notifyOnWakeListener_onStart(mOnWakeListenerList);
            }
            //唤醒成功
            else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS.equals(type)) {
                WakeUpResult result = WakeUpResult.parseJson(type, params);
                //唤醒异常
                if (result.hasError()) {
                    Log.e(TAG, "wakeuper--唤醒成功（有异常），" + result);
                }
                //唤醒正常
                else {
                    Log.e(TAG, "wakeuper--唤醒成功（无异常），" + result);
                }
                CallbackUtils.notifyOnWakeListener_onWakeUp(mOnWakeListenerList);
            }
            //唤醒出错
            else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_ERROR.equals(type)) {
                WakeUpResult result = WakeUpResult.parseJson(type, params);
                Log.e(TAG, "wakeuper--唤醒出错，" + result);
                CallbackUtils.notifyOnWakeListener_onError(mOnWakeListenerList, params);
            }
            //唤醒关闭
            else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_STOPED.equals(type)) {
                Log.e(TAG, "wakeuper--唤醒关闭");
            }
            //音频回调
            else if (SpeechConstant.CALLBACK_EVENT_WAKEUP_AUDIO.equals(type)) { // 音频回调
                Log.e(TAG, "wakeuper--音频回调");
            }
            //其他
            else {
                Log.e(TAG, "wakeuper--其他，type=" + type);
            }
        });
        Log.e(TAG, "初始化Wakeuper成功");
    }

    private void initSpeaker() {
        mSpeaker = SpeechSynthesizer.getInstance();
        mSpeaker.setContext(mContext);
        mSpeaker.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
            @Override
            public void onSynthesizeStart(String s) {
                Log.e(TAG, "Speaker---开始合成，s=" + s);
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i, int i1) {
                Log.e(TAG, "Speaker---音频写入，s=" + s);
            }

            @Override
            public void onSynthesizeFinish(String s) {
                Log.e(TAG, "Speaker---合成结束，s=" + s);
            }

            @Override
            public void onSpeechStart(String s) {
                Log.e(TAG, "Speaker---开始播放，s=" + s);
                CallbackUtils.notifyOnSpeakListener_onStart(mOnSpeakListenerList, mSpeakVoiceExtraBean);
            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {
                Log.e(TAG, "Speaker---播放进度，s=" + s + "，i=" + i);
            }

            @Override
            public void onSpeechFinish(String s) {
                Log.e(TAG, "Speaker---结束播放，s=" + s);
                CallbackUtils.notifyOnSpeakListener_onEnd(mOnSpeakListenerList, mSpeakVoiceExtraBean);
            }

            @Override
            public void onError(String s, SpeechError speechError) {
                Log.e(TAG, "Speaker---播放出错，s=" + s + "，error=" + speechError.toString());
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
            Log.e(TAG, "初始化Speaker成功");
        } else {
            Log.e(TAG, "初始化Speaker失败，错误码=" + code);
        }
    }

    public void startRecognize(VoiceExtraBean voiceExtraBean) {
        mRecognizeVoiceExtraBean = voiceExtraBean;
        if (!mIsRecognizerInitialized) {
            CallbackUtils.notifyOnRecognizeListener_onError(mOnRecognizeListenerList, "Recognizer未初始化", mSpeakVoiceExtraBean);
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("accept-audio-volume", false);
        mRecognizer.send(SpeechConstant.ASR_START, new JSONObject(param).toString(), null, 0, 0);
    }

    public void stopRecognize() {
        if (!mIsRecognizerInitialized) {
            Log.e(TAG, "Recognizer取消成功，Recognizer未初始化");
            return;
        }
        mRecognizer.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
    }

    public void cancelRecognize() {
        if (!mIsRecognizerInitialized) {
            Log.e(TAG, "Recognizer取消成功，Recognizer未初始化");
            return;
        }
        mRecognizer.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    public void startWakeup() {
        if (!mIsWakeuperInitialized) {
            CallbackUtils.notifyOnWakeListener_onError(mOnWakeListenerList, "Wakeuper未初始化");
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        mWakeuper.send(SpeechConstant.WAKEUP_START, new JSONObject(params).toString(), null, 0, 0);
    }

    public void stopWakeup() {
        if (!mIsWakeuperInitialized) {
            Log.e(TAG, "Wakeuper停止成功，Wakeuper未初始化");
            return;
        }
        mWakeuper.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
    }

    public void startSpeak(String text, VoiceExtraBean voiceExtraBean) {
        mSpeakVoiceExtraBean = voiceExtraBean;
        if (TextUtils.isEmpty(text)) {
            CallbackUtils.notifyOnSpeakListener_onError(mOnSpeakListenerList, "播放内容为空", mSpeakVoiceExtraBean);
            return;
        }
        if (!mIsSpeakerInitialized) {
            CallbackUtils.notifyOnSpeakListener_onError(mOnSpeakListenerList, "Speaker未初始化", mSpeakVoiceExtraBean);
            return;
        }
        int code = mSpeaker.speak(text);
        if (code != 0) {
            Log.e(TAG, "Speaker播放失败，错误码=" + code);
            CallbackUtils.notifyOnSpeakListener_onError(mOnSpeakListenerList, "播放失败，错误码=" + code, mSpeakVoiceExtraBean);
        }
    }

    public void pauseSpeak() {
        if (!mIsSpeakerInitialized) {
            Log.e(TAG, "Speaker暂停失败，Speaker未初始化");
            return;
        }
        int code = mSpeaker.pause();
        if (code != 0) {
            Log.e(TAG, "Speaker暂停失败，错误码=" + code);
        } else {
            Log.e(TAG, "Speaker暂停成功");
        }
    }

    public void resumeSpeak() {
        if (!mIsSpeakerInitialized) {
            Log.e(TAG, "Speaker恢复失败，Speaker未初始化");
            return;
        }
        int code = mSpeaker.resume();
        if (code != 0) {
            Log.e(TAG, "Speaker恢复失败，错误码=" + code);
        } else {
            Log.e(TAG, "Speaker恢复成功");
        }
    }

    public void stopSpeak() {
        if (!mIsSpeakerInitialized) {
            Log.e(TAG, "Speaker停止失败，Speaker未初始化");
            return;
        }
        int code = mSpeaker.stop();
        if (code != 0) {
            Log.e(TAG, "Speaker停止失败，错误码=" + code);
        } else {
            Log.e(TAG, "Speaker停止成功");
        }
    }
}
