package com.pause.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    public static final int TIME_DELAY = 2000;
    private static final String TAG = HomeActivity.class.getSimpleName();
    public static long back_pressed;
    public static PushNotificationService p;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    public static DBUtils dbUtils;
    public DashboardBinding binding;
    /*
    > Dummy Data to Actual Data
    > Funds Button
    > Add Fund Page UI
    > Add Task UI
    > Chk for Google Map integration
    > View Task UI
    */

    public static void loadPieChart(Context c, ArrayList<PieEntry> entries, DashboardBinding binding) {
        float total = 0;
        for (PieEntry p : entries) total += p.getValue();
        setupPieChart(c, binding, total);
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

    public static void setupPieChart(Context c, DashboardBinding binding, float total) {
        binding.graph.setDrawHoleEnabled(true);
        binding.graph.setUsePercentValues(false);
        binding.graph.setEntryLabelTextSize(12);
        binding.graph.setEntryLabelColor(Color.WHITE);
        binding.graph.setCenterTextColor(Color.WHITE);
        binding.graph.setCenterText("Total Usage:\n" + total + " Hrs");
        binding.graph.setHoleColor(ContextCompat.getColor(c, R.color.secondary_blue));
        binding.graph.setCenterTextSize(20);
        binding.graph.getDescription().setEnabled(false);
        binding.graph.animateXY(200,600);

        Legend l = binding.graph.getLegend();
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Checkout.preload(getApplicationContext());
        dbUtils = new DBUtils();
        p = new PushNotificationService();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

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
            startActivity(i);
        });

        binding.profile.setOnClickListener(view -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        });

        //get funds from preferences
        String funds = prefs.getString("FUNDS" , "0");
        binding.funds.setText(String.format(this.getResources().getString(R.string.funds), funds));
        dbUtils.getFunds(binding.funds, this);

        binding.imageButton.setOnClickListener(v -> binding.profile.performClick());
        binding.more.setOnClickListener(v -> binding.profile.performClick());
        HomeActivity.dbUtils.getUsage(this, binding);
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
