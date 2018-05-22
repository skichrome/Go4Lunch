package com.skichrome.go4lunch.utils.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.firestore.User;

public class PlaceRatedHelper
{
    private static final String COLLECTION_NAME = "places";
    private static final String SUB_COLLECTION_NAME = "users_linked";

    public static Query getWorkMatesInPlace(String mSortedPlaceKey, String mPlaceId)
    {
        return PlaceHelper.getPlaceCollection()
                .document(mSortedPlaceKey)
                .collection(COLLECTION_NAME)
                .document(mPlaceId)
                .collection(SUB_COLLECTION_NAME);
    }

    public static Task<Void> createPlace(String mSortedPlaceKey, FormattedPlace mPlace)
    {
        return PlaceHelper.getPlaceCollection()
                .document(mSortedPlaceKey)
                .collection(COLLECTION_NAME)
                .document(mPlace.getId())
                .set(mPlace);
    }

    public static Task<Void> createUserIntoPlace(String mSortedPlaceKey, FirebaseUser mUser, FormattedPlace mPlace, FormattedPlace mPlaceInterest)
    {
        User user = new User(mUser.getUid(), mUser.getDisplayName(), mUser.getPhotoUrl() == null ? null : mUser.getPhotoUrl().toString(), mPlaceInterest);

        return PlaceHelper.getPlaceCollection()
                .document(mSortedPlaceKey)
                .collection(COLLECTION_NAME)
                .document(mPlace.getId())
                .collection(SUB_COLLECTION_NAME)
                .document(user.getUid())
                .set(user);
    }

    public static Task<Void> removeUserIntoPlace(String mSortedPlaceKey, String mUserId, String mPlaceId)
    {
        return PlaceHelper.getPlaceCollection()
                .document(mSortedPlaceKey)
                .collection(COLLECTION_NAME)
                .document(mPlaceId)
                .collection(SUB_COLLECTION_NAME)
                .document(mUserId)
                .delete();
    }

    public static Task<QuerySnapshot> getNumberOfWorkmates(String mSortedPlaceKey, String mPlaceId)
    {
        return PlaceHelper.getPlaceCollection()
                .document(mSortedPlaceKey)
                .collection(COLLECTION_NAME)
                .document(mPlaceId)
                .collection(SUB_COLLECTION_NAME)
                .get();
    }
}