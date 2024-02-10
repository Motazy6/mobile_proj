package com.example.mobileproject.model;

public class Housing {
    private int id;
    private String title;
    private String description;
    private double price;
    private String location;
    private String amenities;
    private String leaseDuration;
    private String availableFrom;
    private String utilitiesIncluded;
    private boolean isActive;


    public Housing() {}

    public Housing(int id, String title, String description, double price, String location,
                   String amenities, String leaseDuration, String availableFrom, String utilitiesIncluded) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.amenities = amenities;
        this.leaseDuration = leaseDuration;
        this.availableFrom = availableFrom;
        this.utilitiesIncluded = utilitiesIncluded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getLeaseDuration() {
        return leaseDuration;
    }

    public void setLeaseDuration(String leaseDuration) {
        this.leaseDuration = leaseDuration;
    }

    public String getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(String availableFrom) {
        this.availableFrom = availableFrom;
    }

    public String getUtilitiesIncluded() {
        return utilitiesIncluded;
    }

    public void setUtilitiesIncluded(String utilitiesIncluded) {
        this.utilitiesIncluded = utilitiesIncluded;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
