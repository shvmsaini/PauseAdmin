package com.pause.admin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pause.admin.databinding.FundsActivityBinding;

import java.util.ArrayList;

public class FundsHistoryActivity extends AppCompatActivity {
    public FundsDisplayAdapter adapter;
    public FundsActivityBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLayout();
    }

    private void initializeLayout() {
        binding = FundsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<String[]> arrayList = new ArrayList<>();
        adapter = new FundsDisplayAdapter(arrayList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        HomeActivity.dbUtils.getFundsHistory(arrayList, adapter, binding);

        binding.back.setOnClickListener(v -> super.onBackPressed());
    }
}
