package com.example.weixindemo.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.weixindemo.R;
import com.example.weixindemo.adapter.ViewPagerAdapter;
import com.example.weixindemo.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends Activity {

    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private List<String> mPaths;
    private int position;
    private List<View> mViews;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mPaths = new ArrayList<String>();
        mPaths.addAll(getIntent().getStringArrayListExtra("PATH"));
        position = getIntent().getIntExtra("POSITIOIN",0);
        initView();
        initData();
    }

    private void initData() {
        mViews = new ArrayList<>();
        for (int i = 0; i < mPaths.size(); i++) {
            View v = LayoutInflater.from(PhotoActivity.this).inflate(R.layout.photo_activity_layout, null);
            mViews.add(v);
        }
        mAdapter = new PhotoViewPager(mViews);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(position);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_photo_viewpager);
    }

    private class PhotoViewPager extends ViewPagerAdapter {

        public PhotoViewPager(List<View> pageViews) {
            super(pageViews);
        }

        /***
         * 获取每一个item类于listview中的getview
         */
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            View v = pageViews.get(arg1);
            ((ViewPager) arg0).addView(v);
            ImageView iv = (ImageView) v.findViewById(R.id.id_photo_viewpager_im);
            ImageLoader.getInstance(2, ImageLoader.Type.LIFO).loadImage(mPaths.get(arg1), iv);
            return v;
        }

    }


}

