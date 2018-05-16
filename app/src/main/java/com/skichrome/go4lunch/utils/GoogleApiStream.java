package com.skichrome.go4lunch.utils;

import com.skichrome.go4lunch.models.googleplace.MainGooglePlaceSearch;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GoogleApiStream
{
    public static Observable<MainGooglePlaceSearch> getNearbyPlacesOnGoogleWebApi (String mKey, String mId)
    {
        GoogleApiService googleApiService = GoogleApiService.retrofitGetPlaceDetails.create(GoogleApiService.class);
        return googleApiService.getPlaceDetails(mKey, mId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(20, TimeUnit.SECONDS);
    }
}