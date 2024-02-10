package com.example.mobileproject.model;

public class Task {
    private int id;
    private int userId;
    private String description;
    private long notificationTime;
    private boolean isDone;

    public Task() { }
    public Task(int id, int userId, String description, long notificationTime, boolean isDone) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.notificationTime = notificationTime;
        this.isDone = isDone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(long notificationTime) {
        this.notificationTime = notificationTime;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}

