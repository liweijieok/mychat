package com.example.weixindemo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileUtils {
    public static String SDPATH = Environment.getExternalStorageDirectory()
            + "/formats/";

    /**
     * 获取所有的emoji图片的名字
     * 比如是这样的
     * emoji_91.png,[外星人54]
     * 半含名字，描述
     *
     * @param context
     * @return
     */
    public static List<String> getEmojiFile(Context context) {
        List<String> list = new ArrayList<String>();
        InputStream in = null;
        BufferedReader br = null;
        try {
            in = context.getResources().getAssets().open("emoji");//
            br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String str = null;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 删除
     *
     * @param fileName
     */
    public static void delFile(String fileName) {
        File file = new File(SDPATH + fileName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }

    }
}
