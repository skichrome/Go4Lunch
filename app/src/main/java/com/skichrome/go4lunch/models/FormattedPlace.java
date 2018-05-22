package com.skichrome.go4lunch.models;

import android.graphics.Bitmap;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class FormattedPlace implements Serializable
{
    //=========================================
    // Fields
    //=========================================

    private String id;
    private String name;
    private String address;
    private double locationLatitude;
    private double locationLongitude;

    private String photoReference;
    private String website;
    private String phoneNumber;
    private String distance;
    private String aperture;

    @Exclude
    private transient Bitmap photo;

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

    @Exclude
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

    @Exclude
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
                '}';
    }
}