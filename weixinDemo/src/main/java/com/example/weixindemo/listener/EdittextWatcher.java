package com.example.weixindemo.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by user on 2015/12/3.
 */
public class EdittextWatcher implements TextWatcher {
    private EditText mEditTextContent;
    private ImageView btn_photo;
    private Button mBtnSend;

    public EdittextWatcher(EditText mEditTextContent, ImageView btn_photo, Button mBtnSend) {
        this.mEditTextContent = mEditTextContent;
        this.btn_photo = btn_photo;
        this.mBtnSend = mBtnSend;
    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = mEditTextContent.getText().toString();
        if (!(text.length() > 0)) {
            btn_photo.setVisibility(View.VISIBLE);
            mBtnSend.setVisibility(View.GONE);
        } else {
            btn_photo.setVisibility(View.GONE);
            mBtnSend.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                  int arg3) {
    }
}
