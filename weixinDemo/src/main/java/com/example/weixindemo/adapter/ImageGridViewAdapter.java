package com.example.weixindemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.weixindemo.R;
import com.example.weixindemo.app.ShowAllLocalPicActivity;
import com.example.weixindemo.app.ShowOnePicActivity;
import com.example.weixindemo.utils.AtomIntegerUtil;
import com.example.weixindemo.utils.ImageLoader;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;


public class ImageGridViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<String> mInfo;
    private String mDir;
    private Activity context;

    public ImageGridViewAdapter(Activity context, List<String> mInfo, String dir) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mInfo = mInfo;
        this.mDir = dir;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return mInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static List<String> mSelectedList = new ArrayList<String>();

    public static List<String> getSelectedList() {
        return mSelectedList;
    }

    public static void clearSelectedSet() {
        if (mSelectedList != null)
            mSelectedList.clear();
    }


    public static void addSelected(String str) {
        mSelectedList.add(str);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.local_pic__detail_item, parent, false);
            holder.mBtn = (ImageButton) convertView.findViewById(R.id.id_item_select);
            holder.mImg = (ImageView) convertView.findViewById(R.id.id_item_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 复原
        holder.mImg.setImageResource(R.drawable.pictures_no);
        holder.mImg.setColorFilter(null);
        holder.mBtn.setImageResource(R.drawable.picture_unselected);

        final String dirpath;
        if (null == mDir) {
            dirpath = mInfo.get(position);
        } else {
            dirpath = mDir + "/" + mInfo.get(position);
        }
        System.out.println(dirpath);
        ImageLoader.getInstance().loadImage(dirpath, holder.mImg);
        if (mSelectedList.contains(dirpath)) {
            holder.mImg.setColorFilter(Color.parseColor("#77000000"));
            holder.mBtn.setImageResource(R.drawable.pictures_selected);
        }
        //启动浏览Activity
        holder.mImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context, ShowOnePicActivity.class);
                mIntent.putExtra(ShowAllLocalPicActivity.IS_FROM_DETAIL, true);
                mIntent.putExtra("POSITION", position);
                mIntent.putExtra("DIR", mDir);
                context.startActivityForResult(mIntent, AtomIntegerUtil.getDiffIntCode());
            }
        });
        holder.mBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSelectedList.contains(dirpath)) {
                    mSelectedList.remove(dirpath);
                    holder.mImg.setColorFilter(null);
                    holder.mBtn.setImageResource(R.drawable.picture_unselected);
                } else {
                    if (mSelectedList.size() <= 9) {
                        mSelectedList.add(dirpath);
                        holder.mImg.setColorFilter(Color.parseColor("#77000000"));
                        holder.mBtn.setImageResource(R.drawable.pictures_selected);
                    } else {
                        Toast.makeText(context, "最多可以选择9张图片", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView mImg;
        ImageButton mBtn;
    }

}
