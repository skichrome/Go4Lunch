package com.skichrome.go4lunch.models;

import android.graphics.Bitmap;

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

    private String photoReference;
    private String website;
    private String phoneNumber;
    private String distance;
    private String aperture;
    private String rating;
    private List<String> workmates;
    private Bitmap photo;

    //=========================================
    // Constructor
    //=========================================

    public FormattedPlace(String mId,
                          String mName,
                          String mAddress,
                          String mPhoneNumber,
                          String mWebsite,
                          double mLocationLatitude,
                          double mLocationLongitude,
                          String mRating,
                          List<String> mWorkmates)
    {
        this.id = mId;
        this.name = mName;
        this.address = mAddress;
        this.phoneNumber = mPhoneNumber;
        this.website = mWebsite;
        this.locationLatitude = mLocationLatitude;
        this.locationLongitude = mLocationLongitude;
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

    public String getPhotoReference()
    {
        return photoReference;
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

    public Bitmap getPhoto()
    {
        return photo;
    }

    //=========================================
    // Setters
    //=========================================

    public void setPhotoReference(String mPhotoReference)
    {
        photoReference = mPhotoReference;
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

    public void setPhoto(Bitmap mPhoto)
    {
        photo = mPhoto;
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
                ", imageUrl='" + photoReference + '\'' +
                ", rating='" + rating + '\'' +
                ", workmates=" + getNumberOfWorkmates() +
                '}';
    }
}