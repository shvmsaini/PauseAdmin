package com.pause.admin;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.pause.admin.databinding.AddFundsActivityBinding;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class AddFundsActivity extends AppCompatActivity implements PaymentResultListener {
    public AddFundsActivityBinding binding;
    private int amount = 0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLayout();
    }

    private void initializeLayout() {
        binding = AddFundsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.radio1.setOnClickListener(view -> {
            binding.radioGroup.clearCheck();
            binding.radio1.toggle();
            amount = 100;
            binding.amount.setText("\u20B9 100");
        });
        binding.radio2.setOnClickListener(view -> {
            binding.radioGroup.clearCheck();
            binding.radio2.toggle();
            amount = 200;
            binding.amount.setText("\u20B9 200");
        });
        binding.radio3.setOnClickListener(view -> {
            binding.radioGroup.clearCheck();
            binding.radio3.toggle();
            amount = 500;
            binding.amount.setText("\u20B9 500");
        });
        binding.radio4.setOnClickListener(view -> {
            binding.radioGroup.clearCheck();
            binding.radio4.toggle();
            amount = 1000;
            binding.amount.setText("\u20B9 1000");
        });

        binding.back.setOnClickListener(view -> super.onBackPressed());
        binding.addAmount.setOnClickListener(view -> startPayment());
    }

    private void startPayment() {
        final int fund = amount;
        Checkout checkout = new Checkout();
        checkout.setImage(R.mipmap.ic_launcher); // logo
        checkout.setKeyID("rzp_test_JWw8SF8VJ9RJ2Q");
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Pause App");
            options.put("description", "Reference No. #123456");
            options.put("currency", "INR");
            options.put("amount", fund + "00");
            checkout.open(this, options);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }
}
