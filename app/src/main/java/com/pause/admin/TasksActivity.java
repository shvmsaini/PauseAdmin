package com.pause.admin;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.pause.admin.databinding.TasksActivityBinding;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {
    private static final String TAG = TasksActivity.class.getSimpleName();
    public TasksDisplayAdapter adapter;
    public TasksActivityBinding binding;
    public ArrayList<Task> TasksList;
    public TasksActivity tasksActivity;
    public static String token = "c5QofccVTzCHsrkiyLSv3O:APA91bEYnVo4rubY9d00by0AaLzr4k322GHAS9NYKSYsWCMrEktyUqHAQpwqnQc8tbuBeIyUBzaXeAySO94Kgb3PSDvg2a0DMzqBTubOjQqM5sQi5g3tZ7J2oKcv6o3qLLRHIHtstI8G";

    public void deleteTask(String KEY, int position) {
        HomeActivity.dbUtils.deleteTask(KEY, adapter, position);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLayout();
        tasksActivity = new TasksActivity();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                token = task.getResult();
                Log.d(TAG, "token = " + token);
            }
        });
    }

    private void initializeLayout() {
        binding = TasksActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TasksList = new ArrayList<>();
        adapter = new TasksDisplayAdapter(this, TasksList, tasksActivity);
        HomeActivity.dbUtils.getTask(TasksList, adapter, binding);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // back button
        binding.back.setOnClickListener(view -> super.onBackPressed());
    }
}
