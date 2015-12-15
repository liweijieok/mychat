package com.example.weixindemo.db;

import android.content.Context;

/**
 * Created by liweijie on 2015/12/14.
 */
public class DBChatInfoService {
    private DBHelper helper;
    public DBChatInfoService(Context contxt) {
        helper = DBHelper.getInstance(contxt);
    }

    //
}
