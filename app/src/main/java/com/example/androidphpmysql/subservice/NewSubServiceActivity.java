package com.example.androidphpmysql.subservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.main.MainActivity;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.includes.Constants;
import com.example.androidphpmysql.service.ServiceActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewSubServiceActivity extends AppCompatActivity {
    private EditText editTextNewSubServiceName, editTextNewSubServiceDesc, editTextNewSubServicePrice, editTextNewSubServiceDuration;
    private ProgressDialog progressDialog;
    private int serviceId, subServiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sub_service);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        if (!sharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTitle(getString(R.string.adding_sub_serrice));
        Intent intent = getIntent();
        subServiceId = intent.getIntExtra("sub_service_id", 0);
        serviceId = intent.getIntExtra("service_id", 0);
        editTextNewSubServiceName = findViewById(R.id.editTextNewSubServiceName);
        editTextNewSubServiceDesc = findViewById(R.id.editTextNewSubServiceDesc);
        editTextNewSubServicePrice = findViewById(R.id.editTextNewSubServicePrice);
        editTextNewSubServiceDuration = findViewById(R.id.editTextNewSubServiceDuration);
        Button buttonSaveSubService = findViewById(R.id.buttonSaveSubService);
        progressDialog = new ProgressDialog(this);
        if (subServiceId != 0) {
            loadData();
        }
        buttonSaveSubService.setOnClickListener(view -> saveSubService());
    }

    private void loadData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_SUB_SERVICES + "?id=" + subServiceId, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                editTextNewSubServiceName.setText(jsonObject.getString("sub_service_name"));
                setTitle(jsonObject.getString("sub_service_name"));
                editTextNewSubServiceDesc.setText(jsonObject.getString("sub_service_description"));
                editTextNewSubServicePrice.setText(jsonObject.getString("price"));
                editTextNewSubServiceDuration.setText(jsonObject.getString("duration"));
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

    private void saveSubService() {
        final String subServiceName = editTextNewSubServiceName.getText().toString().trim();
        final String subServiceDesc = editTextNewSubServiceDesc.getText().toString().trim();
        final String price = editTextNewSubServicePrice.getText().toString().trim();
        final String duration = editTextNewSubServiceDuration.getText().toString().trim();
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SUB_SERVICES, response -> {
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
                if (subServiceName.length() > 0 && subServiceDesc.length() > 0 && price.length() > 0 && duration.length() > 0) {
                    if (subServiceId == 0) {
                        params.put("edit", "0");
                        params.put("service_id", String.valueOf(serviceId));
                    } else {
                        params.put("edit", "1");
                        params.put("sub_service_id", String.valueOf(subServiceId));
                    }
                    params.put("sub_service_name", subServiceName);
                    params.put("sub_service_desc", subServiceDesc);
                    params.put("price", price);
                    params.put("duration", duration);
                }
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}