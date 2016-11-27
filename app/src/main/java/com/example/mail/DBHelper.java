package com.example.mail;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 秦 on 2016/11/16.
 */

class DBHelper extends SQLiteOpenHelper {
    //数据库版本号
    private static final int DATABASE_VERSION=4;

    //数据库名称
    private static final String DATABASE_NAME="crud.db";

    DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        String CREATE_TABLE_USER="CREATE TABLE User ( id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "account  VARCHAR(20), " +
                "password  VARCHAR(20))";
        //  "foreign key (time) references Student(id) on delete cascade)" ;        //好吧，不是最后一行，是所有行都闹鬼了
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //如果旧表存在，删除，所以数据将会消失
        db.execSQL("DROP TABLE IF EXISTS User");
        //再次创建表
        onCreate(db);
    }
}
