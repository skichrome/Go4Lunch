package com.skichrome.go4lunch.utils;

import com.skichrome.go4lunch.models.googleplace.MainGooglePlaceSearch;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiService
{
    String baseUrl = "https://maps.googleapis.com/maps/api/place/";
    String maxWidth = "&maxwidth=800";

    @GET("details/json")
    Observable<MainGooglePlaceSearch> getPlaceDetails(@Query("key") String mKey,
                                                      @Query("placeid") String mId);

    Retrofit retrofitGetPlaceDetails = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}