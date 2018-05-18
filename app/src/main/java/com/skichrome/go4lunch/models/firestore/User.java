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

    //=========================================
    // Constructors
    //=========================================

    public User()
    {
    }

    public User(String uid, String username, @Nullable String urlPicture)
    {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
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
}