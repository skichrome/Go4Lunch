package com.skichrome.go4lunch.controllers.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

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
        View view = inflater.inflate(getFragmentLayout(), container, false);
        ButterKnife.bind(this, view);
        this.configureFragment();
        return(view);
    }
}