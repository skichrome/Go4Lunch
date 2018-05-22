package com.skichrome.go4lunch.utils.firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlaceHelper
{
    private static final String COLLECTION_NAME = "places_types";

    // Collection reference
    public static CollectionReference getPlaceCollection()
    {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }
}