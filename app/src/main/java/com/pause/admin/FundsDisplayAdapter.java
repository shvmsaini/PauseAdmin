package com.pause.admin;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class FundsDisplayAdapter  extends RecyclerView.Adapter<FundsDisplayAdapter.ItemViewHolder>{
    private final String TAG = FundsDisplayAdapter.class.getSimpleName();
    ArrayList<String[]> arrayList;

    public FundsDisplayAdapter(ArrayList<String[]> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public FundsDisplayAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.funds_item, parent, false);
        return new FundsDisplayAdapter.ItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FundsDisplayAdapter.ItemViewHolder holder, int position) {
        String[] strings = arrayList.get(position);
        Log.d(TAG, "onBindViewHolder: " + Arrays.toString(strings));
        Date date = new Date(Long.parseLong(strings[0]));
        String fullDay = new SimpleDateFormat("dd MMMM, yyyy", Locale.US).format(date);
        holder.date.setText(fullDay);
        holder.amount.setText( "₹" + strings[1]);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView date, amount;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}
