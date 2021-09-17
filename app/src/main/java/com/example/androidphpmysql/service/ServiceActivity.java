package com.example.androidphpmysql.service;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.other.MapsActivity;
import com.example.androidphpmysql.subservice.NewSubServiceActivity;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.includes.Constants;
import com.example.androidphpmysql.provider.ProviderActivity;
import com.example.androidphpmysql.statics.Address;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceActivity extends AppCompatActivity {
    private ImageView rating1, rating2, rating3, rating4, rating5;
    private RecyclerView recyclerView;
    private TextView textViewServiceName, textViewServiceProvider, textViewServiceDesc, textViewServiceAddress, textViewServiceEmail, textViewServicePhoneNumber, textViewServiceWorkTime;
    private SubServiceAdapter adapter;
    private List<SubServiceListItem> listItems;
    private ProgressDialog progressDialog;
    private int serviceId, providerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        boolean isLogged = sharedPrefManager.getInstance(this).isLoggedIn();
        Intent intent = getIntent();
        serviceId = intent.getIntExtra("service_id", 0);
        rating1 = findViewById(R.id.rating1);
        rating2 = findViewById(R.id.rating2);
        rating3 = findViewById(R.id.rating3);
        rating4 = findViewById(R.id.rating4);
        rating5 = findViewById(R.id.rating5);
        recyclerView = findViewById(R.id.recyclerViewSubService);
        textViewServiceName = findViewById(R.id.textViewServiceName2);
        textViewServiceProvider = findViewById(R.id.textViewServiceProvider2);
        textViewServiceDesc = findViewById(R.id.textViewServiceDesc2);
        textViewServiceAddress = findViewById(R.id.textViewServiceAddress);
        textViewServiceEmail = findViewById(R.id.textViewServiceEmail);
        textViewServicePhoneNumber = findViewById(R.id.textViewServicePhoneNumber);
        textViewServiceWorkTime = findViewById(R.id.textViewServiceWorkTime);
        Button buttonMap = findViewById(R.id.buttonMap);
        FloatingActionButton fabEdit = findViewById(R.id.fabEditService);
        FloatingActionButton fabDelete = findViewById(R.id.fabDeleteService);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddSubService);
        listItems = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (isLogged) {
            fabEdit.setVisibility(View.VISIBLE);
            fabDelete.setVisibility(View.VISIBLE);
            fabAdd.setVisibility(View.VISIBLE);
        } else {
            fabEdit.setVisibility(View.GONE);
            fabDelete.setVisibility(View.GONE);
            fabAdd.setVisibility(View.GONE);
        }
        loadData();
        buttonMap.setOnClickListener(view -> {
            Address.setAddress(textViewServiceAddress.getText().toString().trim());
            startActivity(new Intent(this, MapsActivity.class));
        });
        textViewServiceProvider.setOnClickListener(view -> {
            Intent newIntent = new Intent(getApplicationContext(), ProviderActivity.class);
            newIntent.putExtra("provider_id", providerId);
            startActivity(newIntent);
        });
        fabEdit.setOnClickListener(View -> {
            Intent newIntent = new Intent(this, NewServiceActivity.class);
            newIntent.putExtra("provider_id", providerId);
            newIntent.putExtra("service_id", serviceId);
            finish();
            startActivity(newIntent);
        });
        fabDelete.setOnClickListener(View -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActivity.this);
            builder.setMessage(R.string.delete_confirm);
            builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> deleteService());
            builder.setNegativeButton(R.string.no, null);
            builder.show();
        });
        fabAdd.setOnClickListener(View -> {
            Intent newIntent = new Intent(this, NewSubServiceActivity.class);
            newIntent.putExtra("service_id", serviceId);
            finish();
            startActivity(newIntent);
        });
    }

    private void loadData() {
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_SERVICES + "?id=" + serviceId, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                textViewServiceName.setText(jsonObject.getString("service_name"));
                setTitle(jsonObject.getString("service_name"));
                textViewServiceProvider.setText(MessageFormat.format("{0} {1}", jsonObject.getString("provider_firstname"), jsonObject.getString("provider_lastname")));
                textViewServiceProvider.getPaint().setUnderlineText(true);
                textViewServiceDesc.setText(jsonObject.getString("service_description"));
                textViewServiceAddress.setText(MessageFormat.format("{0}, {1}", jsonObject.getString("address"), jsonObject.getString("city_name")));
                textViewServiceEmail.setText(jsonObject.getString("service_email"));
                textViewServicePhoneNumber.setText(jsonObject.getString("phone_number"));
                Linkify.addLinks(textViewServicePhoneNumber, Patterns.PHONE, "tel:", Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter);
                textViewServicePhoneNumber.setMovementMethod(LinkMovementMethod.getInstance());
                providerId = jsonObject.getInt("provider_id");
                String rating = jsonObject.getString("rating");
                if (!rating.equals("null")) {
                    switch (Integer.parseInt(rating)) {
                        case 5:
                            rating5.setImageResource(R.drawable.bg_button_star);
                        case 4:
                            rating4.setImageResource(R.drawable.bg_button_star);
                        case 3:
                            rating3.setImageResource(R.drawable.bg_button_star);
                        case 2:
                            rating2.setImageResource(R.drawable.bg_button_star);
                        case 1:
                            rating1.setImageResource(R.drawable.bg_button_star);
                    }
                }
                JSONArray jsonArray = jsonObject.getJSONArray("sub_services");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    SubServiceListItem listItem = new SubServiceListItem(object.getInt("sub_service_id"), object.getString("sub_service_name"), object.getString("sub_service_description"));
                    listItems.add(listItem);
                }
                JSONArray jsonArrayWorkTime = jsonObject.getJSONArray("work_time");
                StringBuilder workTime = new StringBuilder();
                for (int i = 0; i < jsonArrayWorkTime.length(); i++) {
                    JSONObject object = jsonArrayWorkTime.getJSONObject(i);
                    String timeStart = object.getString("time_start").substring(0, 5);
                    String timeEnd = object.getString("time_end").substring(0, 5);
                    workTime.append(object.getString("day_name")).append(": ").append(timeStart).append(" - ").append(timeEnd).append("\n");
                }
                textViewServiceWorkTime.setText(workTime.toString());
                adapter = new SubServiceAdapter(listItems, getApplicationContext());
                recyclerView.setAdapter(adapter);
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

    private void deleteService() {
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETE_SERVICE, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                if (jsonObject.getString("error").equals("false")) {
                    Intent intent = new Intent(this, ProviderActivity.class);
                    intent.putExtra("provider_id", providerId);
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
                params.put("id", String.valueOf(serviceId));
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}