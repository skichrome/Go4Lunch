package com.skichrome.go4lunch.models;

import java.io.Serializable;
import java.util.List;

public class FormattedPlace implements Serializable
{
    //=========================================
    // Fields
    //=========================================

    private final String id;
    private final String name;
    private final String address;
    private final String aperture;
    private final double locationLatitude;
    private final double locationLongitude;
    private final String distance;
    private final String website;
    private final String phoneNumber;
    private final String imageUrl;

    private String rating;
    private List<String> workmates;

    //=========================================
    // Constructor
    //=========================================

    public FormattedPlace(String mId,
                          String mName,
                          String mAddress,
                          String mAperture,
                          double mLocationLatitude,
                          double mLocationLongitude,
                          String mDistance,
                          String mWebsite,
                          String mPhoneNumber,
                          String mImageUrl,
                          String mRating,
                          List<String> mWorkmates)
    {
        this.id = mId;
        this.name = mName;
        this.address = mAddress;
        this.aperture = mAperture;
        this.locationLatitude = mLocationLatitude;
        this.locationLongitude = mLocationLongitude;
        this.distance = mDistance;
        this.website = mWebsite;
        this.phoneNumber = mPhoneNumber;
        this.imageUrl = mImageUrl;
        this.rating = mRating;
        this.workmates = mWorkmates;
    }

    //=========================================
    // Getters
    //=========================================

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getAddress()
    {
        return address;
    }

    public String getAperture()
    {
        return aperture;
    }

    public double getLocationLatitude()
    {
        return locationLatitude;
    }

    public double getLocationLongitude()
    {
        return locationLongitude;
    }

    public String getDistance()
    {
        return distance;
    }

    public String getWebsite()
    {
        return website;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public String getRating()
    {
        return rating;
    }

    public void setRating(String mRating)
    {
        rating = mRating;
    }

    //=========================================
    // Setters
    //=========================================

    public List<String> getWorkmates()
    {
        return workmates;
    }

    public void setWorkmates(List<String> mWorkmates)
    {
        workmates = mWorkmates;
    }

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
                ", aperture='" + aperture + '\'' +
                ", distance='" + distance + '\'' +
                ", website='" + website + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", rating='" + rating + '\'' +
                ", workmates=" + workmates +
                '}';
    }
}
