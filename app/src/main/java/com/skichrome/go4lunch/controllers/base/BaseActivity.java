package com.skichrome.go4lunch.controllers.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * This abstract class is used to define common parts for Activities in this app
 */
public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks
{
    //=========================================
    // Fields
    //=========================================

    public static final int RC_LOCATION_CODE = 4123;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isLocationListenerInitialized = false;

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
    protected abstract void updateActivityWithLocationUpdates(Location location);

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayout());
        ButterKnife.bind(this);
        this.configureActivity();
        this.configurePermissions();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (isLocationListenerInitialized) locationManager.removeUpdates(locationListener);
    }

    // ------------------------
    // Permission Methods
    // ------------------------

    /**
     * Used to pass the management of permissions to EasyPermission library
     *
     * @param requestCode  Integer, identifier of the permission requested
     * @param permissions  String, the list of requested permissions
     * @param grantResults the result of user to the request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void configurePermissions()
    {
        String perms = Manifest.permission.ACCESS_FINE_LOCATION;

        if (!EasyPermissions.hasPermissions(this, perms))
        {
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, RC_LOCATION_CODE, perms)
                            .setRationale("Autoriser l'accès à la position de l'appareil ?")
                            .setPositiveButtonText("Oui")
                            .setNegativeButtonText("Non")
                            .build());
        } else
        {
            this.configureLocation();
        }
        Log.i("EasyPerm in activity", "askUserToEnableLocationPermission: Location Access granted");
    }

    @SuppressLint("MissingPermission")
    private void configureLocation()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                if (location != null)
                {
                    Log.d("BaseActivity : ", "You have a location request : " + location.toString());
                    updateActivityWithLocationUpdates(location);
                } else
                    Log.e("BaseActivity : ", "Error, location is null, cancel PlaceApi request");
            }
            @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override public void onProviderEnabled(String provider) { }
            @Override public void onProviderDisabled(String provider) { }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener);
        isLocationListenerInitialized = true;
    }

    /**
     * Used to close the app when the user deny the requested permissions
     *
     * @param requestCode Integer, identifier of the permission requested
     * @param perms       String, the list of requested permissions
     */
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms)
    {
        Log.e("BaseActivity : ", "User has denied Location permission, this app doesn't work without location");
        Toast.makeText(this, "You must enable location to use this app", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) { }

    //=========================================
    // Firebase Utils
    //=========================================

    /**
     * Used to get the current logged user
     *
     * @return Nullable instance of FireBaseUser containing the current logged user
     */
    @Nullable
    public FirebaseUser getCurrentUser() { return FirebaseAuth.getInstance().getCurrentUser(); }
    /**
     * Return true if a user is connected and false if no user is detected
     *
     * @return Boolean, status of user
     */
    public Boolean isCurrentUserLogged() { return (this.getCurrentUser() != null); }
}