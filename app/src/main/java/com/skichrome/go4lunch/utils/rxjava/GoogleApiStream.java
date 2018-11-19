package com.skichrome.go4lunch.utils.rxjava;

import com.skichrome.go4lunch.models.googleplacedetails.MainPlaceDetails;
import com.skichrome.go4lunch.models.googleplacesearch.MainGooglePlaceAPI;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GoogleApiStream
{
    private static final String PLACE_TYPE = "restaurants";
    private static final String API_FILTERS = "name,rating,formatted_address,formatted_phone_number,opening_hours,website,place_id";

    private static Observable<MainGooglePlaceAPI> streamGetNearbyPlaces (String key, String location, int radius)
    {
        GoogleApiService googleApiService = GoogleApiService.retrofit.create(GoogleApiService.class);
        return googleApiService.getNearbyPlaces(key, location, radius, PLACE_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(20, TimeUnit.SECONDS);
    }

    private static Observable<MainPlaceDetails> streamGetPlaceDetails (String key, String placeId)
    {
        GoogleApiService googleApiService = GoogleApiService.retrofit.create(GoogleApiService.class);
        return googleApiService.getPlaceDetails(key, placeId, API_FILTERS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(20, TimeUnit.SECONDS);
    }

    public static Observable<MainPlaceDetails> streamFetchPlaces(String key, String location, int radius)
    {
        return streamGetNearbyPlaces(key, location, radius)
                .concatMapIterable(results -> results.getResults())
                .concatMap(place -> streamGetPlaceDetails(key, place.getPlaceId()));
    }
}