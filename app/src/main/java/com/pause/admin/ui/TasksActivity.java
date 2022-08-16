package com.pause.admin.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.pause.admin.R;
import com.pause.admin.adapters.TasksDisplayAdapter;
import com.pause.admin.databinding.TasksActivityBinding;
import com.pause.admin.viewmodels.TasksViewModel;

import org.w3c.dom.Text;

import java.util.Collections;

public class TasksActivity extends AppCompatActivity {
    public static final String PARENT_TOKEN_KEY = "PARENT_TOKEN";
    public static final String CHILD_TOKEN_KEY = "CHILD_TOKEN";
    private static final String TAG = TasksActivity.class.getSimpleName();
    public static String childToken = "";
    public static String parentToken = "";
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;
    public TasksDisplayAdapter adapter;
    public TasksActivityBinding binding;
    public TasksActivity tasksActivity;
    public TasksViewModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tasksActivity = new TasksActivity();
        model = new ViewModelProvider(this).get(TasksViewModel.class);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        parentToken = prefs.getString(PARENT_TOKEN_KEY, "");
        childToken = prefs.getString(CHILD_TOKEN_KEY, "");

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String newToken = task.getResult();
                if (!newToken.equals(parentToken)) {
                    editor.putString(PARENT_TOKEN_KEY, newToken).apply(); // store in prefs
                    parentToken = newToken; // set token application wide
                    model.postToken(newToken); // send to db
                }
                Log.d(TAG, "Parent token = " + newToken);
            }
        });
        // get child token
        model.getChildToken().observe(this, str -> {
            TasksActivity.childToken = str;
            prefs.edit().putString(CHILD_TOKEN_KEY, str).apply();
            Log.d(TAG, "getChildToken: Success, childToken:" + str);
        });

        initializeLayout();
    }

    private void initializeLayout() {
        binding = TasksActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonsParent.setVisibility(View.GONE);

        model.getTasks().observe(this, tasks -> {
            Collections.sort(tasks);
            adapter = new TasksDisplayAdapter(this, tasks, tasksActivity, model);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setAdapter(adapter);
            binding.progressCircular.setVisibility(View.GONE);
            if (tasks.isEmpty()) binding.emptyView.setVisibility(View.VISIBLE);
            else binding.buttonsParent.setVisibility(View.VISIBLE);
            TasksDisplayAdapter unattAdapter = new TasksDisplayAdapter(this, adapter.unattended, tasksActivity, model);
            TasksDisplayAdapter attAdapter = new TasksDisplayAdapter(this, adapter.attended, tasksActivity, model);
            // attended
            binding.attended.setOnClickListener(v -> {
                clearColorsExpectOne(binding.attended);
                binding.recyclerView.setAdapter(attAdapter);
            });

            // all
            binding.all.setOnClickListener(v -> {
                clearColorsExpectOne(binding.all);
                binding.recyclerView.setAdapter(adapter);
            });

            // unattended
            binding.unattended.setOnClickListener(v -> {
                clearColorsExpectOne(binding.unattended);
                binding.recyclerView.setAdapter(unattAdapter);
            });
        });

        // back button
        binding.back.setOnClickListener(view -> super.onBackPressed());
    }

    public void clearColorsExpectOne(TextView view) {
        if (view != binding.all)
            binding.all.setTextColor(getColor(R.color.white));
        if (view != binding.attended)
            binding.attended.setTextColor(getColor(R.color.white));
        if (view != binding.unattended)
            binding.unattended.setTextColor(getColor(R.color.white));
        view.setTextColor(getColor(R.color.primary_blue));
    }
}
