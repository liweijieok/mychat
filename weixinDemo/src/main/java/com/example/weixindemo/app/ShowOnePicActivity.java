package com.example.weixindemo.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weixindemo.R;
import com.example.weixindemo.adapter.ImageGridViewAdapter;
import com.example.weixindemo.adapter.OnePicPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/12/2.
 */
public class ShowOnePicActivity extends Activity implements View.OnClickListener {
    private List<String> mDatas;
    private ImageButton backBt;
    private Button clearBt;
    private ViewPager mViewPager;
    private List<View> mViews;
    private OnePicPagerAdapter mAdapter;
    private int position = 0;
    private String mDir = null;
    private Button sureBtn;
    private TextView numText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_one_pic_layout);

        mDatas = new ArrayList<String>();
        if (!getIntent().getBooleanExtra(ShowAllLocalPicActivity.IS_FROM_DETAIL, false)) {
            mDatas.addAll(ImageGridViewAdapter.getSelectedList());
        } else {
            position = getIntent().getIntExtra("POSITION", 0);
            mDir = getIntent().getStringExtra("DIR");
            if(mDir == null) {
                mDatas.addAll(MyApplication.allPhoto);
            }else {
                mDatas.addAll(MyApplication.mInfo);
            }

        }
        initView();
        initViewPager();
        mViewPager.setCurrentItem(position);
    }

    private void initView() {
        backBt = (ImageButton) findViewById(R.id.id_one_pic_ib);
        clearBt = (Button) findViewById(R.id.id_one_pic_ib1);
        mViewPager = (ViewPager) findViewById(R.id.id_one_pic_vp);
        numText = (TextView) findViewById(R.id.id_one_pic_b1);
        sureBtn = (Button) findViewById(R.id.id_one_pic_b2);
        backBt.setOnClickListener(this);
        clearBt.setOnClickListener(this);
        sureBtn.setOnClickListener(this);
        numText.setText(ImageGridViewAdapter.getSelectedList().size()+"/9");
    }

    private void initViewPager() {
        mViews = new ArrayList<View>();
        for(int i = 0;i <mDatas.size();i++)
        {
            View v = LayoutInflater.from(this).inflate(R.layout.show_ont_pic_item,null);
            mViews.add(v);
        }
        mAdapter = new OnePicPagerAdapter(mViews,mDatas,mDir);
        mViewPager.setAdapter(mAdapter);
        mAdapter.setOnSelectedChangeListener(new OnePicPagerAdapter.OnSelectedChangeListener() {
            @Override
            public void changeData() {
                numText.setText(ImageGridViewAdapter.getSelectedList().size()+"/9");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_one_pic_ib:
                ShowOnePicActivity.this.finish();
                break;
            case R.id.id_one_pic_ib1:
                ImageGridViewAdapter.clearSelectedSet();
                //
                mAdapter.notifyData();
                break;
            case R.id.id_one_pic_b2:
                ShowOnePicActivity.this.finish();
                break;
        }
    }
}
