package com.pause.admin.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pause.admin.R;

public class WelcomeActivity extends AppCompatActivity {
    public final static String LOGIN_KEY = "isLoggedIn";
    public Button loginButton;
    public Button signupButton;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        boolean loggedIn = prefs.getBoolean(LOGIN_KEY, false);
        if (loggedIn) {
            Intent i = new Intent(this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
        initializeLayout();
    }

    private void initializeLayout() {
        setContentView(R.layout.welcome_screen);
        loginButton = findViewById(R.id.welcome_login);
        signupButton = findViewById(R.id.welcome_signup);
        loginButton.setOnClickListener(view -> {
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
        signupButton.setOnClickListener(view -> {
            Intent i = new Intent(this, SignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}
