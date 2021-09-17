package com.example.androidphpmysql.service;

import static com.example.androidphpmysql.R.string.no_selected_service;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.profile.ProfileActivity;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.includes.Constants;
import com.example.androidphpmysql.provider.ProvidersActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ServicesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private List<ServiceListItem> listItems;
    private int category, city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        if (sharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTitle(getString(R.string.service_list));
        Intent intent = getIntent();
        category = intent.getIntExtra("category", 0);
        city = intent.getIntExtra("city", 0);
        recyclerView = findViewById(R.id.recyclerViewService);
        Button buttonProviders = findViewById(R.id.buttonProviders);
        listItems = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadRecyclerViewData();
        buttonProviders.setOnClickListener(view -> {
            Intent newIntent = new Intent(getApplicationContext(), ProvidersActivity.class);
            newIntent.putExtra("category", category);
            newIntent.putExtra("city", city);
            startActivity(newIntent);
        });
    }

    private void loadRecyclerViewData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_SERVICES + "?category=" + category + "&city=" + city, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("services");
                if (jsonArray.length() == 0) {
                    Toast.makeText(this, no_selected_service, Toast.LENGTH_SHORT).show();
                    finish();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    ServiceListItem listItem = new ServiceListItem(object.getInt("service_id"), object.getString("service_name"), object.getString("service_description"), object.getString("provider_firstname"), object.getString("provider_lastname"), null, null);
                    listItems.add(listItem);
                }
                adapter = new ServiceAdapter(listItems, getApplicationContext());
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