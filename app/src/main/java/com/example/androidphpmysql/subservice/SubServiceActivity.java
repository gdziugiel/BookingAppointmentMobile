package com.example.androidphpmysql.subservice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.reservation.ReservationActivity;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.includes.Constants;
import com.example.androidphpmysql.service.ServiceActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class SubServiceActivity extends AppCompatActivity {
    private TextView textViewServiceName, textViewSubServiceName, textViewSubServiceDesc, textViewSubServicePrice, textViewSubServiceDuration;
    private ProgressDialog progressDialog;
    private int serviceId, subServiceId, duration, min_duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subservice);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        boolean isLogged = sharedPrefManager.getInstance(this).isLoggedIn();
        Intent intent = getIntent();
        subServiceId = intent.getIntExtra("sub_service_id", 0);
        textViewServiceName = findViewById(R.id.textViewServiceName3);
        textViewSubServiceName = findViewById(R.id.textViewSubServiceName2);
        textViewSubServiceDesc = findViewById(R.id.textViewSubServiceDesc2);
        textViewSubServicePrice = findViewById(R.id.textViewSubServicePrice);
        textViewSubServiceDuration = findViewById(R.id.textViewSubServiceDuration);
        Button buttonOrder = findViewById(R.id.buttonOrder);
        FloatingActionButton fabEdit = findViewById(R.id.fabEditSubService);
        FloatingActionButton fabDelete = findViewById(R.id.fabDeleteSubService);
        progressDialog = new ProgressDialog(this);
        if (isLogged) {
            buttonOrder.setVisibility(View.GONE);
        }
        if (isLogged) {
            fabEdit.setVisibility(View.VISIBLE);
            fabDelete.setVisibility(View.VISIBLE);
        } else {
            fabEdit.setVisibility(View.GONE);
            fabDelete.setVisibility(View.GONE);
        }
        buttonOrder.setOnClickListener(view -> {
            Intent newIntent = new Intent(getApplicationContext(), ReservationActivity.class);
            newIntent.putExtra("sub_service_id", subServiceId);
            newIntent.putExtra("duration", duration);
            newIntent.putExtra("min_duration", min_duration);
            startActivity(newIntent);
        });
        loadData();
        fabEdit.setOnClickListener(View -> {
            Intent newIntent = new Intent(this, NewSubServiceActivity.class);
            newIntent.putExtra("service_id", serviceId);
            newIntent.putExtra("sub_service_id", subServiceId);
            finish();
            startActivity(newIntent);
        });
        fabDelete.setOnClickListener(View -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SubServiceActivity.this);
            builder.setMessage(R.string.delete_confirm);
            builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> deleteSubService());
            builder.setNegativeButton(R.string.no, null);
            builder.show();
        });
    }

    private void loadData() {
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_SUB_SERVICES + "?id=" + subServiceId, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                textViewServiceName.setText(jsonObject.getString("service_name"));
                textViewSubServiceName.setText(jsonObject.getString("sub_service_name"));
                setTitle(jsonObject.getString("service_name") + " - " + jsonObject.getString("sub_service_name"));
                textViewSubServiceDesc.setText(jsonObject.getString("sub_service_description"));
                textViewSubServicePrice.setText(MessageFormat.format("{0} zł", jsonObject.getString("price")));
                textViewSubServiceDuration.setText(MessageFormat.format("{0} min", jsonObject.getString("duration")));
                duration = jsonObject.getInt("duration");
                min_duration = jsonObject.getInt("min_duration");
                serviceId = jsonObject.getInt("service_id");
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

    private void deleteSubService() {
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETE_SUB_SERVICE, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                if (jsonObject.getString("error").equals("false")) {
                    Intent intent = new Intent(this, ServiceActivity.class);
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
                params.put("id", String.valueOf(subServiceId));
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}