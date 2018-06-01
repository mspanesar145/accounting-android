package com.logo.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by deepaksingh on 23/05/18.
 */

public class AppUtil {

    public static final String CATEGORY_ID = "categoryId";
    public static final String SUB_CATEGORY_ID = "subCategoryId";
    public static final String CONTAINS_VIDEO = "containsVideo";
    public static final String PARAM_DEVICE_TYPE = "deviceType";
    public static final String DEVICE_TYPE = "android";
    public static final String DEVICE_TOKEN = "deviceToken";

    public static void updateListViewHeight(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            return;
        }
        //get listview height
        int totalHeight = 0;
        int adapterCount = myListAdapter.getCount();
        for (int size = 0; size < adapterCount ; size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //Change Height of ListView
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (adapterCount - 1));
        myListView.setLayoutParams(params);
    }

    public static boolean isImage(String url) {
        if (url.contains(".png") || url.contains(".jpg") || url.contains(".jpeg")) {
            return true;
        } else {
            return false;
        }
    }
}
