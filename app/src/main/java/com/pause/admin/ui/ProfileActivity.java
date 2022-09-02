package com.pause.admin.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pause.admin.R;
import com.pause.admin.databinding.ProfileActivityBinding;
import com.pause.admin.viewmodels.ProfileViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    public ProfileActivityBinding binding;
    public ProfileViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(this).get(ProfileViewModel.class);
        initializeLayout();
    }

    private void initializeLayout() {
        binding = ProfileActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fundsHistory.setOnClickListener(v -> {
            Intent i = new Intent(this, FundsHistoryActivity.class);
            startActivity(i);
        });
        model.getAllUsage().observe(this, sb -> {
            loadBarChart(sb.barEntries, sb.entriesDate, sb.appNames, sb.maxSize);
            binding.progressCircular.setVisibility(View.GONE);
            if (sb.barEntries.isEmpty()) {
                binding.loading.setVisibility(View.VISIBLE);
            }
        });
        binding.back.setOnClickListener(v -> super.onBackPressed());
    }

    private void loadBarChart(ArrayList<BarEntry> yVals, ArrayList<String> xVals, String[] appNames, int maxSize) {
        BarChart mChart = binding.stackedBar;
        /* Only Add colors as many as the maxSize */
        ArrayList<Integer> colors = new ArrayList<>();
        int i = 0;
        while (colors.size() < maxSize) {
            if (i < 4) colors.add(ColorTemplate.MATERIAL_COLORS[i]);
            else if (i < 9) colors.add(ColorTemplate.PASTEL_COLORS[i % 5]);
            else colors.add(ColorTemplate.LIBERTY_COLORS[i % 5]);
            ++i;
        }
//            for(int c : ColorTemplate.PASTEL_COLORS) colors.add(c);
//            for(int c : ColorTemplate.MATERIAL_COLORS) colors.add(c);

        BarDataSet dataSet = new BarDataSet(yVals, "Usage");
        dataSet.setDrawIcons(false);
        dataSet.setColors(colors);
        dataSet.setStackLabels(appNames);
        dataSet.setValueTextColor(Color.WHITE);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        /* individual data */
        BarData data = new BarData(dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(12f);
        //data.setValueFormatter(new IndexAxisValueFormatter());
        mChart.setData(data);
        mChart.setNoDataTextColor(Color.WHITE);
        Legend l = mChart.getLegend();
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        dataSet.setLabel("Daily usage by date");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
        xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        yAxis = mChart.getAxisRight();
        yAxis.setTextColor(Color.WHITE);

        mChart.animateX(400);
        mChart.animateY(400);
        mChart.getDescription().setText("Daily per usage by hours");
        mChart.getDescription().setTextColor(Color.WHITE);
        mChart.setPadding(0, 0, 0, 0);
        mChart.setClipToPadding(false);
        mChart.setFitBars(true);
        mChart.getAxisRight().setEnabled(false);
        mChart.invalidate();
    }

    private void loadLineChart() {
        List<String> xAxisValues = new ArrayList<>(Arrays.asList("01/01/01", "02/01/01", "03/01/01", "04/01/01", "05/01/01", "06/01/01", "07/01/01"));
        List<Entry> incomeEntries = getIncomeEntries();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        LineDataSet set1 = new LineDataSet(incomeEntries, "Usage");
        set1.setColor(Color.rgb(65, 168, 121));
        set1.setValueTextColor(Color.rgb(55, 70, 73));
        set1.setValueTextSize(10f);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSets.add(set1);

//customization
        LineChart v = null;
        LineChart mLineGraph = null; // bind graph here mLineGraph = binding.graph;
        mLineGraph.setTouchEnabled(true);
        mLineGraph.setDragEnabled(true);
        mLineGraph.setScaleEnabled(true);
        mLineGraph.setPinchZoom(true);
        mLineGraph.setDrawGridBackground(false);
        mLineGraph.setExtraLeftOffset(0);
        mLineGraph.setExtraRightOffset(0);
//to hide background lines
        mLineGraph.getXAxis().setDrawGridLines(false);
        mLineGraph.getAxisLeft().setDrawGridLines(false);
        mLineGraph.getAxisRight().setDrawGridLines(false);

//to hide right Y and top X border
        YAxis rightYAxis = mLineGraph.getAxisRight();
        rightYAxis.setEnabled(false);
        YAxis leftYAxis = mLineGraph.getAxisLeft();
        leftYAxis.setEnabled(true);
        XAxis topXAxis = mLineGraph.getXAxis();
        topXAxis.setEnabled(true);

        XAxis xAxis = mLineGraph.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        set1.setLineWidth(4f);
        set1.setCircleRadius(5f);
        set1.setDrawValues(true);
        set1.setCircleHoleColor(getResources().getColor(R.color.purple_500));
        set1.setCircleColor(getResources().getColor(R.color.purple_200));

//String setter in x-Axis
        mLineGraph.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));

        LineData data = new LineData(dataSets);
        mLineGraph.setData(data);
        mLineGraph.animateX(500);
        mLineGraph.invalidate();
        mLineGraph.getLegend().setEnabled(true);
        mLineGraph.getDescription().setEnabled(true);
    }

    private List<Entry> getIncomeEntries() {
        ArrayList<Entry> incomeEntries = new ArrayList<>();
        incomeEntries.add(new Entry(1, 13));
        incomeEntries.add(new Entry(2, 17));
        incomeEntries.add(new Entry(3, 16));
        incomeEntries.add(new Entry(4, 8));
        incomeEntries.add(new Entry(5, 10));
        incomeEntries.add(new Entry(6, 15));
        incomeEntries.add(new Entry(7, 18));
        return incomeEntries;
    }
}


