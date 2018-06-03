package com.logo.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.logo.R;
import com.logo.adapters.CommentsAdapter;

import java.util.ArrayList;

/**
 * Created by deepaksingh on 01/06/18.
 */

public class ViewCommentFragment extends DialogFragment {

    RecyclerView recComments;
    CommentsAdapter adapter;

    public ViewCommentFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        super.onCreateView(inflater, parent, state);
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_view_comment, parent, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recComments = (RecyclerView) view.findViewById(R.id.rec_comments);

        recComments.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (null != getArguments() && getArguments().containsKey("comments")) {
            ArrayList<String> commentList = (ArrayList<String>) getArguments().getStringArrayList("comments");

            adapter = new CommentsAdapter(commentList);
            recComments.setAdapter(adapter);
        }
    }
}
