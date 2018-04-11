package com.logo.coremanager;

import com.logo.application.LogoApplication;
import com.logo.database.manager.UserManager;
import com.logo.database.managerimpl.UserManagerImpl;
import com.logo.services.manager.AlertManager;
import com.logo.services.manager.ApiManager;
import com.logo.services.manager.DeviceManager;
import com.logo.services.manager.InternetManager;
import com.logo.services.managerimpl.AlertManagerImpl;
import com.logo.services.managerimpl.ApiManagerImpl;
import com.logo.services.managerimpl.DeviceManagerImpl;
import com.logo.services.managerimpl.InternetManagerImpl;

public class CoreManager {

    public UserManager userManager;
    public AlertManager alertManager;
    public InternetManager internetManager;
    public DeviceManager deviceManager;
    public ApiManager apiManager;

    public  CoreManager(LogoApplication logoApplication){
        userManager = new UserManagerImpl(logoApplication);
        alertManager = new AlertManagerImpl(logoApplication);
        internetManager = new InternetManagerImpl(logoApplication);
        deviceManager = new DeviceManagerImpl(logoApplication);
        apiManager = new ApiManagerImpl(logoApplication);
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public AlertManager getAlertManager() {
        return alertManager;
    }

    public InternetManager getInternetManager() {
        return internetManager;
    }

    public DeviceManager getDeviceManager() {
        return deviceManager;
    }

    public ApiManager getApiManager() {
        return apiManager;
    }
}
