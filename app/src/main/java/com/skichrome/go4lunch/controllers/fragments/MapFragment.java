package com.skichrome.go4lunch.controllers.fragments;

import android.support.v4.app.Fragment;

import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseFragment;

/**
 * This Fragment is used to display a mapView with map api, it will display some restaurants around the user mainly
 */
public class MapFragment extends BaseFragment
{
    //=========================================
    // Fields
    //=========================================

    //=========================================
    // New Instance method
    //=========================================

    public static Fragment newInstance()
    {
        return new MapFragment();
    }

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected int getFragmentLayout()
    {
        return R.layout.fragment_map;
    }

    @Override
    protected void configureFragment()
    {
        this.configureMapApi();
    }

    @Override
    protected void updateFragment()
    {
    }

    //=========================================
    // Configuration Methods
    //=========================================

    private void configureMapApi()
    {
    }
}