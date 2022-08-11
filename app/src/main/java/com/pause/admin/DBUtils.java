package com.pause.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pause.admin.databinding.DashboardBinding;
import com.pause.admin.databinding.FundsActivityBinding;
import com.pause.admin.databinding.TasksActivityBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class DBUtils {
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String TAG = DBUtils.class.getSimpleName();
    private final String FUND_HISTORY = "funds_history";

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

    @SuppressLint("NotifyDataSetChanged")
    public void getTask(ArrayList<Task> list, TasksDisplayAdapter adapter, TasksActivityBinding binding) {
        DatabaseReference ref = database.getReference("tasks");
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("DBUtils", ": Error getting tasks data");
            } else {
                Log.d("DBUtils", ": Task Successful");
                DataSnapshot res = task.getResult();
                binding.progressCircular.setVisibility(View.GONE);
                for (DataSnapshot postSnapshot : res.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) postSnapshot.getValue();
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
                }
                // Sort the data
                list.sort((t1, t2) -> {
                    boolean ddt1 = t1.getDoneDate() != null;
                    boolean ddt2 = t2.getDoneDate() != null;
                    if (!ddt1 && !ddt2) {
                        Log.d(TAG, t1.getDeadline());
                        LocalDate d1 = LocalDate.parse(t1.getDeadline(), DateTimeFormatter.ofPattern("dd/MM/yy"));
                        LocalDate d2 = LocalDate.parse(t2.getDeadline(), DateTimeFormatter.ofPattern("dd/MM/yy"));
                        return d1.compareTo(d2); // Nearest Deadline first
                    }
                    if (!ddt1) return 1;
                    if (!ddt2) return -1;
                    LocalDate d1 = LocalDate.parse(t1.getDoneDate(), DateTimeFormatter.ofPattern("dd/MM/yy"));
                    LocalDate d2 = LocalDate.parse(t2.getDoneDate(), DateTimeFormatter.ofPattern("dd/MM/yy"));
                    return d2.compareTo(d1); // Latest done task first
                });
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void postPoint(Context c) {
        DatabaseReference ref = database.getReference("points");
        ref.get().addOnCompleteListener(task -> {
            DataSnapshot snapshot = task.getResult();
            int prev = 0;
            if(snapshot.getValue()!= null) prev = Integer.parseInt(snapshot.getValue().toString());
            prev += 10;
            ref.setValue(prev).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    Toast.makeText(c, "Reward unlocked!", Toast.LENGTH_SHORT).show();
                } else {
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

    public void getUsage(boolean todayOnly, Context c, DashboardBinding binding) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        //String today = dateFormat.format(myCalendar.getTime());
        String today = "\"01-01-01\""; // remove these ""
        DatabaseReference ref = database.getReference("Usage");
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Map<String, String> map = (Map<String, String>) postSnapshot.getValue();
                    if (map == null || map.isEmpty()) {
                        Log.e(TAG, "getUsage: Usage Map is NULL");
                        return;
                    }
                    if (postSnapshot.getKey() == null) return;
                    if (todayOnly && !today.equals(postSnapshot.getKey())) {
                        Log.e(TAG, "getUsage: Today's data not found");
                        continue;
                    }
                    Log.d(TAG, "Today's data found: " + map);
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        entries.add(new PieEntry(Float.parseFloat(entry.getValue()), entry.getKey()));
                    }
                    binding.graph.setVisibility(View.VISIBLE);
                    HomeActivity.loadPieChart(c,entries, binding);
                    return;
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getFundsHistory(ArrayList<String[]> list, FundsDisplayAdapter adapter, FundsActivityBinding binding) {
        DatabaseReference ref = database.getReference(FUND_HISTORY);
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Couldn't get funds history");
                return;
            }
            DataSnapshot snapshot = task.getResult();
            Log.d(TAG, "getFundsHistory: " + snapshot);
            for (DataSnapshot child : snapshot.getChildren()) {
                Log.d(TAG, "getFundsHistory: " + child);
                Map<String, String> map = (Map<String, String>) child.getValue();
                assert map != null;
                list.add(new String[]{map.get("date"), map.get("amount")});
            }
            binding.progressCircular.setVisibility(View.GONE);
            if (list.size() == 0) binding.emptyView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        });
    }

    public void postFundsHistory(int amount) {
        DatabaseReference ref = database.getReference(FUND_HISTORY).push();
        Map<String, String> map = new TreeMap<>();
        map.put("date", String.valueOf(System.currentTimeMillis()));
        map.put("amount", String.valueOf(amount));
        ref.setValue(map);
        postFunds(amount);
    }

    public void postFunds(int amount) {
        DatabaseReference ref = database.getReference("funds");
        ref.get().addOnCompleteListener(task -> {
            DataSnapshot snapshot = task.getResult();
            int prev = 0;
            if (snapshot.getValue() != null) prev = Integer.parseInt(snapshot.getValue().toString());
            prev += amount;
            ref.setValue(prev).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    Log.d(TAG, "Funds updated.");
                } else {
                    Log.d(TAG, "whoops! Funds not updated.");
                }
            });
        });
    }

    @SuppressLint("SetTextI18n")
    public void getFunds(TextView view) {
        DatabaseReference ref = database.getReference("funds");
        ref.get().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Log.e(TAG, "getFunds: Unable to get funds", task.getException());
                return;
            }
            int amount = 0;
            if(task.getResult().getValue()!=null)
                amount = Integer.parseInt(task.getResult().getValue().toString());
            view.setText(view.getText() + " "+ amount);
        });
    }

    public void postToken(String token) {
        DatabaseReference ref = database.getReference("token/parent");
        ref.setValue(token).addOnCompleteListener(task -> {
            if(!task.isSuccessful()) {
                Log.e(TAG, "postToken: ", task.getException());
            }
        });
    }

    public void getChildToken(SharedPreferences pref) {
        DatabaseReference ref = database.getReference("token/child");
        ref.get().addOnCompleteListener(task -> {
            if(!task.isSuccessful()) {
                Log.e(TAG, "getChildToken: Failure", task.getException());
                return;
            }
            TasksActivity.childToken = Objects.requireNonNull(task.getResult().getValue()).toString();
            pref.edit().putString(TasksActivity.CHILD_TOKEN_KEY, task.getResult().getValue().toString()).apply();
            Log.d(TAG, "getChildToken: Success, childToken:" + task.getResult().getValue());
        });
    }
}
