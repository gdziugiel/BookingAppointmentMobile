package com.example.androidphpmysql.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidphpmysql.statics.CurrentCity;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.service.ServicesActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final List<CategoryListItem> listItems;
    private final Context context;

    public CategoryAdapter(List<CategoryListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryListItem listItem = listItems.get(position);
        holder.textViewCategoryName.setText(listItem.getName());
        holder.linearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, ServicesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("category", listItem.getId());
            intent.putExtra("city", CurrentCity.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCategoryName;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            linearLayout = itemView.findViewById(R.id.LinearLayoutCategory);
        }
    }
}
