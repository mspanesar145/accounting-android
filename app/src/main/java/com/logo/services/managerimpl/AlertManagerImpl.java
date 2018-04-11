package com.logo.services.managerimpl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.logo.R;
import com.logo.application.LogoApplication;
import com.logo.services.manager.AlertManager;

public class AlertManagerImpl implements AlertManager {

    LogoApplication logoApplication;
    public boolean isPopUp = true;

    public AlertManagerImpl(LogoApplication logoApplication) {
        this.logoApplication = logoApplication;

    }

    @Override
    public void alert(String message, String title, final Context context,
                      final Intent intent) {
        // TODO Auto-generated method stub

        if (isPopUp) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            // set title
            alertDialogBuilder.setTitle(title);
            // set dialog message
            alertDialogBuilder
                    .setMessage(message)
                    .setCancelable(false)
                    .setNeutralButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    dialog.cancel();
                                    if(intent != null) {
                                        isPopUp = true;
                                        context.startActivity(intent);
                                        ((Activity) context).finish();
                                    }else{
                                        isPopUp = true;

                                    }
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it

            alertDialog.show();
            isPopUp = false;
        }

    }




}
