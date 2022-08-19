package com.pause.admin.viewmodels;

import static com.pause.admin.ui.HomeActivity.database;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.pause.admin.adapters.TasksDisplayAdapter;
import com.pause.admin.pojo.Task;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class TasksViewModel extends ViewModel {
    private static final String TAG = TasksViewModel.class.getSimpleName();
    public MutableLiveData<ArrayList<Task>> list;
    public MutableLiveData<Boolean> flag = null;

    public MutableLiveData<ArrayList<Task>> getTasks() {
        if (list == null) {
            list = new MutableLiveData<>();
            loadTasks();
        }
        return list;
    }

    public MutableLiveData<Boolean> postTask(Task t) {
        flag = new MutableLiveData<>();
        postTaskHelper(t);
        return flag;
    }

    private void loadTasks() {
        DatabaseReference ref = database.getReference("tasks");
        ref.get().addOnCompleteListener(task -> {
            ArrayList<Task> l = new ArrayList<>();
            if (!task.isSuccessful()) {
                Log.d("DBUtils", ": Error getting tasks data");
            } else {
                Log.d("DBUtils", ": Task Successful");
                DataSnapshot res = task.getResult();
                for (DataSnapshot postSnapshot : res.getChildren()) {
                    Map<String, String> map = (Map<String, String>) postSnapshot.getValue();
                    if (map == null || map.isEmpty()) {
                        return;
                    }
                    Task t = new Task(
                            map.get("detail"),
                            map.get("deadline"),
                            map.get("taskType"),
                            map.get("typeDetail"));

                    if (map.containsKey("response")) {
                        t.setResponse(map.get("response"));
                        t.setDoneDate(map.get("doneDate"));
                    }
                    t.setKEY(postSnapshot.getKey());
                    l.add(t);
                }
                list.postValue(l);
            }
        });
    }

    public void postTaskHelper(Task t) {
        DatabaseReference ref = database.getReference("tasks").push();
        ref.setValue(t).addOnCompleteListener(task -> {
            if (task.isSuccessful()) flag.postValue(true);
            else flag.postValue(false);
        });
    }

    public MutableLiveData<String> getChildToken() {
        DatabaseReference ref = database.getReference("token/child");
        MutableLiveData<String> tok = new MutableLiveData<>();
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "getChildToken: Failure", task.getException());
                return;
            }
            String token = Objects.requireNonNull(task.getResult().getValue()).toString();
            tok.postValue(token);
        });
        return tok;
    }

    /**
     * Delete task and add to tasks history
     */
    public void deleteTask(TasksDisplayAdapter adapter, int position, Task t) {
        DatabaseReference ref = database.getReference("tasks").child(t.getKEY());
        ref.removeValue();
        postTaskHistory(t);
        adapter.notifyItemRemoved(position);
    }

    public void postTaskHistory(Task t) {
        DatabaseReference ref = database.getReference("tasks_history").push();
        ref.setValue(t).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "postTaskHistory: Task History posted");
            } else Log.e(TAG, "postTaskHistory: Unable to post task history", task.getException());
        });
    }

    public void postToken(String token) {
        DatabaseReference ref = database.getReference("token/parent");
        ref.setValue(token).addOnCompleteListener(task -> {
            if (task.isSuccessful()) Log.e(TAG, "postToken: Token posted");
            else Log.e(TAG, "postToken: ", task.getException());
        });
    }

    public MutableLiveData<Boolean> postPoint() {
        DatabaseReference ref = database.getReference("points");
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        ref.get().addOnCompleteListener(task -> {
            DataSnapshot snapshot = task.getResult();
            int prev = 0;
            if (snapshot.getValue() != null)
                prev = Integer.parseInt(snapshot.getValue().toString());
            prev += 10;
            ref.setValue(prev).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) success.postValue(true);
                else success.postValue(false);
            });
            Log.d(TAG, "s = " + snapshot);
        });
        return success;
    }
}
