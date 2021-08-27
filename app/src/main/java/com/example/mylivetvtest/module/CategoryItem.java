package com.example.mylivetvtest.module;

public class CategoryItem {

    String categoryName;
    boolean isPlaying = false;

    public CategoryItem(String categoryName) {
        this.categoryName = categoryName;
    }


    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     *  true 为播放中
     *  false 为正常背景
     */
    public boolean isPlaying() {
        return isPlaying;
    }
    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }


}
