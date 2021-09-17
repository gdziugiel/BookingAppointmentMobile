package com.example.androidphpmysql.freetime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidphpmysql.R;

import java.text.MessageFormat;
import java.util.List;

public class FreeTimeAdapter extends RecyclerView.Adapter<FreeTimeAdapter.ViewHolder> {
    private final List<FreeTimeListItem> listItems;

    public FreeTimeAdapter(List<FreeTimeListItem> listItems) {
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.free_time_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FreeTimeListItem listItem = listItems.get(position);
        String startTime = listItem.getTimeStart();
        String endTime = listItem.getTimeEnd();
        if (listItem.isAllDay()) {
            holder.textViewFreeTime.setText(MessageFormat.format("{0} - {1}", startTime.substring(0, startTime.length() - 9), endTime.substring(0, endTime.length() - 9)));
        } else {
            holder.textViewFreeTime.setText(MessageFormat.format("{0} - {1}", startTime.substring(0, startTime.length() - 3), endTime.substring(0, endTime.length() - 3)));
        }
        holder.textViewFreeTimeDelete.setOnClickListener(view -> FreeTimeActivity.getInstance().deleteFreeTime(listItem.getId()));
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;
        public TextView textViewFreeTime, textViewFreeTimeDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayoutWorkingTime);
            textViewFreeTime = itemView.findViewById(R.id.textViewFreeTime);
            textViewFreeTimeDelete = itemView.findViewById(R.id.textViewFreeTimeDelete);
        }
    }
}
