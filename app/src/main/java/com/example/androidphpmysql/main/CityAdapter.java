package com.example.androidphpmysql.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.androidphpmysql.R;

import java.util.List;

public class CityAdapter extends ArrayAdapter<City> {
    public TextView textViewCityName;

    public CityAdapter(List<City> listItems, Context context) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_list_item, parent, false);
        textViewCityName = view.findViewById(R.id.textViewCityName);
        City listItem = getItem(position);
        textViewCityName.setText(listItem.getName());
        return view;
    }
}
