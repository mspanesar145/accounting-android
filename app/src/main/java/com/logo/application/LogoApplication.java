package com.logo.application;

import android.app.Application;

import com.logo.coremanager.CoreManager;

public class LogoApplication extends Application{
    private CoreManager coreManager = null;

    @Override
    public void onCreate() {

        coreManager = new CoreManager(this);

    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    public void setCoreManager(CoreManager coreManager) {
        this.coreManager = coreManager;
    }
}
