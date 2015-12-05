package com.example.weixindemo.adapter;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.weixindemo.R;
import com.example.weixindemo.utils.ImageLoader;

import java.util.List;

/**
 * Created by liweijie on 2015/12/2.
 */
public class OnePicPagerAdapter extends ViewPagerAdapter {
    private List<String> mDatas;
    private String mDir;

    public OnePicPagerAdapter(List<View> pageViews, List<String> mDatas, String mDir) {
        super(pageViews);
        this.mDatas = mDatas;
        this.mDir = mDir;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void notifyData() {
        this.notifyDataSetChanged();
    }

    public interface OnSelectedChangeListener {
        void changeData();
    }

    private OnSelectedChangeListener mListener;

    public void setOnSelectedChangeListener(OnSelectedChangeListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public Object instantiateItem(View arg0, final int arg1) {
        View v = pageViews.get(arg1);
        ((ViewPager) arg0).addView(v);
        final String path;
        if (null == mDir) {
            path = mDatas.get(arg1);
        } else {
            path = mDir + "/" + mDatas.get(arg1);
        }
        final ImageButton imageButton = (ImageButton) v.findViewById(R.id.id_one_pic_item_ib);
        ImageView imageView = (ImageView) v.findViewById(R.id.id_one_pic_item_im);
        if (ImageGridViewAdapter.getSelectedList().contains(path)) {
            imageButton.setImageResource(R.drawable.pictures_selected);
        } else {
            imageButton.setImageResource(R.drawable.picture_unselected);
        }


        ImageLoader.getInstance(2, ImageLoader.Type.LIFO).loadImage(path, imageView);

        imageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ImageGridViewAdapter.getSelectedList().contains(path)) {
                            ImageGridViewAdapter.getSelectedList().remove(path);
                            imageButton.setImageResource(R.drawable.picture_unselected);
                        } else {
                            if (ImageGridViewAdapter.getSelectedList().size() <= 9) {
                                ImageGridViewAdapter.addSelected(path);
                                imageButton.setImageResource(R.drawable.pictures_selected);
                            }
                        }
                        //位置不能变
                        if (mListener != null) {
                            mListener.changeData();
                        }
                    }
                }

        );
        return v;
    }


}
