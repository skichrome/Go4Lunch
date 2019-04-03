package com.skichrome.go4lunch.utils;

import com.skichrome.go4lunch.models.googleplacedetails.MainPlaceDetails;
import com.skichrome.go4lunch.models.googleplacesearch.MainGooglePlaceAPI;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service for RxJava calls on Google APIs.
 */
public interface GoogleApiService
{
    /**
     * Base url for Google API calls.
     */
    String baseUrl = "https://maps.googleapis.com/maps/api/place/";

    /**
     * Get a list of places available near a location.
     * @param key
     *      Google API key.
     * @param location
     *      Location in String format, compatible with Google API format.
     * @param radius
     *      The radius for the request, to limit the results.
     * @param placeType
     *      The type of establishment to fetch.
     * @return
     *      An observable.
     */
    @GET("nearbysearch/json")
    Observable<MainGooglePlaceAPI> getNearbyPlaces( @Query("key") String key,
                                                    @Query("location") String location,
                                                    @Query("radius") int radius,
                                                    @Query("type") String placeType);
    /**
     * Get more details about on place, with the id in parameter.
     * @param key
     *      Google API key.
     * @param id
     *      The id of the place.
     * @return
     *      An observable.
     */
    @GET("details/json")
    Observable<MainPlaceDetails> getPlaceDetails(@Query("key") String key,
                                                 @Query("placeid") String id,
                                                 @Query("fields") String filters);

    /**
     * Retrofit initialisation.
     */
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}