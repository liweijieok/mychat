package com.example.weixindemo.bean;

/**
 * Created by user on 2015/12/3.
 */
public class RecorderBean {
    float time;
    String filaPath;

    public RecorderBean(float time, String filaPath) {
        this.time = time;
        this.filaPath = filaPath;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilaPath() {
        return filaPath;
    }

    public void setFilaPath(String filaPath) {
        this.filaPath = filaPath;
    }
}
