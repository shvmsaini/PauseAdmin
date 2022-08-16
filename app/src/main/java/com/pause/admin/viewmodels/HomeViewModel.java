package com.pause.admin.viewmodels;

import static com.pause.admin.ui.HomeActivity.database;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Map;

public class HomeViewModel extends ViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();
    MutableLiveData<ArrayList<PieEntry>> pieEntry;

    public MutableLiveData<ArrayList<PieEntry>> getUsage() {
        if (pieEntry == null) {
            pieEntry = new MutableLiveData<>();
            getUsageHelper();
        }
        return pieEntry;
    }

    private void getUsageHelper() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        //String today = dateFormat.format(myCalendar.getTime());
        String today = "\"01-01-01\""; // remove these ""
        DatabaseReference ref = database.getReference("usage");
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "getUsage: Unable to get usage data");
            } else {
                DataSnapshot snapshot = task.getResult();
                Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                if (map == null || map.isEmpty()) {
                    Log.e(TAG, "getUsage: Usage Map is NULL");
                } else if (map.containsKey(today)) {
                    Map<String, String> todayUsage = (Map<String, String>) map.get(today);
                    assert todayUsage != null;
                    for (Map.Entry<String, String> entry : todayUsage.entrySet())
                        entries.add(new PieEntry(Float.parseFloat(entry.getValue()), entry.getKey()));
                    pieEntry.postValue(entries);
                } else Log.e(TAG, "getUsage: Today's usage not found");
            }
        });
    }


}
