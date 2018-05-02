package com.skichrome.go4lunch.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * This abstract class is used to define common parts for Activities in this app
 */
public abstract class BaseActivity extends AppCompatActivity
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
    }
}