package com.example.mylivetvtest.module;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CollectorDBOpenHelper extends SQLiteOpenHelper {

    public CollectorDBOpenHelper(Context context) {
        super(context, "collector.db", null, 1);
        Log.e("@@@@", "创建数据库成功");
    }

    @Override
    //数据库第一次创建时被调用
    public void onCreate(SQLiteDatabase db) {
        //收藏表中只有 该收藏频道的： 分类下标 和 频道自身下标
        db.execSQL("CREATE TABLE collectlist(categoryindex INTEGER ,"
                                            + "channelindex INTEGER)");

        Log.e("@@@@", "创建表成功");

    }

    //软件版本号发生改变时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("ALTER TABLE person ADD phone VARCHAR(12)");
    }

}
