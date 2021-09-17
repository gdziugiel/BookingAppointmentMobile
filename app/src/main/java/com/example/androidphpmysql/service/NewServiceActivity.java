package com.example.androidphpmysql.service;

import static com.example.androidphpmysql.R.string.invalid_email;
import static com.example.androidphpmysql.R.string.invalid_phone_number;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.main.MainActivity;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.main.CityAdapter;
import com.example.androidphpmysql.includes.Constants;
import com.example.androidphpmysql.main.CategoryListItem;
import com.example.androidphpmysql.main.City;
import com.example.androidphpmysql.provider.ProviderActivity;
import com.example.androidphpmysql.statics.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class NewServiceActivity extends AppCompatActivity {
    private static NewServiceActivity instance;
    private EditText editTextNewServiceName, editTextNewServiceDesc, editTextNewServiceAddress, editTextNewServiceEmail, editTextNewServicePhoneNumber, editTextNewCategory, editTextNewCity;
    private LinearLayout linearLayoutNewCategory, linearLayoutNewCity, linearLayoutWorkingTime;
    private RecyclerView recyclerView;
    private Spinner spinnerCategory, spinnerCity;
    private CategoryAdapterSpinner categoryAdapter;
    private CityAdapter cityAdapter;
    private WeekdayAdapter weekdayAdapter;
    private List<CategoryListItem> categories;
    private List<City> cities;
    private List<WeekdayListItem> weekdayListItems;
    private ProgressDialog progressDialog;
    private boolean workTimeChanged;
    private int serviceId, providerId, categoryId, cityId;
    private String currentCategoryName, currentCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_service);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        if (!sharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        instance = this;
        setTitle(getString(R.string.adding_service));
        Intent intent = getIntent();
        providerId = intent.getIntExtra("provider_id", 0);
        serviceId = intent.getIntExtra("service_id", 0);
        editTextNewServiceName = findViewById(R.id.editTextNewServiceName);
        editTextNewServiceDesc = findViewById(R.id.editTextNewServiceDesc);
        editTextNewServiceAddress = findViewById(R.id.editTextNewServiceAddress);
        editTextNewServiceEmail = findViewById(R.id.editTextNewServiceEmail);
        editTextNewServicePhoneNumber = findViewById(R.id.editTextNewServicePhoneNumber);
        editTextNewCategory = findViewById(R.id.editTextNewCategory);
        editTextNewCity = findViewById(R.id.editTextNewCity);
        linearLayoutNewCategory = findViewById(R.id.linearLayoutNewCategory);
        linearLayoutNewCity = findViewById(R.id.linearLayoutNewCity);
        linearLayoutWorkingTime = findViewById(R.id.linearLayoutWorkingTime);
        recyclerView = findViewById(R.id.recyclerViewWorkTime);
        spinnerCategory = findViewById(R.id.spinnerCategoryNewService);
        spinnerCity = findViewById(R.id.spinnerCityNewService);
        Button buttonAddCategory = findViewById(R.id.newCategory);
        Button buttonAddCity = findViewById(R.id.newCity);
        Button buttonSaveCategory = findViewById(R.id.addCategory);
        Button buttonSaveCity = findViewById(R.id.addCity);
        Button buttonSaveService = findViewById(R.id.buttonSaveService);
        Button buttonEditWorkingTime = findViewById(R.id.editWorkingTime);
        Button buttonSaveWorkingTime = findViewById(R.id.buttonSaveWorkingTime);
        categories = new ArrayList<>();
        cities = new ArrayList<>();
        weekdayListItems = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        workTimeChanged = false;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (serviceId == 0) {
            buttonSaveWorkingTime.setVisibility(View.GONE);
        }
        if (serviceId != 0) {
            loadData();
        }
        loadCategories();
        loadCities();
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CategoryListItem category = (CategoryListItem) adapterView.getSelectedItem();
                categoryId = category.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                City city = (City) adapterView.getSelectedItem();
                cityId = city.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        buttonAddCategory.setOnClickListener(view -> linearLayoutNewCategory.setVisibility(android.view.View.VISIBLE));
        buttonAddCity.setOnClickListener(view -> linearLayoutNewCity.setVisibility(android.view.View.VISIBLE));
        buttonSaveCategory.setOnClickListener(view -> addCategory());
        buttonSaveCity.setOnClickListener(view -> addCity());
        buttonSaveService.setOnClickListener(view -> saveService());
        buttonEditWorkingTime.setOnClickListener(view -> {
            if (linearLayoutWorkingTime.getVisibility() == View.GONE) {
                linearLayoutWorkingTime.setVisibility(android.view.View.VISIBLE);
                if (!workTimeChanged) {
                    loadWeekdays();
                }
            }
        });
        buttonSaveWorkingTime.setOnClickListener(view -> saveWorkTime(0));
    }

    public static NewServiceActivity getInstance() {
        return instance;
    }

    private void loadData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_SERVICES + "?id=" + serviceId, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                editTextNewServiceName.setText(jsonObject.getString("service_name"));
                setTitle(jsonObject.getString("service_name"));
                editTextNewServiceDesc.setText(jsonObject.getString("service_description"));
                editTextNewServiceAddress.setText(jsonObject.getString("address"));
                editTextNewServiceEmail.setText(jsonObject.getString("service_email"));
                editTextNewServicePhoneNumber.setText(jsonObject.getString("phone_number"));
                currentCategoryName = jsonObject.getString("category_name");
                currentCityName = jsonObject.getString("city_name");
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

    private void loadCategories() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_CATEGORIES, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("categories");
                CategoryListItem currentCategory = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    CategoryListItem category = new CategoryListItem(object.getInt("category_id"), object.getString("category_name"));
                    if (currentCategoryName != null && currentCategoryName.equals(object.getString("category_name"))) {
                        currentCategory = category;
                    }
                    categories.add(category);
                }
                categoryAdapter = new CategoryAdapterSpinner(categories, getApplicationContext());
                spinnerCategory.setAdapter(categoryAdapter);
                if (currentCategory != null) {
                    spinnerCategory.setSelection(categoryAdapter.getPosition(currentCategory));
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

    private void loadCities() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_CITIES, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("cities");
                City currentCity = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    City city = new City(object.getInt("city_id"), object.getString("city_name"));
                    if (currentCityName != null && currentCityName.equals(object.getString("city_name"))) {
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

    private void addCategory() {
        final String categoryName = editTextNewCategory.getText().toString().trim();
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CATEGORIES, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                if (jsonObject.getString("error").equals("false")) {
                    editTextNewCategory.setText("");
                    linearLayoutNewCategory.setVisibility(View.GONE);
                    spinnerCategory.setAdapter(null);
                    categoryAdapter.clear();
                    loadCategories();
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
                if (categoryName.length() > 0) {
                    params.put("category_name", categoryName);
                }
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void addCity() {
        final String cityName = editTextNewCity.getText().toString().trim();
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CITIES, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                if (jsonObject.getString("error").equals("false")) {
                    editTextNewCity.setText("");
                    linearLayoutNewCity.setVisibility(View.GONE);
                    spinnerCity.setAdapter(null);
                    cityAdapter.clear();
                    loadCities();
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
                if (cityName.length() > 0) {
                    params.put("city_name", cityName);
                }
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void saveService() {
        final String serviceName = editTextNewServiceName.getText().toString().trim();
        final String serviceDesc = editTextNewServiceDesc.getText().toString().trim();
        final String address = editTextNewServiceAddress.getText().toString().trim();
        final String email = editTextNewServiceEmail.getText().toString().trim();
        final String phoneNumber = editTextNewServicePhoneNumber.getText().toString().trim();
        if (!Validation.isValidEmail(email)) {
            Toast.makeText(getApplicationContext(), invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Validation.isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(getApplicationContext(), invalid_phone_number, Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVICES, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                if (jsonObject.getString("error").equals("false")) {
                    if (linearLayoutWorkingTime.getVisibility() == View.VISIBLE && serviceId == 0) {
                        saveWorkTime(jsonObject.getInt("id"));
                    } else {
                        Intent intent = new Intent(this, ProviderActivity.class);
                        intent.putExtra("provider_id", providerId);
                        finish();
                        startActivity(intent);
                    }
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
                if (serviceName.length() > 0 && serviceDesc.length() > 0 && email.length() > 0 && address.length() > 0 && phoneNumber.length() > 0) {
                    if (serviceId == 0) {
                        params.put("edit", "0");
                        params.put("provider_id", String.valueOf(providerId));
                    } else {
                        params.put("edit", "1");
                        params.put("service_id", String.valueOf(serviceId));
                    }
                    params.put("service_name", serviceName);
                    params.put("service_desc", serviceDesc);
                    params.put("address", address);
                    params.put("email", email);
                    params.put("phone_number", phoneNumber);
                    params.put("category_id", String.valueOf(categoryId));
                    params.put("city_id", String.valueOf(cityId));
                }
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void loadWeekdays() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, serviceId != 0 ? (Constants.URL_WEEKDAYS + "?id=" + serviceId) : Constants.URL_WEEKDAYS, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("weekdays");
                JSONArray jsonArray2 = null;
                if (serviceId != 0) {
                    jsonArray2 = jsonObject.getJSONArray("work_time");
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    boolean workingDay = false;
                    if (serviceId != 0) {
                        for (int j = 0; j < Objects.requireNonNull(jsonArray2).length(); j++) {
                            JSONObject object2 = jsonArray2.getJSONObject(j);
                            if (object2.getString("day_name").equals(object.getString("day_name"))) {
                                String timeStart = object2.getString("time_start");
                                String timeEnd = object2.getString("time_end");
                                WeekdayListItem weekdayListItem = new WeekdayListItem(object.getInt("day_id"), object2.getInt("work_time_id"), object.getString("day_name"), timeStart.substring(0, timeStart.length() - 3), timeEnd.substring(0, timeEnd.length() - 3));
                                weekdayListItems.add(weekdayListItem);
                                workingDay = true;
                                break;
                            }
                        }
                    }
                    if (!workingDay) {
                        WeekdayListItem weekdayListItem = new WeekdayListItem(object.getInt("day_id"), 0, object.getString("day_name"), null, null);
                        weekdayListItems.add(weekdayListItem);
                    }
                }
                weekdayAdapter = new WeekdayAdapter(weekdayListItems);
                recyclerView.setAdapter(weekdayAdapter);
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

    private void saveWorkTime(int id) {
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_WORK_TIME, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                if (jsonObject.getString("error").equals("false")) {
                    if (id == 0) {
                        linearLayoutWorkingTime.setVisibility(View.GONE);
                        workTimeChanged = true;
                    } else {
                        Intent intent = new Intent(this, ProviderActivity.class);
                        intent.putExtra("provider_id", providerId);
                        finish();
                        startActivity(intent);
                    }
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
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    WeekdayAdapter.ViewHolder holder = (WeekdayAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (Objects.requireNonNull(holder).checkBox.isChecked()) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        Calendar timeStart = Calendar.getInstance();
                        Calendar timeEnd = Calendar.getInstance();
                        try {
                            timeStart.setTime(Objects.requireNonNull(simpleDateFormat.parse(Objects.requireNonNull(holder).timeStart.getText().toString().trim())));
                            timeEnd.setTime(Objects.requireNonNull(simpleDateFormat.parse(Objects.requireNonNull(holder).timeEnd.getText().toString().trim())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (timeEnd.getTimeInMillis() <= timeStart.getTimeInMillis()) {
                            return null;
                        }
                    }
                    params.put("day_id-" + i, String.valueOf(i + 1));
                    params.put("work_time_id-" + i, Objects.requireNonNull(holder).textViewWorkTimeId.getText().toString().trim());
                    if (Objects.requireNonNull(holder).checkBox.isChecked() && holder.timeStart.length() > 0 && holder.timeEnd.length() > 0) {
                        params.put("checked-" + i, "1");
                        params.put("time_start-" + i, holder.timeStart.getText().toString().trim() + ":00");
                        params.put("time_end-" + i, holder.timeEnd.getText().toString().trim() + ":00");
                    } else {
                        params.put("checked-" + i, "0");
                        params.put("time_start-" + i, "");
                        params.put("time_end-" + i, "");
                    }
                }
                if (id == 0) {
                    params.put("service_id", String.valueOf(serviceId));
                } else {
                    params.put("service_id", String.valueOf(id));
                }
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}