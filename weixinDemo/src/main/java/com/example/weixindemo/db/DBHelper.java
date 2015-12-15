package com.example.weixindemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liweijie on 2015/12/14.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "chat";
    private static final String CREATE_CHAT = "create table chat if not exits()";
    private static final String DROP_CHAT = "create table chat if not exits()";

    private static DBHelper mInstance = null;

    public static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (null == mInstance) {
                    mInstance = new DBHelper(context);
                }
            }
        }
        return mInstance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CHAT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_CHAT);
        onCreate(db);

    }
}
