package com.example.mobileproject.model;

public class HousingHistory {
    private int id;
    private int userId;
    private String housingName;
    private String address;
    private String startDate;
    private String endDate;
    private String notes;

    public HousingHistory() { }
    public HousingHistory(int id, int userId, String housingName, String address, String startDate, String endDate, String notes) {
        this.id = id;
        this.userId = userId;
        this.housingName = housingName;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getHousingName() {
        return housingName;
    }

    public void setHousingName(String housingName) {
        this.housingName = housingName;
    }
}

