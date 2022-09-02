package com.pause.admin.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.pause.admin.R;
import com.pause.admin.databinding.DashboardBinding;
import com.pause.admin.service.PushNotificationService;
import com.pause.admin.service.locker;
import com.pause.admin.viewmodels.AirtableSpecific;
import com.pause.admin.viewmodels.FundsViewModel;
import com.pause.admin.viewmodels.HomeViewModel;
import com.razorpay.Checkout;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    public static final int TIME_DELAY = 2000;
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final int RESULT_ENABLE = 11;
    private static final String TAG = HomeActivity.class.getSimpleName();
    public static long back_pressed;
    public static PushNotificationService notificationService;
    public DashboardBinding binding;
    public FundsViewModel fundsViewModel;
    public HomeViewModel homeViewModel;
    private SharedPreferences prefs;
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
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName compName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Checkout.preload(getApplicationContext());
        notificationService = new PushNotificationService();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        fundsViewModel = new ViewModelProvider(this).get(FundsViewModel.class);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        compName = new ComponentName(this, locker.class);
        layoutInitialize();

        PackageManager packageManager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

//        List<ResolveInfo> appList = packageManager.queryIntentActivities(mainIntent, 0);
//        appList.sort(new ResolveInfo.DisplayNameComparator(packageManager));
//        List<PackageInfo> packs = packageManager.getInstalledPackages(0);
//        for (int i = 0; i < packs.size(); i++) {
//            PackageInfo p = packs.get(i);
//            ApplicationInfo a = p.applicationInfo;
//            // skip system apps if they shall not be included
//            if ((a.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
//                continue;
//            }
//            Intent intent = new Intent();
//            intent.setComponent(new ComponentName("com.myapp", "com.myapp.launcher.settings"));
//            ResolveInfo app = packageManager.resolveActivity(intent, 0);
//            packageManager.resolveActivity();
//            appList.add(app);
//        }

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

        binding.lock.setOnClickListener(v -> {
            Log.d(TAG, "layoutInitialize: Clicked on lock");
            boolean active = devicePolicyManager.isAdminActive(compName);
            if (active) {
                devicePolicyManager.lockNow();
                return;
            } else {
                Toast.makeText(this, "You need to enable the Admin Device Features", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Additional text explaining why we need this permission");
            startActivityForResult(intent, RESULT_ENABLE);

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
        AirtableSpecific specific = new ViewModelProvider(this).get(AirtableSpecific.class);
        specific.getUsage(this).observe(this, task -> {
            ArrayList<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(Float.parseFloat(Objects.requireNonNull(task.get("whatsapp")).toString()), "WhatsApp"));
            entries.add(new PieEntry(Float.parseFloat(Objects.requireNonNull(task.get("instagram")).toString()), "Instagram"));
            entries.add(new PieEntry(Float.parseFloat(Objects.requireNonNull(task.get("others")).toString()), "Others"));
            entries.add(new PieEntry(Float.parseFloat(Objects.requireNonNull(task.get("chrome")).toString()), "Chrome"));
            entries.add(new PieEntry(Float.parseFloat(Objects.requireNonNull(task.get("youtube")).toString()), "Youtube"));

            Log.d(TAG, "layoutInitialize: " + entries);
            binding.graph.setVisibility(View.VISIBLE);
            binding.progressCircular.setVisibility(View.GONE);
            loadPieChart(entries);
        });

//        homeViewModel.getUsage().observe(this, entries -> {
//            binding.graph.setVisibility(View.VISIBLE);
//            binding.progressCircular.setVisibility(View.GONE);
//            loadPieChart(entries);
//        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_ENABLE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(HomeActivity.this, "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(HomeActivity.this, "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
