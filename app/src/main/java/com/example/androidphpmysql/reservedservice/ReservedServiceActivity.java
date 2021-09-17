package com.example.androidphpmysql.reservedservice;

import static com.example.androidphpmysql.R.string.no_message;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.other.JavaMailAPI;
import com.example.androidphpmysql.main.MainActivity;
import com.example.androidphpmysql.other.MapsActivity;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.broadcast.ReminderBroadcast;
import com.example.androidphpmysql.includes.Constants;
import com.example.androidphpmysql.statics.Address;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ReservedServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonCancel, buttonRealised, buttonEmail, buttonMap;
    private EditText editTextReservationNumber, editTextReservationEmail, editTextMessage;
    private ImageButton rating1, rating2, rating3, rating4, rating5;
    private LinearLayout linearLayoutSearchService, linearLayoutReservedService, linearLayoutRatingButtons, linearLayoutSendEmail;
    private TextView textViewReservationNumber, textViewServiceName, textViewServiceProvider, textViewSubServiceName, textViewSubServicePrice, textViewDateTime, textViewSubServiceDuration, textViewContact, textViewEmail, textViewPhoneNumber, textViewRealised, textViewCanceled, textViewRateService;
    private ProgressDialog progressDialog;
    private int id;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_service);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTitle(getString(R.string.searching_service));
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        email = intent.getStringExtra("email");
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonRealised = findViewById(R.id.buttonRealised);
        buttonEmail = findViewById(R.id.buttonEmail);
        Button buttonSend = findViewById(R.id.buttonSend);
        editTextReservationNumber = findViewById(R.id.editTextReservationNumber);
        editTextReservationEmail = findViewById(R.id.editTextReservationEmil);
        editTextMessage = findViewById(R.id.editTextMessage);
        rating1 = findViewById(R.id.ratingButton1);
        rating2 = findViewById(R.id.ratingButton2);
        rating3 = findViewById(R.id.ratingButton3);
        rating4 = findViewById(R.id.ratingButton4);
        rating5 = findViewById(R.id.ratingButton5);
        linearLayoutSearchService = findViewById(R.id.linearLayoutSearchService);
        linearLayoutReservedService = findViewById(R.id.linearLayoutReservedService);
        linearLayoutRatingButtons = findViewById(R.id.linearLayoutRatingButtons);
        linearLayoutSendEmail = findViewById(R.id.linearLayoutSendEmail);
        textViewReservationNumber = findViewById(R.id.textViewReservationNumberR);
        textViewServiceName = findViewById(R.id.textViewServiceNameR);
        textViewServiceProvider = findViewById(R.id.textViewServiceProviderR);
        textViewSubServiceName = findViewById(R.id.textViewSubServiceNameR);
        textViewSubServicePrice = findViewById(R.id.textViewSubServicePriceR);
        textViewDateTime = findViewById(R.id.textViewDateTimeR);
        textViewSubServiceDuration = findViewById(R.id.textViewSubServiceDurationR);
        textViewContact = findViewById(R.id.textViewContactR);
        textViewEmail = findViewById(R.id.textViewEmailR);
        textViewPhoneNumber = findViewById(R.id.textViewPhoneNumberR);
        textViewRealised = findViewById(R.id.textViewRealisedR);
        textViewCanceled = findViewById(R.id.textViewCanceledR);
        textViewRateService = findViewById(R.id.textViewRateService);
        Button buttonSearch = findViewById(R.id.buttonSearch);
        buttonMap = findViewById(R.id.buttonMapR);
        FloatingActionButton fab = findViewById(R.id.fabHomeReserved);
        progressDialog = new ProgressDialog(this);
        if (id != 0 && email != null) {
            searchService();
        }
        buttonSearch.setOnClickListener(view -> searchService());
        buttonCancel.setOnClickListener(view -> cancelService());
        buttonRealised.setOnClickListener(view -> realiseService());
        buttonEmail.setOnClickListener(view -> linearLayoutSendEmail.setVisibility(View.VISIBLE));
        buttonSend.setOnClickListener(view -> sendMail(email != null ? email : editTextReservationEmail.getText().toString().trim(), textViewServiceName.getText().toString(), textViewSubServiceName.getText().toString(), textViewDateTime.getText().toString(), editTextMessage.getText().toString().trim()));
        buttonMap.setOnClickListener(view -> {
            Address.setAddress(textViewContact.getText().toString().trim());
            startActivity(new Intent(this, MapsActivity.class));
        });
        rating1.setOnClickListener(this);
        rating2.setOnClickListener(this);
        rating3.setOnClickListener(this);
        rating4.setOnClickListener(this);
        rating5.setOnClickListener(this);
        fab.setOnClickListener(View -> {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });
    }

    @Override
    public void onClick(View view) {
        if (view == rating1) {
            setRating(1);
        } else if (view == rating2) {
            setRating(2);
        } else if (view == rating3) {
            setRating(3);
        } else if (view == rating4) {
            setRating(4);
        } else if (view == rating5) {
            setRating(5);
        }
    }

    private void searchService() {
        String reservationNumber = String.valueOf(id);
        String reservationEmail = email;
        if (id == 0 && email == null) {
            reservationNumber = editTextReservationNumber.getText().toString().trim();
            reservationEmail = editTextReservationEmail.getText().toString().trim();
            id = Integer.parseInt(reservationNumber);
        }
        progressDialog.setMessage(getString(R.string.searching));
        progressDialog.show();
        String finalReservationEmail = reservationEmail;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_RESERVED_SERVICE + "?id=" + reservationNumber + "&email=" + reservationEmail, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("error").equals("false")) {
                    SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
                    boolean isLogged = sharedPrefManager.getInstance(this).isLoggedIn();
                    textViewReservationNumber.setText(String.valueOf(id));
                    textViewServiceName.setText(jsonObject.getString("service_name"));
                    textViewServiceProvider.setText(MessageFormat.format("{0} {1}", jsonObject.getString("provider_firstname"), jsonObject.getString("provider_lastname")));
                    textViewSubServiceName.setText(jsonObject.getString("sub_service_name"));
                    textViewSubServicePrice.setText(MessageFormat.format("{0} zł", jsonObject.getString("price")));
                    String dateTime = jsonObject.getString("date_time");
                    textViewDateTime.setText(dateTime.substring(0, dateTime.length() - 3));
                    textViewSubServiceDuration.setText(MessageFormat.format("{0} min", jsonObject.getString("duration")));
                    textViewPhoneNumber.setMovementMethod(LinkMovementMethod.getInstance());
                    if (isLogged) {
                        textViewContact.setText(MessageFormat.format("{0} {1}", jsonObject.getString("client_firstname"), jsonObject.getString("client_lastname")));
                        textViewEmail.setText(finalReservationEmail);
                        textViewPhoneNumber.setText(jsonObject.getString("client_phone_number"));
                        buttonEmail.setVisibility(View.VISIBLE);
                        buttonMap.setVisibility(View.GONE);
                    } else {
                        textViewContact.setText(MessageFormat.format("{0}, {1}", jsonObject.getString("address"), jsonObject.getString("city_name")));
                        textViewEmail.setText(jsonObject.getString("service_email"));
                        textViewPhoneNumber.setText(jsonObject.getString("phone_number"));
                    }
                    Linkify.addLinks(textViewPhoneNumber, Patterns.PHONE, "tel:", Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter);
                    if (jsonObject.getString("realised").equals("0")) {
                        textViewRealised.setText(R.string.no);
                    } else {
                        textViewRealised.setText(R.string.yes);
                    }
                    if (jsonObject.getString("canceled").equals("0")) {
                        textViewCanceled.setText(R.string.no);
                    } else {
                        textViewCanceled.setText(R.string.yes);
                    }
                    if (jsonObject.getString("realised").equals("1") || jsonObject.getString("canceled").equals("1")) {
                        buttonCancel.setVisibility(View.INVISIBLE);
                    } else {
                        buttonCancel.setVisibility(View.VISIBLE);
                    }
                    if (jsonObject.getString("realised").equals("1") || jsonObject.getString("canceled").equals("1") || !isLogged) {
                        buttonRealised.setVisibility(View.INVISIBLE);
                    } else {
                        buttonRealised.setVisibility(View.VISIBLE);
                    }
                    if (jsonObject.getString("realised").equals("1")) {
                        textViewRateService.setVisibility(View.VISIBLE);
                        linearLayoutRatingButtons.setVisibility(View.VISIBLE);
                        if (isLogged || !jsonObject.getString("rating").equals("null")) {
                            rating1.setClickable(false);
                            rating2.setClickable(false);
                            rating3.setClickable(false);
                            rating4.setClickable(false);
                            rating5.setClickable(false);
                            textViewRateService.setText(getString(R.string.rating));
                            switch (Integer.parseInt(jsonObject.getString("rating"))) {
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
                    }
                    linearLayoutSearchService.setVisibility(View.GONE);
                    linearLayoutReservedService.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
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

    private void cancelService() {
        progressDialog.setMessage(getString(R.string.canceling));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CANCEL, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("error").equals("false")) {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    textViewCanceled.setText(R.string.yes);
                    buttonCancel.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(getApplicationContext(), ReminderBroadcast.class);
                    @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, Intent.FILL_IN_DATA);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    sendMail(email != null ? email : editTextReservationEmail.getText().toString().trim(), textViewServiceName.getText().toString(), textViewSubServiceName.getText().toString(), textViewDateTime.getText().toString(), null);
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
                params.put("id", String.valueOf(id));
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void realiseService() {
        progressDialog.setMessage(getString(R.string.canceling));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REALISE, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("error").equals("false")) {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    textViewRealised.setText(R.string.yes);
                    buttonCancel.setVisibility(View.INVISIBLE);
                    buttonRealised.setVisibility(View.INVISIBLE);
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
                params.put("id", String.valueOf(id));
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void setRating(int value) {
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_RATINGS, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("error").equals("false")) {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    rating1.setClickable(false);
                    rating2.setClickable(false);
                    rating3.setClickable(false);
                    rating4.setClickable(false);
                    rating5.setClickable(false);
                    switch (value) {
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
                params.put("service", String.valueOf(id));
                params.put("value", String.valueOf(value));
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void sendMail(String email, String serviceName, String subServiceName, String dateTime, String msg) {
        String subject;
        String message;
        int option;
        if (msg == null) {
            subject = getString(R.string.canceled_service);
            message = getString(R.string.canceled_service) + "\n" + serviceName + " - " + subServiceName + "\n" + dateTime;
            option = 0;
        } else {
            if (msg.length() > 0) {
                subject = getString(R.string.message_from_provider);
                message = serviceName + " - " + subServiceName + "\n" + dateTime + "\n" + msg;
                option = 1;
            } else {
                Toast.makeText(getApplicationContext(), no_message, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email, subject, message, option);
        javaMailAPI.execute();
        editTextMessage.setText("");
        linearLayoutSendEmail.setVisibility(View.GONE);
    }
}