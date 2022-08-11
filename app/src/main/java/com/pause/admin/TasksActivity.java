package com.pause.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.pause.admin.databinding.TasksActivityBinding;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {
    public static final String PARENT_TOKEN_KEY = "PARENT_TOKEN";
    public static final String CHILD_TOKEN_KEY = "CHILD_TOKEN";
    private static final String TAG = TasksActivity.class.getSimpleName();
    public static String childToken = "";
    public static String parentToken = "";
    public SharedPreferences.Editor editor;
    public TasksDisplayAdapter adapter;
    public TasksActivityBinding binding;
    public ArrayList<Task> TasksList;
    public TasksActivity tasksActivity;

    public void deleteTask(String KEY, int position) {
        HomeActivity.dbUtils.deleteTask(KEY, adapter, position);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tasksActivity = new TasksActivity();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        parentToken = prefs.getString(PARENT_TOKEN_KEY, "");
        childToken = prefs.getString(CHILD_TOKEN_KEY, "");

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String newToken = task.getResult();
                if (!newToken.equals(parentToken)) {
                    editor.putString(PARENT_TOKEN_KEY, newToken).apply(); // store in prefs
                    parentToken = newToken; // set token application wide
                    HomeActivity.p.onNewToken(newToken); // send to db
                }
                Log.d(TAG, "Parent token = " + newToken);
            }
        });
        // get child token
        HomeActivity.dbUtils.getChildToken(prefs);

        initializeLayout();
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
