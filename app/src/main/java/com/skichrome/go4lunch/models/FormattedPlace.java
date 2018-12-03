package com.skichrome.go4lunch.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class FormattedPlace implements Serializable
{
    //=========================================
    // Fields
    //=========================================

    // Fields completed by first Google API
    private String id;
    private String name;
    private double locationLatitude;
    private double locationLongitude;
    private double rating;
    private String photoReference;

    // Fields completed by second Google API
    private String address;
    private String website;
    private String phoneNumber;
    private String aperture;
    private String isOpenNow;

    // Field calculated from second Google API
    private String distance;

    //=========================================
    // Constructor
    //=========================================

    public FormattedPlace() { }

    public FormattedPlace(String id,
                          String name,
                          String address,
                          String phoneNumber,
                          String website,
                          double locationLatitude,
                          double locationLongitude)
    {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
    }

    public FormattedPlace(String id,
                          String name,
                          double locationLatitude,
                          double locationLongitude,
                          double rating,
                          String photoRef)
    {
        this.id = id;
        this.name = name;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.rating = rating;
        this.photoReference = photoRef;
    }

    //=========================================
    // Getters
    //=========================================

    public String getId() { return id; }
    public String getName() { return name; }
    public double getLocationLatitude() { return locationLatitude; }
    public double getLocationLongitude() { return locationLongitude; }
    public double getRating() { return rating; }
    public String getPhotoReference() { return photoReference; }
    public String getAddress() { return address; }
    public String getWebsite() { return website; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAperture() { return aperture; }
    public String getIsOpenNow() { return isOpenNow; }
    public String getDistance() { return distance; }

    //=========================================
    // Setters
    //=========================================

    public void setWebsite(String website) { this.website = website; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setDistance(String distance) { this.distance = distance; }
    public void setAperture(String aperture) { this.aperture = aperture; }
    public void setIsOpenNow(String isOpenNow) { this.isOpenNow = isOpenNow; }

    //=========================================
    // Method [DEBUG]
    //=========================================

    @NonNull
    @Override
    public String toString()
    {
        return "FormattedPlace{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", locationLatitude=" + locationLatitude +
                ", locationLongitude=" + locationLongitude +
                ", rating=" + rating +
                ", photoReference='" + photoReference + '\'' +
                ", address='" + address + '\'' +
                ", website='" + website + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", aperture='" + aperture + '\'' +
                ", isOpenNow='" + isOpenNow + '\'' +
                ", distance='" + distance + '\'' +
                '}';
    }
}