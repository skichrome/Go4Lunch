package com.skichrome.go4lunch.models.firestore;

import android.support.annotation.Nullable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Place
{
    //=========================================
    // Fields
    //=========================================

    private String id;
    private String name;
    private String type;
    @Nullable
    private List<User> usersRatedThis;
    @Nullable
    private List<User> userInterested;

    private Date datePlaceCreated;

    //=========================================
    // Constructors
    //=========================================

    public Place(String mId, String mName, String mType, @Nullable List<User> mUsersRatedThis, @Nullable List<User> mUserInterested)
    {
        id = mId;
        name = mName;
        type = mType;
        usersRatedThis = mUsersRatedThis;
        userInterested = mUserInterested;
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

    public String getType()
    {
        return type;
    }

    @Nullable
    public List<User> getUsersRatedThis()
    {
        return usersRatedThis;
    }

    @Nullable
    public List<User> getUserInterested()
    {
        return userInterested;
    }

    @ServerTimestamp
    public Date getDatePlaceCreated()
    {
        return datePlaceCreated;
    }

//=========================================
    // Setters
    //=========================================

    public void setId(String mId)
    {
        id = mId;
    }

    public void setName(String mName)
    {
        name = mName;
    }

    public void setType(String mType)
    {
        type = mType;
    }

    public void setUsersRatedThis(@Nullable List<User> mUsersRatedThis)
    {
        usersRatedThis = mUsersRatedThis;
    }

    public void setUserInterested(@Nullable List<User> mUserInterested)
    {
        userInterested = mUserInterested;
    }

    public void setDatePlaceCreated(Date mDatePlaceCreated)
    {
        datePlaceCreated = mDatePlaceCreated;
    }
}