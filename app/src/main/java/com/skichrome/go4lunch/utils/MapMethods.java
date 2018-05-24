package com.skichrome.go4lunch.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.googleplace.MainGooglePlaceSearch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class MapMethods
{
    //=========================================
    // Callback interfaces
    //=========================================

    public interface ListenersNearbyPlaces
    {
        void onResult(ArrayList<FormattedPlace> mPlaceHashMap);
    }

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

    @SuppressLint("MissingPermission")
    public static void getLastKnownLocationForPlaceAutocomplete(PlaceAutocompleteListener mCallback, Activity mActivity)
    {
        FusedLocationProviderClient mClient = new FusedLocationProviderClient(mActivity);
        mClient.getLastLocation().addOnSuccessListener(mLocation ->
        {
            if (mLocation != null) launchPlaceAutocompleteActivity(mCallback, mActivity, mLocation);
            else Toast.makeText(mActivity, R.string.toast_frag_no_location, Toast.LENGTH_SHORT).show();
        });
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

    // ------------------------
    // List of places
    // ------------------------

    @SuppressLint("MissingPermission")
    public static void getNearbyPlaces(final ListenersNearbyPlaces mCallback, GoogleApiClient mGoogleApiClient)
    {
        final ArrayList<FormattedPlace> places = new ArrayList<>();
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(likelyPlaces ->
        {
            for (PlaceLikelihood placeLikelihood : likelyPlaces)
            {
                if (placeLikelihood.getPlace().getPlaceTypes().contains(Place.TYPE_RESTAURANT))
                {
                    Place tempPlace = placeLikelihood.getPlace();
                    FormattedPlace place = new FormattedPlace(tempPlace.getId(),
                            tempPlace.getName().toString(),
                            tempPlace.getAddress() != null ? tempPlace.getAddress().toString() : "",
                            tempPlace.getPhoneNumber() == null ? null : tempPlace.getPhoneNumber().toString(),
                            tempPlace.getWebsiteUri() == null ? null : tempPlace.getWebsiteUri().toString(),
                            tempPlace.getLatLng().latitude,
                            tempPlace.getLatLng().longitude);
                    places.add(place);
                }
            }
            likelyPlaces.release();
            mCallback.onResult(places);
        });
    }

    public static void updatePlaceDetails(MainGooglePlaceSearch mMainGooglePlaceSearch, FormattedPlace mPlace)
    {
        if (mMainGooglePlaceSearch.getStatus() != null) Log.i("RX_JAVA", "Response code " + mMainGooglePlaceSearch.getStatus());

        if (mMainGooglePlaceSearch.getResult() != null && mMainGooglePlaceSearch.getResult().getPhotos() != null)
        {
            mPlace.setPhotoReference(mMainGooglePlaceSearch.getResult().getPhotos().get(0).getPhotoReference());

            if (mMainGooglePlaceSearch.getResult().getOpeningHours() != null && mMainGooglePlaceSearch.getResult().getOpeningHours().getOpenNow())
                mPlace.setAperture(convertAperture(mMainGooglePlaceSearch.getResult().getOpeningHours().getWeekdayText(), Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
            else mPlace.setAperture("Closed now");
        }
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