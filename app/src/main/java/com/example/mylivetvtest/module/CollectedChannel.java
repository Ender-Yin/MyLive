package com.example.mylivetvtest.module;

/**
 *与数据表 collectlist对应
 *只有两个变量
 *categoryindex 该频道 分类下标
 *channelindex  该频道下标
 */
public class CollectedChannel {

    int categoryindex;
    int channelindex;

    public CollectedChannel(int categoryindex, int channelindex) {
        this.categoryindex = categoryindex;
        this.channelindex = channelindex;
    }

    public int getCategoryindex() {
        return categoryindex;
    }
    public void setCategoryindex(int categoryindex) {
        this.categoryindex = categoryindex;
    }

    public int getChannelindex() {
        return channelindex;
    }
    public void setChannelindex(int channelindex) {
        this.channelindex = channelindex;
    }
}
