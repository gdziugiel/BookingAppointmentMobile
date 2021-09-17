package com.example.androidphpmysql.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.includes.Constants;
import com.example.androidphpmysql.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;

public class StatisticsActivity extends AppCompatActivity {
    private TextView textViewNewClients, textViewOldClients, textViewMostPopularHours, textViewMostPopularHoursSum, textViewMostPopularServices, textViewMostPopularServicesSum, textViewMostPopularSubServices, textViewMostPopularSubServicesSum, textViewMostProfitableServices, textViewMostProfitableServicesSum, textViewMostProfitableSubServices, textViewMostProfitableSubServicesSum, textViewRealisedReservations, textViewUnrealisedReservations, textViewCanceledReservations, textViewRatingsAvg;
    private ProgressDialog progressDialog;
    private int providerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        if (!sharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTitle(getString(R.string.statistics));
        Intent intent = getIntent();
        providerId = intent.getIntExtra("provider_id", 0);
        textViewNewClients = findViewById(R.id.textViewNewClients);
        textViewOldClients = findViewById(R.id.textViewOldClients);
        textViewMostPopularHours = findViewById(R.id.textViewMostPopularHours);
        textViewMostPopularHoursSum = findViewById(R.id.textViewMostPopularHoursSum);
        textViewMostPopularServices = findViewById(R.id.textViewMostPopularServices);
        textViewMostPopularServicesSum = findViewById(R.id.textViewMostPopularServicesSum);
        textViewMostPopularSubServices = findViewById(R.id.textViewMostPopularSubServices);
        textViewMostPopularSubServicesSum = findViewById(R.id.textViewMostPopularSubServicesSum);
        textViewMostProfitableServices = findViewById(R.id.textViewMostProfitableServices);
        textViewMostProfitableServicesSum = findViewById(R.id.textViewMostProfitableServicesSum);
        textViewMostProfitableSubServices = findViewById(R.id.textViewMostProfitableSubServices);
        textViewMostProfitableSubServicesSum = findViewById(R.id.textViewMostProfitableSubServicesSum);
        textViewRealisedReservations = findViewById(R.id.textViewRealisedReservations);
        textViewUnrealisedReservations = findViewById(R.id.textViewUnrealisedReservations);
        textViewCanceledReservations = findViewById(R.id.textViewCanceledReservations);
        textViewRatingsAvg = findViewById(R.id.textViewRatingsAvg);
        progressDialog = new ProgressDialog(this);
        loadData();
    }

    private void loadData() {
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_STATISTICS + "?id=" + providerId, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                textViewNewClients.setText(jsonObject.getString("new_clients"));
                textViewOldClients.setText(jsonObject.getString("old_clients"));
                JSONArray jsonArray = jsonObject.getJSONArray("most_popular_hours");
                for (int i = 0; i < (Math.min(jsonArray.length(), 3)); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String time = object.getString("time");
                    textViewMostPopularHours.append(MessageFormat.format("{0}\n", time.substring(0, time.length() - 3)));
                    textViewMostPopularHoursSum.append(MessageFormat.format("{0}\n", object.getString("sum")));
                }
                jsonArray = jsonObject.getJSONArray("most_popular_services");
                for (int i = 0; i < (Math.min(jsonArray.length(), 3)); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    textViewMostPopularServices.append(MessageFormat.format("{0}\n", object.getString("service_name")));
                    textViewMostPopularServicesSum.append(MessageFormat.format("{0}\n", object.getString("sum")));
                }
                jsonArray = jsonObject.getJSONArray("most_popular_sub_services");
                for (int i = 0; i < (Math.min(jsonArray.length(), 3)); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    textViewMostPopularSubServices.append(MessageFormat.format("{0}\n- {1}\n", object.getString("service_name"), object.getString("sub_service_name")));
                    textViewMostPopularSubServicesSum.append(MessageFormat.format("{0}\n", object.getString("sum")));
                }
                jsonArray = jsonObject.getJSONArray("most_profitable_services");
                for (int i = 0; i < (Math.min(jsonArray.length(), 3)); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    textViewMostProfitableServices.append(MessageFormat.format("{0}\n", object.getString("service_name")));
                    textViewMostProfitableServicesSum.append(MessageFormat.format("{0} zł\n", object.getString("sum")));
                }
                jsonArray = jsonObject.getJSONArray("most_profitable_sub_services");
                for (int i = 0; i < (Math.min(jsonArray.length(), 3)); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    textViewMostProfitableSubServices.append(MessageFormat.format("{0}\n- {1}\n", object.getString("service_name"), object.getString("sub_service_name")));
                    textViewMostProfitableSubServicesSum.append(MessageFormat.format("{0} zł\n", object.getString("sum")));
                }
                textViewRealisedReservations.setText(jsonObject.getString("realised"));
                textViewUnrealisedReservations.setText(jsonObject.getString("unrealised"));
                textViewCanceledReservations.setText(jsonObject.getString("canceled"));
                textViewRatingsAvg.setText(!jsonObject.getString("rating").equals("null") ? jsonObject.getString("rating") : "0");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }, error -> {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        });
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}