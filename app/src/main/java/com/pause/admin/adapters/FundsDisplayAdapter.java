package com.pause.admin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pause.admin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class FundsDisplayAdapter extends RecyclerView.Adapter<FundsDisplayAdapter.ItemViewHolder> {
    private final String TAG = FundsDisplayAdapter.class.getSimpleName();
    ArrayList<String[]> arrayList;
    int lastPosition = -1;
    Context mContext;

    public FundsDisplayAdapter(Context mContext, ArrayList<String[]> arrayList) {
        this.mContext = mContext;
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
        holder.amount.setText("₹" + strings[1]);
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
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
