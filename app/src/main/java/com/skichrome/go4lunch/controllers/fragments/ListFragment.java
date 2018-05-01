package com.skichrome.go4lunch.controllers.fragments;

import android.support.v4.app.Fragment;

import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseFragment;

public class ListFragment extends BaseFragment
{
    //=========================================
    // Fields
    //=========================================

    //=========================================
    // New Instance method
    //=========================================

    public static Fragment newInstance()
    {
        return new ListFragment();
    }

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected int getFragmentLayout()
    {
        return R.layout.fragment_list;
    }

    @Override
    protected void configureFragment()
    {
    }

    @Override
    protected void updateFragment()
    {
    }

    //=========================================
    // Configuration Methods
    //=========================================
}
