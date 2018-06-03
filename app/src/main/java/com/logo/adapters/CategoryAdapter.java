package com.logo.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.logo.R;
import com.logo.bo.CategorySelectionListener;

/**
 * Created by deepaksingh on 03/06/18.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private String[] mCategory;
    private CategorySelectionListener mListener;

    public CategoryAdapter(String[] category, CategorySelectionListener listener) {
        mCategory = category;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_categories, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvCategory.setText(mCategory[position]);
        holder.tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvCategory.setChecked(!holder.tvCategory.isChecked());
                mListener.onCategorySelected(holder.getAdapterPosition(), mCategory[holder.getAdapterPosition()]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCategory.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CheckedTextView tvCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCategory = (CheckedTextView) itemView.findViewById(R.id.ctv_category);
        }
    }
}
