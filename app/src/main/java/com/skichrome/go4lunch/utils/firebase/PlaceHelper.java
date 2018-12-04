package com.skichrome.go4lunch.utils.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.skichrome.go4lunch.models.FormattedPlace;

public class PlaceHelper
{
    private static final String COLLECTION_NAME = "restaurants";

    // Collection reference
    private static CollectionReference getPlaceCollection()
    {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Get
    public static Task<QuerySnapshot> getAllPlaces()
    {
        return PlaceHelper.getPlaceCollection()
                .get();
    }

    // update
    public static Task<Void> updateRestaurant(FormattedPlace place)
    {
        return PlaceHelper.getPlaceCollection()
                .document(place.getId())
                .set(place, SetOptions.merge());
    }

    public static Task<Void> updateRestaurantDetails(String placeId, String website, String phoneNumber, String address, String aperture, String isOpenNow)
    {
        return PlaceHelper.getPlaceCollection()
                .document(placeId)
                .update(
                        "website", website,
                        "phoneNumber", phoneNumber,
                        "address", address,
                        "aperture", aperture,
                        "isOpenNow", isOpenNow
                );
    }

    public static Task<Void> updateRatingOfRestaurant(String placeId)
    {
        return PlaceHelper.getPlaceCollection()
                .document(placeId)
                .update("rated_by_user", true);
    }

    // Delete
    public static Task<Void> deleteRestaurants(String restaurantId)
    {
        return PlaceHelper.getPlaceCollection()
                .document(restaurantId)
                .delete();
    }
}