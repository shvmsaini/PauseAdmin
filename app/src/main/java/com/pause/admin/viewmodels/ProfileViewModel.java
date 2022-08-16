package com.pause.admin.viewmodels;

import static com.pause.admin.ui.HomeActivity.database;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.pause.admin.pojo.StackedBar;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

public class ProfileViewModel extends ViewModel {
    private static final String TAG = ProfileViewModel.class.getSimpleName();
    MutableLiveData<StackedBar> stackedBarData;

    public MutableLiveData<StackedBar> getAllUsage() {
        if (stackedBarData == null) {
            stackedBarData = new MutableLiveData<>();
            getAllUsageHelper();
        }
        return stackedBarData;
    }

    public void getAllUsageHelper() {
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
                    String date = Objects.requireNonNull(postSnapshot.getKey()).replace("-", "/");
                    entriesDate.add(date.substring(1, date.length() - 4));
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
                for (String s : labels) {
                    appNames[i++] = s;
                }
                StackedBar b = new StackedBar(allEntries, entriesDate, appNames, maxSize);
                stackedBarData.postValue(b);
            }
        });
    }
}
