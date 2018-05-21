package com.skichrome.go4lunch.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.googleplace.MainGooglePlaceSearch;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public abstract class MapMethods
{
    //=========================================
    // Callback interfaces
    //=========================================

    public interface ListenersNearbyPlaces
    {
        void OnResult(HashMap<String, FormattedPlace> mPlaceHashMap);
    }

    //=========================================
    // Constructor
    //=========================================

    private MapMethods() {}

    //=========================================
    // Methods
    //=========================================

    @Nullable
    public static Intent launchPlaceAutocompleteActivity(Activity mActivity)
    {
        try
        {
            // Define a research filter for possible places
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                    .build();

            // Create an intent and start an activity with request code
            return new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(filter)
                    .build(mActivity);
        }
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException mE)
        {
            mE.printStackTrace();
            return null;
        }
    }

    // ------------------------
    // List of places
    // ------------------------

    @SuppressLint("MissingPermission")
    public static void getNearbyPlaces(final ListenersNearbyPlaces mCallback, GoogleApiClient mGoogleApiClient)
    {
        final HashMap<String, FormattedPlace> placeHashMap = new HashMap<>();
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);

        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>()
        {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces)
            {
                for (PlaceLikelihood placeLikelihood : likelyPlaces)
                {
                    if (placeLikelihood.getPlace().getPlaceTypes().contains(Place.TYPE_RESTAURANT) /*placeLikelihood.getPlace().getPlaceTypes().size() != 0*/)
                    {
                        Place tempPlace = placeLikelihood.getPlace();

                        FormattedPlace place = new FormattedPlace(
                                tempPlace.getId(),
                                tempPlace.getName().toString(),
                                tempPlace.getAddress() != null ? tempPlace.getAddress().toString() : "",
                                tempPlace.getPhoneNumber() == null ? null : tempPlace.getPhoneNumber().toString(),
                                tempPlace.getWebsiteUri() == null ? null : tempPlace.getWebsiteUri().toString(),
                                tempPlace.getLatLng().latitude,
                                tempPlace.getLatLng().longitude,
                                "-",
                                null);
                        placeHashMap.put(tempPlace.getName().toString(), place);
                    }
                }
                mCallback.OnResult(placeHashMap);
                likelyPlaces.release();
            }
        });
    }

    public static void updatePlaceDetails(MainGooglePlaceSearch mMainGooglePlaceSearch, FormattedPlace mPlace)
    {
        if (mMainGooglePlaceSearch.getStatus() != null)
            Log.i("RX_JAVA", "Response code " + mMainGooglePlaceSearch.getStatus());

        if (mMainGooglePlaceSearch.getResult() != null && mMainGooglePlaceSearch.getResult().getPhotos() != null)
        {
            mPlace.setPhotoReference(mMainGooglePlaceSearch.getResult().getPhotos().get(0).getPhotoReference());

            if (mMainGooglePlaceSearch.getResult().getOpeningHours() != null && mMainGooglePlaceSearch.getResult().getOpeningHours().getOpenNow())
                mPlace.setAperture(convertAperture(mMainGooglePlaceSearch.getResult().getOpeningHours().getWeekdayText(), Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
            else
                mPlace.setAperture("Closed now");
        }
    }

    private static String convertAperture(List<String> mOpeningHours, int mDayCalendar)
    {
        String currentOpeningHours;
        int correctedIndex;

        switch (mDayCalendar)
        {
            case 1:
                correctedIndex = 6;
                break;

            default:
                correctedIndex = mDayCalendar - 2;
                break;
        }

        currentOpeningHours = mOpeningHours.get(correctedIndex);

        if (!mOpeningHours.get(correctedIndex).contains("0"))
            return "Closed Today";

        return currentOpeningHours.replaceAll("[a-zA-Z]+","").replace(": ", "");
    }
}