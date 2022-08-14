package com.pause.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pause.admin.databinding.DashboardBinding;
import com.pause.admin.databinding.FundsActivityBinding;
import com.pause.admin.databinding.ProfileActivityBinding;
import com.pause.admin.databinding.TasksActivityBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

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
                    Task t = new Task((String) map.get("detail"),
                            (String) map.get("deadline"),
                            (String) map.get("taskType"),
                            (String) map.get("typeDetail"));
                    if (map.containsKey("response")) {
                        t.setResponse((String) map.get("response"));
                        t.setDoneDate((String) map.get("doneDate"));
                    }
                    t.setKEY(postSnapshot.getKey());
                    list.add(t);
                }
                // Sort the data and this shouldn't be here
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
            if (snapshot.getValue() != null)
                prev = Integer.parseInt(snapshot.getValue().toString());
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

    public void getUsage(Context c, DashboardBinding binding) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        //String today = dateFormat.format(myCalendar.getTime());
        String today = "\"01-01-01\""; // remove these ""
        DatabaseReference ref = database.getReference("usage");
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                if (map == null || map.isEmpty()) {
                    Log.e(TAG, "getUsage: Usage Map is NULL");
                    return;
                }
                if (map.containsKey(today)) {
                    Log.d(TAG, "Today's data found: " + map);
                    Map<String, String> todayUsage = (Map<String, String>) map.get(today);
                    assert todayUsage != null;
                    for (Map.Entry<String, String> entry : todayUsage.entrySet()) {
                        entries.add(new PieEntry(Float.parseFloat(entry.getValue()), entry.getKey()));
                    }
                    binding.graph.setVisibility(View.VISIBLE);
                    binding.progressCircular.setVisibility(View.GONE);
                    HomeActivity.loadPieChart(c, entries, binding);
                } else {
                    Log.e(TAG, "getUsage: Today's usage not found");
                }
            }
        });
    }

    public void getAllUsage(Context c, ProfileActivityBinding binding) {
        DatabaseReference ref = database.getReference("usage");
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "getAllUsage: ", task.getException());
            } else {
                DataSnapshot snapshot = task.getResult();
                ArrayList<BarEntry> allEntries = new ArrayList<>();
                ArrayList<String> entriesDate = new ArrayList<>();
                String[] appNames;
                int maxSize = 0;
                int i = 0;
                TreeSet<String> labels = new TreeSet<>();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Map<String, String> mp = (Map<String, String>) postSnapshot.getValue();
                    assert mp != null;
                    maxSize = Math.max(maxSize, mp.size());
                }
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Map<String, String> mp = (Map<String, String>) postSnapshot.getValue();
                    assert mp != null;
                    TreeMap<String, String> sortedMap = new TreeMap<>(mp);
                    String date = Objects.requireNonNull(postSnapshot.getKey()).replace("-","/");
                    entriesDate.add(date.substring(1,date.length()-4));
                    float[] arr = new float[maxSize];
                    int j = 0;
                    Log.d(TAG, "getAllUsage: before " + mp.entrySet());
                    for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
                        arr[j++] = Float.parseFloat(entry.getValue());
                        labels.add(entry.getKey());
                    }
                    allEntries.add(new BarEntry(i++, arr));
                }
                i = 0;
                appNames = new String[labels.size()];
                for(String s : labels){
                    appNames[i++] = s;
                }
                ProfileActivity.loadBarChart(allEntries, binding, entriesDate, appNames, maxSize);
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
            if (snapshot.getValue() != null)
                prev = Integer.parseInt(snapshot.getValue().toString());
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
    public void getFunds(TextView view, Context c) {
        DatabaseReference ref = database.getReference("funds");
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "getFunds: Unable to get funds", task.getException());
                return;
            }
            String amount = Objects.requireNonNull(task.getResult().getValue()).toString();
            String str = String.format(c.getResources().getString(R.string.funds), amount);
            view.setText(str);
            // Store in prefs
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(c).edit();
            editor.putString("FUNDS", amount).apply();

        });
    }

    public void postToken(String token) {
        DatabaseReference ref = database.getReference("token/parent");
        ref.setValue(token).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "postToken: ", task.getException());
            }
        });
    }

    public void getChildToken(SharedPreferences pref) {
        DatabaseReference ref = database.getReference("token/child");
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "getChildToken: Failure", task.getException());
                return;
            }
            TasksActivity.childToken = Objects.requireNonNull(task.getResult().getValue()).toString();
            pref.edit().putString(TasksActivity.CHILD_TOKEN_KEY, task.getResult().getValue().toString()).apply();
            Log.d(TAG, "getChildToken: Success, childToken:" + task.getResult().getValue());
        });
    }
}
