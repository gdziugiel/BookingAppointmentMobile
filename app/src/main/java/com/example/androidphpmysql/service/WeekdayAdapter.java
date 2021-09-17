package com.example.androidphpmysql.service;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidphpmysql.R;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeekdayAdapter extends RecyclerView.Adapter<WeekdayAdapter.ViewHolder> {
    private final List<WeekdayListItem> listItems;

    public WeekdayAdapter(List<WeekdayListItem> listItems) {
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weekday_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeekdayListItem listItem = listItems.get(position);
        holder.checkBox.setText(listItem.getName());
        if (listItem.getWorkTimeId() != 0) {
            holder.checkBox.setChecked(true);
            holder.timeStart.setText(listItem.getTimeStart());
            holder.timeEnd.setText(listItem.getTimeEnd());
        }
        holder.textViewWorkTimeId.setText(String.valueOf(listItem.getWorkTimeId()));
        holder.timeStart.setOnClickListener(view -> showTimePicker(holder.timeStart));
        holder.timeEnd.setOnClickListener(view -> showTimePicker(holder.timeEnd));
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public EditText timeStart, timeEnd;
        public LinearLayout linearLayout;
        public TextView textViewWorkTimeId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            timeStart = itemView.findViewById(R.id.editTextStartTime);
            timeEnd = itemView.findViewById(R.id.editTextEndTime);
            linearLayout = itemView.findViewById(R.id.linearLayoutWorkingTime);
            timeStart.setInputType(InputType.TYPE_NULL);
            timeEnd.setInputType(InputType.TYPE_NULL);
            textViewWorkTimeId = itemView.findViewById(R.id.textViewWorkTimeId);
        }
    }

    private void showTimePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance((timePicker, i, i1, is24HourMode) -> {
            calendar.set(Calendar.HOUR_OF_DAY, i);
            calendar.set(Calendar.MINUTE, i1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            editText.setText(simpleDateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show(NewServiceActivity.getInstance().getSupportFragmentManager(), "TimePickerDialog");
        timePickerDialog.setThemeDark(true);
    }
}
