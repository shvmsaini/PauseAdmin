package com.pause.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.pause.admin.databinding.NewTaskActivityBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {
    public NewTaskActivityBinding binding;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = NewTaskActivityBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        initializeLayout();
    }

    private void initializeLayout() {
        // spinner
        final String[] items = new String[]{"Location Type", "App Type", "Image Type"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        binding.taskType.setAdapter(adapter);

        // add task button
        //TODO: make sure none of them is NULL
        binding.addTask.setOnClickListener(view -> create());

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
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        binding.taskDeadline.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void create(){
        final String details = binding.taskDetail.getText().toString();
        final String deadline = binding.taskDeadline.getText().toString();
        final String assignee = binding.taskAssignee.getText().toString();
        final String type = binding.taskType.getSelectedItem().toString();
        final String typeDetail = binding.taskTypeDetail.getText().toString();
        JSONObject task = new JSONObject();
        JSONArray taskDetails = new JSONArray();
        try {
            taskDetails.put(new JSONObject()
                    .put("details", details)
                    .put("deadline", deadline)
                    .put("type", type)
                    .put("assignee", assignee)
                    .put("type_details", typeDetail)
                    .put("approve", "false"));
            task.put(String.valueOf(System.currentTimeMillis()), taskDetails);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(NewTaskActivity.class.getName(), "Unable to create JSONObject");
        }
        DBUtils.postTask(task);
    }
}