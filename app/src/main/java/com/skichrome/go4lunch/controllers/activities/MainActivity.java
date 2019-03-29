package com.skichrome.go4lunch.controllers.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.google.firebase.auth.FirebaseUser;
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

/**
 * <h1>Main Activity Class</h1>
 * <p>The main activity that is launched when the app is started.</p>
 * <p>
 *     This activity manage 3 fragments (a map, a list of places and a list of workmates).<br/>
 *     It also get the user location and manage the firebase account system, and different api calls.
 * </p>
 *
 */
public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener,
        MapFragment.MapFragmentListeners
{
    //=========================================
    // Fields
    //=========================================

    /**
     * Used to show SnackBar
     */
    @BindView(R.id.main_activity_constraint_layout_container) ConstraintLayout mConstraintLayout;
    /**
     * Used to configure {@link BottomNavigationView}
     */
    @BindView(R.id.activity_main_bottomNavigationView) BottomNavigationView mBottomNavigationView;
    /**
     * Toolbar of the app
     */
    @BindView(R.id.activity_toolbar) Toolbar mToolbar;
    /**
     * Navigation Drawer of the app
     */
    @BindView(R.id.activity_main_menu_drawer_layout) DrawerLayout mDrawerLayout;
    /**
     * Navigation View of the app, used for updates in Navigation Drawer.
     */
    @BindView(R.id.activity_main_navigation_view) NavigationView mNavigationView;
    /**
     * <h1>Represent the current fragment displayed</h1>
     * <p>
     *     Used to save the current fragment displayed, to restore the last fragment displayed when the MainActivity is exited
     * </p>
     */
    @State int mFragmentDisplayed;

    /**
     * Instance of #MapFragment
     */
    private MapFragment mMapFragment;
    /**
     * Instance of #ListFragment
     */
    private ListFragment mListFragment;
    /**
     * Instance of #WorkmatesFragment
     */
    private WorkmatesFragment mWorkmatesFragment;
    /**
     * <h1>Status of http request</h1>
     * <p>
     *     Value changed when an http request is launched, to avoid useless multiple calls on Google API.<br/>
     *     Set to false again when user request position update, to relaunch http request for results update.
     * </p>
     */
    private boolean mIsHttpRequestAlreadyLaunched = false;
    /**
     * <h1>Fragment status</h1>
     * <p>
     *     Used to know if the #MapFragment location need to be updated, to avoid some useless updates.
     * </p>
     */
    private boolean mIsFragmentLocationUpdated = false;
    /**
     * <h1>Disposable for Http request</h1>
     * <p>
     *     Variable used for Http request, mainly for reset if the user exit the app before Http request finish.
     * </p>
     */
    private Disposable mDisposable;
    /**
     * <h1>LocationManager Service</h1>
     * <p>
     *     Used to get user position.
     * </p>
     */
    private LocationManager mLocationManager;
    /**
     * <h1>Callback for LocationManager</h1>
     * <p>
     *     Callback used for LocationManager, called each time a new location is available.
     * </p>
     */
    private LocationListener mLocationListener;
    /**
     * <h1>Last known position</h1>
     * <p>
     *     Last known position send by the callback. Used to update the location on the map, and launch the Http Request.
     * </p>
     */
    private Location mLastKnownLocation;
    /**
     * <h1>Code for Place AutoComplete in {@link #onActivityResult(int, int, Intent)}</h1>
     * <p>
     *     Used to identify the result of placeAutocomplete API, called with startActivityForResult method.
     * </p>
     */
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 4124;

    //=========================================
    // Superclass Methods
    //=========================================

    /**
     * @see BaseActivity#getActivityLayout()
     */
    @Override
    protected int getActivityLayout() { return R.layout.activity_main; }

    /**
     * <h1>MainActivity initialisation</h1>
     * <p>
     *     Configuration of things that does not require location permission.
     * </p>
     * @see BaseActivity#configureActivity()
     */
    @Override
    protected void configureActivity()
    {
        this.configureToolBar();
        this.configureMenuDrawer();
        this.configureNavigationView();
        this.configureBottomNavigationView();
    }

    /**
     * <h1>MainActivity initialisation</h1>
     * <p>
     *     Used to configure things that needs location permission (crash without permission).
     * </p>
     * @see BaseActivity#updateActivity()
     */
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

    /**
     * <h1>MainActivity initialisation</h1>
     * <p>
     *     Check if the user is identified with an account on Firebase Authentication.<br/>
     *     If not, the app request the authentication screen, if yes, the app initialise the fields by calling {@link #updateDrawerFields()}
     * </p>
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        if (isCurrentUserLogged()) this.updateDrawerFields();
        else startActivityForResult(FireStoreAuthentication.startSignInActivity(), RC_SIGN_IN);
    }

    /**
     * <h1>MainActivity clean pause</h1>
     * <p>
     *     To avoid memory leaks, the location manager and it's callback needs to be canceled here.
     *     The disposable is also canceled here, to avoid useless API call.
     * </p>
     */
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

    /**
     * <h1>Results from other activities</h1>
     * <p>
     *     Get the results from Place Autocomplete API and Firestore Authentication.
     *     <ul>
     *         <li>Place Autocomplete : if the place is not null an update of displayed fragment is requested (if not null).</li>
     *         <li>Firestore Authentication : Forward results to FireStoreAuthentication class. Show a SnackBar message if used canceled login.</li>
     *     </ul>
     * </p>
     * @see FireStoreAuthentication#onActivityResult(Activity, FirebaseUser, int, int, Intent)
     */
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

    /**
     * <h1>Inflate the menu defined in xml</h1>
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    /**
     * <h1>Attach toolbar to the activity</h1>
     * <p>
     *     Set the toolbar to the activity and set an elevation.
     * </p>
     */
    private void configureToolBar()
    {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(4);
    }

    /**
     * <h1>Attach listener to the navigationView</h1>
     * <p>
     *     Attach a listener to the navigationView, to be able to use the items defined into it.
     * </p>
     */
    private void configureNavigationView() { mNavigationView.setNavigationItemSelectedListener(this); }

    /**
     * <h1>Navigation Drawer configuration</h1>
     * <p>
     *     Add the navigation drawer to the toolbar.
     * </p>
     */
    private void configureMenuDrawer()
    {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * <h1>Bottom navigation View configuration</h1>
     */
    private void configureBottomNavigationView() { mBottomNavigationView.setOnNavigationItemSelectedListener(this); }

    /**
     * <h1>Configure the location updates</h1>
     * <p>
     *     The LocationManager is initialised here, and a callback is linked to be notified when a new location is available.<br/>
     *     When a new location is available, the method {@link #locationUpdates(Location)} is called.
     * </p>
     */
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

    /**
     * <h1>Location updates processing</h1>
     * <p>
     *     Here we need to update the location in the map fragment, and execute the httpRequest with the new location, but only if the {@link #mIsFragmentLocationUpdated} is set to false.<br/>
     *     The fragment is updated only if he need to be updated.
     * </p>
     * @param location
     *      The new location needed to update fragment and execute http request.
     */
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

    /**
     * <h1>Toolbar search icon Listener</h1>
     * <p>
     *     The search button respond only on MapFragment and ListFragment, on WorkmatesFragment the feature isn't implemented (only show a toast)
     * </p>
     */
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

    /**
     * <h1>Navigation drawer and Bottom Navigation View Listener</h1>
     * <p>
     *     Change the fragment displayed according to where the user has clicked.
     * </p>
     */
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

    /**
     * Navigation drawer configuration.
     */
    @Override
    public void onBackPressed()
    {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) this.mDrawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    //=========================================
    // Fragment Methods
    //=========================================

    /**
     * <h1>Replace current fragment</h1>
     * <p>
     *     Replace the current displayed fragment with the fragment in parameter, only if the fragment in parameter is another fragment.
     * </p>
     * @param fragment
     *      The fragment that will be displayed.
     */
    private void displayFragment(Fragment fragment)
    {
        if (!fragment.isVisible()) getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_frame_layout_for_fragments, fragment)
                .commit();
    }

    /**
     * <h1>MapFragment configuration</h1>
     * <p>
     *     If the Map Fragment has not been initialised, create a new instance of it and call {@link #displayFragment(Fragment)} method to display it.<br/>
     *     Also set the ID {@link #mFragmentDisplayed} and request update of the fragment by changing the value of {@link #mIsFragmentLocationUpdated}
     * /p>
     */
    private void configureMapFragment()
    {
        if (mMapFragment == null) mMapFragment = MapFragment.newInstance();
        displayFragment(mMapFragment);
        this.mIsFragmentLocationUpdated = false;
        this.mFragmentDisplayed = 0;
    }

    /**
     * <h1>ListFragment configuration</h1>
     * <p>
     *     If the List Fragment has not been initialised, create a new instance of it and call {@link #displayFragment(Fragment)} method to display it.<br/>
     *     Also set the ID {@link #mFragmentDisplayed}.
     * /p>
     */
    private void configureListFragment()
    {
        if (mListFragment == null) mListFragment = ListFragment.newInstance();
        displayFragment(mListFragment);
        this.mFragmentDisplayed = 1;
    }

    /**
     * <h1>WorkmatesFragment configuration</h1>
     * <p>
     *     If the Workmates Fragment has not been initialised, create a new instance of it and call {@link #displayFragment(Fragment)} method to display it.<br/>
     *     Also set the ID {@link #mFragmentDisplayed}.
     * /p>
     */
    private void configureWorkmatesFragment()
    {
        if (mWorkmatesFragment == null) mWorkmatesFragment = WorkmatesFragment.newInstance();
        displayFragment(mWorkmatesFragment);
        this.mFragmentDisplayed = 2;
    }

    /**
     * <h1>Launch Setting Activity</h1>
     */
    private void launchSettingActivityAndFragment() { startActivity(new Intent(MainActivity.this, SettingsActivity.class)); }

    /**
     * <h1>Launch Restaurant Details Activity</h1>
     * <p>
     *     Launch the RestaurantDetailsActivity with the place selected by the user.<br/>
     *     If the user hasn't selected a place, a toast is shown.
     * </p>
     */
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

    /**
     * <h1>Show a message with a SnackBar</h1>
     * @param message
     *      THe message that need to be displayed.
     */
    public void showSnackBarMessage(String message) { Snackbar.make(this.mConstraintLayout, message, Snackbar.LENGTH_SHORT).show(); }

    /**
     * <h1>Update the fields in the Navigation Drawer Header</h1>
     */
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

    /**
     * <h1>Download all restaurant near the location of the user</h1>
     * <p>
     *     This method configure the field {@link #mDisposable}, to download all restaurant near the user.
     *     When the Http Request is ended, the visible fragment is updated.
     * </p>
     * @param location
     *      The location of the user.
     */
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

    /**
     * <h1>Place Autocomplete API request</h1>
     * <p>
     *     Create a new request for PlaceAutocomplete API. We request results with startActivityForResult method.
     * </p>
     */
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

    /**
     * <h1>Fragment Callback</h1>
     * <p>
     *     This callback is called when the user has requested an update, in MapFragment or in ListFragment.
     * </p>
     */
    @Override
    public void fragmentNeedUpdateCallback() { this.mIsHttpRequestAlreadyLaunched = false; }
}