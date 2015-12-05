package com.example.weixindemo.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;

import com.example.weixindemo.R;
import com.example.weixindemo.adapter.ImageGridViewAdapter;

/**
 * Created by user on 2015/12/2.
 */
public class ShowLocalDetailPicActivity extends Activity{
    private TextView back;
    private GridView mGridView;
    private ImageGridViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.local_pic_detail_layout);
        initView();
        initDatas();
        initEvent();
    }

    private void initView()
    {
        // 设置标题
        TextView tv = (TextView) findViewById(R.id.id_show_title);
        tv.setText(getIntent().getStringExtra("title"));
        back = (TextView) findViewById(R.id.id_show_back);
        mGridView = (GridView) findViewById(R.id.id_show_gridview);
    }

    private void initDatas()
    {
        // TODO Auto-generated method stub
        if (getIntent().getIntExtra("position", 0) == 0)
        {
            mAdapter = new ImageGridViewAdapter(ShowLocalDetailPicActivity.this, MyApplication.allPhoto, null);
        } else
        {
            mAdapter = new ImageGridViewAdapter(ShowLocalDetailPicActivity.this, MyApplication.mInfo,
                    getIntent().getStringExtra("dir"));
        }
        mGridView.setAdapter(mAdapter);
    }

    private void initEvent()
    {
        // TODO Auto-generated method stub
        back.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                ShowLocalDetailPicActivity.this.finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter.notifyDataSetChanged();
    }
}
