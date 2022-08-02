package com.pause.admin;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pause.admin.databinding.TasksActivityBinding;

import java.util.ArrayList;
import java.util.Map;

public class DBUtils {
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String TAG = DBUtils.class.getSimpleName();

    public void postTask(Task task, Context c) {
        DatabaseReference ref = database.getReference("tasks").push();
        ref.setValue(task).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText(c, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(c, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getTask(ArrayList<Task> list, TasksDisplayAdapter adapter, TasksActivityBinding binding) {
        DatabaseReference ref = database.getReference("tasks");
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("DBUtils", ": Error getting tasks data");
            } else {
                Log.d("DBUtils", ": Task Successful");
                DataSnapshot res = task.getResult();
                for (DataSnapshot postSnapshot : res.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) postSnapshot.getValue();
                    binding.progressCircular.setVisibility(View.GONE);
                    ;
                    if (map == null || map.isEmpty()) {
                        binding.emptyView.setVisibility(View.VISIBLE);
                        return;
                    }
                    Task t;
                    if (!map.containsKey("response")) {
                        t = new Task(
                                (String) map.get("detail"),
                                (String) map.get("deadline"),
                                (String) map.get("status"),
                                (String) map.get("taskType"),
                                (String) map.get("typeDetail"));
                    } else {
                        t = new Task(
                                (String) map.get("detail"),
                                (String) map.get("deadline"),
                                (String) map.get("status"),
                                (String) map.get("taskType"),
                                (String) map.get("typeDetail"),
                                (String) map.get("response"),
                                (String) map.get("doneDate"));
                    }
                    t.setKEY(postSnapshot.getKey());
                    list.add(t);
                    adapter.notifyItemInserted(list.size() - 1);
                }
            }
        });
    }

    public void postPoint(Context c) {
        DatabaseReference ref = database.getReference("points");
        ref.get().addOnCompleteListener(task -> {
            DataSnapshot snapshot = task.getResult();
            int prev = Integer.parseInt(snapshot.getValue().toString());
            prev += 10;
            ref.setValue(prev).addOnCompleteListener(task1 ->{
                if(task1.isSuccessful()){
                    Toast.makeText(c, "Reward unlocked!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(c, "Something's wrong. Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
            Log.d(TAG, "s = " + snapshot);
        });
    }

    public void deleteTask(String KEY, TasksDisplayAdapter adapter, int position) {
        DatabaseReference ref = database.getReference("tasks").child(KEY);
        ref.removeValue();
        adapter.notifyItemRemoved(position);
    }
}
