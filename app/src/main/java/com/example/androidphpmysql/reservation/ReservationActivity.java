package com.example.androidphpmysql.reservation;


import static com.example.androidphpmysql.R.string.invalid_phone_number;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.broadcast.CustomerService;
import com.example.androidphpmysql.other.JavaMailAPI;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.time.Time;
import com.example.androidphpmysql.broadcast.ReminderBroadcast;
import com.example.androidphpmysql.includes.Constants;
import com.example.androidphpmysql.profile.ProfileActivity;
import com.example.androidphpmysql.reservedservice.ReservedServiceActivity;
import com.example.androidphpmysql.statics.Validation;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ReservationActivity extends AppCompatActivity {
    private static ReservationActivity instance;
    private EditText editTextClientFirstname, editTextClientLastname, editTextClientEmail, editTextClientPhoneNumber, editTextDateTime;
    private Time time;
    private ProgressDialog progressDialog;
    private int subServiceId, minDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        if (sharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        instance = this;
        setTitle(getString(R.string.service_reservation));
        Intent intent = getIntent();
        subServiceId = intent.getIntExtra("sub_service_id", 0);
        minDuration = intent.getIntExtra("min_duration", 0);
        int duration = intent.getIntExtra("duration", 0);
        editTextClientFirstname = findViewById(R.id.editTextClientFirstname);
        editTextClientLastname = findViewById(R.id.editTextClientLastname);
        editTextClientEmail = findViewById(R.id.editTextClientEmail);
        editTextClientPhoneNumber = findViewById(R.id.editTextClientPhoneNumber);
        editTextDateTime = findViewById(R.id.editTextTime);
        Button buttonReservation = findViewById(R.id.buttonReservation);
        Button buttonGenerate = findViewById(R.id.buttonGenerate);
        time = new Time(subServiceId, duration, minDuration, getApplicationContext(), ReservationActivity.this);
        progressDialog = new ProgressDialog(this);
        editTextDateTime.setInputType(InputType.TYPE_NULL);
        time.loadWorkTime();
        time.loadFreeTime();
        editTextDateTime.setOnClickListener(view -> showDatePicker());
        buttonReservation.setOnClickListener(view -> bookAppointment());
        buttonGenerate.setOnClickListener(view -> showDialogAlert(0, null));
    }

    public static ReservationActivity getInstance() {
        return instance;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((datePicker, i, i1, i2) -> {
            calendar.set(Calendar.YEAR, i);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DAY_OF_MONTH, i2);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            time.loadReservedTime(simpleDateFormat.format(calendar.getTime()), calendar.get(Calendar.DAY_OF_WEEK) - 1, 0);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setSelectableDays(time.getWeekdays());
        datePickerDialog.setDisabledDays(time.getFreeDays());
        datePickerDialog.setFirstDayOfWeek(2);
        datePickerDialog.setThemeDark(true);
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.setTitle(getString(R.string.choose_day));
        datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
    }

    public void showTimePicker(Calendar calendar) {
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance((timePicker, i, i1, is24HourMode) -> {
            calendar.set(Calendar.HOUR_OF_DAY, i);
            calendar.set(Calendar.MINUTE, i1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            editTextDateTime.setText(simpleDateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle(getString(R.string.choose_hour));
        timePickerDialog.show(getSupportFragmentManager(), "TimePickerDialog");
        double interval = (double) minDuration / 60;
        int hour = (int) interval;
        int minute = (int) ((interval - hour) * 60);
        if (minute == 0) {
            timePickerDialog.setTimeInterval(hour);
        } else {
            timePickerDialog.setTimeInterval(hour == 0 ? 1 : hour, minute);
        }
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

    private void showDialogAlert(int option, Calendar calendar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReservationActivity.this);
        switch (option) {
            case 0:
                builder.setMessage(R.string.excluding_date);
                builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> showExcludingDatePicker());
                builder.setNegativeButton(R.string.no, (dialogInterface, i) -> time.startGenerate());
                break;
            case 1:
                builder.setMessage(R.string.excluding_all_day);
                builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    time.saveExcludingTime(calendar, null);
                    showDialogAlert(2, null);
                });
                builder.setNegativeButton(R.string.no, (dialogInterface, i) -> showExcludingTimePicker(true, calendar));
                break;
            case 2:
                builder.setMessage(R.string.excluding_next_date);
                builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> showExcludingDatePicker());
                builder.setNegativeButton(R.string.no, (dialogInterface, i) -> time.startGenerate());
                break;
        }
        builder.show();
    }

    private void showExcludingDatePicker() {
        Calendar calendar = Calendar.getInstance();
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((datePicker, i, i1, i2) -> {
            calendar.set(Calendar.YEAR, i);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DAY_OF_MONTH, i2);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            time.loadReservedTime(simpleDateFormat.format(calendar.getTime()), calendar.get(Calendar.DAY_OF_WEEK) - 1, 2);
            showDialogAlert(1, calendar);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setSelectableDays(time.getWeekdays());
        datePickerDialog.setDisabledDays(time.getFreeDays());
        datePickerDialog.setFirstDayOfWeek(2);
        datePickerDialog.setThemeDark(true);
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.setTitle(getString(R.string.exclude_date));
        datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
    }

    private void showExcludingTimePicker(boolean start, Calendar startTime) {
        Calendar calendar = (Calendar) startTime.clone();
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance((timePicker, i, i1, is24HourMode) -> {
            calendar.set(Calendar.HOUR_OF_DAY, i);
            calendar.set(Calendar.MINUTE, i1);
            if (start) {
                showExcludingTimePicker(false, calendar);
            } else {
                time.saveExcludingTime(startTime, calendar);
                showDialogAlert(2, null);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        if (start) {
            timePickerDialog.setTitle(getString(R.string.exclude_start_time));
        } else {
            timePickerDialog.setTitle(getString(R.string.exclude_end_time));
        }
        timePickerDialog.show(getSupportFragmentManager(), "TimePickerDialog");
        double interval = (double) minDuration / 60;
        int hour = (int) interval;
        int minute = (int) ((interval - hour) * 60);
        if (minute == 0) {
            timePickerDialog.setTimeInterval(hour);
        } else {
            timePickerDialog.setTimeInterval(hour == 0 ? 1 : hour, minute);
        }
        Timepoint[] disableTimes = time.getDisableTime(calendar, start ? 0 : 1);
        Timepoint[] workingTime = time.getWorkingTime(calendar.get(Calendar.DAY_OF_WEEK) - 1, disableTimes, calendar, start ? 0 : 1);
        if (workingTime != null) {
            timePickerDialog.setDisabledTimes(disableTimes);
            timePickerDialog.setThemeDark(true);
            if (start) {
                timePickerDialog.setMinTime(workingTime[0]);
            } else {
                timePickerDialog.setMinTime(new Timepoint(Objects.requireNonNull(startTime).get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE)));
            }
            timePickerDialog.setMaxTime(workingTime[1]);
        } else {
            timePickerDialog.dismiss();
        }
    }

    private void bookAppointment() {
        final String clientFirstname = editTextClientFirstname.getText().toString().trim();
        final String clientLastname = editTextClientLastname.getText().toString().trim();
        final String clientEmail = editTextClientEmail.getText().toString().trim();
        final String clientPhoneNumber = editTextClientPhoneNumber.getText().toString().trim();
        final String dateTime = editTextDateTime.getText().toString().trim() + ":00";
        if (!Validation.isValidEmail(clientEmail)) {
            Toast.makeText(getApplicationContext(), R.string.invalid_email, Toast.LENGTH_SHORT).show();
            return;
        } else if (!Validation.isValidPhoneNumber(clientPhoneNumber)) {
            Toast.makeText(getApplicationContext(), invalid_phone_number, Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage(getString(R.string.booking));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_RESERVATION, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("error").equals("false")) {
                    Toast.makeText(getApplicationContext(), MessageFormat.format("{0}{1}{2}", jsonObject.getString("message"), getString(R.string.number_of_reservation), jsonObject.getString("id")), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), ReminderBroadcast.class);
                    intent.putExtra("id", jsonObject.getInt("id"));
                    intent.putExtra("service_name", jsonObject.getString("service_name"));
                    intent.putExtra("sub_service_name", jsonObject.getString("sub_service_name"));
                    intent.putExtra("date_time", editTextDateTime.getText().toString().trim());
                    intent.putExtra("client_email", clientEmail);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, Intent.FILL_IN_DATA);
                    Calendar calendar = Calendar.getInstance();
                    Calendar today = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    try {
                        calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(dateTime)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) + 1) {
                        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
                    } else {
                        calendar.add(Calendar.DATE, -1);
                    }
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Intent serviceIntent = new Intent(getApplicationContext(), CustomerService.class);
                    serviceIntent.putExtra("id", jsonObject.getInt("id"));
                    getApplicationContext().startService(serviceIntent);
                    sendMail(clientEmail, jsonObject.getString("service_name"), jsonObject.getString("sub_service_name"), editTextDateTime.getText().toString().trim());
                    Intent newIntent = new Intent(getApplicationContext(), ReservedServiceActivity.class);
                    newIntent.putExtra("id", jsonObject.getInt("id"));
                    newIntent.putExtra("email", clientEmail);
                    finish();
                    startActivity(newIntent);
                } else {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                if (clientFirstname.length() > 0 && clientLastname.length() > 0 && clientEmail.length() > 0 && clientPhoneNumber.length() > 0) {
                    params.put("client_firstname", clientFirstname);
                    params.put("client_lastname", clientLastname);
                    params.put("client_email", clientEmail);
                    params.put("client_phone_number", clientPhoneNumber);
                    params.put("date_time", dateTime);
                    params.put("sub_service_id", String.valueOf(subServiceId));
                }
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void sendMail(String email, String serviceName, String subServiceName, String dateTime) {
        String subject = getString(R.string.booked_service);
        String message = getString(R.string.booked_service) + "\n" + serviceName + " - " + subServiceName + "\n" + dateTime;
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email, subject, message, 0);
        javaMailAPI.execute();
    }
}