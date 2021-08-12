package com.example.mylivetvtest.module;

import java.util.List;

public class ModelTV {


    public String classify;
    public List<ListItem> list;

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public List<ListItem> getList() {
        return list;
    }

    public void setList(List<ListItem> list) {
        this.list = list;
    }

    public static class ListItem {

        public String dname;
        public String url;
        public String ico;
        public String order;
        public String id;
        public List<String> UrlList;
        private boolean isPlaying = false;

        public List<String> getUrlList() {
            return UrlList;
        }

        public void setUrlList(List<String> urlList) {
            UrlList = urlList;
        }

        public String getDname() {
            return dname;
        }

        public void setDname(String dname) {
            this.dname = dname;
        }

        public String getUrls() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIco() {
            return ico;
        }

        public void setIco(String ico) {
            this.ico = ico;
        }

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isPlaying() {
            return isPlaying;
        }
        public void setPlaying(boolean playing) {
            isPlaying = playing;
        }
    }

}
