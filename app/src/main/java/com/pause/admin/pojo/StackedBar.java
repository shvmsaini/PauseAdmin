package com.pause.admin.pojo;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class StackedBar {
    public ArrayList<BarEntry> barEntries;
    public ArrayList<String> entriesDate;
    public String[] appNames;
    public int maxSize;

    public StackedBar(ArrayList<BarEntry> barEntries, ArrayList<String> entriesDate, String[] appNames, int maxSize) {
        this.barEntries = barEntries;
        this.entriesDate = entriesDate;
        this.appNames = appNames;
        this.maxSize = maxSize;
    }
}
