package com.example.mail;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import java.util.ArrayList;
import java.util.HashMap;


class UserRepo {
    private DBHelper dbHelper;

    UserRepo(Context context){
         dbHelper = new DBHelper(context);
    }

    int insert(User user) {
        //打开连接，写入数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO User(account,password) values(?,?)",
                new Object[]{user.account,user.password});
        return 0;
    }

    ArrayList<HashMap<String, String>> getUserList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM User";
        ArrayList<HashMap<String, String>> userList = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> user = new HashMap<String, String>();
                user.put("account", cursor.getString(cursor.getColumnIndex("account")));
                user.put("password", cursor.getString(cursor.getColumnIndex("password")));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }
}
