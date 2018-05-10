package com.skichrome.go4lunch.controllers.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseActivity;
import com.skichrome.go4lunch.controllers.fragments.ListFragment;
import com.skichrome.go4lunch.controllers.fragments.MapFragment;
import com.skichrome.go4lunch.controllers.fragments.WorkmatesFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.utils.ActivitiesCallbacks;
import com.skichrome.go4lunch.utils.RequestCodes;

import java.util.HashMap;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, ActivitiesCallbacks.MarkersChangedListener
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.activity_main_bottomNavigationView) BottomNavigationView bottomNavigationView;
    @BindView(R.id.activity_toolbar) Toolbar toolbar;
    @BindView(R.id.activity_main_menu_drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.activity_main_navigation_view) NavigationView navigationView;

    private Fragment mapFragment;
    private Fragment listFragment;
    private Fragment workmatesFragment;

    private GoogleApiClient googleApiClient;
    public HashMap<String, FormattedPlace> placeHashMap;

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
        MapFragment.setContextWeakReference(this);

        this.configureBottomNavigationView();
        this.configureToolBar();
        this.configureMenuDrawer();
        this.configureNavigationView();
        this.configureMapFragment();

        this.configureGoogleApiClient();
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

    private void configureGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }

    //=========================================
    // Listeners Methods
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
            listFragment = ListFragment.newInstance();
        displayFragment(listFragment);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RequestCodes.PLACE_AUTOCOMPLETE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.d("Place Autocomplete", "onActivityResult: User typed this : " + place.getName());
            }
            if (resultCode == PlaceAutocomplete.RESULT_ERROR)
                Log.e("Place Autocomplete", "onActivityResult: Place autocomplete error ! \n\n" + PlaceAutocomplete.getStatus(this, data).getStatusMessage());
            if (resultCode == RESULT_CANCELED)
                Log.i("Place Autocomplete", "onActivityResult: User canceled !");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (googleApiClient != null && googleApiClient.isConnected())
        {
            googleApiClient.stopAutoManage(this);
            googleApiClient.disconnect();
        }
    }

    //=========================================
    // Google Place Api Methods
    //=========================================

    private void getPlacesOnGoogleAPI()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            this.askUserToGrandPermission();
        else
        {
            try
            {
                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);

                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>()
                {
                    @Override
                    public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces)
                    {
                        placeHashMap = new HashMap<>();

                        for (PlaceLikelihood placeLikelihood : likelyPlaces)
                        {
                            if (placeLikelihood.getPlace().getPlaceTypes().size(/*contains(Place.TYPE_RESTAURANT)*/) != 0)
                            {
                                Place tempPlace = placeLikelihood.getPlace();

                                FormattedPlace place = new FormattedPlace(
                                        tempPlace.getId(),
                                        tempPlace.getName().toString(),
                                        tempPlace.getAddress() != null ? tempPlace.getAddress().toString() : null,
                                        null,
                                        tempPlace.getLatLng().latitude,
                                        tempPlace.getLatLng().longitude,
                                        null,
                                        tempPlace.getWebsiteUri() != null ? tempPlace.getWebsiteUri().toString() : null,
                                        tempPlace.getPhoneNumber() != null ? tempPlace.getPhoneNumber().toString() : null,
                                        null,
                                        null,
                                        null);

                                placeHashMap.put(tempPlace.getName().toString(), place);
                            }
                        }
                        likelyPlaces.release();
                        MapFragment.updateMarkerOnMap(placeHashMap);
                    }
                });
            }
            catch (SecurityException e)
            {
                Log.e("ERROR", "getPlacesOnGoogleAPI: ", e);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult mConnectionResult)
    {
        Log.e("ERROR", "onConnectionFailed: ERROR " + mConnectionResult.getErrorMessage());
    }

    //=========================================
    // Permission Methods
    //=========================================

    private void askUserToGrandPermission()
    {
        // Check with EasyPermissions if tha app have access to the location
        if (!EasyPermissions.hasPermissions(this, RequestCodes.LOCATION_PERMISSION_REQUEST))
            EasyPermissions.requestPermissions(this, getString(R.string.map_fragment_easy_permission_location_user_request), RequestCodes.RC_LOCATION_CODE, RequestCodes.LOCATION_PERMISSION_REQUEST);
        else
        {
            RequestCodes.setLocationPermissionState();
            Log.i("EasyPerm in activity", "askUserToEnableLocationPermission: Location Access granted");
        }
    }

    //=========================================
    // Callback Methods
    //=========================================

    @Override
    public void getMarkerOnMap()
    {
        this.getPlacesOnGoogleAPI();
    }

    @Override
    public void displayRestaurantDetailsOnMarkerClick(FormattedPlace mDetailsRestaurants)
    {
        Intent intent = new Intent(this, RestaurantDetailsActivity.class);
        intent.putExtra(RequestCodes.ACTIVITY_DETAILS_CODE, mDetailsRestaurants);
        startActivity(intent);
    }
}