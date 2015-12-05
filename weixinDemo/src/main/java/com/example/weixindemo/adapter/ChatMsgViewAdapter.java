package com.example.weixindemo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weixindemo.R;
import com.example.weixindemo.app.PhotoActivity;
import com.example.weixindemo.bean.ChatMsgEntity;
import com.example.weixindemo.manager.MediaManager;
import com.example.weixindemo.utils.FaceConversionUtil;
import com.example.weixindemo.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ChatMsgViewAdapter extends BaseAdapter {

    public enum IMsgViewType {
        MSG_TEXT, MSG_AUDIO, MSG_PIC
    }

    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();
    private List<ChatMsgEntity> coll;
    private LayoutInflater mInflater;
    private Activity ctx;
    private Resources res;

    public ChatMsgViewAdapter(Activity context, List<ChatMsgEntity> coll) {
        ctx = context;
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
        res = context.getResources();
    }

    @Override
    public int getCount() {
        return coll.size();
    }

    @Override
    public Object getItem(int position) {
        return coll.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMsgEntity entity = coll.get(position);
        switch (entity.getType()) {
            case MSG_TEXT:
                return 0;
            case MSG_AUDIO:
                return 1;
            case MSG_PIC:
                return 2;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    private ImageView animView;

    @Override
    public View getView(final int postion, View converView, ViewGroup parent) {
        final ChatMsgEntity entity = coll.get(postion);
        final ViewHolder holder;
        //一定要这样子排列，不能先判断converview是否我null，否则重用
        if (!entity.isWhereFrom()) {
            //自己
            if (converView == null || ((ViewHolder) converView.getTag()).who) {
                holder = new ViewHolder();
                converView = mInflater.inflate(R.layout.chat_right, null);
                initViewHolder(postion, converView, holder,false);
            } else {
                holder = (ViewHolder) converView.getTag();
            }
        } else {
            if (converView == null || !((ViewHolder) converView.getTag()).who) {
                //别人
                holder = new ViewHolder();
                converView = mInflater.inflate(R.layout.chat_left, null);
                initViewHolder(postion, converView, holder,true);
            } else {
                holder = (ViewHolder) converView.getTag();
            }

        }

        holder.tvSendTime.setText(entity.getDate());
        holder.tvUserName.setText(entity.getName());
        holder.tvTime.setVisibility(View.GONE);
        holder.imageView.setVisibility(View.GONE);
        holder.tvContent.setVisibility(View.GONE);
        holder.imageView2.setVisibility(View.GONE);
        switch (holder.type) {
            case 0:
                holder.tvContent.setVisibility(View.VISIBLE);
                holder.tvContent.setText(FaceConversionUtil.getInstace().
                        getExpressionString(ctx, entity.getText().getText()));
                break;
            case 1:
                holder.tvTime.setVisibility(View.VISIBLE);
                holder.imageView2.setVisibility(View.VISIBLE);
                holder.tvContent.setText(null);
                holder.tvTime.setText(Math.round(entity.getBean().getTime()) + "''");
                holder.imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (animView != null) {
                            animView.setImageResource(R.drawable.adj);
                            animView = null;
                        }
                        animView = holder.imageView2;
                        animView.setImageResource(R.drawable.play_anim);
                        final AnimationDrawable anim = (AnimationDrawable) animView.getDrawable();
                        anim.start();
                        //播放音频
                        MediaManager.playSound(entity.getBean().getFilaPath(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                //停止动画
                                animView.setImageResource(R.drawable.adj);
                            }
                        });
                    }
                });
                break;
            case 2:
                holder.imageView.setVisibility(View.VISIBLE);
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(ctx, PhotoActivity.class);
                        ArrayList<String> list = new ArrayList<String>();
                        for (int i = 0; i < coll.size(); i++) {
                            if (coll.get(i).getType().equals(IMsgViewType.MSG_PIC)) {
                                list.add(coll.get(i).getPath());
                            }
                        }
                        int position = list.indexOf(entity.getPath());
                        mIntent.putExtra("POSITIOIN", position);
                        mIntent.putExtra("PATH", list);

                        ctx.startActivity(mIntent);
                    }
                });
                ImageLoader.getInstance(1, ImageLoader.Type.LIFO).loadImage(entity.getPath(), holder.imageView);
                break;

        }
        return converView;
    }

    private void initViewHolder(int postion, View converView, ViewHolder holder,boolean who) {
        holder.tvSendTime = (TextView) converView.findViewById(R.id.id_chat_item_sendtime);
        holder.tvContent = (TextView) converView.findViewById(R.id.id_chat_item_chatcontent);
        holder.tvTime = (TextView) converView.findViewById(R.id.id_chat_item_time);
        holder.tvUserName = (TextView) converView.findViewById(R.id.id_chat_item_username);
        holder.imageView = (ImageView) converView.findViewById(R.id.id_chat_item_imageviwe);
        holder.imageView2 = (ImageView) converView.findViewById(R.id.id_chat_item_imageview1);
        holder.type = getItemViewType(postion);
        holder.who = who;
        converView.setTag(holder);
    }

    static class ViewHolder {
        public TextView
                tvSendTime,
                tvUserName,
                tvContent,
                tvTime;
        public ImageView imageView, imageView2;
        public int type;
        public boolean who;
    }


}
