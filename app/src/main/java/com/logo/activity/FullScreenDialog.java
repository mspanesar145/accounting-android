package com.logo.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logo.R;

import org.json.JSONObject;

/**
 * Created by mandeep on 28/4/18.
 */

public class FullScreenDialog extends DialogFragment {

    public static final String TAG = "FullScreenDialog";

    TextView tvContentDescription,tvContentTitle;
    JSONObject dialogData;
    public FullScreenDialog() {

    }
    @SuppressLint("ValidFragment")
    public FullScreenDialog(JSONObject dialogData) {
        this.dialogData = dialogData;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.OstFullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            try {
                //dialog.
                tvContentTitle.setText(dialogData.getString("title"));
                tvContentDescription.setText(dialogData.getString("content"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        super.onCreateView(inflater, parent, state);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_content, parent, false);
        tvContentDescription = (TextView) view.findViewById(R.id.tv_content_desc);
        tvContentTitle = (TextView) view.findViewById(R.id.tv_content_title);
        return view;
    }


 }