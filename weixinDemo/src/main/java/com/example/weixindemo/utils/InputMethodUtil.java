package com.example.weixindemo.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by liweijie on 2015/12/3.
 */
public class InputMethodUtil {

    public static void hideInput(Context context,EditText et_sendmessage) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0); //强制隐藏键盘
    }

    public static void showInput(Context context, EditText et_sendmessage) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_sendmessage, InputMethodManager.SHOW_FORCED);
    }

}
