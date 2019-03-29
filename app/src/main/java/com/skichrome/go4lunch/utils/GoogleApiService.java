package com.skichrome.go4lunch.utils;

import com.skichrome.go4lunch.models.googleplacedetails.MainPlaceDetails;
import com.skichrome.go4lunch.models.googleplacesearch.MainGooglePlaceAPI;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiService
{
    String baseUrl = "https://maps.googleapis.com/maps/api/place/";

    @GET("nearbysearch/json")
    Observable<MainGooglePlaceAPI> getNearbyPlaces( @Query("key") String key,
                                                    @Query("location") String location,
                                                    @Query("radius") int radius,
                                                    @Query("type") String placeType);

    @GET("details/json")
    Observable<MainPlaceDetails> getPlaceDetails(@Query("key") String key,
                                                 @Query("placeid") String id,
                                                 @Query("fields") String filters);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}