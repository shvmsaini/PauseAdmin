package com.pause.admin.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pause.admin.adapters.FundsDisplayAdapter;
import com.pause.admin.databinding.FundsActivityBinding;
import com.pause.admin.viewmodels.FundsHistoryViewModel;

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

        FundsHistoryViewModel model = new ViewModelProvider(this).get(FundsHistoryViewModel.class);
        model.getHistory().observe(this, history -> {
            // Sorting by latest date first
            history.sort((s1, s2) -> s2[0].compareTo(s1[0]));

            adapter = new FundsDisplayAdapter(this, history);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setAdapter(adapter);
            binding.progressCircular.setVisibility(View.GONE);
            if (history.size() == 0) binding.emptyView.setVisibility(View.VISIBLE);
        });

        // HomeActivity.dbUtils.getFundsHistory(arrayList, adapter, binding);

        binding.back.setOnClickListener(v -> super.onBackPressed());
    }
}
