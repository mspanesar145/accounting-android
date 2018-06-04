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
import android.widget.Button;

import com.logo.R;
import com.logo.adapters.CategoryAdapter;
import com.logo.bo.CategoryListItem;
import com.logo.bo.CategorySelectionListener;
import com.logo.bo.MainCategoryListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deepaksingh on 03/06/18.
 */

public class SelectCourseDialogFragment extends DialogFragment implements CategorySelectionListener {

    RecyclerView recCategories;
    Button btnDone;
    MainCategoryListener mainCategoryListener;
    private List<String> tempCategoryNames;
    boolean isMainCategory;

    CategoryAdapter adapter;
    List<CategoryListItem> categoryListItems;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_select_courses, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recCategories = (RecyclerView) view.findViewById(R.id.rec_categories);
        btnDone = (Button) view.findViewById(R.id.bt_done);

        recCategories.setLayoutManager(new LinearLayoutManager(getActivity()));
        isMainCategory = getArguments().getBoolean("mainCourses", false);
        if (getArguments().containsKey("category")) {
            ArrayList<String> category = getArguments().getStringArrayList("category");
            ArrayList<String> selectedCourses = getArguments().getStringArrayList("selectedCourses");
            if (tempCategoryNames == null) {
                tempCategoryNames = new ArrayList<>();
            }
            tempCategoryNames.addAll(selectedCourses);

            categoryListItems = new ArrayList<>();
            for (String course : category) {
                boolean isExist = false;
                for (String selectedCourse : selectedCourses) {
                    if (course.equals(selectedCourse)) {
                        isExist = true;
                        break;
                    }
                }
                categoryListItems.add(new CategoryListItem(course, isExist));
            }
            adapter = new CategoryAdapter(categoryListItems,this);
            recCategories.setAdapter(adapter);
        }

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mainCategoryListener) {
                    if (isMainCategory) {
                        mainCategoryListener.onMainCategorySelected(tempCategoryNames);
                    } else {
                        mainCategoryListener.onSubCategorySelected(tempCategoryNames);
                    }
                }
                dismissAllowingStateLoss();
            }
        });
    }

    @Override
    public void onCategorySelected(int pos, String name) {
        if (null == tempCategoryNames) {
            tempCategoryNames = new ArrayList<>();
        }
        if (!tempCategoryNames.contains(name)) {
            tempCategoryNames.add(name);
        } else {
            tempCategoryNames.remove(name);
        }
        if (null != categoryListItems) {
            categoryListItems.get(pos).setSelected(!categoryListItems.get(pos).isSelected());
        }
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setMainCategoryListener(MainCategoryListener listener) {
        mainCategoryListener = listener;
    }
}
