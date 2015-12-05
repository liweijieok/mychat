package com.example.weixindemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weixindemo.R;
import com.example.weixindemo.bean.ImageBean;
import com.example.weixindemo.utils.ImageLoader;

import java.util.List;


public class ListImageAdapter extends BaseAdapter {
    private Context context;
    private List<ImageBean> mDatas;

    public ListImageAdapter(Context context, List<ImageBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.local_all_pic_item, parent, false);
            holder.iv = (ImageView) convertView.findViewById(R.id.id_listview_imageview);
            holder.tvCount = (TextView) convertView.findViewById(R.id.id_listview_count);
            holder.tvName = (TextView) convertView.findViewById(R.id.id_listview_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageBean bean = mDatas.get(position);
        holder.tvCount.setText(bean.getCount() + "张");
        holder.tvName.setText(bean.getName() + "");
        holder.iv.setImageResource(R.drawable.pictures_no);
        ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(bean.getFirstImage(), holder.iv);

        return convertView;
    }

    class ViewHolder {
        ImageView iv;
        TextView tvName;
        TextView tvCount;
    }

}
