package com.example.androidphpmysql.service;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidphpmysql.R;
import com.example.androidphpmysql.subservice.SubServiceActivity;

import java.util.List;

public class SubServiceAdapter extends RecyclerView.Adapter<SubServiceAdapter.ViewHolder> {
    private final List<SubServiceListItem> listItems;
    private final Context context;

    public SubServiceAdapter(List<SubServiceListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subservice_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubServiceListItem listItem = listItems.get(position);
        holder.textViewSubServiceName.setText(listItem.getName());
        holder.textViewSubServiceDesc.setText(listItem.getDescription());
        holder.buttonSubService.setOnClickListener(view -> {
            Intent intent = new Intent(context, SubServiceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("sub_service_id", listItem.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewSubServiceName, textViewSubServiceDesc;
        public LinearLayout linearLayout;
        public Button buttonSubService;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSubServiceName = itemView.findViewById(R.id.textViewSubServiceName);
            textViewSubServiceDesc = itemView.findViewById(R.id.textViewSubServiceDesc);
            linearLayout = itemView.findViewById(R.id.LinearLayoutSubService);
            buttonSubService = itemView.findViewById(R.id.buttonSubService);
        }
    }
}
