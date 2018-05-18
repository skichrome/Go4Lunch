package com.skichrome.go4lunch.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.skichrome.go4lunch.controllers.activities.MainActivity;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.googleplace.MainGooglePlaceSearch;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MapMethods
{
    //=========================================
    // Fields
    //=========================================

    private MainActivity mainActivity;
    private Disposable disposable;
    private WeakReference<ActivitiesCallbacks.RxJavaListener> callback;

    //=========================================
    // Constructor
    //=========================================

    public MapMethods()
    {
    }

    public MapMethods(MainActivity mMainActivity)
    {
        this.mainActivity = mMainActivity;
    }

    public MapMethods(ActivitiesCallbacks.RxJavaListener mCallback)
    {
        this.callback = new WeakReference<>(mCallback);
    }

    //=========================================
    // Methods
    //=========================================

    public void disconnectFromDisposable()
    {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
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
    public void getNearbyPlaces()
    {
        final HashMap<String, FormattedPlace> placeHashMap = new HashMap<>();
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mainActivity.googleApiClient, null);

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
                mainActivity.updatePlacesHashMap(placeHashMap);
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
                    Log.i("RX_JAVA", "STATUS " + mMainGooglePlaceSearch.getStatus());

                if (mMainGooglePlaceSearch.getResult() != null && mMainGooglePlaceSearch.getResult().getPhotos() != null)
                {
                    mPlace.setPhotoReference(mMainGooglePlaceSearch.getResult().getPhotos().get(0).getPhotoReference());

                    if (mMainGooglePlaceSearch.getResult().getOpeningHours() != null && mMainGooglePlaceSearch.getResult().getOpeningHours().getOpenNow())
                        mPlace.setAperture(convertAperture(mMainGooglePlaceSearch.getResult().getOpeningHours().getWeekdayText(), Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
                    else
                        mPlace.setAperture("Closed now");
                }
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
            }
        });
    }

    public String convertAperture(List<String> mOpeningHours, int mDayCalendar)
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