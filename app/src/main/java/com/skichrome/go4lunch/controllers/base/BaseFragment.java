package com.skichrome.go4lunch.controllers.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * This abstract class is used to define common parts for fragments in this app
 */
public abstract class BaseFragment extends Fragment
{
    //=========================================
    // Base Abstract Methods
    //=========================================

    /**
     * Used to get the layout file and bind it to the view
     */
    protected abstract int getFragmentLayout();
    /**
     * Used to configure the design, for example here will be executed the http Request, the RecyclerView will be configured here
     */
    protected abstract void configureFragment();

    //=========================================
    // Empty Constructor
    //=========================================

    /**
     * Empty constructor, needed for Fragment instantiation, not modifiable
     */
    public BaseFragment() { }

    //=========================================
    // Superclass Methods
    //=========================================

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // Get layout identifier from abstract method
        View view = inflater.inflate(getFragmentLayout(), container, false);
        //bind view
        ButterKnife.bind(this, view);

        // Configure and update Design (Developer will implement these method instead of override onCreateView())
        this.configureFragment();

        return(view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}