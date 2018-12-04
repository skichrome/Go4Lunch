package com.skichrome.go4lunch.utils.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.firestore.User;

import java.util.Calendar;

public class UserHelper
{
    private static final String COLLECTION_NAME = "users";

    // Collection reference
    private static CollectionReference getUsersCollection()
    {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create
    public static Task<Void> createUser(String uid, String username, String urlPicture, FormattedPlace selectedPlace)
    {
        int date;
        if (selectedPlace != null) date = Calendar.DAY_OF_MONTH;
        else date = -1;

        User user = new User(uid, username, urlPicture, selectedPlace, date);
        return UserHelper.getUsersCollection().document(uid).set(user);
    }

    // Get
    public static Task<DocumentSnapshot> getUser(String uid)
    {
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // Get users that have subscribed to a place
    public static Query getUsersInterestedByPlace(String placeId)
    {
        return UserHelper.getUsersCollection().whereEqualTo("selectedPlaceId", placeId);
    }

    public static Task<QuerySnapshot> getUsersForMapFragment(String placeId)
    {
        return getUsersInterestedByPlace(placeId).get();
    }

    // Get all users
    public static Query getAllUsers()
    {
        return UserHelper.getUsersCollection().limit(50);
    }

    // Update username
    public static Task<Void> updateUsername(String uid, String username)
    {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    // Update chosen place
    public static Task<Void> updateChosenPlace(String uid, FormattedPlace place)
    {
        if (place != null)
        return UserHelper.getUsersCollection().document(uid)
                .update(
                        "selectedPlace.address", place.getAddress(),
                        "selectedPlace.aperture", place.getAperture(),
                        "selectedPlace.distance", place.getDistance(),
                        "selectedPlace.id", place.getId(),
                        "selectedPlace.isOpenNow", place.getIsOpenNow(),
                        "selectedPlace.locationLongitude", place.getLocationLongitude(),
                        "selectedPlace.locationLatitude", place.getLocationLatitude(),
                        "selectedPlace.name", place.getName(),
                        "selectedPlace.phoneNumber", place.getPhoneNumber(),
                        "selectedPlace.photoReference", place.getPhotoReference(),
                        "selectedPlace.rating", place.getRating(),
                        "selectedPlace.website", place.getWebsite(),

                        "selectedPlaceId", place.getId(),
                        "dateForSelectedPlace", Calendar.DAY_OF_MONTH
                );
        else return UserHelper.getUsersCollection().document(uid).update(
                "selectedPlace", null,
                "selectedPlaceId", null,
                "dateForSelectedPlace", -1);
    }

    // delete
    public static Task<Void> deleteUser(String uid)
    {
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}