package com.pause.admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TasksDisplayAdapter extends RecyclerView.Adapter<TasksDisplayAdapter.ItemViewHolder> {
    private final String TAG = TasksDisplayAdapter.class.getName();
    public List<Task> tasks;
    public Context mContext;
    TasksActivity tasksActivity;

    public TasksDisplayAdapter(android.content.Context context, List<Task> tasks, TasksActivity tasksActivity) {
        this.tasks = tasks;
        this.mContext = context;
        this.tasksActivity = tasksActivity;
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
        holder.status.setText(t.getStatus());
        holder.taskType.setText(t.getTaskType());
        if (t.getResponse() == null || t.getStatus().equals("UNATTENDED")) {
            holder.doneDateParent.setVisibility(View.GONE);
            holder.responseParent.setVisibility(View.GONE);
            holder.approve.setVisibility(View.GONE);
            holder.disapprove.setVisibility(View.GONE);
            holder.reminder.setOnClickListener(v -> {
                HomeActivity.p.pushNotification(mContext, TasksActivity.childToken,
                        "Your task deadline is near!", t.getDetail());
                Toast.makeText(mContext, "Sent!", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.reminder.setVisibility(View.GONE);
            holder.itemView.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.teal_700));
            holder.response.setText(t.getResponse());
            holder.doneDate.setText(t.getDoneDate());
            holder.approve.setOnClickListener(view -> {
                Log.d(TAG, "onBindViewHolder: Task Approved");
                HomeActivity.dbUtils.postPoint(mContext);
                tasksActivity.deleteTask(t.getKEY(), position);
            });
            holder.disapprove.setOnClickListener(view -> {
                Log.d(TAG, "onBindViewHolder: Task Disapproved");
                tasksActivity.deleteTask(t.getKEY(), position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView taskDetail, deadline, status, response, taskType, doneDate;
        Button approve, disapprove, reminder;
        // Parents
        LinearLayout responseParent, doneDateParent;


        public ItemViewHolder(View itemView) {
            super(itemView);
            taskDetail = itemView.findViewById(R.id.task_detail);
            deadline = itemView.findViewById(R.id.task_deadline);
            status = itemView.findViewById(R.id.task_status);
            response = itemView.findViewById(R.id.task_response);
            approve = itemView.findViewById(R.id.approve);
            disapprove = itemView.findViewById(R.id.disapprove);
            doneDate = itemView.findViewById(R.id.done_date);
            taskType = itemView.findViewById(R.id.task_type_detail);
            // parents
            responseParent = itemView.findViewById(R.id.task_response_parent);
            doneDateParent = itemView.findViewById(R.id.task_done_date_parent);
            reminder = itemView.findViewById(R.id.reminder);
        }
    }


}
