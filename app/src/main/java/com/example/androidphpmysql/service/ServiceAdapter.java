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

import java.text.MessageFormat;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    private final List<ServiceListItem> listItems;
    private final Context context;

    public ServiceAdapter(List<ServiceListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceListItem listItem = listItems.get(position);
        holder.textViewServiceName.setText(listItem.getName());
        holder.textViewServiceDesc.setText(listItem.getDescription());
        holder.textViewServiceProvider.setText(MessageFormat.format("{0} {1}", listItem.getProviderFirstname(), listItem.getProviderLastname()));
        holder.buttonService.setOnClickListener(view -> {
            Intent intent = new Intent(context, ServiceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("service_id", listItem.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewServiceName, textViewServiceDesc, textViewServiceProvider;
        public LinearLayout linearLayout;
        public Button buttonService;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewServiceName = itemView.findViewById(R.id.textViewServiceName);
            textViewServiceDesc = itemView.findViewById(R.id.textViewServiceDesc);
            textViewServiceProvider = itemView.findViewById(R.id.textViewServiceProvider);
            linearLayout = itemView.findViewById(R.id.LinearLayoutService);
            buttonService = itemView.findViewById(R.id.buttonService);
        }
    }
}
