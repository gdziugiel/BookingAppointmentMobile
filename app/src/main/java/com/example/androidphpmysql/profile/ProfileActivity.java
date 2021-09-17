package com.example.androidphpmysql.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.androidphpmysql.broadcast.ProviderService;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.handler.SharedPrefManager;
import com.example.androidphpmysql.provider.ProviderActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        if (!sharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        TextView textViewUsername = findViewById(R.id.textViewUsername);
        TextView textViewUserEmail = findViewById(R.id.textViewUserEmail);
        TextView textViewUserFirstname = findViewById(R.id.textViewUserFirstname);
        TextView textViewUserLastname = findViewById(R.id.textViewUserLastname);
        textViewUsername.setText(sharedPrefManager.getInstance(this).getUsername());
        textViewUserEmail.setText(sharedPrefManager.getInstance(this).getUserEmail());
        textViewUserFirstname.setText(sharedPrefManager.getInstance(this).getUserFirstname());
        textViewUserLastname.setText(sharedPrefManager.getInstance(this).getUserLastname());
        Intent intent = new Intent(getApplicationContext(), ProviderService.class);
        getApplicationContext().startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuLogout) {
            Intent intent = new Intent(this, ProviderService.class);
            stopService(intent);
            SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
            sharedPrefManager.getInstance(this).logout();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else if (item.getItemId() == R.id.menuSettings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (item.getItemId() == R.id.menuServices) {
            SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), ProviderActivity.class);
            intent.putExtra("provider_id", sharedPrefManager.getUserId());
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuStatistics) {
            SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
            intent.putExtra("provider_id", sharedPrefManager.getUserId());
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuFreeTime) {
            SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), ProviderActivity.class);
            intent.putExtra("provider_id", sharedPrefManager.getUserId());
            intent.putExtra("set_free_time", true);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuCalendar) {
            SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), ProviderActivity.class);
            intent.putExtra("provider_id", sharedPrefManager.getUserId());
            intent.putExtra("get_calendar", true);
            startActivity(intent);
        }
        return true;
    }
}