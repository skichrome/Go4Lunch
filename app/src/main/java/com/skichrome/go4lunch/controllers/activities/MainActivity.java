package com.skichrome.go4lunch.controllers.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseActivity;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.activity_main_bottomNavigationView) BottomNavigationView bottomNavigationView;

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected int getActivityLayout()
    {
        return R.layout.activity_main;
    }

    @Override
    protected void configureActivity()
    {
        this.configureBottomNavigationView();
    }

    //=========================================
    // Bottom navigation view Methods
    //=========================================

    private void configureBottomNavigationView()
    {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem mItem)
    {
        switch (mItem.getItemId())
        {
            case R.id.menu_bottom_nav_view_map_view:
                break;

            case R.id.menu_bottom_nav_view_list_view:
                break;

            case R.id.menu_bottom_nav_view_workmates:
                break;

                default:
                    return false;
        }
        return true;
    }

    //=========================================
    // Methods
    //=========================================
}