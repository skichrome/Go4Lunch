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

public abstract class MapMethods
{
    //=========================================================
    // Constructor
    //=========================================================

    private MapMethods() {}

    //=========================================================
    // Methods
    //=========================================================

    public static LatLngBounds convertToBounds(Location location, double radiusInMeters)
    {
        LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southWest = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northEast = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southWest, northEast);
    }

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