package com.example.weixindemo.utils;

import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liweijie on 2015/12/1.
 */
public class AtomIntegerUtil {
    private static final AtomicInteger CREATE_CODE = new AtomicInteger(1);

    /**
     * 获取不同的int值
     * 线程安全，int唯一
     * 用于产生resultCode，
     * handler的what等
     * @return
     */
    public static int getDiffIntCode()
    {
        return CREATE_CODE.getAndIncrement();
    }
}
