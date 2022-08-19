package com.pause.admin.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.Tasks;
import com.pause.admin.R;
import com.pause.admin.adapters.TasksHistoryDisplayAdapter;
import com.pause.admin.databinding.TasksHistoryActivityBinding;
import com.pause.admin.pojo.Task;
import com.pause.admin.viewmodels.TasksHistoryViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class TasksHistoryActivity extends AppCompatActivity {

    public TasksHistoryActivityBinding binding;
    public TasksHistoryViewModel tasksHistoryViewModel;
    public TasksHistoryDisplayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tasksHistoryViewModel = new ViewModelProvider(this).get(TasksHistoryViewModel.class);
        initializeLayout();
    }

    private void initializeLayout() {
        binding = TasksHistoryActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tasksHistoryViewModel.getTasks().observe(this, tasks -> {
            Collections.sort(tasks);
            adapter = new TasksHistoryDisplayAdapter(this, tasks, this);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setAdapter(adapter);
            binding.progressCircular.setVisibility(View.GONE);
            if (tasks.isEmpty()) binding.emptyView.setVisibility(View.VISIBLE);
            else binding.buttonsParent.setVisibility(View.VISIBLE);
            TasksHistoryDisplayAdapter disAdapter = new TasksHistoryDisplayAdapter(this,
                    adapter.disapproved, this);
            TasksHistoryDisplayAdapter apAdapter = new TasksHistoryDisplayAdapter(this,
                    adapter.approved, this);

            // approved
            binding.approved.setOnClickListener(v -> {
                clearColorsExpectOne(binding.approved);
                binding.recyclerView.setAdapter(apAdapter);
            });

            // all
            binding.all.setOnClickListener(v -> {
                clearColorsExpectOne(binding.all);
                binding.recyclerView.setAdapter(adapter);
            });

            // disapproved
            binding.disapproved.setOnClickListener(v -> {
                clearColorsExpectOne(binding.disapproved);
                binding.recyclerView.setAdapter(disAdapter);
            });
        });

        // back button
        binding.back.setOnClickListener(view -> super.onBackPressed());
    }

    public void clearColorsExpectOne(TextView view) {
        if (view != binding.all)
            binding.all.setTextColor(getColor(R.color.white));
        if (view != binding.approved)
            binding.approved.setTextColor(getColor(R.color.white));
        if (view != binding.disapproved)
            binding.disapproved.setTextColor(getColor(R.color.white));
        view.setTextColor(getColor(R.color.primary_blue));
    }
}