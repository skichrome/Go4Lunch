package com.skichrome.go4lunch.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
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
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.activities.MainActivity;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.googleplace.MainGooglePlaceSearch;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;

public class MapMethods implements GoogleApiClient.OnConnectionFailedListener
{
    //=========================================
    // Fields
    //=========================================
    private MainActivity mainActivity;
    private GoogleApiClient googleApiClient;
    private Disposable disposable;
    private WeakReference<ActivitiesCallbacks.RxJavaListeners> callback;
    private PendingResult<PlaceLikelihoodBuffer> result;

    //=========================================
    // Constructor
    //=========================================

    public MapMethods(MainActivity mMainActivity)
    {
        this.mainActivity = mMainActivity;
    }

    public MapMethods(ActivitiesCallbacks.RxJavaListeners mCallback)
    {
        this.callback = new WeakReference<>(mCallback);
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
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult mConnectionResult)
    {
        Log.e("GoogleAPIClient ERROR", "onConnectionFailed ERROR CODE : " + mConnectionResult.getErrorCode());
    }

    public void launchPlaceAutocompleteActivity()
    {
        try
        {
            // Define a research filter for possible places
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                    .build();

            // Create an intent and start an activity with request code
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(filter)
                    .build(mainActivity);
            mainActivity.startActivityForResult(intent, RequestCodes.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        }
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException mE)
        {
            mE.printStackTrace();
        }
    }

    // ------------------------
    // List of places
    // ------------------------

    @SuppressLint("MissingPermission")
    public void getNearbyPlaces(final int mFragID)
    {
        final HashMap<String, FormattedPlace> placeHashMap = new HashMap<>();
        result = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);

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
                                tempPlace.getPhoneNumber() == null ? null : tempPlace.getPhoneNumber().toString(),
                                tempPlace.getWebsiteUri() == null ? null : tempPlace.getWebsiteUri().toString(),
                                tempPlace.getLatLng().latitude,
                                tempPlace.getLatLng().longitude,
                                "-",
                                null);

                        placeHashMap.put(tempPlace.getName().toString(), place);
                    }
                }
                mainActivity.updatePlacesHashMap(mFragID, placeHashMap);
                likelyPlaces.release();
            }
        });
    }

    public void getPlaceDetails(String mApiKey, final FormattedPlace mPlace)
    {
        disposable = GoogleApiStream.getNearbyPlacesOnGoogleWebApi(mApiKey, mPlace.getId()).subscribeWith(new DisposableObserver<MainGooglePlaceSearch>()
        {
            @Override
            public void onNext(MainGooglePlaceSearch mMainGooglePlaceSearch)
            {
                if (mMainGooglePlaceSearch.getStatus() != null)
                    Log.e("RX_JAVA", "STATUS " + mMainGooglePlaceSearch.getStatus());

                if (mMainGooglePlaceSearch.getResult() != null && mMainGooglePlaceSearch.getResult().getPhotos() != null)
                    mPlace.setPhotoReference(mMainGooglePlaceSearch.getResult().getPhotos().get(0).getPhotoReference());
            }

            @Override
            public void onError(Throwable e)
            {
                Log.e("RX_JAVA", "onError ", e);
            }

            @Override
            public void onComplete()
            {
                callback.get().onComplete(mPlace);
                Log.e("RX_JAVA", "onComplete");
            }
        });
    }

    // ------------------------
    // Permission Methods
    // ------------------------

    public void askUserToGrandPermission()
    {
        // Check with EasyPermissions if tha app have access to the location
        if (!EasyPermissions.hasPermissions(mainActivity, RequestCodes.LOCATION_PERMISSION_REQUEST))
        {
            EasyPermissions.requestPermissions(mainActivity, mainActivity.getString(R.string.map_fragment_easy_permission_location_user_request), RequestCodes.RC_LOCATION_CODE, RequestCodes.LOCATION_PERMISSION_REQUEST);
            return;
        }
        Log.i("EasyPerm in activity", "askUserToEnableLocationPermission: Location Access granted");
    }
}