package com.example.weixindemo.app;

import android.app.Application;

import java.util.List;

/**
 * Created by user on 2015/12/2.
 */
public class MyApplication extends Application {
    public static List<String> allPhoto;// 全部图片
    public static List<String> mInfo;// 图片文件夹

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }
}
