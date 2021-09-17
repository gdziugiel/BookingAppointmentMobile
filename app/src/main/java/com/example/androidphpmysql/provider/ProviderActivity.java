package com.example.androidphpmysql.provider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.service.NewServiceActivity;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.includes.Constants;
import com.example.androidphpmysql.service.ServiceListItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ProviderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView textViewProviderName, textViewProviderEmail;
    private ProviderServiceAdapter adapter;
    private List<ServiceListItem> listItems;
    private boolean setFreeTime, getCalendar;
    private int providerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        boolean isLogged = sharedPrefManager.getInstance(this).isLoggedIn();
        Intent intent = getIntent();
        providerId = intent.getIntExtra("provider_id", 0);
        setFreeTime = intent.getBooleanExtra("set_free_time", false);
        getCalendar = intent.getBooleanExtra("get_calendar", false);
        recyclerView = findViewById(R.id.recyclerViewProviderService);
        textViewProviderName = findViewById(R.id.textViewProviderName2);
        textViewProviderEmail = findViewById(R.id.textViewProviderEmail);
        LinearLayout linearLayoutProviderGeneral = findViewById(R.id.linearLayoutProviderGeneral);
        TextView textViewChooseService = findViewById(R.id.textViewChooseService);
        FloatingActionButton fab = findViewById(R.id.fabAddService);
        listItems = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (isLogged && !setFreeTime && !getCalendar) {
            fab.setVisibility(View.VISIBLE);
        } else if (isLogged) {
            textViewChooseService.setVisibility(View.VISIBLE);
            linearLayoutProviderGeneral.setVisibility(View.GONE);
        }
        loadData();
        fab.setOnClickListener(View -> {
            Intent newIntent = new Intent(this, NewServiceActivity.class);
            newIntent.putExtra("provider_id", providerId);
            finish();
            startActivity(newIntent);
        });
    }

    private void loadData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_PROVIDERS + "?id=" + providerId, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                textViewProviderName.setText(MessageFormat.format("{0} {1}", jsonObject.getString("provider_firstname"), jsonObject.getString("provider_lastname")));
                setTitle(MessageFormat.format("{0} {1}", jsonObject.getString("provider_firstname"), jsonObject.getString("provider_lastname")));
                textViewProviderEmail.setText(jsonObject.getString("email"));
                providerId = jsonObject.getInt("provider_id");
                JSONArray jsonArray = jsonObject.getJSONArray("services");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    ServiceListItem listItem = new ServiceListItem(object.getInt("service_id"), object.getString("service_name"), object.getString("service_description"), null, null, object.getString("category_name"), object.getString("city_name"));
                    listItems.add(listItem);
                }
                adapter = new ProviderServiceAdapter(listItems, getApplicationContext(), getCalendar ? 2 : (setFreeTime ? 1 : 0));
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