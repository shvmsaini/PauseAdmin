package com.pause.admin.viewmodels;

import static com.pause.admin.ui.HomeActivity.database;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class FundsViewModel extends ViewModel {
    private static final String TAG = FundsViewModel.class.getSimpleName();
    MutableLiveData<Integer> funds;

    public MutableLiveData<Integer> postFunds(int amount) {
        if (funds == null) funds = new MutableLiveData<>();
        postFundsHelper(amount);
        return funds;
    }

    public MutableLiveData<Integer> getFunds() {
        funds = new MutableLiveData<>();
        getFundsHelper();
        return funds;
    }

    private void postFundsHelper(int amount) {
        DatabaseReference ref = database.getReference("funds");
        ref.get().addOnCompleteListener(task -> {
            DataSnapshot snapshot = task.getResult();
            assert snapshot.getValue() != null;
            int prev = Integer.parseInt(snapshot.getValue().toString()) + amount;
            ref.setValue(prev).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    funds.postValue(amount);
                    Log.d(TAG, "Funds updated.");
                } else {
                    Log.d(TAG, "whoops! Funds not updated.");
                }
            });
        });
    }

    public void postHistory(int amount) {
        String FUND_HISTORY = "funds_history";
        DatabaseReference ref = database.getReference(FUND_HISTORY).push();
        Map<String, String> map = new TreeMap<>();
        map.put("date", String.valueOf(System.currentTimeMillis()));
        map.put("amount", String.valueOf(amount));
        ref.setValue(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) Log.d(TAG, "Funds History posted");
            else Log.d(TAG, "Unable to post funds history");
        });
    }

    /**
     * Get funds, Set in view and stores in prefs
     */
    public void getFundsHelper() {
        DatabaseReference ref = database.getReference("funds");
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "getFunds: Unable to get funds", task.getException());
                return;
            }
            String amount = Objects.requireNonNull(task.getResult().getValue()).toString();
            funds.postValue(Integer.valueOf(amount));
        });
    }
}
