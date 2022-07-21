package com.pause.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    public Button loginButton;
    public Button signupButton;
    private String LOGIN_KEY = "isLoggedIn";
    private boolean loggedIn = false;
    private SharedPreferences prefs;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: if user already logged in directly went to the homeActivity
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        loggedIn = prefs.getBoolean(LOGIN_KEY, false);
        // TODO: create launcher activity to avoid showing this activity when already logged in
        if (true) {
            Intent i = new Intent(this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
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
