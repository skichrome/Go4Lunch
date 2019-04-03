package com.skichrome.go4lunch.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import java.util.Arrays;

/**
 * <h1>Utils class, mainly for Firebase</h1>
 */
public abstract class FireStoreAuthentication
{
    //=========================================
    // Callback
    //=========================================

    /**
     *
     */
    public interface GetUserListener { void onSuccess(Intent intent);}

    //=========================================
    // Fields
    //=========================================

    /**
     * code to identify log out task.
     */
    private static final int SIGN_OUT_TASK = 100;
    /**
     * code to identify delete user task.
     */
    private static final int DELETE_USER_TASK = 200;
    /**
     * Sign in code
     */
    public static final int RC_SIGN_IN = 1234;

    /**
     * <h1>Log tag</h1>
     */
    private static final String LOG_TAG = "FireStoreAuth";
    /**
     * check settings code.
     */
    private static final int REQUEST_CHECK_SETTINGS = 12000;

    //=========================================
    // Constructor
    //=========================================

    /**
     * <h1>Prevent class instantiation</h1>
     */
    private FireStoreAuthentication() { }

    //=========================================
    // Methods
    //=========================================

    /**
     * <h1>Launch log in activity</h1>
     * <p>
     *     Ask user to authenticate, by launching a Firebase Auth log in activity.<br/>
     *     Email, Google, Facebook and Twitter log in is supported and activated on Firebase Authentication.
     * </p>
     * @return
     *      Log in Intent.
     */
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

    /**
     * <h1>onActivityResult results managements</h1>
     * <p>
     *
     * </p>
     * @param activity
     *      where the method is called, for context.
     * @param user
     *      The user logged in
     * @param data
     *      Intent that contain the place fetched in PlaceAutocomplete API, and link to {@link RestaurantDetailsActivity}
     * @return
     *      String that contain a status message.
     */
    @Nullable
    public static String onActivityResult(Activity activity, FirebaseUser user, int requestCode, int resultCode, Intent data)
    {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        switch (requestCode)
        {
            case RC_SIGN_IN :
                if (resultCode == Activity.RESULT_OK)                                                  // SUCCESS
                {
                    createUserInCloud(activity, user);
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
                    return activity.getString(R.string.location_permission_granted);
                if (resultCode == Activity.RESULT_CANCELED)
                    return activity.getString(R.string.location_permission_denied);

            default:
                return null;
        }
    }

    /**
     * <h1>Create a new user in Firebase</h1>
     * <p>
     *     Create a new user in Firebase, with the user in parameter.
     * </p>
     * @param activity
     *      where the method is called, for context.
     * @param user
     *      The user to create.
     */
    private static void createUserInCloud(Activity activity, FirebaseUser user)
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

    /**
     * <h1>Logout Method</h1>
     * <p>
     *     Log out from Firebase Auth.
     * </p>
     * @param activity
     *      where the method is called, for context.
     */
    public static void logout(Activity activity)
    {
        AuthUI.getInstance()
                .signOut(activity)
                .addOnSuccessListener(activity, updateUIAfterRESTRequestsCompleted(activity, SIGN_OUT_TASK));
    }

    /**
     * <h1>Account deletion</h1>
     * <p>
     *     Delete the user from Firebase (FirebaseAuth).
     * </p>
     * @param activity
     *      where the method is called, for context.
     * @param user
     *      The user to delete.
     */
    public static void deleteAccount(Activity activity, FirebaseUser user)
    {
        if (user != null)
        {
            UserHelper.deleteUser(user.getUid()).addOnFailureListener(onFailureListener(activity));

            AuthUI.getInstance()
                    .delete(activity)
                    .addOnSuccessListener(activity, updateUIAfterRESTRequestsCompleted(activity, DELETE_USER_TASK));
        }
    }

    /**
     * <h1>Actions after Firebase success tasks</h1>
     * <p>
     *     For sign out : restart the activity.
     *     For delete account : close the activity.
     * </p>
     * @param activity
     *      where the method is called, for context.
     * @param origin
     *      To know what to do according to the origin.
     * @return
     *      Success Listener
     */
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

    /**
     * <h1>Failure listener</h1>
     * @param activity
     *      For context.
     * @return
     *      Failure Listener.
     */
    private static OnFailureListener onFailureListener(final Activity activity)
    {
        return mE -> Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.fui_error_unknown), Toast.LENGTH_SHORT).show();
    }

    //=========================================
    // Update Methods
    //=========================================

    /**
     * <h1>Update the place selected by the user</h1>
     * <p>
     *     Connect to Cloud Firestore database and update the place selected by the user.
     * </p>
     *
     * @param activity
     *      where the method is called, for context.
     * @param user
     *      User that we need to update.
     * @param placeInterest
     *      The place that will be saved in Firebase.
     */
    public static void updateChosenRestaurant(Activity activity, final FirebaseUser user, FormattedPlace placeInterest)
    {
        UserHelper.updateChosenPlace(user.getUid(), placeInterest)
                .addOnSuccessListener( success -> Log.i(LOG_TAG, "Successfully updated chosen restaurant !"))
                .addOnFailureListener(onFailureListener(activity));
    }

    /**
     * <h1>Return the place selected by user</h1>
     * <p>
     *     Connect to cloud Firestore database and get the restaurant selected by the user.
     * </p>
     * @param callback
     *      Listener for results
     * @param activity
     *      where the method is called, for context.
     * @param user
     *      The user that we need to get his selected place.
     */
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