package com.example.mobileproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.R;
import com.example.mobileproject.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private final OnTaskDoneListener onTaskDoneListener;

    public TaskAdapter(List<Task> tasks, OnTaskDoneListener onTaskDoneListener) {
        this.tasks = tasks;
        this.onTaskDoneListener = onTaskDoneListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView, onTaskDoneListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.bind(currentTask);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public interface OnTaskDoneListener {
        void onTaskDone(Task task);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTaskDescription;
        private final TextView textViewNotificationTime;
        private final CheckBox checkBoxIsDone;
        private Task currentTask;
        private OnTaskDoneListener onTaskDoneListener;

        TaskViewHolder(View itemView, OnTaskDoneListener onTaskDoneListener) {
            super(itemView);
            textViewTaskDescription = itemView.findViewById(R.id.textViewTaskDescription);
            textViewNotificationTime = itemView.findViewById(R.id.textViewNotificationTime);
            checkBoxIsDone = itemView.findViewById(R.id.checkBoxIsDone);
            this.onTaskDoneListener = onTaskDoneListener;
        }

        private void setOnClickListenerForCheckBox(CheckBox checkBoxIsDone, OnTaskDoneListener onTaskDoneListener)
        {
            checkBoxIsDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && currentTask != null) {
                    onTaskDoneListener.onTaskDone(currentTask);
                }
            });
        }

        void bind(Task task) {
            currentTask = task;
            textViewTaskDescription.setText(task.getDescription());
            textViewNotificationTime.setText(formatTime(task.getNotificationTime()));
            checkBoxIsDone.setChecked(task.isDone());
            setOnClickListenerForCheckBox(checkBoxIsDone, this.onTaskDoneListener);
        }

        private String formatTime(long timeMillis) {
            return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date(timeMillis));
        }
    }
}