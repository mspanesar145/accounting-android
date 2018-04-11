package com.logo.services.managerimpl;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.logo.application.LogoApplication;
import com.logo.services.manager.DeviceManager;

public class DeviceManagerImpl implements DeviceManager{

    public  LogoApplication logoApplication;

    public DeviceManagerImpl(LogoApplication logoApplication){
        this.logoApplication = logoApplication;
    }

    @Override
    public void hideKeypad(EditText editText, Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
