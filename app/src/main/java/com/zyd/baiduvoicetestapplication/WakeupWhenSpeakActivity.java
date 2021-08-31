package com.zyd.baiduvoicetestapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zyd.baiduvoicetestapplication.voice.VoiceManager;

public class WakeupWhenSpeakActivity extends AppCompatActivity {

    @Override
    protected void onPause() {
        super.onPause();
        //停止播放
        VoiceManager.getInstance().stopSpeak();
        //停止识别
        VoiceManager.getInstance().stopRecognize();
        //停止唤醒
        VoiceManager.getInstance().stopWakeup();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VoiceManager.getInstance().startWakeup();
        VoiceManager.getInstance().startSpeak("123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789", null);
    }

}
