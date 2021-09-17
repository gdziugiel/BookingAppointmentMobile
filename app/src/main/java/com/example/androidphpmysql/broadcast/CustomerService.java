package com.example.androidphpmysql.broadcast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.broadcast.CancelServiceBroadcast;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.includes.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class CustomerService extends Service {
    private Timer timer;
    private int reservedServiceId;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        reservedServiceId = intent.getIntExtra("id", 0);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                loadData();
            }
        }, 0, 5 * 60 * 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void loadData() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_RESERVED_SERVICE + "?id=" + reservedServiceId, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject object = jsonObject.getJSONObject("reserved_service");
                if (object.getInt("canceled") == 1) {
                    Intent intent = new Intent(getApplicationContext(), CancelServiceBroadcast.class);
                    intent.putExtra("id", reservedServiceId);
                    intent.putExtra("service_name", object.getString("service_name"));
                    intent.putExtra("sub_service_name", object.getString("sub_service_name"));
                    String dateTime = object.getString("date_time");
                    intent.putExtra("date_time", dateTime.substring(0, dateTime.length() - 3));
                    intent.putExtra("client_email", object.getString("client_email"));
                    @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, Intent.FILL_IN_DATA);
                    Calendar calendar = Calendar.getInstance();
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    timer.cancel();
                    stopSelf();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}