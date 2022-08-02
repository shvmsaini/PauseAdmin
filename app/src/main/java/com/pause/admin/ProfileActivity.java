package com.pause.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.pause.admin.databinding.ProfileActivityBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    public ProfileActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLayout();
    }

    private void initializeLayout() {
        binding = ProfileActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.fundsHistory.setOnClickListener(v -> {
            Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
        });
        loadLineChart();
        binding.back.setOnClickListener(v -> super.onBackPressed());
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
        LineChart mLineGraph = binding.graph;
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
        mLineGraph.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

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

        return incomeEntries.subList(0, 7);
    }
}