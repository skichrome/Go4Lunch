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
    public static Task<Void> createUser(String uid, String username, String urlPicture, FormattedPlace selectedPlace)
    {
        User user = new User(uid, username, urlPicture, selectedPlace);
        return UserHelper.getUsersCollection().document(uid).set(user);
    }

    // Get
    public static Task<DocumentSnapshot> getUser(String uid)
    {
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // Get all users
    public static Query getAllUsers()
    {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
                .limit(50);
    }

    // Update username
    public static Task<Void> updateUsername(String uid, String username)
    {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    // Update chosen place
    public static Task<Void> updateChosenPlace(String uid, FormattedPlace place)
    {
        HashMap<String, FormattedPlace> map = new HashMap<>();
        map.put("selectedPlace", place);

        return UserHelper.getUsersCollection().document(uid).set(map, SetOptions.mergeFields("selectedPlace"));
    }

    // delete
    public static Task<Void> deleteUser(String uid)
    {
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}