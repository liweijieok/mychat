package com.example.weixindemo.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.weixindemo.R;
import com.example.weixindemo.adapter.ChatMsgViewAdapter;
import com.example.weixindemo.bean.ChatMsgEntity;
import com.example.weixindemo.bean.MsgNomalText;
import com.example.weixindemo.bean.RecorderBean;
import com.example.weixindemo.listener.EdittextWatcher;
import com.example.weixindemo.manager.AudioManager;
import com.example.weixindemo.manager.MediaManager;
import com.example.weixindemo.utils.AtomIntegerUtil;
import com.example.weixindemo.utils.InputMethodUtil;
import com.example.weixindemo.view.AudioRecoderButton;
import com.example.weixindemo.view.FaceRelativeLayout;
import com.example.weixindemo.view.PopupWindows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.logging.Handler;

public class MainActivity extends Activity implements OnClickListener, AudioRecoderButton.OnAudioFinishRecorderListener, View.OnLayoutChangeListener {
    private Button mBtnSend;
    private EditText mEditTextContent;
    private ListView mListView;
    private RelativeLayout mBottom;
    private AudioRecoderButton mBtnRcd;
    private ChatMsgViewAdapter mAdapter;
    private List<ChatMsgEntity> mDateArrays = new ArrayList<ChatMsgEntity>();

    public static final int SHOW_ALL_PICTURE = AtomIntegerUtil.getDiffIntCode();//从相册查找图片
    public static final int SHOW_ALL_PICTURE_FROM = AtomIntegerUtil.getDiffIntCode();//从相册查找图片

    public static final int TAKE_PICTURE = AtomIntegerUtil.getDiffIntCode();
    public static final int SHOW_CAMER_PICTURE = AtomIntegerUtil.getDiffIntCode();//从相册查找图片
    public static final int SHOW_CAMER_PICTURE_FROM = AtomIntegerUtil.getDiffIntCode();//从相册查找图片

    public static final int MSG_HIDE_INPUT = AtomIntegerUtil.getDiffIntCode();

    //语音
    private ImageView chatting_mode_btn;
    //照片
    private ImageView btn_photo;

    private String[] msgArray = new String[]{"PHP是最壕的语言", "nmb", "哈哈...", "我大java不服", "口亨口亨",
            "不然做过一场试试", "不要..雅蠛蝶", "卧槽，你个bitch", "手动再见", "):", "最近在学JNI啊", "你开心就好", "少年不努力，你州是傻逼", "少年不哭，站起来撸，123，嗨起来"};
    private String[] dataArray = new String[]{"2015-05-08 13:01", "2015-05-08 13:02", "2015-05-08 13:03", "2015-05-08 13:03", "2015-05-08 13:04",
            "2015-05-08 13:04", "2015-05-08 13:05", "2015-05-08 13:05", "2015-05-08 13:06", "2015-05-08 13:06",
            "2015-05-08 13:07", "2015-05-08 13:07", "2015-05-08 13:07", "2015-05-08 13:08"};
    private final static int COUNT = 14;
    private int heightDifference;

    private boolean btn_voice = false;
    private PopupWindows popupWindows;

