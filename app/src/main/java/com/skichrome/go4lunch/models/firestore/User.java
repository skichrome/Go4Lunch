package com.skichrome.go4lunch.models.firestore;

import android.support.annotation.Nullable;

public class User
{
    //=========================================
    // Fields
    //=========================================

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    @Nullable
    private Place selectedPlace;

    //=========================================
    // Constructors
    //=========================================

    public User()
    {
    }

    public User(String mUid, String mUsername, @Nullable String mUrlPicture, @Nullable Place mSelectedPlace)
    {
        uid = mUid;
        username = mUsername;
        urlPicture = mUrlPicture;
        selectedPlace = mSelectedPlace;
    }

    //=========================================
    // Getters
    //=========================================

    public String getUid()
    {
        return uid;
    }

    public String getUsername()
    {
        return username;
    }

    @Nullable
    public String getUrlPicture()
    {
        return urlPicture;
    }

    @Nullable
    public Place getSelectedPlace()
    {
        return selectedPlace;
    }

    //=========================================
    // Setters
    //=========================================

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public void setUrlPicture(@Nullable String urlPicture)
    {
        this.urlPicture = urlPicture;
    }

    public void setSelectedPlace(@Nullable Place mSelectedPlace)
    {
        selectedPlace = mSelectedPlace;
    }
}