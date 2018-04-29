package com.logo.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by mandeep on 16/4/18.
 */

public class LogoUtils {

    public static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)px;
    }

    public static boolean isEmpty(String text) {
        if (null == text || text.equals("null") || text == "" || text.length() == 0) {
            return true;
        }
        return false;
    }
}
