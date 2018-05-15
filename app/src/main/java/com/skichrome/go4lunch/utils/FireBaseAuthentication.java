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

import java.util.Arrays;

import static com.skichrome.go4lunch.utils.RequestCodes.DELETE_USER_TASK;
import static com.skichrome.go4lunch.utils.RequestCodes.SIGN_OUT_TASK;

public class FireBaseAuthentication
{
    //=========================================
    // Fields
    //=========================================

    private MainActivity mainActivity;

    //=========================================
    // Constructor
    //=========================================

    public FireBaseAuthentication(MainActivity mMainActivity)
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
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),      // EMAIL
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),           // GOOGLE
                                        new AuthUI.IdpConfig.FacebookBuilder().build()))        // FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.lunch)
                        .build(),
                RequestCodes.RC_SIGN_IN);
    }

    // manage activities results cases
    public void handleResponseAfterSignIn(int mRequestCode, int mResultCode, Intent mData)
    {
        IdpResponse response = IdpResponse.fromResultIntent(mData);

        if (mRequestCode == RequestCodes.RC_SIGN_IN)
        {
            if (mResultCode == Activity.RESULT_OK)
            { // SUCCESS
                mainActivity.showSnackBarMessage("Login successful");
            }
            else
            { // ERRORS
                if (response == null)
                {
                    mainActivity.showSnackBarMessage("Login canceled");
                }
                else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK)
                {
                    mainActivity.showSnackBarMessage("No active network detected");
                }
                else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR)
                {
                    Log.e("StartActivity", "onActivityResult error : ", response.getError());
                    mainActivity.showSnackBarMessage("Error when login");
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

    public void logoutFromFirebase()
    {
    AuthUI.getInstance()
            .signOut(mainActivity)
            .addOnSuccessListener(mainActivity, this.updateUIAfterRESTRequestsCompleted(RequestCodes.SIGN_OUT_TASK));
    mainActivity.finish();
    }

    public void deleteAccountFromFirebase()
    {
        if (mainActivity.isCurrentUserLogged())
        {
            AuthUI.getInstance()
                    .delete(mainActivity)
                    .addOnSuccessListener(mainActivity, this.updateUIAfterRESTRequestsCompleted(RequestCodes.DELETE_USER_TASK));
        }
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
                        mainActivity.finish();
                        break;
                    case DELETE_USER_TASK:
                        mainActivity.finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }
}