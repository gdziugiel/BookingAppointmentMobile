package com.example.androidphpmysql.freetime;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.time.Time;
import com.example.androidphpmysql.main.MainActivity;
import com.example.androidphpmysql.includes.Constants;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FreeTimeActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private static FreeTimeActivity instance;
    private CheckBox allDayCheckbox;
    private EditText editTextTimeStart, editTextTimeEnd;
    private RecyclerView recyclerView;
    private FreeTimeAdapter adapter;
    private List<FreeTimeListItem> listItems;
    private Time time;
    private ProgressDialog progressDialog;
    private int serviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_time);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        if (!sharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        instance = this;
        Intent intent = getIntent();
        serviceId = intent.getIntExtra("service_id", 0);
        allDayCheckbox = findViewById(R.id.allDayCheckbox);
        editTextTimeStart = findViewById(R.id.editTextStartFreeTime);
        editTextTimeEnd = findViewById(R.id.editTextEndFreeTime);
        recyclerView = findViewById(R.id.recyclerViewFreeDays);
        Button buttonSaveFreeTime = findViewById(R.id.buttonSaveFreeTime);
        editTextTimeStart.setInputType(InputType.TYPE_NULL);
        editTextTimeEnd.setInputType(InputType.TYPE_NULL);
        listItems = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadData();
        loadRecyclerViewData();
        editTextTimeStart.setOnClickListener(view -> showDatePicker(editTextTimeStart));
        editTextTimeEnd.setOnClickListener(view -> showDatePicker(editTextTimeEnd));
        buttonSaveFreeTime.setOnClickListener(view -> save());
    }

    public static FreeTimeActivity getInstance() {
        return instance;
    }

    private void loadData() {
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_DURATION + "?service=" + serviceId, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                time = new Time(0, jsonObject.getInt("duration"), jsonObject.getInt("duration"), getApplicationContext(), FreeTimeActivity.this);
                time.loadWorkTimeByService(serviceId);
                time.loadFreeTimeByService(serviceId, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        });
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void loadRecyclerViewData() {
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String date = simpleDateFormat.format(calendar.getTime());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_FREE_TIME + "?service=" + serviceId + "&date=" + date, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("free_time");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    FreeTimeListItem listItem = new FreeTimeListItem(object.getInt("free_time_id"), object.getString("date_time_start"), object.getString("date_time_end"), object.getInt("all_day") != 0);
                    listItems.add(listItem);
                }
                adapter = new FreeTimeAdapter(listItems);
                recyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        });
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((datePicker, i, i1, i2) -> {
            calendar.set(Calendar.YEAR, i);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DAY_OF_MONTH, i2);
            if (allDayCheckbox.isChecked()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                editText.setText(simpleDateFormat.format(calendar.getTime()));
            } else {
                showTimePicker(calendar, editText);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setSelectableDays(time.getWeekdays());
        datePickerDialog.setDisabledDays(time.getFreeDays());
        datePickerDialog.setFirstDayOfWeek(2);
        datePickerDialog.setThemeDark(true);
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.setTitle(getString(R.string.choose_day));
        datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
    }

    public void showTimePicker(Calendar calendar, EditText editText) {
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance((timePicker, i, i1, is24HourMode) -> {
            calendar.set(Calendar.HOUR_OF_DAY, i);
            calendar.set(Calendar.MINUTE, i1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            editText.setText(simpleDateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle(getString(R.string.choose_hour));
        timePickerDialog.show(getSupportFragmentManager(), "TimePickerDialog");
        Timepoint[] disableTimes = time.getDisableTime(calendar, 0);
        Timepoint[] workingTime = time.getWorkingTime(calendar.get(Calendar.DAY_OF_WEEK) - 1, disableTimes, calendar, 0);
        if (workingTime != null) {
            timePickerDialog.setDisabledTimes(disableTimes);
            timePickerDialog.setThemeDark(true);
            timePickerDialog.setMinTime(workingTime[0]);
            timePickerDialog.setMaxTime(workingTime[1]);
        } else {
            timePickerDialog.dismiss();
        }
    }

    private void save() {
        String start = editTextTimeStart.getText().toString().trim();
        String end = editTextTimeEnd.getText().toString().trim();
        if (allDayCheckbox.isChecked()) {
            if (start.length() > 10) {
                start = start.substring(0, start.length() - 6);
            }
            if (end.length() > 10) {
                end = end.substring(0, end.length() - 6);
            }
            start += " 00:00:00";
            end += " 23:59:59";
        } else {
            if (start.length() < 11) {
                start += " 00:00:00";
            }
            if (end.length() < 11) {
                end += " 23:59:59";
            }
        }
        final String startFinal = start;
        final String endFinal = end;
        final int allDay = allDayCheckbox.isChecked() ? 1 : 0;
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_FREE_TIME, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                if (jsonObject.getString("error").equals("false")) {
                    Intent intent = new Intent(this, FreeTimeActivity.class);
                    intent.putExtra("service_id", serviceId);
                    finish();
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            progressDialog.hide();
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Calendar timeStart = Calendar.getInstance();
                Calendar timeEnd = Calendar.getInstance();
                try {
                    timeStart.setTime(Objects.requireNonNull(simpleDateFormat.parse(startFinal)));
                    timeEnd.setTime(Objects.requireNonNull(simpleDateFormat.parse(endFinal)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (timeEnd.getTimeInMillis() <= timeStart.getTimeInMillis()) {
                    return null;
                }
                if (editTextTimeStart.length() > 0 && editTextTimeEnd.length() > 0) {
                    params.put("time_start", startFinal);
                    params.put("time_end", endFinal);
                    params.put("all_day", String.valueOf(allDay));
                    params.put("service_id", String.valueOf(serviceId));
                }
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void deleteFreeTime(int id) {
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_FREE_TIME, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                if (jsonObject.getString("error").equals("false")) {
                    Intent intent = new Intent(this, FreeTimeActivity.class);
                    intent.putExtra("service_id", serviceId);
                    finish();
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            progressDialog.hide();
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}