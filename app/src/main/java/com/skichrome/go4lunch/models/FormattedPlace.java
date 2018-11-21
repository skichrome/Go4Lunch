package com.skichrome.go4lunch.models;

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

    public FormattedPlace(String mId,
                          String mName,
                          String mAddress,
                          String mPhoneNumber,
                          String mWebsite,
                          double mLocationLatitude,
                          double mLocationLongitude)
    {
        this.id = mId;
        this.name = mName;
        this.address = mAddress;
        this.phoneNumber = mPhoneNumber;
        this.website = mWebsite;
        this.locationLatitude = mLocationLatitude;
        this.locationLongitude = mLocationLongitude;
    }

    public FormattedPlace(String mId,
                          String mName,
                          double mLocationLatitude,
                          double mLocationLongitude,
                          double mRating,
                          String mPhotoRef)
    {
        this.id = mId;
        this.name = mName;
        this.locationLatitude = mLocationLatitude;
        this.locationLongitude = mLocationLongitude;
        this.rating = mRating;
        this.photoReference = mPhotoRef;
    }

    //=========================================
    // Getters
    //=========================================

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhotoReference() { return photoReference; }
    public String getAperture() { return aperture; }
    public double getLocationLatitude() { return locationLatitude; }
    public double getLocationLongitude() { return locationLongitude; }
    public String getDistance() { return distance; }
    public String getWebsite() { return website; }
    public String getPhoneNumber() { return phoneNumber; }
    public double getRating() { return rating; }
    public String getIsOpenNow() { return isOpenNow; }

    //=========================================
    // Setters
    //=========================================

    public void setWebsite(String mWebsite) { website = mWebsite; }
    public void setPhoneNumber(String mPhoneNumber) { phoneNumber = mPhoneNumber; }
    public void setDistance(String mDistance) { distance = mDistance; }
    public void setAperture(String mAperture) { aperture = mAperture; }
    public void setIsOpenNow(String mIsOpenNow) { isOpenNow = mIsOpenNow; }

    //=========================================
    // Method [DEBUG]
    //=========================================

    @Override
    public String toString()
    {
        return "FormattedPlace{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", latitude='" + locationLatitude + '\'' +
                ", longitude='" + locationLongitude + '\'' +
                ", aperture='" + aperture + '\'' +
                ", distance='" + distance + '\'' +
                ", website='" + website + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", imageUrl='" + photoReference + '\'' +
                '}';
    }
}