package com.skichrome.go4lunch.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.activities.MainActivity;
import com.skichrome.go4lunch.controllers.activities.RestaurantDetailsActivity;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.firebase.PlaceRatedHelper;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import java.util.Arrays;

public abstract class FireStoreAuthentication
{
    //=========================================
    // Callback
    //=========================================

    public interface GetUserListener
    {
        void onSuccess(Intent mIntent);
    }

    //=========================================
    // Fields
    //=========================================

    private static final int SIGN_OUT_TASK = 100;
    private static final int DELETE_USER_TASK = 200;
    public static final int RC_SIGN_IN = 1234;

    public static final String ID_PLACE_RATED_CLOUD_FIRESTORE = "places_rated";
    public static final String ID_PLACE_INTEREST_CLOUD_FIRESTORE = "places_interest";

    //=========================================
    // Constructor
    //=========================================

    private FireStoreAuthentication() { }

    //=========================================
    // Methods
    //=========================================

    // Launch sign-in activity
    public static Intent startSignInActivity()
    {
        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(
                        Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),          // EMAIL
                                new AuthUI.IdpConfig.GoogleBuilder().build(),               // GOOGLE
                                new AuthUI.IdpConfig.FacebookBuilder().build()))            // FACEBOOK
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.lunch_logo_makers)
                .build();
    }

    // manage activities results cases
    @Nullable
    public static String onActivityResult(Activity mActivity, FirebaseUser mUser, int mRequestCode, int mResultCode, Intent mData)
    {
        IdpResponse response = IdpResponse.fromResultIntent(mData);

        if (mRequestCode == RC_SIGN_IN)
        {
            if (mResultCode == Activity.RESULT_OK)                                                  // SUCCESS
            {
                createUserInFirebase(mActivity, mUser);
                return mActivity.getString(R.string.firebase_auth_success);
            }
            else                                                                                    // ERRORS
            {
                if (response == null)
                    return mActivity.getString(R.string.firebase_login_cancel);
                else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK)
                    return mActivity.getString(R.string.firebase_no_network_detected);
                else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR)
                {
                    Log.e("StartActivity", "onActivityResult error : ", response.getError());
                    return mActivity.getString(R.string.firebase_error_login);
                }
            }
        }

        if (mRequestCode == MainActivity.PLACE_AUTOCOMPLETE_REQUEST_CODE)
        {
            if (mResultCode == Activity.RESULT_OK)
            {
                Place place = PlaceAutocomplete.getPlace(mActivity, mData);
                Log.d("Place Autocomplete", "onActivityResult: User typed this : " + place.getName());
            }
            if (mResultCode == PlaceAutocomplete.RESULT_ERROR)
                Log.e("Place Autocomplete", "onActivityResult: Place autocomplete error ! \n\n" + PlaceAutocomplete.getStatus(mActivity, mData).getStatusMessage());
            if (mResultCode == Activity.RESULT_CANCELED)
                Log.i("Place Autocomplete", "onActivityResult: User canceled !");
        }

        return null;
    }

    private static void createUserInFirebase(Activity mActivity, FirebaseUser mUser)
    {
        if (mUser != null)
        {
            String uuid = mUser.getUid();
            String username = mUser.getDisplayName();
            String urlPicture = mUser.getPhotoUrl() == null ? null : mUser.getPhotoUrl().toString();

            UserHelper.createUser(uuid, username, urlPicture, null).addOnFailureListener(onFailureListener(mActivity));
        }
    }

    //=========================================
    // Log out and delete methods
    //=========================================

    public static void logoutFromFirestore(Activity mActivity)
    {
        AuthUI.getInstance()
                .signOut(mActivity)
                .addOnSuccessListener(mActivity, updateUIAfterRESTRequestsCompleted(mActivity, SIGN_OUT_TASK));
    }

    public static void deleteAccountFromFirebase(Activity mActivity, FirebaseUser mUser)
    {
        if (mUser != null)
        {
            UserHelper.deleteUser(mUser.getUid()).addOnFailureListener(onFailureListener(mActivity));

            AuthUI.getInstance()
                    .delete(mActivity)
                    .addOnSuccessListener(mActivity, updateUIAfterRESTRequestsCompleted(mActivity, DELETE_USER_TASK));
        }
    }

    private static OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final Activity mActivity, final int origin)
    {
        return new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                switch (origin)
                {
                    case SIGN_OUT_TASK:
                        mActivity.recreate();
                        break;

                    case DELETE_USER_TASK:
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        mActivity.startActivity(intent);
                        mActivity.finish();
                        break;

                    default:
                        break;
                }
            }
        };
    }

    private static OnFailureListener onFailureListener(final Activity mActivity)
    {
        return new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception mE)
            {
                Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.fui_error_unknown), Toast.LENGTH_SHORT).show();
            }
        };
    }

    //=========================================
    // Update Methods
    //=========================================

    private static void updateChosenRestaurant(Activity mActivity, final FirebaseUser mUser, FormattedPlace mPlace, FormattedPlace mPlaceInterest)
    {
        UserHelper.updateChosenPlace(mUser.getUid(), mPlace).addOnFailureListener(onFailureListener(mActivity));
        PlaceRatedHelper.createPlace(ID_PLACE_INTEREST_CLOUD_FIRESTORE, mPlace).addOnFailureListener(onFailureListener(mActivity));
        PlaceRatedHelper.createUserIntoPlace(ID_PLACE_INTEREST_CLOUD_FIRESTORE, mUser, mPlace, mPlaceInterest).addOnFailureListener(onFailureListener(mActivity));
    }

    public static void updateRateRestaurant(Activity mActivity, FirebaseUser mUser, FormattedPlace mPlace, FormattedPlace mPlaceInterest)
    {
        PlaceRatedHelper.createPlace(ID_PLACE_RATED_CLOUD_FIRESTORE, mPlace).addOnFailureListener(onFailureListener(mActivity));
        PlaceRatedHelper.createUserIntoPlace(ID_PLACE_RATED_CLOUD_FIRESTORE, mUser, mPlace, mPlaceInterest).addOnFailureListener(onFailureListener(mActivity));
    }

    public static void deleteUserFromPlace(final Activity mActivity, final FirebaseUser mUser, final FormattedPlace mPlace)
    {
        UserHelper.getUser(mUser.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot mDocumentSnapshot)
            {
                User user = mDocumentSnapshot.toObject(User.class);
                String placeId = user != null ? user.getSelectedPlace() != null ? user.getSelectedPlace().getId() : null : null;

                if (placeId != null)
                {
                    PlaceRatedHelper.removeUserIntoPlace(ID_PLACE_INTEREST_CLOUD_FIRESTORE, mUser.getUid(), placeId).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void mVoid)
                        {
                            updateRestaurant(mActivity, mUser, mPlace); // Calling this method here because we have to wait success of deleting previous place before update new place
                        }
                    });
                }
                else
                    updateRestaurant(mActivity, mUser, mPlace);
            }
        });
    }

    private static void updateRestaurant(Activity mActivity, FirebaseUser mUser, FormattedPlace mPlace)
    {
        FireStoreAuthentication.updateChosenRestaurant(mActivity, mUser, mPlace, mPlace);
    }

    public static void getUserPlace(final GetUserListener mCallback, final Activity mActivity, User mUser)
    {
        UserHelper.getUser(mUser.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot mDocumentSnapshot)
            {
                User user = mDocumentSnapshot.toObject(User.class);
                FormattedPlace place = user != null ? user.getSelectedPlace() : null;
                if (place != null)
                {
                   Intent intent = new Intent(mActivity, RestaurantDetailsActivity.class);
                    intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, place);

                    mCallback.onSuccess(intent);
                }
                else
                    Toast.makeText(mActivity, "This workmate doesn't have selected a place to eat now", Toast.LENGTH_SHORT).show();
            }
        });
    }
}