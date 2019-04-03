package com.skichrome.go4lunch.utils;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.skichrome.go4lunch.models.googleplacedetails.Result;
import com.skichrome.go4lunch.utils.firebase.PlaceHelper;

import java.util.Calendar;
import java.util.List;

/**
 * Util class, for static methods.
 */
public abstract class MapMethods
{
    //=========================================================
    // Constructor
    //=========================================================

    /**
     * Prevent class instantiation.
     */
    private MapMethods() {}

    //=========================================================
    // Methods
    //=========================================================

    /**
     * <h1>Place Autocomplete conversion</h1>
     * <p>
     *     Place Autocomplete need a LatLngBounds object to fetch places around.
     *     This method convert a location and a radius into a LatLngBounds.
     * </p>
     * @param location
     *      The location of the user.
     * @param radiusInMeters
     *      The radius from the center (represented by the location).
     * @return
     *      LatLngBound object, ready to be used by PlaceAutocomplete API.
     */
    public static LatLngBounds convertToBounds(Location location, double radiusInMeters)
    {
        LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southWest = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northEast = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southWest, northEast);
    }

    /**
     * <h1>Convert the opening hours of a place to a usable format</h1>
     * @param openingHours
     *      A list of opening hours of each day of week.
     * @param dayCalendar
     *      The current day index at runtime, need to be corrected.
     * @return
     *      A string of current day aperture.
     */
    public static String convertAperture(List<String> openingHours, int dayCalendar)
    {
        int correctedIndex;
        switch (dayCalendar)
        {
            case 1: correctedIndex = 6;
                break;

            default: correctedIndex = dayCalendar - 2;
                break;
        }
        String currentOpeningHours = openingHours.get(correctedIndex);

        if (!openingHours.get(correctedIndex).contains("0")) return "Closed Today";
        return currentOpeningHours.replaceAll("[a-zA-Z]+","").replace(": ", "");
    }

    /**
     * Add or update a place into Cloud Firestore database.
     * @param result
     *      The place fetched on Google Place API.
     * @param statusCode
     *      Status code, used if an error append.
     */
    public static void updateDetailsOnFirecloud(Result result, String statusCode)
    {
        PlaceHelper.updateRestaurantDetails(
                result.getPlaceId(),
                result.getWebsite(),
                result.getFormattedPhoneNumber(),
                result.getFormattedAddress(),
                result.getOpeningHours() != null ? MapMethods.convertAperture(result.getOpeningHours().getWeekdayText(), Calendar.DAY_OF_WEEK) : null,
                result.getOpeningHours() != null ? result.getOpeningHours().getOpenNow().toString() : "Don't know")
                .addOnSuccessListener(aVoid -> Log.d("RxJava", "Successfully updated place details : " + result.getName()))
                .addOnFailureListener(throwable -> Log.e("RxJava", "Error when update place to Firebase : status code : " + statusCode, throwable));
    }
}