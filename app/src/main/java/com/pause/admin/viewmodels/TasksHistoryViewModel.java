package com.pause.admin.viewmodels;

import static com.pause.admin.ui.HomeActivity.database;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.pause.admin.pojo.Task;

import java.util.ArrayList;
import java.util.Map;

public class TasksHistoryViewModel extends ViewModel {
    public MutableLiveData<ArrayList<Task>> list;

    public MutableLiveData<ArrayList<Task>> getTasks() {
        if (list == null) {
            list = new MutableLiveData<>();
            loadTasks();
        }
        return list;
    }

    private void loadTasks() {
        DatabaseReference ref = database.getReference("tasks_history");
        ref.get().addOnCompleteListener(task -> {
            ArrayList<Task> l = new ArrayList<>();
            if (!task.isSuccessful()) {
                Log.d("DBUtils", ": Error getting tasks data");
            } else {
                DataSnapshot res = task.getResult();
                for (DataSnapshot postSnapshot : res.getChildren()) {
                    Map<String, String> map = (Map<String, String>) postSnapshot.getValue();
                    if (map == null || map.isEmpty()) return;
                    Task t = new Task(
                            map.get("detail"),
                            map.get("deadline"),
                            map.get("taskType"),
                            map.get("typeDetail"),
                            map.get("response"),
                            map.get("doneDate"));
                    t.setKEY(postSnapshot.getKey());
                    t.setStatus(map.get("status"));
                    t.setAttendedDate(map.get("attendedDate"));
                    l.add(t);
                }
                list.postValue(l);
            }
        });
    }

}
