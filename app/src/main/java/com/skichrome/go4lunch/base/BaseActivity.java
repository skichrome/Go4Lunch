package com.skichrome.go4lunch.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skichrome.go4lunch.utils.RequestCodes;

import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * This abstract class is used to define common parts for Activities in this app
 */
public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks
{
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

        //bind view
        ButterKnife.bind(this);

        this.configureActivity();

        if (EasyPermissions.hasPermissions(this, RequestCodes.LOCATION_PERMISSION_REQUEST))
            this.updateActivityWithPermissionGranted();
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
        if (requestCode == RequestCodes.RC_LOCATION_CODE)
            this.updateActivityWithPermissionGranted();
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

    //=========================================
    // Utils
    //=========================================

    /**
     * Used to get the current logged user
     * @return
     *      Nullable instance of FireBaseUser containing the current logged user
     */
    @Nullable
    protected FirebaseUser getCurrentUser()
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
}