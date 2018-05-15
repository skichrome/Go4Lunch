package com.skichrome.go4lunch.controllers.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseActivity;
import com.skichrome.go4lunch.controllers.fragments.ListFragment;
import com.skichrome.go4lunch.controllers.fragments.MapFragment;
import com.skichrome.go4lunch.controllers.fragments.WorkmatesFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.utils.ActivitiesCallbacks;
import com.skichrome.go4lunch.utils.FireBaseAuthentication;
import com.skichrome.go4lunch.utils.MapMethods;
import com.skichrome.go4lunch.utils.RequestCodes;

import java.util.HashMap;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, ActivitiesCallbacks.MarkersChangedListener
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.main_activity_constraint_layout_container) ConstraintLayout constraintLayout;
    @BindView(R.id.activity_main_bottomNavigationView) BottomNavigationView bottomNavigationView;
    @BindView(R.id.activity_toolbar) Toolbar toolbar;
    @BindView(R.id.activity_main_menu_drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.activity_main_navigation_view) NavigationView navigationView;

    private MapFragment mapFragment;
    private ListFragment listFragment;
    private WorkmatesFragment workmatesFragment;

    private FireBaseAuthentication fireBaseAuthentication = new FireBaseAuthentication(this);
    private MapMethods mapMethods = new MapMethods(this);

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
        if (!isCurrentUserLogged())
            fireBaseAuthentication.startSignInActivity();

        this.configureBottomNavigationView();
        this.configureToolBar();
        this.configureMenuDrawer();
        this.configureNavigationView();
        this.configureMapFragment();
        mapMethods.configureGoogleApiClient();
    }

    //=========================================
    // Configuration Methods
    //=========================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    private void configureToolBar()
    {
        setSupportActionBar(toolbar);
    }

    private void configureNavigationView()
    {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void configureMenuDrawer()
    {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureBottomNavigationView()
    {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    //=========================================
    // Listeners Methods
    //=========================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.activity_main_menu_search:
                this.launchPlaceAutocompleteActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem mItem)
    {
        switch (mItem.getItemId())
        {
            // For Bottom Navigation View
            case R.id.menu_bottom_nav_view_map_view:
                this.configureMapFragment();
                break;

            case R.id.menu_bottom_nav_view_list_view:
                this.configureListFragment();
                break;

            case R.id.menu_bottom_nav_view_workmates:
                this.configureWorkmatesFragment();
                break;

            // For Navigation Drawer
            case R.id.activity_main_menu_drawer_your_lunch:
                break;

            case R.id.activity_main_menu_drawer_settings:
                break;

            case R.id.activity_main_menu_drawer_logout:
                fireBaseAuthentication.logoutFromFirebase();
                break;

            default:
                return false;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity as appropriate.
     */
    @Override
    public void onBackPressed()
    {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START))
            this.drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    //=========================================
    // Fragment Methods
    //=========================================

    private void displayFragment(Fragment mFragment)
    {
        if (!mFragment.isVisible())
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout_for_fragments, mFragment).commit();
    }

    private void configureMapFragment()
    {
        if (mapFragment == null)
            mapFragment = MapFragment.newInstance();
        displayFragment(mapFragment);
    }

    private void configureListFragment()
    {
        if (listFragment == null)
            listFragment = ListFragment.newInstance(mapMethods.getGoogleApiClient());
        displayFragment(listFragment);

        mapMethods.getNearbyPlaces(RequestCodes.ID_LIST_FRAGMENT);
    }

    private void configureWorkmatesFragment()
    {
        if (workmatesFragment == null)
            workmatesFragment = WorkmatesFragment.newInstance();
        displayFragment(workmatesFragment);
    }

    //=========================================
    // Place Autocomplete Methods
    //=========================================

    private void launchPlaceAutocompleteActivity()
    {
        try
        {
            // Define a research filter for possible places
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                    .build();

            // Create an intent and start an activity with request code
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(filter)
                    .build(this);
            startActivityForResult(intent, RequestCodes.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        }
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException mE)
        {
            mE.printStackTrace();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapMethods.disconnectFromGoogleApiClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        fireBaseAuthentication.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    //=========================================
    // Update Method
    //=========================================

    public void updatePlacesHashMap(int mFragID, HashMap<String, FormattedPlace> mFormattedPlaceHashMap)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            mapMethods.askUserToGrandPermission();
        else
        {
            switch (mFragID)
            {
                case RequestCodes.ID_MAP_FRAGMENT:
                    mapFragment.updateMarkerOnMap(mFormattedPlaceHashMap);
                    break;

                case RequestCodes.ID_LIST_FRAGMENT:
                    listFragment.updatePlacesHashMap(mFormattedPlaceHashMap);
                    break;

                case RequestCodes.ID_WORKMATES_FRAGMENT:
                    break;

                default:
                    break;
            }
        }
    }

    public void showSnackBarMessage(String mMessage)
    {
        Snackbar.make(this.constraintLayout, mMessage, Snackbar.LENGTH_SHORT).show();
    }

    //=========================================
    // Callback Methods
    //=========================================

    @Override
    public void getMarkerOnMap()
    {
        mapMethods.getNearbyPlaces(RequestCodes.ID_MAP_FRAGMENT);
    }

    @Override
    public void displayRestaurantDetailsOnMarkerClick(FormattedPlace mDetailsRestaurants)
    {
        Intent intent = new Intent(this, RestaurantDetailsActivity.class);
        intent.putExtra(RequestCodes.ACTIVITY_DETAILS_CODE, mDetailsRestaurants);
        startActivity(intent);
    }
}