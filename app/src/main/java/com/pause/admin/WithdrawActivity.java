package com.pause.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pause.admin.databinding.WithdrawFundsActivityBinding;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Transfer;

import org.json.JSONException;
import org.json.JSONObject;

public class WithdrawActivity extends AppCompatActivity{
    public WithdrawFundsActivityBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLayout();
    }
    private void initializeLayout(){
        binding = WithdrawFundsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.terms.setOnClickListener(view ->{
         //   binding.withdrawAmount.setEnabled(binding.terms.isChecked());
        });
        
        binding.withdrawAmount.setOnClickListener(view -> {
            if(!binding.terms.isChecked()){
                Toast.makeText(this, "Accept Terms & Conditions", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                RazorpayClient client = new RazorpayClient("rzp_test_JWw8SF8VJ9RJ2Q", "tusStU28MLtu8ey8etfanuBj");
                JSONObject transferRequest = new JSONObject();
                transferRequest.put("amount",50000);
                transferRequest.put("currency","INR");
                transferRequest.put("account","acc_CPRsN1LkFccllA");
                Thread t = new Thread(() -> {

                    try {
                        Transfer transfer = client.transfers.create(transferRequest);
                    } catch (RazorpayException e) {
                        e.printStackTrace();
                    }
                });
                t.start();

            } catch (RazorpayException | JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
        });

        binding.back.setOnClickListener(view -> super.onBackPressed());
    }
}
