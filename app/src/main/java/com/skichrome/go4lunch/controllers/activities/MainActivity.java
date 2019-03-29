package com.skichrome.go4lunch.controllers.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.base.BaseActivity;
import com.skichrome.go4lunch.controllers.fragments.ListFragment;
import com.skichrome.go4lunch.controllers.fragments.MapFragment;
import com.skichrome.go4lunch.controllers.fragments.WorkmatesFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.models.googleplacedetails.MainPlaceDetails;
import com.skichrome.go4lunch.utils.FireStoreAuthentication;
import com.skichrome.go4lunch.utils.GoogleApiStream;
import com.skichrome.go4lunch.utils.MapMethods;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import butterknife.BindView;
import icepick.State;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.skichrome.go4lunch.utils.FireStoreAuthentication.RC_SIGN_IN;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener,
        MapFragment.MapFragmentListeners
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.main_activity_constraint_layout_container) ConstraintLayout mConstraintLayout;
    @BindView(R.id.activity_main_bottomNavigationView) BottomNavigationView mBottomNavigationView;
    @BindView(R.id.activity_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_main_menu_drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.activity_main_navigation_view) NavigationView mNavigationView;
    @State int mFragmentDisplayed;

    private MapFragment mMapFragment;
    private ListFragment mListFragment;
    private WorkmatesFragment mWorkmatesFragment;
    private boolean mIsHttpRequestAlreadyLaunched = false;
    private boolean mIsFragmentLocationUpdated = false;
    private Disposable mDisposable;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLastKnownLocation;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 4124;

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected int getActivityLayout() { return R.layout.activity_main; }

    @Override
    protected void configureActivity()
    {
        this.configureToolBar();
        this.configureMenuDrawer();
        this.configureNavigationView();
        this.configureBottomNavigationView();
    }

    @Override
    protected void updateActivity()
    {
        switch (this.mFragmentDisplayed)
        {
            case 1 : this.configureListFragment();
                break;
            case 2 : this.configureWorkmatesFragment();
                break;
                default: this.configureMapFragment();
        }
        this.configureLocation();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (isCurrentUserLogged()) this.updateDrawerFields();
        else startActivityForResult(FireStoreAuthentication.startSignInActivity(), RC_SIGN_IN);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
        if (this.mLocationManager != null)
        {
            this.mLocationManager.removeUpdates(mLocationListener);
            this.mLocationManager = null;
        }
        if (this.mLocationListener != null) this.mLocationListener = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == RESULT_OK)
                {
                    Place place = PlaceAutocomplete.getPlace(this, data);

                    if (place != null)
                    {
                        FormattedPlace formattedPlace = new FormattedPlace(place.getId(),
                                place.getName().toString(),
                                place.getAddress() == null ? null : place.getAddress().toString(),
                                place.getPhoneNumber() == null ? null : place.getPhoneNumber().toString(),
                                place.getWebsiteUri() == null ? null : place.getWebsiteUri().toString(),
                                place.getLatLng().latitude,
                                place.getLatLng().longitude);
                        if (mMapFragment != null && mMapFragment.isVisible()) mMapFragment.updateMapForAutocomplete(formattedPlace);
                        if (mListFragment != null && mListFragment.isVisible()) mListFragment.updateListForAutocomplete(formattedPlace);
                    }
                    return;
                }
        }
        String result = FireStoreAuthentication.onActivityResult(this, this.getCurrentUser(), requestCode, resultCode, data);
        if (result != null)
        {
            this.showSnackBarMessage(result);
            if (result.equals(getString(R.string.firebase_login_cancel))) this.finish();
        }
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
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(4);
    }

    private void configureNavigationView() { mNavigationView.setNavigationItemSelectedListener(this); }

    private void configureMenuDrawer()
    {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureBottomNavigationView() { mBottomNavigationView.setOnNavigationItemSelectedListener(this); }

    @SuppressLint("MissingPermission")
    private void configureLocation()
    {
        this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.mLocationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                if (location != null)
                {
                    mLastKnownLocation = location;
                    locationUpdates(location);
                } else
                    Log.e("BaseActivity : ", "Error, location is null, cancel PlaceApi request");
            }
            @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override public void onProviderEnabled(String provider) { }
            @Override public void onProviderDisabled(String provider) { }
        };
        this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this.mLocationListener);
    }

    private void locationUpdates(Location location)
    {
        if (!this.mIsFragmentLocationUpdated && this.mMapFragment != null && this.mMapFragment.isVisible())
        {
            this.mMapFragment.updateLocation(location);
            this.mIsFragmentLocationUpdated = true;
        }
        if (!this.mIsHttpRequestAlreadyLaunched)
        {
            this.executeHttpRequest(location);
            this.mIsHttpRequestAlreadyLaunched = true;
        }
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
                if (mLastKnownLocation != null && mWorkmatesFragment == null || !mWorkmatesFragment.isVisible()) launchPlaceAutocompleteActivity();
                else Toast.makeText(this, "This feature isn't implemented yet !", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
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
                this.launchRestaurantDetailsActivity();
                break;

            case R.id.activity_main_menu_drawer_settings:
                this.launchSettingActivityAndFragment();
                break;

            case R.id.activity_main_menu_drawer_logout:
                FireStoreAuthentication.logout(this);
                break;

            default:
                return false;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) this.mDrawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    //=========================================
    // Fragment Methods
    //=========================================

    private void displayFragment(Fragment fragment)
    {
        if (!fragment.isVisible()) getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_frame_layout_for_fragments, fragment)
                .commit();
    }

    private void configureMapFragment()
    {
        if (mMapFragment == null) mMapFragment = MapFragment.newInstance();
        displayFragment(mMapFragment);
        this.mIsFragmentLocationUpdated = false;
        this.mFragmentDisplayed = 0;
    }

    private void configureListFragment()
    {
        if (mListFragment == null) mListFragment = ListFragment.newInstance();
        displayFragment(mListFragment);
        this.mFragmentDisplayed = 1;
    }

    private void configureWorkmatesFragment()
    {
        if (mWorkmatesFragment == null) mWorkmatesFragment = WorkmatesFragment.newInstance();
        displayFragment(mWorkmatesFragment);
        this.mFragmentDisplayed = 2;
    }

    private void launchSettingActivityAndFragment() { startActivity(new Intent(MainActivity.this, SettingsActivity.class)); }

    private void launchRestaurantDetailsActivity()
    {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(mDocumentSnapshot ->
        {
            User user = mDocumentSnapshot.toObject(User.class);
            FormattedPlace place = user != null ? user.getSelectedPlace() : null;
            if (place != null)
            {
                Intent intent = new Intent(this, RestaurantDetailsActivity.class);
                intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, place);
                startActivity(intent);
            }
            else Toast.makeText(getApplicationContext(), R.string.activity_main_toast_no_restaurant_selected, Toast.LENGTH_SHORT).show();
        });
    }

    //=========================================
    // Update Method
    //=========================================

    public void showSnackBarMessage(String message) { Snackbar.make(this.mConstraintLayout, message, Snackbar.LENGTH_SHORT).show(); }

    private void updateDrawerFields()
    {
        View headerView = mNavigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.nav_drawer_header_username);
        TextView email = headerView.findViewById(R.id.nav_drawer_header_email);
        ImageView profilePicture = headerView.findViewById(R.id.nav_drawer_header_user_image_profile);

        name.setText(getCurrentUser().getDisplayName());
        email.setText(getCurrentUser().getEmail());

        if (getCurrentUser().getPhotoUrl() != null) Glide.with(headerView).load(getCurrentUser().getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(profilePicture);
    }

    //=========================================
    // Http request Method
    //=========================================

    private void executeHttpRequest(Location location)
    {
        this.mDisposable = GoogleApiStream.streamFetchPlaces(getString(R.string.google_place_api_key), location, 20).subscribeWith(new DisposableObserver<MainPlaceDetails>()
        {
            @Override
            public void onNext(MainPlaceDetails mainPlaceDetails) { MapMethods.updateDetailsOnFirecloud(mainPlaceDetails.getResult(), mainPlaceDetails.getStatus()); }
            @Override
            public void onError(Throwable e) { Log.e("Main activity : ", "Something went wrong with http request", e); }
            @Override
            public void onComplete()
            {
                if (mMapFragment != null && mMapFragment.isVisible())
                {
                    mMapFragment.updateMarkerOnMap();
                    mIsFragmentLocationUpdated = false;
                }
                if (mListFragment != null && mListFragment.isVisible()) mListFragment.updatePlacesList();
            }
        });
    }

    private void launchPlaceAutocompleteActivity()
    {
        try
        {
            AutocompleteFilter filter = new AutocompleteFilter.Builder()                             // Define a research filter for possible places
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)      // Create an intent and start an activity with request code
                    .setFilter(filter)
                    .setBoundsBias(MapMethods.convertToBounds(mLastKnownLocation, 30))
                    .build(this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        }
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException mE) { mE.printStackTrace(); }
    }

    //=========================================
    // Fragment Callbacks Method
    //=========================================

    @Override
    public void fragmentNeedUpdateCallback() { this.mIsHttpRequestAlreadyLaunched = false; }
}