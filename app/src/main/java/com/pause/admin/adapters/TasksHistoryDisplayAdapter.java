package com.pause.admin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pause.admin.R;
import com.pause.admin.pojo.Task;
import com.pause.admin.ui.TasksHistoryActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TasksHistoryDisplayAdapter extends RecyclerView.Adapter<TasksHistoryDisplayAdapter.ItemViewHolder> {
    private static final String TAG = TasksDisplayAdapter.class.getName();
    public List<Task> tasks;
    public Context context;
    public TasksHistoryActivity tasksActivity;
    public List<Task> approved;
    public List<Task> disapproved;
    private int lastPosition = -1;

    public TasksHistoryDisplayAdapter(Context context, List<Task> tasks,
                                      TasksHistoryActivity tasksActivity) {
        this.tasks = tasks;
        this.context = context;
        this.tasksActivity = tasksActivity;
        approved = new ArrayList<>();
        disapproved = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getStatus().equals("1")) approved.add(t);
            else disapproved.add(t);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tasks_history_item, parent, false);
        return new ItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TasksHistoryDisplayAdapter.ItemViewHolder holder, int position) {
        Task t = tasks.get(position);
        holder.taskDetail.setText(t.getDetail());

        // Deadline
        LocalDate date = LocalDate.parse(t.getDeadline(), DateTimeFormatter.ofPattern("dd/MM/yy"));
        int dat = date.getDayOfMonth();
        String month = date.getMonth().toString().substring(0, 3);
        month = month.substring(0,1).toUpperCase(Locale.ROOT) + month.substring(1, 3).toLowerCase(Locale.ROOT);
        int year = date.getYear();
        String formattedDate = dat + " " + month + ", " + year % 100;
        holder.deadline.setText(formattedDate);

        // Attended On
        date = LocalDate.parse(t.getAttendedDate(), DateTimeFormatter.ofPattern("dd/MM/yy"));
        dat = date.getDayOfMonth();
        month = date.getMonth().toString().substring(0, 3);
        month = month.substring(0,1).toUpperCase(Locale.ROOT) + month.substring(1, 3).toLowerCase(Locale.ROOT);
        year = date.getYear();
        formattedDate = dat + " " + month + ", " + year % 100;
        holder.attended.setText(formattedDate);

        // Complete on
        date = LocalDate.parse(t.getAttendedDate(), DateTimeFormatter.ofPattern("dd/MM/yy"));
        dat = date.getDayOfMonth();
        month = date.getMonth().toString().substring(0, 3);
        month = month.substring(0,1).toUpperCase(Locale.ROOT) + month.substring(1, 3).toLowerCase(Locale.ROOT);
        year = date.getYear();
        formattedDate = dat + " " + month + ", " + year % 100;
        holder.doneDate.setText(formattedDate);

        holder.taskType.setText(t.getTaskType());
        if (t.getTaskType().equals("Location Type"))
            holder.taskType.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_location_on_24, 0, 0, 0);
        else if (t.getTaskType().equals("App Type"))
            holder.taskType.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_android_24, 0, 0, 0);

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

        if (tasks.get(position).getStatus().equals("1")) {
            holder.itemView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.approve));
            holder.attended_sibling.setText("Approved On");
        } else {
            holder.itemView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.disapprove));
            holder.attended_sibling.setText("Disapproved On");
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
        TextView taskDetail, deadline, response, taskType, doneDate, attended;
        // Parents
        LinearLayout response_parent, doneDate_parent, dates_parent;
        // Sibling
        TextView response_sibling, attended_sibling;

        public ItemViewHolder(View itemView) {
            super(itemView);
            taskDetail = itemView.findViewById(R.id.task_detail);
            deadline = itemView.findViewById(R.id.task_deadline);
            response = itemView.findViewById(R.id.task_response);
            doneDate = itemView.findViewById(R.id.done_date);
            taskType = itemView.findViewById(R.id.task_type_detail);
            attended = itemView.findViewById(R.id.task_attended);
            // parents
            response_parent = itemView.findViewById(R.id.task_response_parent);
            doneDate_parent = itemView.findViewById(R.id.task_done_date_parent);
            dates_parent = itemView.findViewById(R.id.LL_dates);
            // sibling
            response_sibling = itemView.findViewById(R.id.task_response_sibling);
            attended_sibling = itemView.findViewById(R.id.task_attended_sibling);
        }
    }

}

