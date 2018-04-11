package com.logo.services.manager;

import android.content.Context;
import android.content.Intent;

public interface AlertManager {
    public void alert(String message, String title, final Context context,
                      final Intent intent);
}
