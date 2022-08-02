package com.pause.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pause.admin.databinding.DashboardBinding;
import com.razorpay.Checkout;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    public static final int TIME_DELAY = 2000;
    public static long back_pressed;
    private static final String TAG = HomeActivity.class.getSimpleName();
    public DashboardBinding binding;
    public static DBUtils dbUtils;
    /*
    TODO: 1. Graph and analytics : inprogress MPAndroid Chart
    TODO: 2. Fund Add and withdraw :inprogress
    TODO: 3. App data tracker
    TODO: 4. Admin app to child app push notify :inprogress
    TODO: 5. Transaction Tracker
    TODO: 6. Task Assign (approve / disapproval) : inprogress
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInitialize();
        Checkout.preload(getApplicationContext());
        dbUtils = new DBUtils();
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
            startActivity(i);
        });

        binding.withdrawFunds.setOnClickListener(view -> {
            Intent i = new Intent(this, WithdrawActivity.class);
            startActivity(i);
        });
        binding.profile.setOnClickListener(v ->{
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        });
        binding.imageButton.setOnClickListener(v->{
            binding.profile.performClick();
        });
        binding.more.setOnClickListener(v ->{
            binding.profile.performClick();
        });
        setupPieChart();
        loadPieChart();
    }

    private void loadPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        //TODO: get this data from db
        entries.add(new PieEntry(0.2f, "Bromite"));
        entries.add(new PieEntry(0.5f, "Dog"));
        entries.add(new PieEntry(0.1f, "Kot"));
        entries.add(new PieEntry(0.6f, "Clac"));
        entries.add(new PieEntry(0.2f, "App"));
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }
        PieDataSet dataSet = new PieDataSet(entries, "Example Usage");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(binding.graph));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        binding.graph.setData(data);
        binding.graph.invalidate();

    }

    private void setupPieChart() {
        binding.graph.setDrawHoleEnabled(true);
        binding.graph.setUsePercentValues(true);
        binding.graph.setEntryLabelTextSize(12);
        binding.graph.setEntryLabelColor(Color.BLACK);
        binding.graph.setCenterTextColor(Color.WHITE);
        binding.graph.setCenterText("Total Usage:\n" + "8.5 Hrs");
        binding.graph.setHoleColor(ContextCompat.getColor(this, R.color.secondary_blue));
        binding.graph.setCenterTextSize(22);
        binding.graph.getDescription().setEnabled(false);
        Legend l = binding.graph.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
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
