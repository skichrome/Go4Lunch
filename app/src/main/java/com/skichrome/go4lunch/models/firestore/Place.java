package com.skichrome.go4lunch.models.firestore;

import android.support.annotation.Nullable;

public class Place
{
    private String id;
    private String name;
    private String address;

    @Nullable
    private String numberOfRate;

    private String userId;

    public Place()
    {
    }

    public Place(String mId, String mName, String mAddress)
    {
        id = mId;
        name = mName;
        address = mAddress;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String mId)
    {
        id = mId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String mName)
    {
        name = mName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String mAddress)
    {
        address = mAddress;
    }
}