package com.zyd.baiduvoicetestapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.zyd.baiduvoicetestapplication.voice.VoiceManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //开始识别
        findViewById(R.id.tv_start_recog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceManager.getInstance().startRecognize(null);
            }
        });
        //取消识别
        findViewById(R.id.tv_cancel_recog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceManager.getInstance().cancelRecognize();
            }
        });
        //结束识别
        findViewById(R.id.tv_stop_recog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceManager.getInstance().stopRecognize();
            }
        });

        //开始唤醒
        findViewById(R.id.tv_start_wakeup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceManager.getInstance().startWakeup();
            }
        });
        //结束唤醒
        findViewById(R.id.tv_stop_wakeup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceManager.getInstance().stopWakeup();
            }
        });

        //开始合成（播放）
        findViewById(R.id.tv_start_speak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceManager.getInstance().startSpeak("123456789", null);
            }
        });
        //暂停合成
        findViewById(R.id.tv_pause_speak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceManager.getInstance().pauseSpeak();
            }
        });
        //恢复合成
        findViewById(R.id.tv_resume_speak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceManager.getInstance().resumeSpeak();
            }
        });
        //停止合成
        findViewById(R.id.tv_stop_speak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceManager.getInstance().stopSpeak();
            }
        });

        //边播放边唤醒
        findViewById(R.id.tv_wakeup_when_speak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WakeupWhenSpeakActivity.class));
            }
        });
        //边识别边唤醒
        findViewById(R.id.tv_wakeup_when_recognize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WakeupWhenRecognizeActivity.class));
            }
        });
    }

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

}