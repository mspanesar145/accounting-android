package com.logo.database.managerimpl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.logo.application.LogoApplication;
import com.logo.bo.User;
import com.logo.database.LogoDatabase;
import com.logo.database.manager.UserManager;

public class UserManagerImpl implements UserManager{

    LogoApplication logoApplication;
    LogoDatabase logoDatabase;
    SQLiteDatabase sqLiteDatabase;

    public UserManagerImpl(LogoApplication logoApplication){
        this.logoApplication = logoApplication;
        this.logoDatabase = new LogoDatabase(logoApplication);
        this.sqLiteDatabase = this.logoDatabase.getWriteableDatabase();
    }

    @Override
    public void addUser(User user) {
        // TODO Auto-generated method stub
        sqLiteDatabase.execSQL("insert into table_user values('" + user.getUsername()
                + "' , '" + user.getEmail()
                + "','"+user.getUserId()
                +"','"+user.getAuthToken()
                +"','"+user.getFirstName()
                +"','"+user.getLastName()
                +"','"+user.getPhone()
                +"','"+user.getCity()
                +"')");
    }

    @Override
    public void deleteUser() {
        // TODO Auto-generated method stub
        sqLiteDatabase.execSQL("delete from table_user");
    }

    @Override
    public User getUser() {
        // TODO Auto-generated method stub
        Cursor cursor = sqLiteDatabase.rawQuery(
                "select * from table_user  ", null);
        User user = null;

        if (cursor.moveToFirst()) {
            user = new User();
            user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setUserId(cursor.getInt(cursor.getColumnIndex("userId")));
            user.setAuthToken(cursor.getString(cursor.getColumnIndex("token")));
            user.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
            user.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
            user.setCity(cursor.getString(cursor.getColumnIndex("city")));
            user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            return user;
        }
        return user;
    }

    @Override
    public boolean isUserExist() {
        // TODO Auto-generated method stub
        boolean isUser = false;
        Cursor cursor = sqLiteDatabase.rawQuery(
                "select * from table_user  ", null);


        if (cursor.moveToFirst()) {
        isUser = true;
        }
        return isUser;
    }
}
