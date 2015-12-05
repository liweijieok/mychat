package com.example.weixindemo.bean;

import com.example.weixindemo.adapter.ChatMsgViewAdapter;

import java.io.Serializable;

public class ChatMsgEntity implements Serializable {
    private static final String TAG = ChatMsgEntity.class.getSimpleName();
    private String name; //名字
    private String date;//日期

    private ChatMsgViewAdapter.IMsgViewType type;
    private String path;
    private RecorderBean bean;
    private MsgNomalText text;
    private boolean whereFrom;
    public ChatMsgEntity()
    {

    }
    public ChatMsgEntity(String name, String date, ChatMsgViewAdapter.IMsgViewType type, String path, RecorderBean bean, MsgNomalText text, boolean whereFrom) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.path = path;
        this.bean = bean;
        this.text = text;
        this.whereFrom = whereFrom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ChatMsgViewAdapter.IMsgViewType getType() {
        return type;
    }

    public void setType(ChatMsgViewAdapter.IMsgViewType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RecorderBean getBean() {
        return bean;
    }

    public void setBean(RecorderBean bean) {
        this.bean = bean;
    }

    public MsgNomalText getText() {
        return text;
    }

    public void setText(MsgNomalText text) {
        this.text = text;
    }

    public boolean isWhereFrom() {
        return whereFrom;
    }

    public void setWhereFrom(boolean whereFrom) {
        this.whereFrom = whereFrom;
    }
}
