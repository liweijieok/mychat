package com.example.weixindemo.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.weixindemo.R;
import com.example.weixindemo.bean.RecorderBean;
import com.example.weixindemo.manager.AudioManager;
import com.example.weixindemo.manager.DialogManager;

/**
 * Created by user on 2015/11/30.
 */
public class AudioRecoderButton extends Button implements AudioManager.AudioStateListener {

    private static int DISTANCE_Y_CANCLE = 0;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDERING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    //录音是否准备好
    boolean isRecordering = false;
    //当前状态
    private int mCurrentState = STATE_NORMAL;
    private AudioManager mAudioManager;
    private DialogManager mDialogManager;

    //三个消息，准备录音完毕，声音量改变，显示录音时间太短
    private final int MEAASGE_AUDIO_PREPARED = 0x11;
    private final int MEAASGE_VOICE_LEVEL_CHANGE = 0x12;
    private final int MEAASGE_DIALOG_DIMSS = 0x13;

    private float mTime;
    //是否触发LongClick
    private boolean isReady = false;

    public interface OnAudioFinishRecorderListener {
        void onFinish(RecorderBean bean);
    }

    private OnAudioFinishRecorderListener listener;

    public void setOnAudioFinishRecorderListener(OnAudioFinishRecorderListener listener) {
        this.listener = listener;
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MEAASGE_AUDIO_PREPARED:
                    //真正显示在AudioPrepare之后
                    mDialogManager.showRecordingDialog();
                    isRecordering = true;
                    //更新Voice
                    new Thread() {
                        @Override
                        public void run() {
                            while (isRecordering) {
                                try {
                                    Thread.sleep(100);
                                    mTime += 0.1f;
                                    mHandler.sendEmptyMessage(MEAASGE_VOICE_LEVEL_CHANGE);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                    break;
                case MEAASGE_VOICE_LEVEL_CHANGE:
                    if (!mAudioManager.isNullOfMediaRecorder()) {
                        mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    }
                    break;
                case MEAASGE_DIALOG_DIMSS:
                    mDialogManager.dimssDialog();
                    break;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDERING);
                break;
            case MotionEvent.ACTION_MOVE:

                //根据x,y，是否想要取消

                if (isRecordering) {
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDERING);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (!isReady) {
                    resetState();
                    return super.onTouchEvent(event);
                }

                if (!isRecordering || mTime < 0.6f) {
                    mDialogManager.toShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MEAASGE_DIALOG_DIMSS, 1300);
                } else if (mCurrentState == STATE_RECORDERING) {
                    //release
                    //callback to activity
                    mAudioManager.release();
                    mDialogManager.dimssDialog();
                    if (listener != null) {
                        RecorderBean bean = new RecorderBean(mTime,mAudioManager.getFilePath());
                        listener.onFinish(bean);
                    }

                } else if (mCurrentState == STATE_WANT_TO_CANCEL) {
                    //cancel
                    //删除文件
                    mDialogManager.dimssDialog();
                    mAudioManager.cancel();
                }
                resetState();
                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 重置状态
     */
    private void resetState() {
        isRecordering = false;
        isReady = false;
        changeState(STATE_NORMAL);
        mTime = 0;
    }


    /**
     * 判断是否越界
     *
     * @param x 坐标x
     * @param y 坐标y
     * @return 是否越界
     */
    private boolean wantToCancel(int x, int y) {
        if (x <= 0 || x >= getWidth()) {
            return true;
        }
        if (y <= -DISTANCE_Y_CANCLE || y >= (DISTANCE_Y_CANCLE + getHeight())) {
            return true;
        }
        return false;
    }

    /**
     * 改变button状态
     *
     * @param state 当前状态
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_nomal);
                    setText(R.string.action_state_nomal);
                    break;
                case STATE_RECORDERING:
                    setBackgroundResource(R.drawable.btn_recorderingl);
                    setText(R.string.action_recorderint);
                    if (isRecordering) {
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorderingl);
                    setText(R.string.action_cancel);
                    if (isRecordering) {
                        mDialogManager.wantCancle();
                    }
                    break;

            }
        }

    }

    //准备完成回调
    @Override
    public void wellPrepare() {
        mHandler.sendEmptyMessage(MEAASGE_AUDIO_PREPARED);

    }

    public AudioRecoderButton(Context context) {
        this(context, null);
    }

    public AudioRecoderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(context);
        String dir = Environment.getExternalStorageDirectory() + "/liweijie_recorder";
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setAudioStateListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }


}
