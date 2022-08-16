package com.pause.admin.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.FirebaseDatabase;
import com.pause.admin.PushNotificationService;
import com.pause.admin.R;
import com.pause.admin.databinding.DashboardBinding;
import com.pause.admin.viewmodels.FundsViewModel;
import com.pause.admin.viewmodels.HomeViewModel;
import com.razorpay.Checkout;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    public static final int TIME_DELAY = 2000;
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String TAG = HomeActivity.class.getSimpleName();
    public static long back_pressed;
    public static PushNotificationService notificationService;
    public DashboardBinding binding;
    public FundsViewModel fundsViewModel;
    public HomeViewModel homeViewModel;
    private SharedPreferences prefs;
    /*
    > Funds Button
    > Add Fund Page UI
    > Add Task UI
    > Chk for Google Map integration
    > View Task UI
    */
    public final ActivityResultLauncher<Intent> addFundsResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == HomeActivity.RESULT_OK) {
                //get funds from preferences
                String funds = prefs.getString("FUNDS", "0");
                Log.d(TAG, "Funds added so fetching prefs, funds:" + funds);
                binding.funds.setText(String.format(HomeActivity.this.getResources().getString(R.string.funds), funds));
            } else {
                Log.d(TAG, "Nope. No funds added.");
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Checkout.preload(getApplicationContext());
        notificationService = new PushNotificationService();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        fundsViewModel = new ViewModelProvider(this).get(FundsViewModel.class);
        layoutInitialize();
    }

    private void layoutInitialize() {
        binding = DashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addTask.setOnClickListener(view -> {
            Intent i = new Intent(this, NewTaskActivity.class);
            startActivity(i);
        });

        binding.viewTasks.setOnClickListener(view -> {
            Intent i = new Intent(this, TasksActivity.class);
            startActivity(i);
        });

        binding.addFunds.setOnClickListener(view -> {
            Intent i = new Intent(this, AddFundsActivity.class);
            addFundsResult.launch(i);
        });

        binding.profile.setOnClickListener(view -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        });

        //get funds from preferences
        String funds = prefs.getString("FUNDS", "0");
        binding.funds.setText(String.format(this.getResources().getString(R.string.funds), funds));
        fundsViewModel.getFunds().observe(this, amount -> {
            String fund = amount.toString();
            String str = String.format(this.getResources().getString(R.string.funds), fund);
            binding.funds.setText(str);
            // Store in prefs
            if (!funds.equals(fund)) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putString("FUNDS", fund).apply();
            }
        });

        binding.imageButton.setOnClickListener(v -> binding.profile.performClick());
        binding.more.setOnClickListener(v -> binding.profile.performClick());
        homeViewModel.getUsage().observe(this, entries -> {
            binding.graph.setVisibility(View.VISIBLE);
            binding.progressCircular.setVisibility(View.GONE);
            loadPieChart(entries);
        });
    }

    public void loadPieChart(ArrayList<PieEntry> entries) {
        float total = 0;
        for (PieEntry p : entries) total += p.getValue();
        setupPieChart(total);
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) colors.add(color);
        for (int color : ColorTemplate.VORDIPLOM_COLORS) colors.add(color);
        PieDataSet dataSet = new PieDataSet(entries, "Today's Usage");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(binding.graph));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        binding.graph.setData(data);
        binding.graph.invalidate();
    }

    public void setupPieChart(float total) {
        binding.graph.setDrawHoleEnabled(true);
        binding.graph.setUsePercentValues(false);
        binding.graph.setEntryLabelTextSize(12);
        binding.graph.setEntryLabelColor(Color.WHITE);
        binding.graph.setCenterTextColor(Color.WHITE);
        binding.graph.setCenterText("Total Usage:\n" + total + " Hrs");
        binding.graph.setHoleColor(ContextCompat.getColor(this, R.color.secondary_blue));
        binding.graph.setCenterTextSize(20);
        binding.graph.getDescription().setEnabled(false);
        binding.graph.animateXY(200, 600);

        Legend l = binding.graph.getLegend();
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        }
        Toast.makeText(this, "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}
