package com.skichrome.go4lunch.utils;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.util.List;

public abstract class MapMethods
{
    //=========================================
    // Callback interfaces
    //=========================================

    public interface PlaceAutocompleteListener
    {
        void onPlaceAutocompleteReady(Intent mIntent);
    }

    //=========================================
    // Constructor
    //=========================================

    private MapMethods() {}

    //=========================================
    // Methods
    //=========================================

    private static void launchPlaceAutocompleteActivity(PlaceAutocompleteListener mCallback, Activity mActivity, Location mLocation)
    {
        try
        {
            AutocompleteFilter filter = new AutocompleteFilter.Builder()                             // Define a research filter for possible places
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)      // Create an intent and start an activity with request code
                    .setFilter(filter)
                    .setBoundsBias(convertToBounds(mLocation, 30))
                    .build(mActivity);
            mCallback.onPlaceAutocompleteReady(intent);
        }
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException mE) { mE.printStackTrace(); }
    }

    // ------------------------
    // Location updates
    // ------------------------

    private static LatLngBounds convertToBounds(Location mLocation, double mRadiusInMeters)
    {
        LatLng center = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        double distanceFromCenterToCorner = mRadiusInMeters * Math.sqrt(2.0);
        LatLng southWest = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northEast = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southWest, northEast);
    }

    public static String convertAperture(List<String> mOpeningHours, int mDayCalendar)
    {
        int correctedIndex;
        switch (mDayCalendar)
        {
            case 1: correctedIndex = 6;
                break;

            default: correctedIndex = mDayCalendar - 2;
                break;
        }
        String currentOpeningHours = mOpeningHours.get(correctedIndex);

        if (!mOpeningHours.get(correctedIndex).contains("0")) return "Closed Today";
        return currentOpeningHours.replaceAll("[a-zA-Z]+","").replace(": ", "");
    }
}