package com.example.androidphpmysql.profile;

import static com.example.androidphpmysql.R.string.invalid_email;

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
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.RequestHandler;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.includes.Constants;
import com.example.androidphpmysql.statics.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextUserEmail, editTextUserFirstname, editTextUserLastname, editTextUserOldPassword, editTextUserNewPassword;
    private ProgressDialog progressDialog;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        if (!sharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTitle(R.string.settings);
        editTextUsername = findViewById(R.id.editTextUsernameSettings);
        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        editTextUserFirstname = findViewById(R.id.editTextUserFirstname);
        editTextUserLastname = findViewById(R.id.editTextUserLastname);
        editTextUserOldPassword = findViewById(R.id.editTextUserOldPassword);
        editTextUserNewPassword = findViewById(R.id.editTextUserNewPassword);
        Button buttonSave = findViewById(R.id.buttonSaveSettings);
        Button buttonSavePassword = findViewById(R.id.buttonSavePassword);
        progressDialog = new ProgressDialog(this);
        id = sharedPrefManager.getInstance(this).getUserId();
        editTextUsername.setText(sharedPrefManager.getInstance(this).getUsername());
        editTextUserEmail.setText(sharedPrefManager.getInstance(this).getUserEmail());
        editTextUserFirstname.setText(sharedPrefManager.getInstance(this).getUserFirstname());
        editTextUserLastname.setText(sharedPrefManager.getInstance(this).getUserLastname());
        buttonSave.setOnClickListener(View -> save());
        buttonSavePassword.setOnClickListener(View -> changePassword());
    }

    private void save() {
        final String username = editTextUsername.getText().toString().trim();
        final String email = editTextUserEmail.getText().toString().trim();
        final String firstname = editTextUserFirstname.getText().toString().trim();
        final String lastname = editTextUserLastname.getText().toString().trim();
        if (!Validation.isValidEmail(email)) {
            Toast.makeText(getApplicationContext(), invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_UPDATE_PROFILE, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                if (jsonObject.getString("error").equals("false")) {
                    SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
                    sharedPrefManager.getInstance(this).logout();
                    sharedPrefManager.getInstance(getApplicationContext()).userLogin(id, username, email, firstname, lastname);
                    finish();
                    startActivity(new Intent(this, ProfileActivity.class));
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
                if (username.length() > 0 && email.length() > 0 && firstname.length() > 0 && lastname.length() > 0) {
                    params.put("id", String.valueOf(id));
                    params.put("username", username);
                    params.put("email", email);
                    params.put("firstname", firstname);
                    params.put("lastname", lastname);
                }
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void changePassword() {
        final String oldPassword = editTextUserOldPassword.getText().toString().trim();
        final String newPassword = editTextUserNewPassword.getText().toString().trim();
        progressDialog.setMessage(getString(R.string.saving));
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_UPDATE_PASSWORD, response -> {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                if (jsonObject.getString("error").equals("false")) {
                    finish();
                    startActivity(new Intent(this, ProfileActivity.class));
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
                if (oldPassword.length() > 0 && newPassword.length() > 0) {
                    params.put("id", String.valueOf(id));
                    params.put("old_password", oldPassword);
                    params.put("new_password", newPassword);
                }
                return params;
            }
        };
        RequestHandler requestHandler = new RequestHandler(getApplicationContext());
        requestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}