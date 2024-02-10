package com.example.mobileproject.model;

public class MenuItem {
    private String title;
    private int icon;
    private Class<?> activity;

    public MenuItem(String title, int icon, Class<?> activity) {
        this.title = title;
        this.icon = icon;
        this.activity = activity;
    }

    // Getters
    public String getTitle() { return title; }
    public int getIcon() { return icon; }
    public Class<?> getActivity() { return activity; }
}
