package com.skichrome.go4lunch.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class FormattedPlace
{
    //=========================================
    // Fields
    //=========================================

    private final String id;
    private final String name;
    private final String adress;
    private final String aperture;
    private final LatLng location;
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
                          String mAdress,
                          String mAperture,
                          LatLng mLocation,
                          String mDistance,
                          String mWebsite,
                          String mPhoneNumber,
                          String mImageUrl,
                          String mRating,
                          List<String> mWorkmates)
    {
        this.id = mId;
        this.name = mName;
        this.adress = mAdress;
        this.aperture = mAperture;
        this.location = mLocation;
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

    public String getAdress()
    {
        return adress;
    }

    public String getAperture()
    {
        return aperture;
    }

    public LatLng getLocation()
    {
        return location;
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
                ", adress='" + adress + '\'' +
                ", aperture='" + aperture + '\'' +
                ", location=" + location +
                ", distance='" + distance + '\'' +
                ", website='" + website + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", rating='" + rating + '\'' +
                ", workmates=" + workmates +
                '}';
    }
}
