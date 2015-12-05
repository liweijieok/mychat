package com.example.weixindemo.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weixindemo.R;
import com.example.weixindemo.adapter.ImageGridViewAdapter;
import com.example.weixindemo.adapter.ListImageAdapter;
import com.example.weixindemo.bean.ImageBean;
import com.example.weixindemo.utils.AtomIntegerUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 2015/12/2.
 */
public class ShowAllLocalPicActivity extends Activity implements View.OnClickListener {
    private ListView mListView;
    private ImageButton backIB;
    private Button prevBT;
    private Button sendBt;
    private Button clearBt;
    private TextView numTv;
    public static final String IS_FROM_DETAIL = "is_from_detail";

    private final static int CODE_ALL_TO_DETAIL = AtomIntegerUtil.getDiffIntCode();

    private ListImageAdapter mAdapter;
    private List<ImageBean> mDatas;
    private ProgressDialog mDialog;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (mDialog.isShowing())
                mDialog.dismiss();
            data2ListView();
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loca_all_pic_layout);
        initView();
        initDatas();
        initEvent();
    }

    /**
     * 绑定数据到Lsitview
     */
    protected void data2ListView() {
        if (mDatas.size() <= 0) {
            Toast.makeText(ShowAllLocalPicActivity.this, "未扫描到任何图片", Toast.LENGTH_LONG).show();
            return;
        }
        mAdapter = new ListImageAdapter(ShowAllLocalPicActivity.this, mDatas);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 扫描相册
     */
    private void initDatas() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(ShowAllLocalPicActivity.this, "外部存储卡暂时不可用！", Toast.LENGTH_LONG).show();
            return;
        }
        // 开始扫描
        MyApplication.allPhoto = new ArrayList<String>();
        MyApplication.mInfo = new ArrayList<String>();
        mDatas = new ArrayList<ImageBean>();
        mDialog = ProgressDialog.show(ShowAllLocalPicActivity.this, null, "正在载入中...");
        new Thread() {
            public void run() {
                Looper.prepare();
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver rc = ShowAllLocalPicActivity.this.getContentResolver();
                Cursor cursor = rc.query(uri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?", new String[]
                        {"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

                Set<String> mSets = new HashSet<String>();
                boolean is_first = true;
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    if (path == null) {
                        continue;
                    }
                    if (path.length() <= 0) {
                        continue;
                    }
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null || !parentFile.exists() || parentFile.list().length <= 0) {
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();
                    if (is_first) {
                        is_first = false;
                        ImageBean all = new ImageBean();
                        all.setCount(0);
                        all.setDir("/所有图片");
                        all.setFirstImage(path);
                        mDatas.add(all);
                    }

                    ImageBean bean = null;
                    if (mSets.contains(dirPath)) {
                        continue;
                    } else {
                        mSets.add(dirPath);
                        bean = new ImageBean();
                        bean.setDir(dirPath);
                        bean.setFirstImage(path);
                    }
                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")
                                    || filename.endsWith(".png"))
                                return true;
                            return false;
                        }
                    }).length;
                    bean.setCount(picSize);
                    mDatas.add(bean);
                }

                // 计算所有图片
                File file = null;
                for (int i = 1; i < mDatas.size(); i++) {
                    file = new File(mDatas.get(i).getDir());
                    mDatas.get(0).setCount(mDatas.get(0).getCount() + mDatas.get(i).getCount());
                    File[] listFile = file.listFiles(new FilenameFilter() {

                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")
                                    || filename.endsWith(".png"))
                                return true;
                            return false;
                        }
                    });
                    for (int j = 0; j < listFile.length; j++) {
                        MyApplication.allPhoto.add(listFile[j].getAbsolutePath());
                    }
                }

                cursor.close();
                mSets = null;
                mHandler.sendEmptyMessage(0x11);
                Looper.loop();
            }

            ;
        }.start();

    }

    /*
     * 值空
     *  (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.allPhoto = null;
        MyApplication.mInfo = null;
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShowAllLocalPicActivity.this, ShowLocalDetailPicActivity.class);
                intent.putExtra("position", position);
                //对不同情况进行判断
                if (position == 0) {
                    intent.putExtra("title", "/所有图片");
                } else {
                    intent.putExtra("title", mDatas.get(position).getName());
                    intent.putExtra("dir", mDatas.get(position).getDir());
                    MyApplication.mInfo = Arrays.asList(new File(mDatas.get(position).getDir())
                            .list(new FilenameFilter() {

                                @Override
                                public boolean accept(File dir, String filename) {
                                    if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")
                                            || filename.endsWith(".png"))
                                        return true;
                                    return false;
                                }
                            }));
                }
                startActivityForResult(intent, CODE_ALL_TO_DETAIL);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_ALL_TO_DETAIL) {
            numTv.setText(ImageGridViewAdapter.getSelectedList().size() + "/9");
        }
    }

    /**
     * 初始化VIew
     */
    private void initView() {
        mListView = (ListView) findViewById(R.id.id_local_all_listview);
        backIB = (ImageButton) findViewById(R.id.id_local_all_ib);
        clearBt = (Button) findViewById(R.id.id_local_all_ib1);
        prevBT = (Button) findViewById(R.id.id_local_all_ib2);
        sendBt = (Button) findViewById(R.id.id_local_all_ib3);
        numTv = (TextView) findViewById(R.id.id_local_all_ib4);
        numTv.setText("0/9");
        clearBt.setOnClickListener(this);
        prevBT.setOnClickListener(this);
        sendBt.setOnClickListener(this);
        backIB.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_local_all_ib:
                clearData();
                this.finish();
                break;
            case R.id.id_local_all_ib1:
                clearData();
                break;
            //预览
            case R.id.id_local_all_ib2:
                if (ImageGridViewAdapter.getSelectedList().size() <= 0) {
                    Toast.makeText(ShowAllLocalPicActivity.this, "请选择图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent mIntent = new Intent(ShowAllLocalPicActivity.this, ShowOnePicActivity.class);
                mIntent.putExtra(IS_FROM_DETAIL, false);
                startActivityForResult(mIntent, CODE_ALL_TO_DETAIL);
                break;
            case R.id.id_local_all_ib3:
                if (ImageGridViewAdapter.getSelectedList().size() <= 0) {
                    Toast.makeText(ShowAllLocalPicActivity.this, "请选择图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent itentToMain = new Intent(ShowAllLocalPicActivity.this, MainActivity.class);
                String[] strs = new String[ImageGridViewAdapter.getSelectedList().size()];
                ImageGridViewAdapter.getSelectedList().toArray(strs);
                itentToMain.putExtra("IMAGE_LIST", strs);
                setResult(MainActivity.SHOW_ALL_PICTURE_FROM, itentToMain);
                ShowAllLocalPicActivity.this.finish();
                break;
        }
    }

    /**
     * 清除数据
     */
    private void clearData() {

        numTv.setText("0/9");
        ImageGridViewAdapter.clearSelectedSet();
    }


}
