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
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.activities.MainActivity;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import java.util.Arrays;

public abstract class FireStoreAuthentication
{
    //=========================================
    // Fields
    //=========================================

    private static final int SIGN_OUT_TASK = 100;
    private static final int DELETE_USER_TASK = 200;
    public static final int RC_SIGN_IN = 1234;

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

            UserHelper.createUser(uuid, username, urlPicture).addOnFailureListener(onFailureListener(mActivity));
        }
    }

    //=========================================
    // Log out and delete account methods
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
}