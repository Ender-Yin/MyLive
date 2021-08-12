package com.example.mylivetvtest;

import android.app.Application;

import com.example.mylivetvtest.module.ModelTV;

import java.util.List;

public class MyApplication extends Application {
    public static List<ModelTV> TvListCache = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
