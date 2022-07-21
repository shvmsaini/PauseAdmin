package com.pause.admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TasksDisplayAdapter extends RecyclerView.Adapter<TasksDisplayAdapter.ItemViewHolder> {
    private final String TAG = TasksDisplayAdapter.class.getName();
    public List<Task> tasks;
    public Context mContext;

    public TasksDisplayAdapter(android.content.Context context, List<Task> tasks) {
        this.tasks = tasks;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tasks_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksDisplayAdapter.ItemViewHolder holder, int position) {
        Task t = tasks.get(position);
        holder.taskDetail.setText(t.getDetail());
        holder.deadline.setText(t.getDeadline());
        holder.approved.setText(t.getApprove());
        if(t.getComment() == null && t.getApprove().equals("UNATTENDED")){
            holder.comment.setVisibility(View.GONE);
            holder.approve.setVisibility(View.GONE);
            holder.disapprove.setVisibility(View.GONE);
        }
        else{
            holder.itemView.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.teal_700));
            holder.comment.setText(t.getComment());
            // TODO: handle approve/disapprove buttons
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView taskDetail;
        TextView deadline;
        TextView approved;
        TextView comment;
        Button approve;
        Button disapprove;

        public ItemViewHolder(View itemView) {
            super(itemView);
            taskDetail = itemView.findViewById(R.id.task_detail);
            deadline = itemView.findViewById(R.id.task_deadline);
            approved = itemView.findViewById(R.id.task_approved);
            comment = itemView.findViewById(R.id.task_comment);
            approve = itemView.findViewById(R.id.approve);
            disapprove = itemView.findViewById(R.id.disapprove);

        }
    }
}