    private int keyHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        //设置启动不弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        initEvent();
        initData();
    }

    private void initEvent() {
        mBtnSend.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        mEditTextContent.setOnClickListener(this);
        chatting_mode_btn.setOnClickListener(this);
        //监听录音
        mBtnRcd.setOnAudioFinishRecorderListener(this);
        mEditTextContent.addTextChangedListener(
                new EdittextWatcher(mEditTextContent, btn_photo, mBtnSend));
        mListView.setItemsCanFocus(true);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodUtil.hideInput(MainActivity.this, mEditTextContent);
                ((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView();
                if (popupWindows != null) {
                    if (popupWindows.isShowing()) {
                        popupWindows.dismiss();
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
        AudioManager.getInstance(Environment.getExternalStorageDirectory() + "/liweijie_recorder").deleteAllFile();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    public void initView() {
        btn_photo = (ImageView) findViewById(R.id.btn_photo);
        mListView = (ListView) findViewById(R.id.listview);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        chatting_mode_btn = (ImageView) findViewById(R.id.ivPopUp);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        mBtnRcd = (AudioRecoderButton) findViewById(R.id.btn_rcd);
        mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.root);
        rl.addOnLayoutChangeListener(this);
        //监听键盘事件
        keyHeight = getWindowManager().getDefaultDisplay().getHeight() / 3;
        //获取键盘高度
        rl.getViewTreeObserver().
                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    /**
                     * the result is pixels
                     */
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        rl.getWindowVisibleDisplayFrame(r);

                        int screenHeight = rl.getRootView().getHeight();
                        heightDifference = screenHeight - (r.bottom - r.top);
//                Toast.makeText(MainActivity.this, heightDifference+"", Toast.LENGTH_SHORT).show();
                        //boolean visible = heightDiff > screenHeight / 3;
                    }
                });
    }


    public void initData() {
        for (int i = 0; i < COUNT; i++) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(dataArray[i]);
            entity.setType(ChatMsgViewAdapter.IMsgViewType.MSG_TEXT);
            if (i % 2 == 0) {
                entity.setName("you");
                entity.setWhereFrom(true);
            } else {
                entity.setName("me");
                entity.setWhereFrom(false);
            }
            entity.setText(new MsgNomalText(msgArray[i]));
            mDateArrays.add(entity);
        }
        mAdapter = new ChatMsgViewAdapter(this, mDateArrays);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                send();
                break;
            case R.id.btn_photo:
                popupWindows = new PopupWindows(MainActivity.this, btn_photo);
                // 隐藏表情选择框
                InputMethodUtil.hideInput(this, mEditTextContent);
                ((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView();

                break;
            case R.id.et_sendmessage:
                ((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView();
                break;
            case R.id.ivPopUp:
                changeMode();
                break;
        }
    }

    /**
     * 改变底部的可见性问题
     */
    private void changeMode() {
        if (btn_voice) {
            InputMethodUtil.showInput(this, mEditTextContent);
            mBtnRcd.setVisibility(View.GONE);
            mBottom.setVisibility(View.VISIBLE);
            btn_voice = false;
            chatting_mode_btn.setImageResource(R.drawable.chatting_setmode_msg_btn);
            ((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView();
        } else {
            btn_voice = true;
            InputMethodUtil.hideInput(this, mEditTextContent);
            mBtnRcd.setVisibility(View.VISIBLE);
            mBottom.setVisibility(View.GONE);
            chatting_mode_btn.setImageResource(R.drawable.chatting_setmode_voice_btn);
            ((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView();
        }
    }


    public void send() {
        String conString = mEditTextContent.getText().toString();
        if (conString.length() > 0) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(getDate());
            entity.setName("me");
            entity.setWhereFrom(false);
            entity.setType(ChatMsgViewAdapter.IMsgViewType.MSG_TEXT);
            entity.setText(new MsgNomalText(conString));
            mDateArrays.add(entity);
            mAdapter.notifyDataSetChanged();
            mEditTextContent.setText("");
            //有动画
            mListView.smoothScrollToPosition(mListView.getCount() - 1);
        }
    }

    /**
     * 获取日期的string
     *
     * @return
     */
    public String getDate() {
        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins);
        return sbBuffer.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //发送图片
        if (requestCode == SHOW_ALL_PICTURE && resultCode == SHOW_ALL_PICTURE_FROM) {
            List<String> mImageUrls = Arrays.asList(data.getStringArrayExtra("IMAGE_LIST"));
            int size = mImageUrls.size();
            if (size <= 0 || size > 9) {
                return;
            }
            for (int i = 0; i < size; i++) {
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setDate(getDate());
                entity.setName("me");
                entity.setWhereFrom(false);
                entity.setPath(mImageUrls.get(i));
                entity.setType(ChatMsgViewAdapter.IMsgViewType.MSG_PIC);
                mDateArrays.add(entity);
            }
            mAdapter.notifyDataSetChanged();
            mListView.smoothScrollToPosition(mListView.getCount() - 1);

        } else if (requestCode == TAKE_PICTURE) {
            Intent mIntent = new Intent(MainActivity.this, CameraActivity.class);
            Bundle bun = new Bundle();
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(MainActivity.this, "外部存储不可用", Toast.LENGTH_LONG).show();
                return;
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/liweijie/");
            if (!file.exists()) {
                file.mkdirs();
            }
            File image = new File(file, UUID.randomUUID() + ".jpg");
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap == null) {
                Toast.makeText(MainActivity.this, "出现未知错误", Toast.LENGTH_LONG).show();
                return;
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                bun.putString("camera", image.getAbsolutePath());
                mIntent.putExtras(bun);
                startActivityForResult(mIntent, SHOW_CAMER_PICTURE);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else if (requestCode == SHOW_CAMER_PICTURE && resultCode == SHOW_CAMER_PICTURE_FROM) {
            String imgUrl = data.getStringExtra("imgUrl");
            if (imgUrl == null) {
                return;
            }
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(getDate());
            entity.setName("me");
            entity.setWhereFrom(false);
            entity.setPath(imgUrl);
            entity.setType(ChatMsgViewAdapter.IMsgViewType.MSG_PIC);
            mDateArrays.add(entity);
            mAdapter.notifyDataSetChanged();
            mListView.smoothScrollToPosition(mListView.getCount() - 1);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && ((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout)).hideFaceView()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 录音发送回调
     *
     * @param bean 录音的bean
     */
    @Override
    public void onFinish(RecorderBean bean) {
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setDate(getDate());
        entity.setName("me");
        entity.setWhereFrom(false);
        entity.setType(ChatMsgViewAdapter.IMsgViewType.MSG_AUDIO);
        entity.setBean(bean);
        mDateArrays.add(entity);
        mAdapter.notifyDataSetChanged();
        mListView.smoothScrollToPosition(mListView.getCount() - 1);
    }


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {

//            Toast.makeText(MainActivity.this, "监听到软键盘弹起...", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessage(MSG_HIDE_INPUT);

        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {

//            Toast.makeText(MainActivity.this, "监听到软件盘关闭...", Toast.LENGTH_SHORT).show();

        }
    }

    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_HIDE_INPUT) {
                if (!(mListView.getLastVisiblePosition() == mDateArrays.size() - 1)) {
                    mListView.smoothScrollToPosition(mListView.getCount() - 1);
                }
            }
        }
    };
}
