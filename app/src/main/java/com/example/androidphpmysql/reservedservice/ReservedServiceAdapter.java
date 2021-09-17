package com.example.androidphpmysql.reservedservice;

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

import java.util.List;

public class ReservedServiceAdapter extends RecyclerView.Adapter<ReservedServiceAdapter.ViewHolder> {
    private final List<ReservedServiceListItem> listItems;
    private final Context context;

    public ReservedServiceAdapter(List<ReservedServiceListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reserved_service_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReservedServiceListItem listItem = listItems.get(position);
        holder.textViewSubServiceName.setText(listItem.getName());
        String time = listItem.getTime();
        holder.textViewTime.setText(time.substring(0, time.length() - 3));
        holder.buttonReservedService.setOnClickListener(view -> {
            Intent intent = new Intent(context, ReservedServiceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("id", listItem.getId());
            intent.putExtra("email", listItem.getEmail());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewSubServiceName, textViewTime;
        public LinearLayout linearLayout;
        public Button buttonReservedService;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSubServiceName = itemView.findViewById(R.id.textViewReservedSubServiceName);
            textViewTime = itemView.findViewById(R.id.textViewReservedServiceTime);
            linearLayout = itemView.findViewById(R.id.LinearLayoutService);
            buttonReservedService = itemView.findViewById(R.id.buttonReservedServiceCalendar);
        }
    }
}
