package com.skichrome.go4lunch.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.activities.MainActivity;
import com.skichrome.go4lunch.models.FormattedPlace;

import java.util.HashMap;

import pub.devrel.easypermissions.EasyPermissions;

public class MapMethods implements GoogleApiClient.OnConnectionFailedListener
{
    //=========================================
    // Fields
    //=========================================

    private MainActivity mainActivity;
    private GoogleApiClient googleApiClient;

    //=========================================
    // Constructor
    //=========================================

    public MapMethods(MainActivity mMainActivity)
    {
        mainActivity = mMainActivity;
    }

    //=========================================
    // Getters
    //=========================================

    public GoogleApiClient getGoogleApiClient()
    {
        return googleApiClient;
    }

    //=========================================
    // Methods
    //=========================================

    // ------------------------
    // Google API configuration
    // ------------------------

    public void configureGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient
                .Builder(mainActivity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(mainActivity, this)
                .build();
    }

    public void disconnectFromGoogleApiClient()
    {
        if (googleApiClient != null && googleApiClient.isConnected())
        {
            googleApiClient.stopAutoManage(mainActivity);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult mConnectionResult)
    {
        Log.e("GoogleAPIClient ERROR", "onConnectionFailed ERROR CODE : " + mConnectionResult.getErrorCode());
    }

    // ------------------------
    // List of places
    // ------------------------

    public HashMap<String, FormattedPlace> getNearbyPlaces(final int mFragID)
    {
        final HashMap<String, FormattedPlace> placeHashMap = new HashMap<>();

        try
        {
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);

            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>()
            {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces)
                {

                    for (PlaceLikelihood placeLikelihood : likelyPlaces)
                    {
                        if (placeLikelihood.getPlace().getPlaceTypes().size(/*contains(Place.TYPE_RESTAURANT)*/) != 0)
                        {
                            Place tempPlace = placeLikelihood.getPlace();

                            FormattedPlace place = new FormattedPlace(
                                    tempPlace.getId(),
                                    tempPlace.getName().toString(),
                                    tempPlace.getAddress() != null ? tempPlace.getAddress().toString() : "",
                                    "-",
                                    tempPlace.getLatLng().latitude,
                                    tempPlace.getLatLng().longitude,
                                    "-",
                                    tempPlace.getWebsiteUri() != null ? tempPlace.getWebsiteUri().toString() : "",
                                    tempPlace.getPhoneNumber() != null ? tempPlace.getPhoneNumber().toString() : "",
                                    null,
                                    null,
                                    null);

                            placeHashMap.put(tempPlace.getName().toString(), place);
                        }
                    }
                    likelyPlaces.release();
                    mainActivity.updatePlacesHashMap(mFragID, placeHashMap);
                }
            });
        }
        catch (SecurityException e)
        {
            Log.e("ERROR", "updatePlacesHashMap: ", e);
        }

        return placeHashMap;
    }

    // ------------------------
    // Permission Methods
    // ------------------------

    public void askUserToGrandPermission()
    {
        // Check with EasyPermissions if tha app have access to the location
        if (!EasyPermissions.hasPermissions(mainActivity, RequestCodes.LOCATION_PERMISSION_REQUEST))
            EasyPermissions.requestPermissions(mainActivity, mainActivity.getString(R.string.map_fragment_easy_permission_location_user_request), RequestCodes.RC_LOCATION_CODE, RequestCodes.LOCATION_PERMISSION_REQUEST);
        else
        {
            RequestCodes.setLocationPermissionState();
            Log.i("EasyPerm in activity", "askUserToEnableLocationPermission: Location Access granted");
        }
    }
}
