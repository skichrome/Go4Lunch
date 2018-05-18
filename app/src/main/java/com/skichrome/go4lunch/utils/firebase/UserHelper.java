package com.skichrome.go4lunch.utils.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skichrome.go4lunch.models.firestore.Place;
import com.skichrome.go4lunch.models.firestore.User;

public class UserHelper
{
    private static final String COLLECTION_NAME = "users";

    // Collection reference
    public static CollectionReference getUsersCollection()
    {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create
    public static Task<Void> createUser(String mUid, String mUsername, String mUrlPicture)
    {
        User user = new User(mUid, mUsername, mUrlPicture);
        return UserHelper.getUsersCollection().document(mUid).set(user);
    }

    // Get
    public static Task<DocumentSnapshot> getUser(String mUid)
    {
        return UserHelper.getUsersCollection().document(mUid).get();
    }

    // Update username
    public static Task<Void> updateUsername(String mUid, String mUsername)
    {
        return UserHelper.getUsersCollection().document(mUid).update("username", mUsername);
    }

    // Update chosen place
    public static Task<Void> updateChosenPlace(String mUid, Place mPlace)
    {
        return UserHelper.getUsersCollection().document(mUid).update("selectedPlace", mPlace);
    }

    // delete
    public static Task<Void> deleteUser(String mUid)
    {
        return UserHelper.getUsersCollection().document(mUid).delete();
    }
}
