package com.example.mylivetvtest.module;

public class ChannelItem {
    private int poster;      //频道图片
    private String title;       //频道名字
    private String info;    //视频简介  or  subtitle
    private String url = null;
    private boolean isPlaying = false;
    //private int type;       //主页布局分类

    //给主页 子标题
    public ChannelItem(String title, int poster){
        this.title = title;
        this.poster = poster;
    }
    public ChannelItem(String title, int poster,String url){
        this.title = title;
        this.poster = poster;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfotext() {
        return info;
    }
    public void setInfotext(String infotext) {
        this.info = infotext;
    }

    public int getPoster() {
        return poster;
    }
    public void setPoster(int poster) {
        this.poster = poster;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(int url) {
        this.poster = url;
    }

    @Override
    public String toString() {
        return "DetailInfo{" +
                "mTitle='" + title + '\'' +
                ", mTextDesc=" + info +
                ", mPostImageUrl='" + poster + '\'' +
                '}';
    }

    public boolean isPlaying() {
        return isPlaying;
    }
    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
