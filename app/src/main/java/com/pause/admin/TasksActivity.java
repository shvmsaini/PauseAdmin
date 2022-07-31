package com.pause.admin;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.pause.admin.databinding.TasksActivityBinding;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {
    private static final String TAG = TasksActivity.class.getSimpleName();
    public static TasksDisplayAdapter adapter;
    public TasksActivityBinding binding;
    public ArrayList<Task> TasksList;

    public static void deleteTask(String KEY, int position) {
        DBUtils.deleteTask(KEY, adapter, position);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLayout();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.d(TAG, "token = " + token);
            }
        });
    }

    private void initializeLayout() {
        binding = TasksActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TasksList = new ArrayList<>();
        adapter = new TasksDisplayAdapter(this, TasksList);
        DBUtils.getTask(TasksList, adapter, binding);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // back button
        binding.back.setOnClickListener(view -> super.onBackPressed());
    }

    private void setTasks(ArrayList<Task> TasksList) {
//        DBUtils.getTask(TasksList);
    }
}
