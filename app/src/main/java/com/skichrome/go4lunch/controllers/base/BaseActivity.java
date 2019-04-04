package com.skichrome.go4lunch.controllers.base;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skichrome.go4lunch.R;

import java.util.List;

import butterknife.ButterKnife;
import icepick.Icepick;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

// Default location for emulator : Paris : Latitude : 48.841810 Longitude : 2.325310

public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks
{
    //=========================================
    // Fields
    //=========================================

    /**
     * Code used to identify location permission
     */
    public static final int RC_LOCATION_CODE = 4123;

    //=========================================
    // Base Abstract Methods
    //=========================================

    protected abstract int getActivityLayout();
    protected abstract void configureActivity();
    protected abstract void updateActivity();

    //=========================================
    // Superclass Methods
    //=========================================

    /**
     * <h1>Activity initialisation</h1>
     * <p>
     *     Used to initialize the app by calling {@link #configureActivity()} method.<br/>
     *     Icepick and Butterknife libraries initialisation.
     * </p>
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(getActivityLayout());
        ButterKnife.bind(this);
        this.configureActivity();
    }

    /**
     * <h1>Permission check in onResume, in all activities</h1>
     * <p>
     *     Because the app is useless without location, user must enable it to use the app.
     * </p>
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        this.configurePermissions();
    }

    /**
     * <h1>Used for Icepick to save variables states.</h1>
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    // ------------------------
    // Permission Methods
    // ------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * <h1>Permission request with EasyPermission</h1>
     * <p>
     *     If the app doesn't have location permission, EasyPermission request this permission.<br/>
     *     If the app has location permission, the method {@link #updateActivity()} is called.
     * </p>
     */
    public void configurePermissions()
    {
        String perms = Manifest.permission.ACCESS_FINE_LOCATION;

        if (!EasyPermissions.hasPermissions(this, perms))
        {
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, RC_LOCATION_CODE, perms)
                            .setRationale(R.string.permission_request_rationale)
                            .setPositiveButtonText(R.string.permission_request_yes)
                            .setNegativeButtonText(R.string.permission_request_no)
                            .build());
        } else if (isCurrentUserLogged())
            this.updateActivity();
    }


    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms)
    {
        Log.e("BaseActivity : ", "User has denied Location permission, this app doesn't work without location");
        if (EasyPermissions.permissionPermanentlyDenied(this, Manifest.permission.ACCESS_FINE_LOCATION))
            new AppSettingsDialog.Builder(this).build().show();
    }

    /**
     * <h1>EasyPermission callback</h1>
     * <p>
     *     Used to relaunch check of permission, and retry to launch activity with permissions granted.
     * </p>
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) { this.configurePermissions(); }

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