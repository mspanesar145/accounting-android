package com.logo.services.managerimpl;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.logo.application.LogoApplication;
import com.logo.services.manager.InternetManager;

public class InternetManagerImpl implements InternetManager{
    LogoApplication logoApplication;

    public  InternetManagerImpl(LogoApplication logoApplication){
        this.logoApplication = logoApplication;
    }

    @Override
    public boolean isInternet(Activity activity){
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
