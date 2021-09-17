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

import com.example.androidphpmysql.reservedservice.CalendarActivity;
import com.example.androidphpmysql.freetime.FreeTimeActivity;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.service.ServiceActivity;
import com.example.androidphpmysql.service.ServiceListItem;

import java.util.List;

public class ProviderServiceAdapter extends RecyclerView.Adapter<ProviderServiceAdapter.ViewHolder> {
    private final List<ServiceListItem> listItems;
    private final Context context;
    private final int option;

    public ProviderServiceAdapter(List<ServiceListItem> listItems, Context context, int option) {
        this.listItems = listItems;
        this.context = context;
        this.option = option;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_service_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceListItem listItem = listItems.get(position);
        holder.textViewServiceName.setText(listItem.getName());
        holder.textViewServiceDesc.setText(listItem.getDescription());
        holder.textViewServiceCategory.setText(listItem.getCategory());
        holder.textViewServiceCity.setText(listItem.getCity());
        holder.buttonService.setOnClickListener(view -> {
            Intent intent;
            switch (option) {
                case 2:
                    intent = new Intent(context, CalendarActivity.class);
                    break;
                case 1:
                    intent = new Intent(context, FreeTimeActivity.class);
                    break;
                case 0:
                default:
                    intent = new Intent(context, ServiceActivity.class);
            }
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
        public TextView textViewServiceName, textViewServiceDesc, textViewServiceCategory, textViewServiceCity;
        public LinearLayout linearLayout;
        public Button buttonService;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewServiceName = itemView.findViewById(R.id.textViewProviderServiceName);
            textViewServiceDesc = itemView.findViewById(R.id.textViewProviderServiceDesc);
            textViewServiceCategory = itemView.findViewById(R.id.textViewServiceCategory);
            textViewServiceCity = itemView.findViewById(R.id.textViewServiceCity);
            linearLayout = itemView.findViewById(R.id.LinearLayoutProviderService);
            buttonService = itemView.findViewById(R.id.buttonProviderService);
        }
    }
}
