package com.example.androidphpmysql.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.other.AboutActivity;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.includes.Constants;
import com.example.androidphpmysql.profile.LoginActivity;
import com.example.androidphpmysql.profile.ProfileActivity;
import com.example.androidphpmysql.profile.RegisterActivity;
import com.example.androidphpmysql.reservedservice.ReservedServiceActivity;
import com.example.androidphpmysql.statics.CurrentCity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int REQUEST_CODE = 101;
    private RecyclerView recyclerView;
    private Spinner spinnerCity;
    private CategoryAdapter categoryAdapter;
    private CityAdapter cityAdapter;
    private City currentCity;
    private List<CategoryListItem> listItems;
    private List<City> cities;
    private ProgressDialog progressDialog;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        if (sharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        recyclerView = findViewById(R.id.recyclerViewCategory);
        spinnerCity = findViewById(R.id.spinnerCity);
        ExtendedFloatingActionButton fab = findViewById(R.id.fabYourReservations);
        currentCity = null;
        listItems = new ArrayList<>();
        cities = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadRecyclerViewData();
        checkLocationIsEnabled();
        detectCurrentLocation();
        loadCities();
        createNotificationChannel();
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                City city = (City) adapterView.getSelectedItem();
                changeCity(city);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        fab.setOnClickListener(View -> startActivity(new Intent(this, ReservedServiceActivity.class)));
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        CurrentCity.setLatitude(location.getLatitude());
        CurrentCity.setLongitude(location.getLongitude());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuLogin) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (item.getItemId() == R.id.menuRegister) {
            startActivity(new Intent(this, RegisterActivity.class));
        } else if (item.getItemId() == R.id.menuAbout) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        return true;
    }

    private void loadRecyclerViewData() {
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_CATEGORIES, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("categories");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    CategoryListItem listItem = new CategoryListItem(object.getInt("category_id"), object.getString("category_name"));
                    listItems.add(listItem);
                }
                categoryAdapter = new CategoryAdapter(listItems, getApplicationContext());
                recyclerView.setAdapter(categoryAdapter);
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

    private void checkLocationIsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.turn_on_gps).setCancelable(false).setPositiveButton(R.string.turn_on, (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).setNegativeButton(R.string.cancel, null).show();
        }
    }

    private void detectCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                CurrentCity.setLatitude(currentLocation.getLatitude());
                CurrentCity.setLongitude(currentLocation.getLongitude());
                findAddress();
            } else {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location2 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location2 == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                } else {
                    CurrentCity.setLatitude(location2.getLatitude());
                    CurrentCity.setLongitude(location2.getLongitude());
                    findAddress();
                }
            }
        });
    }

    private void findAddress() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(CurrentCity.getLatitude(), CurrentCity.getLongitude(), 1);
            CurrentCity.setCityName(addresses.get(0).getLocality());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCities() {
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_CITIES, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("cities");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    City city = new City(object.getInt("city_id"), object.getString("city_name"));
                    if (CurrentCity.getCityName() != null && CurrentCity.getCityName().equals(object.getString("city_name"))) {
                        currentCity = city;
                    }
                    cities.add(city);
                }
                cityAdapter = new CityAdapter(cities, getApplicationContext());
                spinnerCity.setAdapter(cityAdapter);
                if (currentCity != null) {
                    spinnerCity.setSelection(cityAdapter.getPosition(currentCity));
                }
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

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "ReservationAppointment";
            String description = "Reservation Appointment";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("notifyReservation", name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void changeCity(City cityListItem) {
        CurrentCity.setId(cityListItem.getId());
    }
}