package com.example.androidphpmysql.handler;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private SharedPrefManager instance;
    private final Context ctx;
    private static final String SHARED_PREF_NAME = "put2021";
    private static final String KEY_USER_ID = "userid";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_FIRSTNAME = "user_firstname";
    private static final String KEY_USER_LASTNAME = "user_lastname";

    public SharedPrefManager(Context context) {
        ctx = context;
    }

    public synchronized SharedPrefManager getInstance(Context context) {
        if (this.instance == null) {
            this.instance = new SharedPrefManager(context);
        }
        return this.instance;
    }

    public void userLogin(int id, String username, String email, String firstname, String lastname) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_FIRSTNAME, firstname);
        editor.putString(KEY_USER_LASTNAME, lastname);
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    public void logout() {
        SharedPreferences sharedPreferences = this.ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public int getUserId() {
        SharedPreferences sharedPreferences = this.ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, 0);
    }

    public String getUsername() {
        SharedPreferences sharedPreferences = this.ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getUserEmail() {
        SharedPreferences sharedPreferences = this.ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public String getUserFirstname() {
        SharedPreferences sharedPreferences = this.ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_FIRSTNAME, null);
    }

    public String getUserLastname() {
        SharedPreferences sharedPreferences = this.ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_LASTNAME, null);
    }
}