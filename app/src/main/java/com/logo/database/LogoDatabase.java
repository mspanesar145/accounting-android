package com.logo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.logo.application.LogoApplication;

public class LogoDatabase {
    public DataBaseOpenHelper dataBaseOpenHelper;

    public LogoDatabase(LogoApplication logoApplication) {
        // TODO Auto-generated constructor stub
        dataBaseOpenHelper = new DataBaseOpenHelper(logoApplication);
    }

    public SQLiteDatabase getReadableDatabase() {
        return dataBaseOpenHelper.getReadableDatabase();
    }

    public SQLiteDatabase getWriteableDatabase() {
        return dataBaseOpenHelper.getWritableDatabase();
    }

    public class DataBaseOpenHelper extends SQLiteOpenHelper {

        public static final String DATA_BASE_NAME = "logo.db";
        public static final int VERSION = 1;

        public DataBaseOpenHelper(Context context) {
            super(context, DATA_BASE_NAME, null, VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL("create table if not exists table_user (username text,email text, userId int" +
                    ",token text,firstName text, lastName text, phone text, city text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
        }

    }
}
