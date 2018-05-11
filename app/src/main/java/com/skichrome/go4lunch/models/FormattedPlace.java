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
    private final double locationLatitude;
    private final double locationLongitude;
    private final String website;
    private final String phoneNumber;

    private String distance;
    private String aperture;
    private String imageUrl;
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

    public List<String> getWorkmates()
    {
        return workmates;
    }

    public int getNumberOfWorkmates()
    {
        return workmates == null ? 0 : workmates.size();
    }

    //=========================================
    // Setters
    //=========================================

    public void setImageUrl(String mImageUrl)
    {
        imageUrl = mImageUrl;
    }

    public void setDistance(String mDistance)
    {
        distance = mDistance;
    }

    public void setAperture(String mAperture)
    {
        aperture = mAperture;
    }

    public void setRating(String mRating)
    {
        rating = mRating;
    }

    public void setWorkmates(List<String> mWorkmates)
    {
        workmates = mWorkmates;
    }

    public void addWorkMate(String mWorkmate)
    {
        if (workmates != null) workmates.add(mWorkmate);
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
                ", latitude='" + locationLatitude + '\'' +
                ", longitude='" + locationLongitude + '\'' +
                ", aperture='" + aperture + '\'' +
                ", distance='" + distance + '\'' +
                ", website='" + website + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", rating='" + rating + '\'' +
                ", workmates=" + workmates.toString() +
                '}';
    }
}
