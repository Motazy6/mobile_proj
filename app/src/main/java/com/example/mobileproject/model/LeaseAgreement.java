package com.example.mobileproject.model;

public class LeaseAgreement {
    private int id;
    private int userId;
    private int housingId;
    private String leaseStartDate;
    private String leaseEndDate;
    private double monthlyRent;

    public LeaseAgreement() {}

    public LeaseAgreement(int id, int userId, int housingId, String leaseStartDate,
                          String leaseEndDate, double monthlyRent) {
        this.id = id;
        this.userId = userId;
        this.housingId = housingId;
        this.leaseStartDate = leaseStartDate;
        this.leaseEndDate = leaseEndDate;
        this.monthlyRent = monthlyRent;
    }

    public LeaseAgreement(int userId, int housingId, String leaseStartDate,
                          String leaseEndDate, double monthlyRent) {
        this.userId = userId;
        this.housingId = housingId;
        this.leaseStartDate = leaseStartDate;
        this.leaseEndDate = leaseEndDate;
        this.monthlyRent = monthlyRent;
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

    public int getHousingId() {
        return housingId;
    }

    public void setHousingId(int housingId) {
        this.housingId = housingId;
    }

    public String getLeaseStartDate() {
        return leaseStartDate;
    }

    public void setLeaseStartDate(String leaseStartDate) {
        this.leaseStartDate = leaseStartDate;
    }

    public String getLeaseEndDate() {
        return leaseEndDate;
    }

    public void setLeaseEndDate(String leaseEndDate) {
        this.leaseEndDate = leaseEndDate;
    }

    public double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }
}
