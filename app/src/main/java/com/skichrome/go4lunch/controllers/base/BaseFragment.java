package com.skichrome.go4lunch.controllers.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment
{
    //=========================================
    // Base Abstract Methods
    //=========================================

    protected abstract int getFragmentLayout();
    protected abstract void configureFragment();

    //=========================================
    // Empty Constructor
    //=========================================

    public BaseFragment() { }

    //=========================================
    // Superclass Methods
    //=========================================

    /**
     * <h1>Fragments initialisation</h1>
     * <p>
     *     Used to initialise Butterknife library and initialise fragments with {@link #configureFragment()} method.
     * </p>
     */
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        ButterKnife.bind(this, view);
        this.configureFragment();
        return(view);
    }
}