package com.pause.admin.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pause.admin.databinding.LoginActivityBinding;

public class LoginActivity extends AppCompatActivity {
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    public LoginActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // For sign up link
        SpannableString signupSpan = new SpannableString(binding.signup.getText().toString());
        signupSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signupIntent);
            }
        }, 23, 29, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        binding.signup.setText(signupSpan);
        binding.signup.setMovementMethod(LinkMovementMethod.getInstance());
        binding.loginButton.setOnClickListener(v -> {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String email = binding.emailForLogin.getText().toString();
            String pass = binding.passwordForLogin.getText().toString();
            String emailOnPrefs = preferences.getString("email", "");
            String passOnPrefs = preferences.getString("pass", "");
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(WelcomeActivity.LOGIN_KEY, true);
            edit.apply();

            if (!email.equals(emailOnPrefs) || !pass.equals(passOnPrefs)) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText() || imm.isActive()) {
            super.onBackPressed();
            return;
        }
        if (back_pressed + TIME_DELAY > System.currentTimeMillis())
            super.onBackPressed();
        else Toast.makeText(getBaseContext(), "Press once again to exit!",
                Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}
