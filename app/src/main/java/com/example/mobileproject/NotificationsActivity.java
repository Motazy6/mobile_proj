package com.example.mobileproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileproject.adapter.TaskAdapter;
import com.example.mobileproject.model.Task;
import com.example.mobileproject.receiver.AlarmReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NotificationsActivity extends AppCompatActivity {

    private EditText editTextTaskDescription;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private long notificationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        editTextTaskDescription = findViewById(R.id.editTextTaskDescription);
        Button buttonSetNotificationTime = findViewById(R.id.buttonSetNotificationTime);
        Button buttonSaveTask = findViewById(R.id.buttonSaveTask);
        recyclerView = findViewById(R.id.recyclerViewTasks);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(new ArrayList<>(), this::onTaskDone);
        recyclerView.setAdapter(adapter);

        buttonSetNotificationTime.setOnClickListener(v -> showDateTimePicker());
        buttonSaveTask.setOnClickListener(v -> saveTask());

        loadTasks();
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Notifications and List Todo");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                notificationTime = date.getTimeInMillis();
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void addTask(Task task) {
        String url = "http://10.0.2.2:5000/task";

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject taskData = new JSONObject();
        try {
            taskData.put("user_id", UserSessionManager.getInstance().getCurrentUser().getId());
            taskData.put("description", task.getDescription());
            taskData.put("notification_time", task.getNotificationTime());
            taskData.put("is_done", task.isDone());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(NotificationsActivity.this, "Error creating task data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, taskData,
                response -> {
                    try {
                        int taskId = response.getInt("task_id");
                        Toast.makeText(NotificationsActivity.this, "Task added successfully with ID: " + taskId, Toast.LENGTH_SHORT).show();
                        scheduleNotification(task.getId(), task.getDescription(), task.getNotificationTime());
                        loadTasks();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(NotificationsActivity.this, "Error reading task ID from response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(NotificationsActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }

    private void saveTask() {
        String description = editTextTaskDescription.getText().toString();
        Task task = new Task();
        task.setDescription(description);
        task.setNotificationTime(notificationTime);
        task.setDone(false);
        addTask(task);
    }

    private void scheduleNotification(long taskId, String taskDescription, long notificationTimeMillis) {
        scheduleAlarm(taskDescription, notificationTimeMillis, 0);

        scheduleAlarm(taskDescription, notificationTimeMillis + TimeUnit.MINUTES.toMillis(10), 1);

        scheduleAlarm(taskDescription, notificationTimeMillis + TimeUnit.MINUTES.toMillis(20), 2);
    }

    private void scheduleAlarm(String taskDescription, long timeMillis, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("TASK_ID", requestCode);
        intent.putExtra("TASK_DESCRIPTION", taskDescription);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
        }
    }

    private void updateTask(Task task) {
        String url = "http://10.0.2.2:5000/task/" + task.getId();

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject taskData = new JSONObject();
        try {
            taskData.put("is_done", task.isDone());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(NotificationsActivity.this, "Error creating update data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, taskData,
                response -> {
                    Toast.makeText(NotificationsActivity.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                    loadTasks();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(NotificationsActivity.this, "Failed to update task", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }


    private void onTaskDone(Task task) {
        task.setDone(true);
        updateTask(task);
    }

    private void fetchAllTasks(int userId) {
        String url = "http://10.0.2.2:5000/tasks/" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Task> tasks = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject taskObject = response.getJSONObject(i);

                            int id = taskObject.getInt("id");
                            String description = taskObject.getString("description");
                            long notificationTime = taskObject.getLong("notification_time");
                            boolean isDone = taskObject.getInt("is_done") == 1 ? true : false;

                            Task task = new Task(id, userId, description, notificationTime, isDone);
                            tasks.add(task);
                        }

                        adapter.setTasks(tasks);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(NotificationsActivity.this, "Error parsing task data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(NotificationsActivity.this, "Error fetching task data", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonArrayRequest);
    }


    private void loadTasks() {
        fetchAllTasks(UserSessionManager.getInstance().getCurrentUser().getId());
    }
}
