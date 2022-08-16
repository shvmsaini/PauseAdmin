package com.pause.admin.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.pause.admin.R;
import com.pause.admin.databinding.NewTaskActivityBinding;
import com.pause.admin.pojo.Task;
import com.pause.admin.viewmodels.TasksViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {
    public final static Calendar myCalendar = Calendar.getInstance();
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.US);
    final String[] items = new String[]{"Location Type", "App Type", "Image Type"};
    public NewTaskActivityBinding binding;
    private TasksViewModel tasksViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        initializeLayout();
    }

    private void initializeLayout() {
        binding = NewTaskActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        binding.taskType.setAdapter(adapter);

        binding.taskType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) binding.taskTypeDetail.setHint("Type location here");
                else if (i == 1) binding.taskTypeDetail.setHint("Type App name here");
                else binding.taskTypeDetail.setHint("Describe your image");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Something is always selected
            }
        });

        binding.addTask.setOnClickListener(view -> {
            if (!isEmpty()) create();
        });

        // date chooser
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };
        binding.taskDeadline.setOnClickListener(view -> new DatePickerDialog(this, date,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        // back button
        binding.back.setOnClickListener(view -> NewTaskActivity.super.onBackPressed());
    }

    private void updateLabel() {
        binding.taskDeadline.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void create() {
        final String details = binding.taskDetail.getText().toString();
        final String deadline = binding.taskDeadline.getText().toString();
        final String type = binding.taskType.getSelectedItem().toString();
        final String typeDetail = binding.taskTypeDetail.getText().toString();
        Task t = new Task(details, deadline, type, typeDetail);

        tasksViewModel.postTask(t).observe(this, bool -> {
            if (bool) Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show();
        });
    }

    public boolean isEmpty() {
        if (binding.taskDetail.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please fill task details", Toast.LENGTH_SHORT).show();
            binding.taskDetail.requestFocus();
            return true;
        }

        if (binding.taskDeadline.getText().toString().equals(getResources().getString(R.string.deadline))) {
            Toast.makeText(getApplicationContext(), "Please Choose a deadline", Toast.LENGTH_SHORT).show();
            binding.taskDeadline.requestFocus();
            return true;
        }
        if (binding.taskTypeDetail.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please fill task type details", Toast.LENGTH_SHORT).show();
            binding.taskTypeDetail.requestFocus();
            return true;
        }
        return false;
    }

}