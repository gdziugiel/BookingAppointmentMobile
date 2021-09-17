package com.example.androidphpmysql.provider;

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

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ViewHolder> {
    private final List<ProviderListItem> listItems;
    private final Context context;

    public ProviderAdapter(List<ProviderListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProviderListItem listItem = listItems.get(position);
        holder.textViewProviderName.setText(MessageFormat.format("{0} {1}", listItem.getProviderFirstname(), listItem.getProviderLastname()));
        holder.textViewEmail.setText(listItem.getEmail());
        holder.buttonProvider.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProviderActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("provider_id", listItem.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewProviderName, textViewEmail;
        public LinearLayout linearLayout;
        public Button buttonProvider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProviderName = itemView.findViewById(R.id.textViewProviderName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            linearLayout = itemView.findViewById(R.id.LinearLayoutProvider);
            buttonProvider = itemView.findViewById(R.id.buttonProvider);
        }
    }
}
