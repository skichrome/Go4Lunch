package com.skichrome.go4lunch.controllers.base;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skichrome.go4lunch.R;

import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * This abstract class is used to define common parts for Activities in this app
 */
public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    //=========================================
    // Fields
    //=========================================

    public GoogleApiClient googleApiClient;

    public static final String LOCATION_PERMISSION_REQUEST = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final int RC_LOCATION_CODE = 4123;

    //=========================================
    // Base Abstract Methods
    //=========================================

    /**
     * Used to get layout of activity
     */
    protected abstract int getActivityLayout();
    /**
     * Used to configure all things needed in activity
     */
    protected abstract void configureActivity();
    /**
     * Used to update activities only when user has granted the requested permissions, without these permissions the user can't
     * access to the app
     */
    protected abstract void updateActivityWithPermissionGranted();

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayout());

        // Permission request
        this.askUserToGrandPermission();

        // Bind view
        ButterKnife.bind(this);

        // Configuration
        this.configureActivity();

        if (EasyPermissions.hasPermissions(this, LOCATION_PERMISSION_REQUEST))
        {
            this.configureGoogleApiClient();
            this.updateActivityWithPermissionGranted();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (googleApiClient != null && googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (googleApiClient != null && !googleApiClient.isConnected())
            googleApiClient.connect();
    }

    //=========================================
    // Utils
    //=========================================

    /**
     * Used to get the current logged user
     * @return
     *      Nullable instance of FireBaseUser containing the current logged user
     */
    @Nullable
    public FirebaseUser getCurrentUser()
    {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Return true if a user is connected and false if no user is detected
     * @return
     *      Boolean, status of user
     */
    public Boolean isCurrentUserLogged()
    {
        return (this.getCurrentUser() != null);
    }

    // ------------------------
    // Permission Methods
    // ------------------------

    public void askUserToGrandPermission()
    {
        // Check with EasyPermissions if tha app have access to the location
        if (!EasyPermissions.hasPermissions(this, LOCATION_PERMISSION_REQUEST))
        {
            EasyPermissions.requestPermissions(this, getString(R.string.map_fragment_easy_permission_location_user_request), RC_LOCATION_CODE, LOCATION_PERMISSION_REQUEST);
            return;
        }
        Log.i("EasyPerm in activity", "askUserToEnableLocationPermission: Location Access granted");
    }

    /**
     * Used to pass the management of permissions to EasyPermission library
     *
     * @param requestCode
     *      Integer, identifier of the permission requested
     * @param permissions
     *      String, the list of requested permissions
     * @param grantResults
     *      the result of user to the request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * Used for the first use of the app, when no permissions are granted, the user is allowed to access to the
     * app only when he has granted the requested permissions.
     *
     * @param requestCode
     *      Integer, identifier of the permission requested
     * @param perms
     *      String, the list of requested permissions
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms)
    {
        if (requestCode == RC_LOCATION_CODE)
        {
            this.configureGoogleApiClient();
            this.updateActivityWithPermissionGranted();
        }
    }

    /**
     * Used to close the app when the user deny the requested permissions
     *
     * @param requestCode
     *      Integer, identifier of the permission requested
     * @param perms
     *      String, the list of requested permissions
     */
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms)
    {
        finish();
    }

    // ------------------------
    // Google API configuration
    // ------------------------

    public void configureGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult mConnectionResult)
    {
        Log.e("GoogleAPIClient ERROR", "onConnectionFailed ERROR CODE : " + mConnectionResult.getErrorCode());
    }
}