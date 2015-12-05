package com.example.weixindemo.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.weixindemo.R;
import com.example.weixindemo.app.MainActivity;
import com.example.weixindemo.app.ShowAllLocalPicActivity;

import java.io.File;
import java.util.UUID;

/**
 * Created by user on 2015/12/2.
 */
public class PopupWindows extends PopupWindow {

    public PopupWindows(final Activity mContext, View parent) {

        super(mContext);

        final View view = View
                .inflate(mContext, R.layout.item_popubwindows, null);
        final LinearLayout ll_popup = (LinearLayout) view
                .findViewById(R.id.ll_popup);
        popupStartAnim(view, ll_popup, mContext);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(parent, Gravity.CENTER, 0, 0);
        update();
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                mContext.startActivityForResult(intent, MainActivity.TAKE_PICTURE);
                dismiss();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShowAllLocalPicActivity.class);
                mContext.startActivityForResult(intent, MainActivity.SHOW_ALL_PICTURE);
                dismiss();
                mContext.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);//设置切换动画，从右边进入，左边退出
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupDismiss(mContext, view, ll_popup);
            }
        });

    }

    private void popupStartAnim(View v1, View v2, Activity context) {
        v1.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.fade_ins));
        v2.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.push_bottom_in_2));
    }

    private void popupDismiss(Activity activity, View view, View v2) {
        view.startAnimation(AnimationUtils.loadAnimation(activity,
                R.anim.fade_outs));

        v2.startAnimation(AnimationUtils.loadAnimation(activity,
                R.anim.fade_outs));
        dismiss();
    }
}

