package com.skichrome.go4lunch.utils.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.firestore.User;

public class PlaceTypeHelper
{
    private static final String COLLECTION_NAME = "places";
    private static final String SUB_COLLECTION_NAME = "users_linked";

    public static Query getWorkMatesInPlace(String sortedPlaceKey, String placeId)
    {
        return PlaceHelper.getPlaceCollection()
                .document(sortedPlaceKey)
                .collection(COLLECTION_NAME)
                .document(placeId)
                .collection(SUB_COLLECTION_NAME);
    }

    public static Task<Void> createPlace(String sortedPlaceKey, FormattedPlace place)
    {
        return PlaceHelper.getPlaceCollection()
                .document(sortedPlaceKey)
                .collection(COLLECTION_NAME)
                .document(place.getId())
                .set(place);
    }

    public static Task<Void> createUserIntoPlace(String sortedPlaceKey, FirebaseUser user, FormattedPlace place, FormattedPlace placeInterest)
    {
        User userModel = new User(user.getUid(), user.getDisplayName(), user.getPhotoUrl() == null ? null : user.getPhotoUrl().toString(), placeInterest);

        return PlaceHelper.getPlaceCollection()
                .document(sortedPlaceKey)
                .collection(COLLECTION_NAME)
                .document(place.getId())
                .collection(SUB_COLLECTION_NAME)
                .document(userModel.getUid())
                .set(userModel);
    }

    public static Task<Void> removeUserIntoPlace(String sortedPlaceKey, String userId, String placeId)
    {
        return PlaceHelper.getPlaceCollection()
                .document(sortedPlaceKey)
                .collection(COLLECTION_NAME)
                .document(placeId)
                .collection(SUB_COLLECTION_NAME)
                .document(userId)
                .delete();
    }

    public static Task<QuerySnapshot> getNumberOfWorkmates(String sortedPlaceKey, String placeId)
    {
        return PlaceHelper.getPlaceCollection()
                .document(sortedPlaceKey)
                .collection(COLLECTION_NAME)
                .document(placeId)
                .collection(SUB_COLLECTION_NAME)
                .get();
    }
}