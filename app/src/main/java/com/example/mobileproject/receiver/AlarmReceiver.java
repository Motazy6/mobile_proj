package com.example.mobileproject.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.mobileproject.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "TASK_NOTIFICATION_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        long taskId = intent.getLongExtra("TASK_ID", -1);
        String taskDescription = intent.getStringExtra("TASK_DESCRIPTION");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Reminder Channel";
            String description = "Channel for Task Reminder Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_rec)
                .setContentTitle("Task Reminder")
                .setContentText(taskDescription)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify((int) taskId, builder.build());
    }
}

