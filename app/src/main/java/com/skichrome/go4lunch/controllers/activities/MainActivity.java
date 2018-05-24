package com.skichrome.go4lunch.controllers.activities;

import android.content.Intent;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.base.BaseActivity;
import com.skichrome.go4lunch.controllers.fragments.ListFragment;
import com.skichrome.go4lunch.controllers.fragments.MapFragment;
import com.skichrome.go4lunch.controllers.fragments.WorkmatesFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.FireStoreAuthentication;
import com.skichrome.go4lunch.utils.MapMethods;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import java.util.ArrayList;

import butterknife.BindView;

import static com.skichrome.go4lunch.utils.FireStoreAuthentication.RC_SIGN_IN;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener,
        MapFragment.MapFragmentListeners,
        ListFragment.OnFragmentReadyListener,
        MapMethods.ListenersNearbyPlaces,
        MapMethods.PlaceAutocompleteListener
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
    private ArrayList<FormattedPlace> placesList;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 4124;

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
        this.configureToolBar();
        this.configureMenuDrawer();
        this.configureNavigationView();
        this.configureBottomNavigationView();
    }

    @Override
    protected void updateActivityWithPermissionGranted()
    {
        MapMethods.getNearbyPlaces(this, googleApiClient);
        this.configureMapFragment();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (isCurrentUserLogged()) this.updateDrawerFields();
        else startActivityForResult(FireStoreAuthentication.startSignInActivity(), RC_SIGN_IN);
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
                        ArrayList<FormattedPlace> list = new ArrayList<>();
                        list.add(formattedPlace);
                        updatePlacesHashMap(list);
                        Log.e("Place Autocomplete", "onActivityResult: User typed this : " + place.getName());
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
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(4);
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
                if (workmatesFragment == null || !workmatesFragment.isVisible()) MapMethods.getLastKnownLocationForPlaceAutocomplete(this, this);
                else Toast.makeText(this, "This feature isn't implemented yet !", Toast.LENGTH_SHORT).show();
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
                this.launchRestaurantDetailsActivity();
                break;

            case R.id.activity_main_menu_drawer_settings:
                this.launchSettingActivityAndFragment();
                break;

            case R.id.activity_main_menu_drawer_logout:
                FireStoreAuthentication.logoutFromFirestore(this);
                break;

            default:
                return false;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) this.drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    //=========================================
    // Fragment Methods
    //=========================================

    private void displayFragment(Fragment mFragment)
    {
        if (!mFragment.isVisible()) getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout_for_fragments, mFragment).commit();
    }

    private void configureMapFragment()
    {
        if (mapFragment == null) mapFragment = MapFragment.newInstance();
        displayFragment(mapFragment);
    }

    private void configureListFragment()
    {
        if (listFragment == null) listFragment = ListFragment.newInstance();
        displayFragment(listFragment);
    }

    private void configureWorkmatesFragment()
    {
        if (workmatesFragment == null) workmatesFragment = WorkmatesFragment.newInstance();
        displayFragment(workmatesFragment);
    }

    private void launchSettingActivityAndFragment()
    {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void launchRestaurantDetailsActivity()
    {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(mDocumentSnapshot ->
        {
            User user = mDocumentSnapshot.toObject(User.class);
            FormattedPlace place = user != null ? user.getSelectedPlace() : null;
            if (place != null)
            {
                Intent intent = new Intent(getApplicationContext(), RestaurantDetailsActivity.class);
                intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, place);
                startActivity(intent);
            }
            else Toast.makeText(getApplicationContext(), R.string.activity_main_toast_no_restaurant_selected, Toast.LENGTH_SHORT).show();
        });
    }

    //=========================================
    // Update Method
    //=========================================

    private void updatePlacesHashMap(ArrayList<FormattedPlace> mPlaces)
    {
        this.placesList = new ArrayList<>();
        this.placesList.addAll(mPlaces);
        if (mapFragment != null && mapFragment.isVisible()) mapFragment.updateMarkerOnMap(placesList);
        if (listFragment != null && listFragment.isVisible()) listFragment.updatePlacesList(placesList);
    }

    public void showSnackBarMessage(String mMessage)
    {
        Snackbar.make(this.constraintLayout, mMessage, Snackbar.LENGTH_SHORT).show();
    }

    private void updateDrawerFields()
    {
        View headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.nav_drawer_header_username);
        TextView email = headerView.findViewById(R.id.nav_drawer_header_email);
        ImageView profilePicture = headerView.findViewById(R.id.nav_drawer_header_user_image_profile);

        name.setText(getCurrentUser().getDisplayName());
        email.setText(getCurrentUser().getEmail());

        if (getCurrentUser().getPhotoUrl() != null) Glide.with(headerView).load(getCurrentUser().getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(profilePicture);
    }

    //=========================================
    // Callback Methods
    //=========================================

    @Override
    public void onListFragmentReady()
    {
        if (placesList != null) listFragment.updatePlacesList(placesList);
    }

    @Override
    public void getResultOnClickFloatingActionBtn()
    {
        MapMethods.getNearbyPlaces(this, googleApiClient);
    }

    @Override
    public void onResult(ArrayList<FormattedPlace> mPlaceHashMap)
    {
        this.updatePlacesHashMap(mPlaceHashMap);
    }

    @Override
    public void onPlaceAutocompleteReady(Intent mIntent)
    {
        startActivityForResult(mIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
    }
}