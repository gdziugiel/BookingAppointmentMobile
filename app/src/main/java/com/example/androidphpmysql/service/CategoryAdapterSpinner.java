package com.example.androidphpmysql.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.androidphpmysql.R;
import com.example.androidphpmysql.main.CategoryListItem;

import java.util.List;

public class CategoryAdapterSpinner extends ArrayAdapter<CategoryListItem> {
    public TextView textViewCategoryName;

    public CategoryAdapterSpinner(List<CategoryListItem> listItems, Context context) {
        super(context, 0, listItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable @org.jetbrains.annotations.Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, parent);
    }

    private View initView(int position, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_spinner_list_item, parent, false);
        textViewCategoryName = view.findViewById(R.id.textViewSpinnerCategoryName);
        CategoryListItem listItem = getItem(position);
        textViewCategoryName.setText(listItem.getName());
        return view;
    }
}
