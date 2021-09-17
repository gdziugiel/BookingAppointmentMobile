package com.example.androidphpmysql.reservedservice;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private static CalendarActivity instance;
    private EditText editTextDateTime;
    private RecyclerView recyclerView;
    private ReservedServiceAdapter adapter;
    private List<ReservedServiceListItem> listItems;
    private Time time;
    private ProgressDialog progressDialog;
    private int serviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        if (!sharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        instance = this;
        setTitle(getString(R.string.calendar));
        Intent intent = getIntent();
        serviceId = intent.getIntExtra("service_id", 0);
        editTextDateTime = findViewById(R.id.editTextTimeCalendar);
        recyclerView = findViewById(R.id.recyclerViewReservedServices);
        listItems = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        editTextDateTime.setInputType(InputType.TYPE_NULL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadData();
        editTextDateTime.setOnClickListener(view -> showDatePicker());
    }

    public static CalendarActivity getInstance() {
        return instance;
    }

    private void loadData() {
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_DURATION + "?service=" + serviceId, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                time = new Time(0, jsonObject.getInt("duration"), jsonObject.getInt("duration"), getApplicationContext(), CalendarActivity.this);
                time.loadWorkTimeByService(serviceId);
                time.loadFreeTimeByService(serviceId, true);
                progressDialog.dismiss();
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

    public void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((datePicker, i, i1, i2) -> {
            calendar.set(Calendar.YEAR, i);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DAY_OF_MONTH, i2);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            editTextDateTime.setText(simpleDateFormat.format(calendar.getTime()));
            listItems.clear();
            recyclerView.setAdapter(null);
            loadRecyclerViewData(editTextDateTime.getText().toString().trim());
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setSelectableDays(time.getWeekdays());
        datePickerDialog.setDisabledDays(time.getFreeDays());
        datePickerDialog.setFirstDayOfWeek(2);
        datePickerDialog.setThemeDark(true);
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.setTitle(getString(R.string.choose_day));
        datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
    }

    private void loadRecyclerViewData(String date) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_RESERVED_SERVICE + "?service_id=" + serviceId + "&date=" + date, response -> {
            try {
                progressDialog.dismiss();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("reserved_services");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    ReservedServiceListItem listItem = new ReservedServiceListItem(object.getInt("reserved_service_id"), object.getString("sub_service_name"), object.getString("time"), object.getString("client_email"));
                    listItems.add(listItem);
                }
                adapter = new ReservedServiceAdapter(listItems, getApplicationContext());
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
}