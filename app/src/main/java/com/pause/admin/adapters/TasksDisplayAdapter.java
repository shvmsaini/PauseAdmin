package com.pause.admin.adapters;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.pause.admin.R;
import com.pause.admin.pojo.Task;
import com.pause.admin.ui.HomeActivity;
import com.pause.admin.ui.TasksActivity;
import com.pause.admin.viewmodels.TasksViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TasksDisplayAdapter extends RecyclerView.Adapter<TasksDisplayAdapter.ItemViewHolder> {
    private static final String TAG = TasksDisplayAdapter.class.getName();
    public List<Task> tasks;
    public Context context;
    public TasksActivity tasksActivity;
    public TasksViewModel model;
    public List<Task> attended;
    public List<Task> unattended;
    private int lastPosition = -1;

    public TasksDisplayAdapter(android.content.Context context, List<Task> tasks,
                               TasksActivity tasksActivity, TasksViewModel model) {
        this.tasks = tasks;
        this.context = context;
        this.tasksActivity = tasksActivity;
        this.model = model;
        attended = new ArrayList<>();
        unattended = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getResponse() == null) unattended.add(t);
            else attended.add(t);
        }
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
        LocalDate date = LocalDate.parse(t.getDeadline(), DateTimeFormatter.ofPattern("dd/MM/yy"));
        String deadline = DateTimeFormatter.ofPattern("d MMMM, yyyy").format(date);
        holder.deadline.setText(deadline);
        holder.taskType.setText(t.getTaskType());
        if (t.getTaskType().equals("Location Type"))
            holder.taskType.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_location_on_24, 0, 0, 0);
        else if (t.getTaskType().equals("App Type"))
            holder.taskType.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_android_24, 0, 0, 0);

        if (t.getResponse() == null) {
            holder.doneDate_parent.setVisibility(View.GONE);
            holder.response_parent.setVisibility(View.GONE);
            holder.approve.setVisibility(View.GONE);
            holder.disapprove.setVisibility(View.GONE);
            holder.dates_parent.setGravity(Gravity.CENTER);
            holder.reminder.setOnClickListener(v -> {
                holder.reminder.setEnabled(false);
                HomeActivity.notificationService.pushNotification(context, TasksActivity.childToken,
                        "Your task deadline is near!", t.getDetail());
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ((TasksActivity) context).runOnUiThread(() -> holder.reminder.setEnabled(true));
                    }
                }, 2000);
                Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.reminder.setVisibility(View.GONE);
            holder.taskType.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.secondary_blue));
            holder.itemView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.zero_blue));
            holder.response.setText(t.getResponse());
            holder.response_sibling.setOnClickListener(v -> {
                boolean visible = holder.response.getVisibility() == View.VISIBLE;
                if (visible) {
                    holder.response.setVisibility(View.GONE);
                    holder.response_sibling.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_baseline_expand_less_24, 0);
                } else {
                    holder.response.setVisibility(View.VISIBLE);
                    holder.response_sibling.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_baseline_expand_more_24, 0);
                }
            });
            date = LocalDate.parse(t.getDoneDate(), DateTimeFormatter.ofPattern("dd/MM/yy"));
            String doneDate = DateTimeFormatter.ofPattern("d MMMM, yyyy").format(date);
            holder.doneDate.setText(doneDate);
            holder.approve.setOnClickListener(view -> {
                Log.d(TAG, "onBindViewHolder: Task Approved");
                model.postPoint().observe((LifecycleOwner) context, bool -> {
                    if (bool)
                        Toast.makeText(context, "Reward unlocked!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Something's wrong. Try again later.", Toast.LENGTH_SHORT).show();
                });
                model.deleteTask(t.getKEY(), tasksActivity.adapter, position);
            });
            holder.disapprove.setOnClickListener(view -> {
                Log.d(TAG, "onBindViewHolder: Task Disapproved");
                model.deleteTask(t.getKEY(), tasksActivity.adapter, position);
            });

        }
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView taskDetail, deadline, response, taskType, doneDate;
        Button approve, disapprove, reminder;
        // Parents
        LinearLayout response_parent, doneDate_parent, dates_parent;
        // Sibling
        TextView response_sibling;

        public ItemViewHolder(View itemView) {
            super(itemView);
            taskDetail = itemView.findViewById(R.id.task_detail);
            deadline = itemView.findViewById(R.id.task_deadline);
            response = itemView.findViewById(R.id.task_response);
            approve = itemView.findViewById(R.id.approve);
            disapprove = itemView.findViewById(R.id.disapprove);
            doneDate = itemView.findViewById(R.id.done_date);
            taskType = itemView.findViewById(R.id.task_type_detail);
            // parents
            response_parent = itemView.findViewById(R.id.task_response_parent);
            doneDate_parent = itemView.findViewById(R.id.task_done_date_parent);
            reminder = itemView.findViewById(R.id.reminder);
            dates_parent = itemView.findViewById(R.id.LL_dates);
            // sibling
            response_sibling = itemView.findViewById(R.id.task_response_sibling);
        }
    }

}
