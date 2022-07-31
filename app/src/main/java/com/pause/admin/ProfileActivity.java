package com.pause.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pause.admin.databinding.ProfileActivityBinding;

public class ProfileActivity extends AppCompatActivity {
    public ProfileActivityBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLayout();
    }

    private void initializeLayout() {
        binding = ProfileActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.back.setOnClickListener(v -> super.onBackPressed());
    }
}