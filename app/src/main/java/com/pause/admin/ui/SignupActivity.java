package com.pause.admin.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pause.admin.databinding.SignupLayoutBinding;

public class SignupActivity extends AppCompatActivity {
    public SignupLayoutBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLayout();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initializeLayout() {
        binding = SignupLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // For log in link
        SpannableString signupSpan = new SpannableString(binding.loginBack.getText().toString());
        signupSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent signupIntent = new Intent(SignupActivity.this, LoginActivity.class);
                signupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signupIntent);
            }
        }, 25, 30, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        binding.signupButton.setOnClickListener(v -> {
            String name = binding.nameForSignup.getText().toString();
            String email = binding.emailForSignup.getText().toString();
            String password = binding.passwordForSignup.getText().toString();
            SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
            prefs.putString("name", name);
            prefs.putString("email", email);
            prefs.putString("pass", password);
            prefs.apply();
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            finish();
        });
        binding.loginBack.setText(signupSpan);
        binding.loginBack.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
