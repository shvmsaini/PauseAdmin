package com.pause.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pause.admin.databinding.AddFundsActivityBinding;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class AddFundsActivity extends AppCompatActivity implements PaymentResultListener {
    public final String TAG = AddFundsActivity.class.getSimpleName();
    public AddFundsActivityBinding binding;
    private int amount = 0;
    SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        initializeLayout();
    }

    private void initializeLayout() {
        binding = AddFundsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String funds = prefs.getString("FUNDS" , "0");
        binding.funds.setText(String.format(this.getResources().getString(R.string.funds), funds));
        HomeActivity.dbUtils.getFunds(binding.funds, this);
        binding.amount.setText(String.format(getResources().getString(R.string.rupee), 0));
        binding.radio1.setText(String.format(getResources().getString(R.string.rupee), 100));
        binding.radio2.setText(String.format(getResources().getString(R.string.rupee), 200));
        binding.radio3.setText(String.format(getResources().getString(R.string.rupee), 500));
        binding.radio4.setText(String.format(getResources().getString(R.string.rupee), 1000));
        binding.radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.radio1) amount = 100;
            else if (i == R.id.radio2) amount = 200;
            else if (i == R.id.radio3) amount = 500;
            else amount = 1000;
            binding.amount.setText(String.format(getResources().getString(R.string.rupee), amount));
        });

        binding.back.setOnClickListener(view -> super.onBackPressed());
        binding.addAmount.setOnClickListener(view -> {
            if (binding.radioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Select amount first.", Toast.LENGTH_SHORT).show();
            } else startPayment();
        });
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
            HomeActivity.dbUtils.postFundsHistory(amount);
            HomeActivity.dbUtils.postFunds(amount);
            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
            HomeActivity.dbUtils.getFunds(binding.funds, this);
        } catch (Exception e) {
            Log.e(TAG, "onPaymentSuccess: Failed", e);
        }

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }
}
