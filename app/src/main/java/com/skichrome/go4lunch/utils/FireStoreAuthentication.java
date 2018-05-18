package com.skichrome.go4lunch.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnSuccessListener;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.activities.MainActivity;
import com.skichrome.go4lunch.controllers.activities.SettingsActivity;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import java.util.Arrays;

import static com.skichrome.go4lunch.utils.RequestCodes.DELETE_USER_TASK;
import static com.skichrome.go4lunch.utils.RequestCodes.SIGN_OUT_TASK;

public class FireStoreAuthentication
{
    //=========================================
    // Fields
    //=========================================

    private MainActivity mainActivity;

    //=========================================
    // Constructor
    //=========================================

    public FireStoreAuthentication(MainActivity mMainActivity)
    {
        this.mainActivity = mMainActivity;
    }

    //=========================================
    // Methods
    //=========================================

    // Launch sign-in activity
    public void startSignInActivity()
    {
        mainActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),          // EMAIL
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),               // GOOGLE
                                        new AuthUI.IdpConfig.FacebookBuilder().build()))            // FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.lunch_logo_makers)
                        .build(),
                RequestCodes.RC_SIGN_IN);
    }

    // manage activities results cases
    public void onActivityResult(int mRequestCode, int mResultCode, Intent mData)
    {
        IdpResponse response = IdpResponse.fromResultIntent(mData);

        if (mRequestCode == RequestCodes.RC_SIGN_IN)
        {
            if (mResultCode == Activity.RESULT_OK)
            {
                this.createUserInFirebase();
                mainActivity.showSnackBarMessage(mainActivity.getString(R.string.firebase_auth_success));       // SUCCESS
            }
            else                                                                                                // ERRORS
            {
                if (response == null)
                {
                    mainActivity.showSnackBarMessage(mainActivity.getString(R.string.firebase_login_cancel));
                    mainActivity.finish();
                }
                else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK)
                {
                    mainActivity.showSnackBarMessage(mainActivity.getString(R.string.firebase_no_network_detected));
                }
                else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR)
                {
                    Log.e("StartActivity", "onActivityResult error : ", response.getError());
                    mainActivity.showSnackBarMessage(mainActivity.getString(R.string.firebase_error_login));
                }
            }
        }

        if (mRequestCode == RequestCodes.PLACE_AUTOCOMPLETE_REQUEST_CODE)
        {
            if (mResultCode == Activity.RESULT_OK)
            {
                Place place = PlaceAutocomplete.getPlace(mainActivity, mData);
                Log.d("Place Autocomplete", "onActivityResult: User typed this : " + place.getName());
            }
            if (mResultCode == PlaceAutocomplete.RESULT_ERROR)
                Log.e("Place Autocomplete", "onActivityResult: Place autocomplete error ! \n\n" + PlaceAutocomplete.getStatus(mainActivity, mData).getStatusMessage());
            if (mResultCode == Activity.RESULT_CANCELED)
                Log.i("Place Autocomplete", "onActivityResult: User canceled !");
        }
    }

    private void createUserInFirebase()
    {
        if (mainActivity.getCurrentUser() != null)
        {
            String uuid = mainActivity.getCurrentUser().getUid();
            String username = mainActivity.getCurrentUser().getDisplayName();
            String urlPicture = mainActivity.getCurrentUser().getPhotoUrl() == null ? null : mainActivity.getCurrentUser().getPhotoUrl().toString();

            UserHelper.createUser(uuid, username, urlPicture).addOnFailureListener(mainActivity.onFailureListener());
        }
    }

    public void logoutFromFirestore()
    {
        AuthUI.getInstance()
                .signOut(mainActivity)
                .addOnSuccessListener(mainActivity, this.updateUIAfterRESTRequestsCompleted(RequestCodes.SIGN_OUT_TASK));
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin)
    {
        return new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                switch (origin)
                {
                    case SIGN_OUT_TASK:
                        mainActivity.recreate();
                        break;
                    case DELETE_USER_TASK:
                        settingsActivity.disableProgressBar();
                        Intent intent = new Intent(settingsActivity, MainActivity.class);
                        settingsActivity.startActivity(intent);
                        settingsActivity.finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    //=========================================
    // Setting Activity methods
    //=========================================

    private SettingsActivity settingsActivity;

    public FireStoreAuthentication(SettingsActivity mSettingsActivity)
    {
        this.settingsActivity = mSettingsActivity;
    }

    public void deleteAccountFromFirebase()
    {
        if (settingsActivity.getCurrentUser() != null)
        {
            UserHelper.deleteUser(settingsActivity.getCurrentUser().getUid()).addOnFailureListener(settingsActivity.onFailureListener());

            AuthUI.getInstance()
                    .delete(settingsActivity)
                    .addOnSuccessListener(settingsActivity, this.updateUIAfterRESTRequestsCompleted(RequestCodes.DELETE_USER_TASK));
        }
    }
}