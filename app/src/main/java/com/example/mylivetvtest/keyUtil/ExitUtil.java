package com.example.mylivetvtest.keyUtil;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

public class ExitUtil {
    public static ExitUtil instance = new ExitUtil();

    public static List<Activity> activityList = new LinkedList();

    public static ExitUtil getInstance(){
        return instance;
    }

    public void addActivity(Activity act){
        activityList.add(act);
    }

    public void exit()
    {

        for(Activity act:activityList)
        {
            act.finish();
        }

        System.exit(0);

    }

}
