package com.example.weixindemo.manager;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weixindemo.R;

/**
 * Created by user on 2015/11/30.
 */
public class DialogManager {
    private Dialog mDialog;
    //图标
    private ImageView mIcon;
    private ImageView mVoice;
    private TextView mLable;
    private Context mContext;
    private View mDialgView;

    /**
     * 构造方法
     *
     * @param context
     */
    public DialogManager(Context context) {
        mContext = context;

    }

    /**
     * 显示录音对话框
     */
    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        mDialgView = LayoutInflater.from(mContext).inflate(R.layout.dialog_recorder_layout, null);
        mIcon = (ImageView) mDialgView.findViewById(R.id.id_recorder_dialog_im1);
        mVoice = (ImageView) mDialgView.findViewById(R.id.id_recorder_dialog_im2);
        mLable = (TextView) mDialgView.findViewById(R.id.id_recorder_dialog_tv);
        mDialog.setContentView(mDialgView);
        mDialog.show();
    }

    /**
     * 正在录音
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.recorder);
            mLable.setText("手指上滑，取消发送");
        }
    }


    /**
     * 这时候松开不发送
     */
    public void wantCancle() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.cancel);
            mLable.setText("松开手指，取消发送");
        }

    }

    /**
     * 时间太短不发送
     */
    public void toShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.voice_to_short);
            mLable.setText("录音时间过短");
        }
    }

    /**
     * 对话框消失
     */
    public void dimssDialog() {
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
                mDialog = null;
            }
        }
    }

    /**
     * 更新Voice图标
     *
     * @param level 1--7
     */
    public void updateVoiceLevel(int level) {
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                int resId = mContext.getResources().getIdentifier("v" + level, "drawable", mContext.getPackageName());
                mVoice.setImageResource(resId);
            }

        }
    }
}
