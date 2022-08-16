package com.pause.admin.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.pause.admin.R;
import com.pause.admin.databinding.AddFundsActivityBinding;
import com.pause.admin.viewmodels.FundsViewModel;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class AddFundsActivity extends AppCompatActivity implements PaymentResultListener {
    public final String TAG = AddFundsActivity.class.getSimpleName();
    public AddFundsActivityBinding binding;
    SharedPreferences prefs;
    private int amount = 0;
    private boolean fundsAdded = false;
    private SharedPreferences.Editor editor;
    private FundsViewModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        model = new ViewModelProvider(this).get(FundsViewModel.class);
        initializeLayout();
    }

    private void initializeLayout() {
        binding = AddFundsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String funds = prefs.getString("FUNDS", "0");
        binding.funds.setText(String.format(this.getResources().getString(R.string.funds), funds));

        model.getFunds().observe(this, amount -> {
            String fund = amount.toString();
            binding.funds.setText(String.format(this.getResources().getString(R.string.funds), fund));
            // Store in prefs
            editor.putString("FUNDS", fund).apply();
        });

        binding.amount.setText(String.format(getResources().getString(R.string.rupee), 0));
        binding.radio1.setText(String.format(getResources().getString(R.string.rupee), 100));
        binding.radio2.setText(String.format(getResources().getString(R.string.rupee), 200));
        binding.radio3.setText(String.format(getResources().getString(R.string.rupee), 500));
        binding.radio4.setText(String.format(getResources().getString(R.string.rupee), 1000));
        binding.radio1.setOnClickListener(view -> {
            resetAllButtons();
            amount = 100;
            binding.amount.setText(String.format(getResources().getString(R.string.rupee), amount));
            binding.radio1.setBackgroundTintList(ContextCompat.getColorStateList(AddFundsActivity.this, R.color.primary_blue));
            binding.radio1.setTextColor(ContextCompat.getColor(AddFundsActivity.this, R.color.white));
        });
        binding.radio2.setOnClickListener(view -> {
            resetAllButtons();
            amount = 200;
            binding.amount.setText(String.format(getResources().getString(R.string.rupee), amount));
            binding.radio2.setBackgroundTintList(ContextCompat.getColorStateList(AddFundsActivity.this, R.color.primary_blue));
            binding.radio2.setTextColor(ContextCompat.getColor(AddFundsActivity.this, R.color.white));
        });
        binding.radio3.setOnClickListener(view -> {
            resetAllButtons();
            amount = 500;
            binding.amount.setText(String.format(getResources().getString(R.string.rupee), amount));
            binding.radio3.setBackgroundTintList(ContextCompat.getColorStateList(AddFundsActivity.this, R.color.primary_blue));
            binding.radio3.setTextColor(ContextCompat.getColor(AddFundsActivity.this, R.color.white));
        });
        binding.radio4.setOnClickListener(view -> {
            resetAllButtons();
            amount = 1000;
            binding.amount.setText(String.format(getResources().getString(R.string.rupee), amount));
            binding.radio4.setBackgroundTintList(ContextCompat.getColorStateList(AddFundsActivity.this, R.color.primary_blue));
            binding.radio4.setTextColor(ContextCompat.getColor(AddFundsActivity.this, R.color.white));
        });

        binding.back.setOnClickListener(view -> super.onBackPressed());
        binding.addAmount.setOnClickListener(view -> {
            if (amount == 0) {
                Toast.makeText(this, "Select amount first.", Toast.LENGTH_SHORT).show();
            } else startPayment();
        });
    }

    private void resetAllButtons() {
        binding.radio1.setBackgroundTintList(ContextCompat.getColorStateList(AddFundsActivity.this, R.color.white));
        binding.radio1.setTextColor(ContextCompat.getColor(AddFundsActivity.this, R.color.primary_blue));
        binding.radio2.setBackgroundTintList(ContextCompat.getColorStateList(AddFundsActivity.this, R.color.white));
        binding.radio2.setTextColor(ContextCompat.getColor(AddFundsActivity.this, R.color.primary_blue));
        binding.radio3.setBackgroundTintList(ContextCompat.getColorStateList(AddFundsActivity.this, R.color.white));
        binding.radio3.setTextColor(ContextCompat.getColor(AddFundsActivity.this, R.color.primary_blue));
        binding.radio4.setBackgroundTintList(ContextCompat.getColorStateList(AddFundsActivity.this, R.color.white));
        binding.radio4.setTextColor(ContextCompat.getColor(AddFundsActivity.this, R.color.primary_blue));
    }

    private void startPayment() {
        final String fund = amount + "00"; // 00 for decimal places
        Checkout checkout = new Checkout();
        checkout.setImage(R.mipmap.ic_launcher); // logo
        checkout.setKeyID("rzp_test_JWw8SF8VJ9RJ2Q");
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Pause App");
            options.put("description", "Reference No. #123456");
            options.put("currency", "INR");
            options.put("amount", fund);
            checkout.open(this, options);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to create JSONObject");
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Log.d(TAG, "onPaymentSuccess:");
        try {
            model.postFunds(amount).observe(this, integer -> {
                binding.funds.setText(integer);
                // Store in prefs
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putString("FUNDS", String.valueOf(integer)).apply();
            });
            fundsAdded = true;
            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
            model.postHistory(amount);
        } catch (Exception e) {
            Log.e(TAG, "onPaymentSuccess: Failed", e);
        }

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        Log.d(TAG, "Finishing activity.");
        Intent i = new Intent();
        if (fundsAdded) setResult(Activity.RESULT_OK, i);
        else setResult(Activity.RESULT_CANCELED, i);
        super.finish();
    }
}
