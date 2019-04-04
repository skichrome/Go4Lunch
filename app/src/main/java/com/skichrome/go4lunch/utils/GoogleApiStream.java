package com.skichrome.go4lunch.utils;

import android.location.Location;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.googleplacedetails.MainPlaceDetails;
import com.skichrome.go4lunch.models.googleplacesearch.MainGooglePlaceAPI;
import com.skichrome.go4lunch.models.googleplacesearch.Result;
import com.skichrome.go4lunch.utils.firebase.PlaceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Stream class for RxJava calls on Google APIs.
 */
public class GoogleApiStream
{
    /**
     * Establishment filter, here we need only restaurants.
     */
    private static final String PLACE_TYPE = "restaurant";
    /**
     * Filters for the call on second API call, we don't need all results of the second API.
     */
    private static final String API_FILTERS = "name,rating,formatted_address,formatted_phone_number,opening_hours,website,place_id";

    /**
     * Call on first Google API, get nearby restaurants.
     * @param key
     *      Google API key.
     * @param location
     *      The location of the user.
     * @param radius
     *      The radius, to limit the API results
     * @return
     *      An observable.
     */
    private static Observable<MainGooglePlaceAPI> streamGetNearbyPlaces (String key, String location, int radius)
    {
        GoogleApiService googleApiService = GoogleApiService.retrofit.create(GoogleApiService.class);
        return googleApiService.getNearbyPlaces(key, location, radius, PLACE_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(20, TimeUnit.SECONDS);
    }
    /**
     * Call on second Google API, get more details of a restaurant.
     * @param key
     *      Google API key.
     * @param placeId
     *      The id of the restaurant that we need more informations.
     * @return
     *      An observable.
     */
    private static Observable<MainPlaceDetails> streamGetPlaceDetails (String key, String placeId)
    {
        GoogleApiService googleApiService = GoogleApiService.retrofit.create(GoogleApiService.class);
        return googleApiService.getPlaceDetails(key, placeId, API_FILTERS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(20, TimeUnit.SECONDS);
    }

    /**
     * Combine the {@link #streamGetNearbyPlaces(String, String, int)} and {@link #streamGetPlaceDetails(String, String)}
     * methods in one, to have only one implementation in other classes.
     * @param key
     *      Google API key.
     * @param location
     *      The location of the user.
     * @param radius
     *      The radius, to limit the API results
     * @return
     *      An observable.
     */
    public static Observable<MainPlaceDetails> streamFetchPlaces(final String key, final Location location, final int radius)
    {
        String locationStr = location.getLatitude() + "," + location.getLongitude();

        return streamGetNearbyPlaces(key, locationStr, radius)
                .concatMapIterable((Function<MainGooglePlaceAPI, Iterable<Result>>) results ->
                {
                    updateFirebaseList(results.getResults());
                    return results.getResults();
                })
                .concatMap(result ->
                {
                    updateFirecloudPlace(convertResults(result, location));
                    return streamGetPlaceDetails(key, result.getPlaceId());
                });
    }

    /**
     * Convert the results of the Google API to an exploitable format.
     * @param result
     *      Google API results.
     * @param location
     *      The location of the user, used to get the approximate distance between the user and the restaurant.
     * @return
     *      New FormattedPlace.
     */
    private static FormattedPlace convertResults(Result result, Location location)
    {
        final Location placeLocation = new Location("placeLocation");
        placeLocation.setLatitude(result.getGeometry().getLocation().getLat());
        placeLocation.setLongitude(result.getGeometry().getLocation().getLng());

        int distance = (int) location.distanceTo(placeLocation);

        return new  FormattedPlace(
                result.getPlaceId(),
                result.getName(),
                result.getGeometry().getLocation().getLat(),
                result.getGeometry().getLocation().getLng(),
                (result.getRating() != null ) ? result.getRating() : 0,
                (result.getPhotos() != null && result.getPhotos().size() != 0) ? result.getPhotos().get(0).getPhotoReference() : null,
                distance
        );
    }

    /**
     * Create or replace a restaurant in Cloud Firestore database.
     * @param place
     *      The place to upload.
     */
    private static void updateFirecloudPlace(FormattedPlace place)
    {
        PlaceHelper.updateRestaurant(place).addOnSuccessListener(aVoid -> Log.d("RxJava : ", "Saved place " + place.getName() + " to Firebase ! "))
        .addOnFailureListener(throwable -> Log.e("RxJava", "Error when uploading place to Firebase; ", throwable));
    }

    /**
     * When all nearby places are downloaded, compare the Cloud Firestore restaurant list to the downloaded restuarant lists,
     * update restaurants already available in the list (only fields that are different),
     * and delete all places that are unavailable into the downloaded restaurants.
     * @param apiResults
     *      The restaurants downloaded.
     */
    private static void updateFirebaseList(List<Result> apiResults)
    {
        List<String> fromApiFormattedPlaces = new ArrayList<>();
        for (Result result : apiResults)
            fromApiFormattedPlaces.add(result.getPlaceId());

        PlaceHelper.getAllPlaces().addOnSuccessListener(firecloudSuccess ->
        {
            for (DocumentSnapshot snap : firecloudSuccess)
            {
                FormattedPlace firecloudPlace = snap.toObject(FormattedPlace.class);

                if (!fromApiFormattedPlaces.contains(firecloudPlace.getId()))
                {
                    Log.e("updateFirebaseList : ", "Firebase doesn't contains results !");
                    PlaceHelper.deleteRestaurants(firecloudPlace.getId()).addOnSuccessListener(successDeleted ->
                            Log.d("updateFirebaseList : ", "Successfully deleted " + firecloudPlace.getName()));
                }
            }
        });
    }
}
