package com.skichrome.go4lunch.controllers.fragments;

import android.support.v4.app.Fragment;

import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseFragment;

public class WorkmatesFragment extends BaseFragment
{
    //=========================================
    // Fields
    //=========================================

    //=========================================
    // New Instance method
    //=========================================

    public static Fragment newInstance()
    {
        return new WorkmatesFragment();
    }

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected int getFragmentLayout()
    {
        return R.layout.fragment_workmates;
    }

    @Override
    protected void configureFragment()
    {
    }

    //=========================================
    // Configuration Methods
    //=========================================
}