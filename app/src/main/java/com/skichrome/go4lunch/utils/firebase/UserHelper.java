package com.skichrome.go4lunch.utils.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.firestore.User;

import java.util.HashMap;

public class UserHelper
{
    private static final String COLLECTION_NAME = "users";

    // Collection reference
    private static CollectionReference getUsersCollection()
    {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create
    public static Task<Void> createUser(String mUid, String mUsername, String mUrlPicture, FormattedPlace mSelectedPlace)
    {
        User user = new User(mUid, mUsername, mUrlPicture, mSelectedPlace);
        return UserHelper.getUsersCollection().document(mUid).set(user);
    }

    // Get
    public static Task<DocumentSnapshot> getUser(String mUid)
    {
        return UserHelper.getUsersCollection().document(mUid).get();
    }

    // Get all users
    public static Query getAllUsers()
    {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
                .limit(50);
    }

    // Update username
    public static Task<Void> updateUsername(String mUid, String mUsername)
    {
        return UserHelper.getUsersCollection().document(mUid).update("username", mUsername);
    }

    // Update chosen place
    public static Task<Void> updateChosenPlace(String mUid, FormattedPlace mPlace)
    {
        HashMap<String, FormattedPlace> map = new HashMap<>();
        map.put("selectedPlace", mPlace);

        return UserHelper.getUsersCollection().document(mUid).set(map, SetOptions.mergeFields("selectedPlace"));
    }

    // delete
    public static Task<Void> deleteUser(String mUid)
    {
        return UserHelper.getUsersCollection().document(mUid).delete();
    }
}