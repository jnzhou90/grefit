package grefitcom.grefit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import grefitcom.grefit.utils.AudioFileFunc;
import grefitcom.grefit.utils.AudioRecordFunc;
import grefitcom.grefit.utils.ErrorCode;
import grefitcom.grefit.utils.WatsonSTT;


public class MainActivity extends Activity {


    private TextView textViewWord;
    private TextView textViewEg;
    private ImageButton imgBtnRecord;
    private ImageView mRecord_1;
    private ImageView mRecord_2;
    private ImageView mRecord_3;

    private Animation mRecord_1_Animation;
    private Animation mRecord_2_Animation;
    private Animation mRecord_3_Animation;
    private static final int RECORD_ING = 1; // 正在录音
    private static final int RECORD_ED = 2; // 完成录音


    private static final float MIN_TIME = 1;// 最短录音时间
    private float recordTime = 0.0f; // 录音时长


    private int mRecord_State = 0; // 录音的状态

    private AudioRecordFunc mRecordMav;
    private Thread mRecordThread;

    private String wavpath ="";
    private   String sttStr ="";
    private Thread  sttThread;
    private   Handler sttHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setListener();

    }


    private void initView() {
        textViewWord = (TextView) findViewById(R.id.textViewWord);
        textViewEg = (TextView) findViewById(R.id.textViewWord);
        mRecord_1 = (ImageView) findViewById(R.id.voice_recordinglight_1);
        mRecord_2 = (ImageView) findViewById(R.id.voice_recordinglight_2);
        mRecord_3 = (ImageView) findViewById(R.id.voice_recordinglight_3);

        imgBtnRecord = (ImageButton)findViewById(R.id.imgbtn_recorder);
    }


    private void setListener() {

        imgBtnRecord.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    //开始录音
                    case MotionEvent.ACTION_DOWN:
                        if (mRecord_State != RECORD_ING) {
                            // imgBtnRecord.setBackgroundResource(R.mipmap.recording);
                            //开始动画效果
                            startRecordLightAnimation();
                            //修改录音状态
                            mRecord_State = RECORD_ING;
                            //录音开始了
                            startRecordMav();
                            recordTimethread();
                        }
                        break;
                    //停止录音
                    case MotionEvent.ACTION_UP:
                        // 停止动画效果
                        stopRecordLightAnimation();
                        // 修改录音状态
                        mRecord_State = RECORD_ED;
                        stopRecordMav();


                        if (recordTime < MIN_TIME) {
                            Toast.makeText(getApplicationContext(), "时间太短了!", Toast.LENGTH_LONG).show();
                            recordTime = 0;
                        } else {

                            wavpath = AudioFileFunc.getWavFilePath();
                            //调用waton 的ＳＴＴ线程
                            runsttThread();
                            sttHandler = new Handler(){

                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    switch (msg.what){
                                        case 10001:
                                            textViewWord.setText(sttStr);
//                                            Uri uri = Uri.parse("http://app.chinaopen.com.cn/ghs/");
//                                            Intent intent =new  Intent(Intent.ACTION_VIEW,uri);
//                                            startActivity(intent);

                                            Intent intent = new Intent();
                                            intent.setClass(MainActivity.this,GhsWebActivity.class);
                                            startActivity(intent);

                                            break;
                                    }
                                }
                            };
                        }
                        break;
                }
                return false;
            }
        });

    }

    /**
     * 用来控制动画效果
     */
    Handler mRecordLightHandler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (mRecord_State == RECORD_ING) {
                        mRecord_1.setVisibility(View.VISIBLE);
                        mRecord_1_Animation = AnimationUtils.loadAnimation(
                                MainActivity.this, R.anim.voice_anim);
                        mRecord_1.setAnimation(mRecord_1_Animation);
                        mRecord_1_Animation.startNow();
                    }
                    break;
                case 1:
                    if (mRecord_State == RECORD_ING) {
                        mRecord_2.setVisibility(View.VISIBLE);
                        mRecord_2_Animation = AnimationUtils.loadAnimation(
                                MainActivity.this, R.anim.voice_anim);
                        mRecord_2.setAnimation(mRecord_2_Animation);
                        mRecord_2_Animation.startNow();
                    }
                    break;
                case 2:
                    if (mRecord_State == RECORD_ING) {
                        mRecord_3.setVisibility(View.VISIBLE);
                        mRecord_3_Animation = AnimationUtils.loadAnimation(
                                MainActivity.this, R.anim.voice_anim);
                        mRecord_3.setAnimation(mRecord_3_Animation);
                        mRecord_3_Animation.startNow();
                    }
                    break;
                case 3:
                    if (mRecord_1_Animation != null) {
                        mRecord_1.clearAnimation();
                        mRecord_1_Animation.cancel();
                        mRecord_1.setVisibility(View.GONE);

                    }
                    if (mRecord_2_Animation != null) {
                        mRecord_2.clearAnimation();
                        mRecord_2_Animation.cancel();
                        mRecord_2.setVisibility(View.GONE);
                    }
                    if (mRecord_3_Animation != null) {
                        mRecord_3.clearAnimation();
                        mRecord_3_Animation.cancel();
                        mRecord_3.setVisibility(View.GONE);
                    }
                    break;



            }
        }
    };
    /**
     * 开始动画
     */
    private void startRecordLightAnimation() {
        mRecordLightHandler.sendEmptyMessageDelayed(0, 0);
        mRecordLightHandler.sendEmptyMessageDelayed(1, 1000);
        mRecordLightHandler.sendEmptyMessageDelayed(2, 2000);
    }

    /**
     * 停止动画效果
     */
    private void stopRecordLightAnimation() {
        mRecordLightHandler.sendEmptyMessage(3);
    }


    /**
     * 开始录音
     */
    private void startRecordMav() {
        int mResult = -1;
        mRecordMav = AudioRecordFunc.getInstance();
        mResult = mRecordMav.startRecordAndFile();
        if (mResult == ErrorCode.SUCCESS) {
            Log.i("record", "Record sucess!");
        } else {
            Log.i("record", "Record failure!");
        }

    }

    /**
     * 停止录音
     */
    private void stopRecordMav() {
        mRecordMav.stopRecordAndFile();
    }
    // 录音计时线程
    void recordTimethread() {
        mRecordThread = new Thread(recordThread);
        mRecordThread.start();
    }

    private Runnable recordThread = new Runnable() {
        @Override
        public void run() {
            recordTime = 0.0f;
            while (mRecord_State == RECORD_ING) {

                try {
                    Thread.sleep(150);
                    recordTime += 0.15;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    };

    //
    //运行watson STT
    void runsttThread() {
        sttThread = new Thread(runsttRunable);
        sttThread.start();
    }

    private Runnable runsttRunable = new Runnable() {
        @Override
        public void run() {
            Message m1 = new Message();
            m1.what = 10001;
            sttStr = WatsonSTT.speechToText(wavpath).replace(" ","");
            sttHandler.sendMessage(m1);
        }
    };
}
