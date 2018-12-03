package com.skichrome.go4lunch.models.firestore;

import android.support.annotation.Nullable;

import com.skichrome.go4lunch.models.FormattedPlace;

public class User
{
    //=========================================
    // Fields
    //=========================================

    private String uid;
    private String username;
    @Nullable private String urlPicture;
    @Nullable private FormattedPlace selectedPlace;
    @Nullable private String selectedPlaceId;
    private int dateForSelectedPlace;

    //=========================================
    // Constructors
    //=========================================

    public User() { }

    public User(String uid, String username, @Nullable String urlPicture, @Nullable FormattedPlace selectedPlace, int dateForSelectedPlace)
    {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.selectedPlace = selectedPlace;
        this.selectedPlaceId = selectedPlace != null ? selectedPlace.getId() : null;
        this.dateForSelectedPlace = dateForSelectedPlace;
    }

    //=========================================
    // Getters
    //=========================================

    public String getUid() { return uid; }
    public String getUsername() { return username; }
    @Nullable public String getUrlPicture() { return urlPicture; }
    @Nullable public FormattedPlace getSelectedPlace() { return selectedPlace; }
    @Nullable public String getSelectedPlaceId() { return selectedPlaceId; }
    public int getDateForSelectedPlace() { return dateForSelectedPlace; }

    //=========================================
    // Setters
    //=========================================

    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture; }
    public void setSelectedPlace(@Nullable FormattedPlace selectedPlace) { this.selectedPlace = selectedPlace; }
    public void setSelectedPlaceId(@Nullable String selectedPlaceId) { this.selectedPlaceId = selectedPlaceId; }
    public void setDateForSelectedPlace(int dateForSelectedPlace) { this.dateForSelectedPlace = dateForSelectedPlace; }
}