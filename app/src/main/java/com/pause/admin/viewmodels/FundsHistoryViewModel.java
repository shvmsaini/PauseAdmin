package com.pause.admin.viewmodels;

import static com.pause.admin.ui.HomeActivity.database;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Map;

public class FundsHistoryViewModel extends ViewModel {
    private static final String TAG = FundsHistoryViewModel.class.getSimpleName();
    private final String FUND_HISTORY = "funds_history";
    public MutableLiveData<ArrayList<String[]>> list;

    public MutableLiveData<ArrayList<String[]>> getHistory() {
        if (list == null) {
            list = new MutableLiveData<>();
            loadHistory();
        }
        return list;
    }

    private void loadHistory() {
        DatabaseReference ref = database.getReference(FUND_HISTORY);
        ref.get().addOnCompleteListener(task -> {
            ArrayList<String[]> tempList = new ArrayList<>();
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
                tempList.add(new String[]{map.get("date"), map.get("amount")});
            }
            list.postValue(tempList);
        });
    }

}
