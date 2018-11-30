package com.skichrome.go4lunch.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.activities.MainActivity;
import com.skichrome.go4lunch.controllers.activities.RestaurantDetailsActivity;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.firebase.PlaceTypeHelper;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import java.util.Arrays;

public abstract class FireStoreAuthentication
{
    //=========================================
    // Callback
    //=========================================

    public interface GetUserListener { void onSuccess(Intent intent);}

    //=========================================
    // Fields
    //=========================================

    private static final int SIGN_OUT_TASK = 100;
    private static final int DELETE_USER_TASK = 200;
    public static final int RC_SIGN_IN = 1234;

    public static final String ID_PLACE_RATED_CLOUD_FIRESTORE = "places_rated";
    public static final String ID_PLACE_INTEREST_CLOUD_FIRESTORE = "places_interest";

    private static final int REQUEST_CHECK_SETTINGS = 12000;

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
                                new AuthUI.IdpConfig.FacebookBuilder().build(),             // FACEBOOK
                                new AuthUI.IdpConfig.TwitterBuilder().build()))             // Twitter
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.lunch_logo_makers)
                .build();
    }

    // manage activities results cases
    @Nullable
    public static String onActivityResult(Activity activity, FirebaseUser user, int requestCode, int resultCode, Intent data)
    {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        switch (requestCode)
        {
            case RC_SIGN_IN :
                if (resultCode == Activity.RESULT_OK)                                                  // SUCCESS
                {
                    createUserInFirebase(activity, user);
                    return activity.getString(R.string.firebase_auth_success);
                }
                else                                                                                    // ERRORS
                {
                    if (response == null)
                        return activity.getString(R.string.firebase_login_cancel);
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK)
                        return activity.getString(R.string.firebase_no_network_detected);
                    if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR)
                        return activity.getString(R.string.firebase_error_login);
                }

            case MainActivity.PLACE_AUTOCOMPLETE_REQUEST_CODE :
                if (resultCode == PlaceAutocomplete.RESULT_ERROR)
                    return activity.getString(R.string.error_unknown_error);
                if (resultCode == Activity.RESULT_CANCELED)
                    return activity.getString(R.string.error_cancel_request);

            case REQUEST_CHECK_SETTINGS :
                if (resultCode == Activity.RESULT_OK)
                    return "Google location updates granted";
                if (resultCode == Activity.RESULT_CANCELED)
                    return "User has denied uses of location updates";

            default:
                return null;
        }
    }

    private static void createUserInFirebase(Activity activity, FirebaseUser user)
    {
        if (user != null)
        {
            String uuid = user.getUid();
            String username = user.getDisplayName();
            String urlPicture = user.getPhotoUrl() == null ? null : user.getPhotoUrl().toString();

            UserHelper.createUser(uuid, username, urlPicture, null).addOnFailureListener(onFailureListener(activity));
        }
    }

    //=========================================
    // Log out and delete methods
    //=========================================

    public static void logoutFromFirestore(Activity activity)
    {
        AuthUI.getInstance()
                .signOut(activity)
                .addOnSuccessListener(activity, updateUIAfterRESTRequestsCompleted(activity, SIGN_OUT_TASK));
    }

    public static void deleteAccountFromFirebase(Activity activity, FirebaseUser user)
    {
        if (user != null)
        {
            UserHelper.deleteUser(user.getUid()).addOnFailureListener(onFailureListener(activity));

            AuthUI.getInstance()
                    .delete(activity)
                    .addOnSuccessListener(activity, updateUIAfterRESTRequestsCompleted(activity, DELETE_USER_TASK));
        }
    }

    private static OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final Activity activity, final int origin)
    {
        return aVoid ->
        {
            switch (origin)
            {
                case SIGN_OUT_TASK:
                    activity.recreate();
                    break;

                case DELETE_USER_TASK:
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                    break;

                default:
                    break;
            }
        };
    }

    private static OnFailureListener onFailureListener(final Activity activity)
    {
        return mE -> Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.fui_error_unknown), Toast.LENGTH_SHORT).show();
    }

    //=========================================
    // Update Methods
    //=========================================

    private static void updateChosenRestaurant(Activity activity, final FirebaseUser user, FormattedPlace place, FormattedPlace placeInterest)
    {
        UserHelper.updateChosenPlace(user.getUid(), place).addOnFailureListener(onFailureListener(activity));
    }

    public static void deleteUserFromPlace(final Activity mActivity, final FirebaseUser mUser, final FormattedPlace mPlace)
    {
        UserHelper.getUser(mUser.getUid()).addOnSuccessListener(mDocumentSnapshot ->
        {
            User user = mDocumentSnapshot.toObject(User.class);
            String placeId = user != null ? user.getSelectedPlace() != null ? user.getSelectedPlace().getId() : null : null;

            if (placeId != null)
            {
                PlaceTypeHelper.removeUserIntoPlace(ID_PLACE_INTEREST_CLOUD_FIRESTORE, mUser.getUid(), placeId).addOnSuccessListener(mVoid ->
                {
                    updateRestaurant(mActivity, mUser, mPlace); // Calling this method here because we have to wait success of deleting previous place before update new place
                });
            }
            else
                updateRestaurant(mActivity, mUser, mPlace);
        });
    }

    private static void updateRestaurant(Activity activity, FirebaseUser user, FormattedPlace place)
    {
        FireStoreAuthentication.updateChosenRestaurant(activity, user, place, place);
    }

    public static void getUserPlace(final GetUserListener callback, final Activity activity, User user)
    {
        UserHelper.getUser(user.getUid()).addOnSuccessListener(mDocumentSnapshot ->
        {
            User databaseUser = mDocumentSnapshot.toObject(User.class);
            FormattedPlace place = databaseUser != null ? databaseUser.getSelectedPlace() : null;
            if (place != null)
            {
               Intent intent = new Intent(activity, RestaurantDetailsActivity.class);
                intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, place);

                callback.onSuccess(intent);
            }
            else
                Toast.makeText(activity, R.string.toast_workmate_note_decided, Toast.LENGTH_SHORT).show();
        });
    }
}