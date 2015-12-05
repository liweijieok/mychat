package com.example.weixindemo.bean;

public class ImageBean
{
    private int count;
    private String firstImage;
    private String name;
    private String dir;

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public String getFirstImage()
    {
        return firstImage;
    }

    public void setFirstImage(String firstImage)
    {
        this.firstImage = firstImage;
    }

    public String getName()
    {
        return name;
    }

    public String getDir()
    {
        return dir;
    }

    public void setDir(String dir)
    {
        this.dir = dir;
        this.name = dir.substring(dir.lastIndexOf("/"));
    }
}