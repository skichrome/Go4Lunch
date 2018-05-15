package com.skichrome.go4lunch.utils;

import android.Manifest;

import com.skichrome.go4lunch.controllers.activities.MainActivity;

/**
 * This class is used to store in one class all special codes used in the app
 */
public class RequestCodes
{

    //=========================================
    // Fragments
    //=========================================

    public static final int ID_MAP_FRAGMENT = 10;
    public static final int ID_LIST_FRAGMENT = 20;
    public static final int ID_WORKMATES_FRAGMENT = 30;

    //=========================================
    // Start Activity
    //=========================================

    public static final int RC_SIGN_IN = 1234;

    //=========================================
    // Firebase
    //=========================================

    public static final int SIGN_OUT_TASK = 10;
    public static final int DELETE_USER_TASK = 20;

    //=========================================
    // Main Activity
    //=========================================

    /**
     * Code used for EasyPermissions to authorise location in the app
     */
    public static final String LOCATION_PERMISSION_REQUEST = Manifest.permission.ACCESS_FINE_LOCATION;
    /**
     * Code used for EasyPermissions to authorise location in the app
     */
    public static final int RC_LOCATION_CODE = 4123;

    /**
     * Used as an identifier in intent for Place Autocomplete widget in {@link MainActivity#launchPlaceAutocompleteActivity()}
     */
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 4124;

    /**
     * Used to store data in intent to launch {@link com.skichrome.go4lunch.controllers.activities.RestaurantDetailsActivity} and store a map contains all details about a restaurant
     */
    public static final String ACTIVITY_DETAILS_CODE = "ACTIVITY_DETAILS_INTENT_CODE";

    /**
     * Used to check if location permission has been accorded to the app
     */
    private static boolean locationPermissionState = false;

    /**
     * Used to get the status of locationPermissionState
     *
     * @return boolean, the status of location permission
     */
    public static boolean isLocationPermissionState()
    {
        return locationPermissionState;
    }

    /**
     * Set the location permission status to true, after user has granted permission to the app
     */
    public static void setLocationPermissionState()
    {
        locationPermissionState = true;
    }
}