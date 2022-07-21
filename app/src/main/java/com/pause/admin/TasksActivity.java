package com.pause.admin;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pause.admin.databinding.TasksActivityBinding;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {
    private static final String TAG = TasksActivity.class.getSimpleName();

      public TasksActivityBinding binding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLayout();
    }

    private void initializeLayout() {
        binding = TasksActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<Task> TasksList = new ArrayList<>();
        Task t1 = new Task("ye karo vo karo","1/1/1","UNATTENDED");
        Task t2 = new Task("ye karo vo karo","1/1/1","PENDING", "ye kardia meine dekhlo");
        //TODO: get data from db
        TasksList.add(t1);
        TasksList.add(t2);
        TasksDisplayAdapter adapter = new TasksDisplayAdapter(this, TasksList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        binding.back.setOnClickListener(view -> super.onBackPressed());
    }
}
