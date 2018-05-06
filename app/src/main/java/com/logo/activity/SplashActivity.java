package com.logo.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.logo.R;
import com.logo.application.LogoApplication;
import com.logo.coremanager.CoreManager;
import com.logo.database.manager.UserManager;

public class SplashActivity extends LogoActivity {

    public LogoApplication logoApplication;
    public CoreManager coreManager;
    public UserManager userManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    public void init(){
        logoApplication = getLogoApplication();
        coreManager = logoApplication .getCoreManager();
        userManager = coreManager.getUserManager();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(userManager.isUserExist()){
                    startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this,LogoActivity.class));
                    finish();
                }
            }

        }, 2000);

    }
}
