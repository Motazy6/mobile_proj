package com.example.mobileproject;

import com.example.mobileproject.model.User;

public class UserSessionManager {
    private static UserSessionManager instance;
    private User currentUser;

    private UserSessionManager() {}

    public static synchronized UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
    }
}